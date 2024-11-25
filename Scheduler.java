import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.time.Clock;

public class Scheduler {
    // Variable declarations.
    private Timer interrupt = new Timer(true);
    private Clock clock;
    private static LinkedList<PCB> highPriority = new LinkedList<>();
    private static LinkedList<PCB> midPriority =  new LinkedList<>();
    private static LinkedList<PCB> lowPriority = new LinkedList<>();
    private static LinkedList<PCB> sleepingProcess = new LinkedList<>();
    private static LinkedHashMap<Integer, PCB> sleepProcessTime = new LinkedHashMap<>();
    private HashMap<Integer, PCB> sendMessage = new HashMap<>();
    private HashMap<Integer, PCB> waitMessage = new HashMap<>();
    private static Kernel kernel = new Kernel();
    public PCB currentlyRunning;
    private int wakeUpTime = 0;

    /* There is an issue with at what point should currentlyRunning become null.
     */
    /**
     * Constructor.
     */
    public Scheduler() {
        clock = Clock.systemDefaultZone();

        interrupt.schedule(new TimerTask() {
            @Override
            public void run() {
                if (currentlyRunning != null) {
                    currentlyRunning.userlandProcess.requestStop();
                    currentlyRunning.timeOut++;
                    Demotion();
                }
            }
        }, 0, 250);
    }

    /**
     * This method is calling the overloaded CreateProcess() method.
     * @param up
     * @return
     */
    public int CreateProcess(UserlandProcess up) {
        return CreateProcess(up, OS.Priority.INTERACTIVE);
    }

    /**
     * Take the currently running process and put it at the end of the list.
     * It then takes the head of the list and runs it.
     */
    public void SwitchProcess() {
        if (!sleepProcessTime.isEmpty()) {
            WakeUp();
        }
        ProcessChange();
    }

