import java.util.Arrays;

public class Ping extends UserlandProcess {
    @Override
    public void main() throws Exception {
        //int pingPID = OS.GetPid();  // Get Ping's PID.
        int pongPID = OS.GetPidByName("Pong");  // Find Pong's PID.
        int counter = 0;

        System.out.println("I am PING, pong = " + pongPID);

        // Wait for initial message from pong.
        KernelMessage initialReply = OS.WaitForMessage();
        System.out.println(initialReply.toString());

        while (true) {
            String data = "Ping" + counter;
            byte[] byteData = data.getBytes();

            // Send message to Pong.
            KernelMessage message = new KernelMessage(OS.GetPid(), pongPID, counter, byteData);
            OS.SendMessage(message);
            String ping = "PING: " + message.ToString();
            System.out.println(ping);
            //System.out.println("PING: from: " + OS.GetPid() + " to: " + pongPID + " what: " + counter);

            // Wait for reply from Pong.
            KernelMessage reply = OS.WaitForMessage();
            System.out.println("Wait was a success " + reply.toString());
            counter++;
        }
    }
}
