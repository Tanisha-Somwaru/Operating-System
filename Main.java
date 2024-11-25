public class Main {
    /**
     * This method calls StartUp() on Init and other processes.
     * @param args
     */
    public static void main(String[] args) {
        // For TA, each method tests each case, comment out which one you want to test to call the method (assignment 5).
        TestHardwareReadAndWrite();
        //TestExtendedMemory();
        //TestInvalidAndTermination();
    }

    /**
     * Test 1.
     */
    public static void TestRandomRead() {
        OS.Startup(new TestRandomRead());
    }

    /**
     * Test 2.
     */
    public static void TestRandomWrite() {
        OS.Startup(new TestRandomWrite());
    }

    /**
     * Test 3.
     */
    public static void TestRandomSeek() {
        OS.Startup(new TestRandomSeek());
    }

    /**
     * Test 4.
     */
    public static void TestFileRead() {
        OS.Startup(new TestFileRead());
    }

    /**
     * Test 5.
     */
    public static void TestFileWrite() {
        OS.Startup(new TestFileWrite());
    }

    /**
     * Test 6.
     */
    public static void TestFileSeek() {
        OS.Startup(new TestFileSeek());
    }

    /**
     * Test 7.
     */
    public static void TestTwoRandoms() {
        OS.Startup(new TwoRandoms());
    }

    /**
     * Test 8.
     */
    public static void TestTwoFiles() {
        OS.Startup(new TwoFiles());
    }

    /**
     * Test for assignment 4.
     */
    public static void TestAssignment4() {
        OS.StartUp(new IdleProcess());
    }

    /**
     * Test for assignment 5.
     */
    public static void TestHardwareReadAndWrite() {
        OS.Startup(new TestHardwareReadAndWrite());
    }

    /**
     * Test for assignment 5.
     */
    public static void TestExtendedMemory() {
        OS.Startup(new TestExtendedMemory());
    }

    public static void TestInvalidAndTermination() {
        OS.Startup(new TestInvalidAndTermination());
    }
}
