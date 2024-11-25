public class Init extends UserlandProcess {
    /**
     * This method calls CreateProcess() on HelloWorld and GoodbyeWorld.
     */
    @Override
    public void main() {
//        System.out.println("process 1 done");
        OS.CreateProcess(new HelloWorld(), OS.Priority.REALTIME);
//        System.out.println("process 2 done");
        OS.CreateProcess(new GoodbyeWorld(), OS.Priority.REALTIME);

        OS.Sleep(20);
        OS.Exit();

//        OS.CreateProcess(new HelloWorld(), OS.Priority.BACKGROUND);
//        OS.CreateProcess(new GoodbyeWorld(), OS.Priority.BACKGROUND);
        //System.out.println("sleep is called");

    }
}
