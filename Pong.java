import java.util.Arrays;

public class Pong extends UserlandProcess{
    @Override
    public void main() throws Exception {
        //int pongPID = OS.GetPid();  // Get Pong's PID.
        int pingPID = OS.GetPidByName("Ping");  // Find Ping's PID.
        int counter = 0;

        System.out.println("I am PONG, ping = " + pingPID);

        // Wait for initial message from ping.
        KernelMessage initialReply = OS.WaitForMessage();
        System.out.println(initialReply.toString());

        while (true) {
            // Wait for a message from Ping.
            KernelMessage message = OS.WaitForMessage();
            System.out.println("PONG: " + message.ToString());

            // Create and send a reply to Ping.
            String data = "Pong" + counter;
            byte[] replyData = data.getBytes();

            KernelMessage reply = new KernelMessage(OS.GetPid(), pingPID, counter, replyData);
            OS.SendMessage(reply);
            counter++;
        }
    }
}