    /**
     * This method creates a new PCB before adding it to the correct priority list based on its priority type.
     * @param up
     * @param priority
     * @return PCB.pid
     */
    public int CreateProcess(UserlandProcess up, OS.Priority priority) {
        // Variable declaration.
        PCB pcb = new PCB(up, priority);

        // Add pcb to the correct queue depending on the priority type.
        switch (priority) {
            case REALTIME:
                highPriority.add(pcb);
                PCB.nextPid++;
                break;
            case INTERACTIVE:
                midPriority.add(pcb);
                PCB.nextPid++;
                break;
            case BACKGROUND:
                lowPriority.add(pcb);
                PCB.nextPid++;
                break;
            default:
                try {
                    throw new Exception("Send help please T_T.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
        if (currentlyRunning == null || currentlyRunning.isDone()) {
            SwitchProcess();
        }
        PCB.pid = PCB.nextPid;
        // Add the pid and PCB into the HashMap so it has all the PCBs with their pid.
        sendMessage.put(PCB.pid, pcb);
        return PCB.pid;
    }

    /**
     * This method puts the process to sleep for a specified number of milliseconds
     * and then sets the wakeup time.
     * @param milliseconds
     */
    public void Sleep(int milliseconds) {
        wakeUpTime = (int) clock.millis() + milliseconds;
        sleepingProcess.add(currentlyRunning);
        sleepProcessTime.put(milliseconds, currentlyRunning);
        SwitchProcess();
    }

    /**
     * Helper method for sleep().
     */
    public void WakeUp() {
        for (Map.Entry<Integer, PCB> element : sleepProcessTime.entrySet()) {
            if ((int) clock.millis() >= element.getKey()) {
                switch(element.getValue().priority) {
                    case REALTIME:
                        highPriority.add(sleepProcessTime.remove(element.getKey()));
                        break;
                    case INTERACTIVE:
                        midPriority.add(sleepProcessTime.remove(element.getKey()));
                        break;
                    case BACKGROUND:
                        lowPriority.add(sleepProcessTime.remove(element.getKey()));
                        break;
                    default:
                }
            }
        }
    }

    /**
     * This helper method adds the process to the correct queue.
     */
    public void AddToCorrectQueue(PCB process) {
        switch (currentlyRunning.priority) {
            case REALTIME:
                highPriority.add(currentlyRunning);
                break;
            case INTERACTIVE:
                midPriority.add(currentlyRunning);
                break;
            case BACKGROUND:
                lowPriority.add(currentlyRunning);
                break;
        }
    }

    /**
     * This method uses random to the process from the correct queue and puts the stopped process into the correct queue.
     */
    public void ProcessChange() {
        // Add to the correct queue if the process is finished.
        if (currentlyRunning != null) {
            if (!currentlyRunning.isDone()) {
                AddToCorrectQueue(currentlyRunning);
            } else {
                // Remove entries.
                if (sendMessage.containsValue(currentlyRunning)) {
                    sendMessage.remove(currentlyRunning);
                }

                // Remove entries.
                if (waitMessage.containsValue(currentlyRunning)) {
                    waitMessage.remove(currentlyRunning);
                }
            }
        }

        Random random = new Random();
        int randomNumber = random.nextInt(10); // 0-9 numbers

        if (!highPriority.isEmpty()) {
            if (randomNumber == 0 && !lowPriority.isEmpty()) {
                currentlyRunning = lowPriority.removeFirst(); // 1/10
            } else if ((randomNumber > 0 && randomNumber <= 3) && !midPriority.isEmpty()) {
                currentlyRunning = midPriority.removeFirst(); // 3/10
            } else {
                currentlyRunning = highPriority.removeFirst(); // 6/10
            }
        } else if (!midPriority.isEmpty()) {
            randomNumber = random.nextInt(4); // 0-3
            if (randomNumber == 0 && !lowPriority.isEmpty()) {
                currentlyRunning = lowPriority.removeFirst(); // 1/4
            } else {
                currentlyRunning = midPriority.removeFirst(); // 3/4
            }
        } else {
            currentlyRunning = lowPriority.removeFirst(); // Background only left.
        }
    }

    /**
     * This method makes the current process end before it starts another one.
     */
    public void Exit() {
        try {
            // Close all open devices.
            CloseDevices(currentlyRunning);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        highPriority.remove(currentlyRunning);
        midPriority.remove(currentlyRunning);
        lowPriority.remove(currentlyRunning);
        sleepProcessTime.entrySet().remove(currentlyRunning);
        sendMessage.entrySet().remove(currentlyRunning);
        waitMessage.entrySet().remove(currentlyRunning);

        // Free memory.
        for (int i = 0; i < currentlyRunning.getMemoryMap().length; i++) {
            if (currentlyRunning.getMemoryMap()[i] != -1) {
                OS.FreeMemory(i * 1024, 1024);
            }
        }
        Hardware.ClearTLB();
        SwitchProcess();
    }

    /**
     * This method demotes a process to lower level of priority if it has more runtime than other processes
     * and then adds it to the correct queue.
     */
    public void Demotion() {
        if (currentlyRunning.timeOut > 5) {
            if (currentlyRunning.priority == OS.Priority.REALTIME) {
                currentlyRunning.priority = OS.Priority.INTERACTIVE;
            } else if (currentlyRunning.priority == OS.Priority.INTERACTIVE) {
                currentlyRunning.priority = OS.Priority.BACKGROUND;
            } else {
                currentlyRunning.priority = OS.Priority.BACKGROUND;
            }
        }
    }

    /**
     * Helper method that returns the current process running.
     * @return currentlyRunning
     */
    public PCB getCurrentlyRunning() {
        return currentlyRunning;
    }

    /**
     * This is a helper method that closes all the devices when a process is completed after exit.
     * @param process
     * @throws IOException
     */
    public void CloseDevices(PCB process) throws IOException {
        for (int i = 0; i < process.getPcbIDs().length; i++) {
            if (process.getPcbIDs()[i] != -1) {
                kernel.Close(i);
            }
        }
    }

    /**
     * This method returns the current process' pid.
     * @return currentlyRunning.pid
     */
    public int GetPid() {
        return currentlyRunning.getPid();
    }

    /**
     * This method returns the pid of a process with that name.
     * @param name
     * @return currentlyRunning.pid
     */
    public int GetPidByName(String name) {
        for (Integer p : sendMessage.keySet()) {
            if (sendMessage.get(p).getProcessName().equals(name)) {
                return sendMessage.get(p).getPid();
            }
        }
        return -1;
    }

    /**
     * This method is a helper method that puts the waiting message into the proper
     * runnable while also populating and removing entries.
     */
    public void RestoreRunnable(PCB process) {
        switch(process.priority) {
            case REALTIME:
                highPriority.add(process);
                break;
            case INTERACTIVE:
                midPriority.add(waitMessage.remove(process));
                break;
            case BACKGROUND:
                lowPriority.add(waitMessage.remove(process));
                break;
            default:
        }
    }

    /**
     * This method returns the send message variable.
     * @return
     */
    public HashMap<Integer, PCB> getSendMessage() {
        return sendMessage;
    }

    /**
     * This method returns the wait message variable.
     * @return
     */
    public HashMap<Integer, PCB> getWaitMessage() {
        return waitMessage;
    }
}
