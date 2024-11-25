import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Kernel extends Process implements Device {
    // Variable declarations.
    private Scheduler schedule;
    private VFS vfs;
    private boolean[] freeList = new boolean[100];

    /**
     * Constructor.
     */
    public Kernel() {
        schedule = new Scheduler();
        vfs = new VFS();
    }

    /**
     * This method is the kernel and manages creating and switching processes.
     */
    @Override
    public void main() throws Exception {
        while (true) {
            switch (OS.currentlyRunning) {
                case CREATEPROCESS:
                    UserlandProcess userProcess = (UserlandProcess) OS.functionParams.getFirst();

                    if (OS.functionParams.size() > 1) {
                        OS.returnValue = schedule.CreateProcess(userProcess, (OS.Priority) OS.functionParams.get(1));
                    } else {
                        OS.returnValue = schedule.CreateProcess(userProcess);
                    }
                    break;
                case SWITCHPROCESS:
                    schedule.SwitchProcess();
                    break;
                case SLEEP:
                    //System.out.println("I'm calling sleep scheduler");
                    int milliseconds = (int) OS.functionParams.getFirst();
                    schedule.Sleep(milliseconds);
                    break;
                case EXIT:
                    schedule.Exit();
                    break;
                case OPEN:
                    OS.returnValue = Open((String) OS.functionParams.getFirst());
                    break;
                case CLOSE:
                    Close((int) OS.functionParams.getFirst());
                    break;
                case READ:
                    if (OS.functionParams.size() > 1) {
                        OS.returnValue = Read((int) OS.functionParams.getFirst(), (int) OS.functionParams.get(1));
                    }
                    break;
                case WRITE:
                    if (OS.functionParams.size() > 1) {
                        OS.returnValue = Write((int) OS.functionParams.getFirst(), (byte[]) OS.functionParams.get(1));
                    }
                    break;
                case SEEK:
                    if (OS.functionParams.size() > 1) {
                        Seek((int) OS.functionParams.getFirst(), (int) OS.functionParams.get(1));
                    }
                    break;
                default:
                    try {
                        throw new Exception("It's in the hands of god now.");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            }
            schedule.currentlyRunning.start();
            stop();
        }
    }

    /**
     * This helper method returns the Scheduler object.
     * @return schedule
     */
    public Scheduler getSchedule() {
        return schedule;
    }

    /**
     * This method calls open() from VFS class.
     * @param s
     * @return i
     * @throws Exception
     */
    @Override
    public int Open(String s) throws Exception {
        // Variable declaration.
        int[] temp = schedule.getCurrentlyRunning().getPcbIDs();

        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == -1) {
                int resultID = vfs.Open(s);

                if (resultID == -1) {
                    return -1;
                } else {
                    temp[i] = resultID;
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * This method calls close() from VFS class and sets the PCB entry to -1.
     * @param id
     * @throws IOException
     */
    @Override
    public void Close(int id) throws IOException {
        // Variable declaration.
        int[] temp = schedule.getCurrentlyRunning().getPcbIDs();

        if (id >= 0 && temp[id] != -1) {
            vfs.Close(temp[id]);
            temp[id] = -1;
        } else {
            throw new IOException("File doesn't exist!");
        }
    }

    /**
     * This method calls read() from VFS class.
     * @param id
     * @param size
     * @return vfs.Read(temp[id], size)
     * @throws IOException
     */
    @Override
    public byte[] Read(int id, int size) throws IOException {
        // Variable declaration.
        int[] temp = schedule.currentlyRunning.getPcbIDs();

        return vfs.Read(temp[id], size);
    }

    /**
     * This method calls seek() from VFS class.
     * @param id
     * @param to
     * @throws IOException
     */
    @Override
    public void Seek(int id, int to) throws IOException {
        int[] temp = schedule.currentlyRunning.getPcbIDs();

        vfs.Seek(temp[id], to);
    }

    /**
     * This method calls write() from VFS class.
     * @param id
     * @param data
     * @return
     * @throws IOException
     */
    @Override
    public int Write(int id, byte[] data) throws IOException {
        int[] temp = schedule.getCurrentlyRunning().getPcbIDs();

        return vfs.Write(temp[id], data);
    }

    /**
     * This method uses the copy constructor on the kernel message and sets the sender PID.
     * It adds to the message queue in PCB if the target PCB PID is in the sendMessage list, if not, it sets
     * the target PCB to a process from the waitMessage list if its PID is in waitMessage before restoring it
     * to its proper runnable.
     * @param km
     */
    public void SendMessage(KernelMessage km) {
        // Make a copy of the original message using the copy constructor.
        KernelMessage copyMessage = new KernelMessage(km);
        // Set the sender pid using OS.GetPid().
        copyMessage.setSenderPID(OS.GetPid());

        // Check if the target PCB is in the queues.
        if (getSchedule().getSendMessage().containsKey(km.getTargetPID())) {
            PCB targetPCB = getSchedule().getSendMessage().remove(km.getTargetPID());
            targetPCB.getMessageQueue().add(copyMessage);
        }

        // Check if the target PCB is waiting for a message.
        if (getSchedule().getWaitMessage().containsKey(km.getTargetPID())) {
            PCB targetPCB = getSchedule().getWaitMessage().remove(km.getTargetPID());
            // Put the target PCB in the proper runnable (queue).
            getSchedule().RestoreRunnable(targetPCB);
        }
    }

    /**
     * This method checks if a process is waiting and returns a KernelMessage. If not, the process is
     * put into a wait list.
     * @return result
     */
    public KernelMessage WaitForMessage() {
        // Variable declaration.
        KernelMessage result = null;

        if (!getSchedule().currentlyRunning.getMessageQueue().isEmpty()) {
            result = getSchedule().currentlyRunning.getMessageQueue().removeFirst();
        } else {
            // Deschedule and add the process to the waitMessage HashMap
            getSchedule().getWaitMessage().put(getSchedule().GetPid(), getSchedule().currentlyRunning);

            // Hold the old process, so we can get its message while a new process is running.
            PCB oldProcess = getSchedule().currentlyRunning;

            // Switch to a new process.
            schedule.SwitchProcess();

            if (!oldProcess.getMessageQueue().isEmpty()) {
                result = oldProcess.getMessageQueue().removeFirst();
            }
        }
        return result;
    }

    /**
     * This method calls GetPid() from Scheduler.
     * @return getSchedule().GetPid()
     */
    public int GetPid() {
        return schedule.GetPid();
    }

    /**
     * This method calls GetPidByName() from Scheduler.
     * @param name
     * @return getSchedule().GetPidByName(name)
     */
    public int GetPidByName(String name) {
        return schedule.GetPidByName(name);
    }

    /**
     * This method randomly updates one of the TLB entries when it finds a mapping. If there is no mapping,
     * it prints "segmentation fault" and calls SwitchProcess() to run something else.
     * @param virtualPageNumber
     */
    public void GetMapping(int virtualPageNumber) {
        // Variable declaration.
        int physicalPage = schedule.currentlyRunning.getMemoryMap()[virtualPageNumber];
        Random random = new Random();
        int randomNumber = random.nextInt(2);

        if (physicalPage != -1) {
            Hardware.TLB[0][randomNumber] = virtualPageNumber;
            Hardware.TLB[1][virtualPageNumber] = physicalPage;
        } else {
            System.out.println("Segmentation fault.");
            Hardware.ClearTLB();
            schedule.SwitchProcess();
        }
    }

    /**
     * This method returns the start virtual address.
     * @param size
     * @return virtualPage * 1024
     */
    public int AllocateMemory(int size) {
        // Variable declarations.
        ArrayList<Integer> freeLocation = new ArrayList<>();
        int pagesNeeded = size / 1024;
        int virtualPage = 0;

        // Find the free page spaces and add their index into the freeLocation, represents the physical pages.
        for (int i = 0; i < freeList.length; i++) {
            if (freeList[i] == false) {
                freeLocation.add(i);
            }

            if (freeList.length == pagesNeeded) {
                break;
            }
        }

        // Find the virtual page.
        for (int j = 0; j < schedule.currentlyRunning.getMemoryMap().length; j++) {
            if (schedule.currentlyRunning.getMemoryMap()[j] == -1) {
                virtualPage = j;
                break;
            }
        }

        // Map to PCB memoryMap.
        for (int k = 0; k < pagesNeeded; k++) {
            int physicalPage = freeLocation.remove(k);
            schedule.currentlyRunning.getMemoryMap()[virtualPage + k] = physicalPage;
            freeList[physicalPage] = true;
        }
        return virtualPage * 1024;
    }

    /**
     * This method takes the virtual address and the amount to free.
     * @param pointer
     * @param size
     * @return freeSuccess
     */
    public boolean FreeMemory(int pointer, int size) {
        // Variable declaration.
        boolean freeSuccess = false;
        int virtualPage = pointer / 1024;
        int numberPages = size / 1024;

        for (int i = virtualPage; i < virtualPage + numberPages; i++) {
            int physcialPage = schedule.currentlyRunning.getMemoryMap()[i];

            if (physcialPage != -1) {
                freeList[physcialPage] = false;
                schedule.currentlyRunning.getMemoryMap()[i] = -1;
                freeSuccess = true;
            }
        }
        return freeSuccess;
    }
}