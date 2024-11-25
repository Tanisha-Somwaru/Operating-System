import java.lang.Thread;
import java.util.Arrays;
import java.util.LinkedList;

public class PCB {
    // Variable declarations.
    static int nextPid;
    static int pid;
    private int[] PcbIDs = new int[10];
    static String processName;
    private LinkedList<KernelMessage> messageQueue;
    static int timeOut = 0;
    UserlandProcess userlandProcess;
    OS.Priority priority;
    private int[] memoryMap = new int[100];

    /**
     * Constructor.
     * @param up
     */
    public PCB(UserlandProcess up, OS.Priority prior) {
        userlandProcess = up;
        priority = prior;
        processName = userlandProcess.getClass().getSimpleName();
        messageQueue = new LinkedList<>();
        timeOut = 0;
        Arrays.fill(PcbIDs, -1);
        Arrays.fill(memoryMap, -1);
    }

    /**
     * Calls userlandprocess’ stop(). Loops with Thread.sleep() until
     * ulp.isStopped() is true.
     */
    public void stop() {
        userlandProcess.stop();
        while (!userlandProcess.isStopped()) {
            try {
                //System.out.println("I'm stuck");
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Calls userlandprocess’ isDone().
     * @return userlandProcess.isDone()
     */
    public boolean isDone() {
        return userlandProcess.isDone();
    }

    /**
     * Calls userlandprocess’ start().
     */
    public void start() {
        userlandProcess.start();
    }

    /**
     * Helper method that returns the IDs array.
     * @return IDs
     */
    public int[] getPcbIDs() {
        return PcbIDs;
    }

    /**
     * This method returns the processName variable.
     * @return processName
     */
    public String getProcessName() {
        return processName;
    }

    /**
     * This method returns the messageQueue variable.
     * @return messageQueue
     */
    public LinkedList<KernelMessage> getMessageQueue() {
        return messageQueue;
    }

    /**
     * This method returns the pid variable.
     * @return pid
     */
    public static int getPid() {
        return pid;
    }

    /**
     * This method returns the memoryMap variable.
     * @return memoryMap
     */
    public int[] getMemoryMap() {
        return memoryMap;
    }
}
