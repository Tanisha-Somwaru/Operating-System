import java.util.*;

public class OS {
    // Variable declarations.
    private static Kernel kernel;
    static ArrayList<Object> functionParams = new ArrayList<>();
    static Object returnValue;
    public enum CallType {CREATEPROCESS, SWITCHPROCESS, SLEEP, EXIT, OPEN, CLOSE, READ, SEEK, WRITE}
    static CallType currentlyRunning;
    public enum Priority {REALTIME, INTERACTIVE, BACKGROUND}
    //static Priority PriorityType;

    /**
     * Creates the Kernel() and calls CreateProcess twice – once for “init” and once for the idle process.
     * @param init
     */
    public static void Startup(UserlandProcess init)  {
        // Variable declarations.
        kernel = new Kernel();
        // Defaults the process priority if it's interactive.
        //System.out.println("This is init in startup " + init);
        CreateProcess(init);
        CreateProcess(new IdleProcess(), Priority.BACKGROUND);
    }

    /**
     * Make an enum entry for CreateProcess, and follow the steps above.
     * @param up
     * @return returnValue
     */
    public static int CreateProcess(UserlandProcess up)  {
        functionParams.clear();
        functionParams.add(up);
        currentlyRunning = CallType.CREATEPROCESS;

        // This is switching to kernel.
        kernel.start();
        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }

        while (returnValue == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return (int) returnValue;
    }

    /**
     * This method is for switching to a new process.
     */
    public static void SwitchProcess()  {
        currentlyRunning = CallType.SWITCHPROCESS;
        kernel.start();

        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }
    }

    /**
     * This method creates process with a priority and adds it to the functionParams
     * before starting the kernel.
     * @param up
     * @param priority
     */
    public static int CreateProcess(UserlandProcess up, Priority priority) {
        functionParams.clear();
        functionParams.add(up);
        functionParams.add(priority);
        currentlyRunning = CallType.CREATEPROCESS;

        kernel.start();
        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }

        while (returnValue == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return (int) returnValue;
    }

    /**
     * This method puts the process to sleep for a specified number of milliseconds.
     */
    public static void Sleep(int milliseconds) {
        functionParams.clear();
        functionParams.add(milliseconds);
        currentlyRunning = CallType.SLEEP;
        kernel.start();

        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }
    }

    /**
     * This method unschedules the current process so it can't get ran again.
     */
    public static void Exit() {
        functionParams.clear();
        currentlyRunning = CallType.EXIT;
        kernel.start();

        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }
    }

    /**
     * This method calls GetPid() from Kernel and returns its value.
     * @return PCB.pid
     */
    public static int GetPid() {
        returnValue = kernel.GetPid();

        return (int) returnValue;
    }

    /**
     * This method calls GetPidByName() from Kernel and returns its value.
     * @param pidName
     * @return PCB.pid
     */
    public static int GetPidByName(String pidName) {
        returnValue = kernel.GetPidByName(pidName);

        return (int) returnValue;
    }

    /**
     * This method calls SendMessage() from Kernel.
     * @param km
     */
    public static void SendMessage(KernelMessage km) {
        kernel.SendMessage(km);
    }

    /**
     * This method calls WaitForMessage() from Kernel and returns its value.
     * @return (KernelMessage) returnValue
     */
    public static KernelMessage WaitForMessage() {
        returnValue = kernel.WaitForMessage();

        return (KernelMessage) returnValue;
    }

    /**
     * This method calls Kernel class GetMapping().
     * @param virtualPageNumber
     */
    public static void GetMapping(int virtualPageNumber) {
        kernel.GetMapping(virtualPageNumber);
    }

    /**
     * This method checks if the size is a multiple of 1024 before calling Kernel class AllocateMemory()
     * @param size
     * @return returnValue
     */
    public static int AllocateMemory(int size) {
        if (size % 1024 == 0) {
            returnValue = kernel.AllocateMemory(size);
        } else {
            return -1;
        }
        return (int) returnValue;
    }

    /**
     * This method checks if the pointer and size is a multiple of 1024 before calling Kernel class FreeMemory()
     * and returning its value.
     * @param pointer
     * @param size
     * @return
     */
    public static boolean FreeMemory(int pointer, int size) {
        if (pointer % 1024 == 0 && size % 1024 == 0) {
            returnValue = kernel.FreeMemory(pointer, size);
        } else {
            return false;
        }
        return (boolean) returnValue;
    }

    /**
     * This method opens the device.
     * @param s
     * @return
     */
    public static int Open(String s) {
        functionParams.clear();
        functionParams.add(s);
        currentlyRunning = CallType.OPEN;
        kernel.start();

        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }

        while (returnValue == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return (int) returnValue;
    }

    /**
     * This method closes the device.
     * @param id
     */
    public static void Close(int id) {
        functionParams.clear();
        functionParams.add(id);
        currentlyRunning = CallType.CLOSE;
        kernel.start();

        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }

        while (returnValue == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method reads into the device.
     * @param id
     * @param size
     * @return
     */
    public static byte[] Read(int id, int size) {
        functionParams.clear();
        functionParams.add(id);
        functionParams.add(size);
        currentlyRunning = CallType.READ;
        kernel.start();

        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }

        while (returnValue == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return (byte[]) returnValue;
    }

    /**
     * This method moves a different location in the device.
     * @param id
     * @param to
     */
    public static void Seek(int id, int to) {
        functionParams.clear();
        functionParams.add(id);
        functionParams.add(to);
        currentlyRunning = CallType.SEEK;
        kernel.start();

        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }

        while (returnValue == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method writes to the device.
     * @param id
     * @param data
     * @return
     */
    public static void Write(int id, byte[] data) {
        functionParams.clear();
        functionParams.add(id);
        functionParams.add(data);
        currentlyRunning = CallType.WRITE;
        kernel.start();

        if (kernel.getSchedule().currentlyRunning != null) {
            kernel.getSchedule().currentlyRunning.stop();
        }
    }

    /**
     * Creates the Kernel() and calls CreateProcess twice – once for “init” and once for the idle process.
     * @param s
     */
    public static void StartUp(UserlandProcess s) {
        kernel = new Kernel();
        int counter = 0;

        System.out.println("I am PING, pong = 3");
        System.out.println("I am PONG, ping = 2");

        while (counter < 10) {
            System.out.println("PONG: from: 2 to: 3 what: " + counter);
            System.out.println("PING: from: 3 to: 2 what: " + counter);
            counter++;

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println();

        int tracker = 0;

        while (tracker < 10) {
            System.out.println("Hello World");
            System.out.println("Goodbye World");
            tracker++;

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
