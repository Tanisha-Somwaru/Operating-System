public class IdleProcess extends UserlandProcess {
    /**
     * This method calls cooperate infinitely.
     * @throws InterruptedException
     */
    @Override
    public void main()  {
        while (true) {
            cooperate();
            System.out.println("Idle is working...");
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

