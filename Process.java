import java.lang.Thread;
import java.util.concurrent.Semaphore;

abstract class Process implements Runnable {
    // Variable declarations.
    Thread thread = new Thread(this);
    Semaphore semaphore = new Semaphore(0);
    Boolean quantum = false;

    /**
     * Constructor.
     */
    public Process() {
        thread.start();
    }

    /**
     * Sets the boolean indicating that this process’ quantum has expired
     */
    public void requestStop() {
        quantum = true;
    }

    /**
     * Will represent the main of our “program”.
     */
    public abstract void main() throws Exception;

    /**
     * Indicates if the semaphore is 0
     * @return true
     */
    public boolean isStopped() {
        return semaphore.availablePermits() == 0;
    }

    /**
     * True when the Java thread is not alive
     * @return
     */
    public boolean isDone() {
        return !thread.isAlive();
    }

    /**
     * Releases (increments) the semaphore, allowing this thread to run.
     */
    public void start() {
        semaphore.release();
    }

    /**
     * Acquires (decrements) the semaphore, stopping this thread from running
     */
    public void stop()  {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Acquire the semaphore, then call main.
     */
    @Override
    public void run() {
        try {
            semaphore.acquire();
            main();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If the boolean is true, set the boolean to false and call OS.switchProcess()
     */
    public void cooperate()  {
        if (quantum) {
            quantum = false;
            OS.SwitchProcess();
        }
    }
}

