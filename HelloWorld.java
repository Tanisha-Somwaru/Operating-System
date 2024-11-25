public class HelloWorld extends UserlandProcess {
   /**
     * This method prints hello world and calls cooperate().
     * @throws InterruptedException
     */
    @Override
    public void main()  {
        while (true) {
            System.out.println("Hello World!");
            cooperate();
            try {
                Thread.sleep(50); // sleep for 50 ms
            } catch (Exception e) { }
        }
    }
}
