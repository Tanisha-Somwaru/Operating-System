import java.awt.image.Kernel;
import java.util.Arrays;

public class KernelMessage {
    // Variable declarations.
    private int senderPID = 0;
    private int targetPID = 0;
    private int message = 0;
    private byte[] data = new byte[10];
    private int countMessage = 0;

    /**
     * Constructor.
     * @param sentData
     */
    public KernelMessage(int sender, int target, int message, byte[] sentData) {
        senderPID = sender;
        targetPID = target;
        this.message = message;
        data = sentData.clone();
    }

    /**
     * Copy Constructor.
     * @param km
     */
    public KernelMessage(KernelMessage km) {
        senderPID = km.senderPID;
        targetPID = km.targetPID;
        message = km.message;
        data = km.data.clone();
    }

    /**
     * This method prints the output for debugging purposes.
     * @return
     */
    public String ToString() {
        return "sent from : " + senderPID + " to: " + targetPID + " for " + message;
    }

    /**
     * This method returns the pid variable.
     * @return
     */
    public int getTargetPID() {
        return targetPID;
    }

    /**
     * This method sets the senderPID.
     * @param senderPID
     */
    public void setSenderPID(int senderPID) {
        this.senderPID = senderPID;
    }

    /**
     * This method returns the sendPID.
     * @return
     */
    public int getSenderPID() {
        return senderPID;
    }

    /**
     * This method returns the countMessage.
     * @return
     */
    public int getCountMessage() {
        return countMessage;
    }
}
