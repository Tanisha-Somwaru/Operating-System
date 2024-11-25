public class GoodbyeWorld extends UserlandProcess {
    @Override
    public void main() {
        while (true) {
            System.out.println("Goodbye World!");
            cooperate();
            try {
                Thread.sleep(50); // sleep for 50 ms
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
