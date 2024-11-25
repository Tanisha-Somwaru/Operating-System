import java.io.IOException;
import java.util.Arrays;

public class VFS implements Device {
    // Variable declarations.
    private Device[] devices;
    private int[] deviceIDs = new int[10];

    /**
     * Constructor.
     */
    public VFS() {
        devices = new Device[10];
        Arrays.fill(deviceIDs, -1);
    }

    /**
     * This method looks at the first word to determine the device, then remove that from the string
     * and pass the remainder to the open() call on the device.
     * @param s
     * @return i
     * @throws Exception
     */
    @Override
    public int Open(String s) throws Exception {
        // Variable declarations.
        String[] splitWord = s.split(" ");
        String deviceType = splitWord[0];
        String seed = "";

        if (splitWord.length > 1) {
            seed = splitWord[1];
        }

        for (int i = 0; i < 10; i++) {
            Device device = DeviceType(deviceType);

            if (devices[i] == null) {
                devices[i] = device;
                deviceIDs[i] = device.Open(seed);
                return i;
            }
        }
        return -1;
    }

    /**
     * This method removes the device and id entries.
     * @param id
     * @throws IOException
     */
    @Override
    public void Close(int id) throws IOException {
        if (id < 0) {
            throw new IOException("ID is -1");
        }
        devices[id].Close(deviceIDs[id]);
        deviceIDs[id] = -1;
    }

    /**
     * This method calls Read() from Device.
     * @param id
     * @param size
     * @return devices[id].Read(deviceIDs[id], size)
     * @throws IOException
     */
    @Override
    public byte[] Read(int id, int size) throws IOException {
        if (id < 0) {
            throw new IOException("ID is -1");
        }
        return devices[id].Read(deviceIDs[id], size);
    }

    /**
     * This method calls Seek() from Device.
     * @param id
     * @param to
     * @throws IOException
     */
    @Override
    public void Seek(int id, int to) throws IOException {
        if (id < 0) {
            throw new IOException("ID is -1");
        }
        devices[id].Seek(deviceIDs[id], to);
    }

    /**
     * This method calls Write() from Device.
     * @param id
     * @param data
     * @return
     * @throws IOException
     */
    @Override
    public int Write(int id, byte[] data) throws IOException {
        if (id < 0) {
            throw new IOException("ID is -1");
        }
        return devices[id].Write(deviceIDs[id], data);
    }

    /**
     * Helper method to determine what type of device the first word is.
     * @param device
     * @return
     */
    public Device DeviceType(String device) {
        if (device.equals("random")) {
            return new RandomDevice();
        } else {
            return new FakeFileSystem();
        }
    }
}
