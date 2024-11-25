import java.util.Random;

public class RandomDevice implements Device {
    // Variable declaration.
    private Random[] randomDevices;

    /**
     * Constructor.
     */
    public RandomDevice() {
        randomDevices = new Random[10];
    }

    /**
     * This method creates a new Random device and put it in an empty spot in the array.
     * @param s
     * @return
     */
    @Override
    public int Open(String s) {
        for (int i = 0; i < randomDevices.length; i++) {
            // Check if s is null or empty.
            if (!s.equals(null) || !s.isEmpty()) {
                // Checks for an empty spot in the array to put s.
                if (randomDevices[i] == null) {
                    randomDevices[i] = new Random(Integer.parseInt(s));
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * This method will null the device entry.
     * @param id
     */
    @Override
    public void Close(int id) {
        randomDevices[id] = null;
    }

    /**
     * This method will create/fill an array with random values.
     * @param id
     * @param size
     * @return
     */
    @Override
    public byte[] Read(int id, int size) {
        if (id < 0 || id >= randomDevices.length || randomDevices[id] == null) {
            throw new IllegalArgumentException("Invalid device ID: " + id);
        }

        byte[] newArray = new byte[size];
        Random deviceObject = randomDevices[id];

        // Loop and add the random object to populate the array.
        for (int i = 0; i < newArray.length; i++) {
            deviceObject.nextBytes(newArray);
        }
        return newArray;
    }

    /**
     * This method will read random bytes but not return them.
     * @param id
     * @param to
     */
    @Override
    public void Seek(int id, int to) {
        if (id < 0 || id >= randomDevices.length || randomDevices[id] == null) {
            throw new IllegalArgumentException("Invalid device ID: " + id);
        }

        // Variable declarations.
        byte[] newArray = new byte[to];
        Random deviceObject = randomDevices[id];

        // Loop and add the random object to populate the array.
        for (int i = 0; i < newArray.length; i++) {
            deviceObject.nextBytes(newArray);
        }
    }

    /**
     * This method will return 0 length and do nothing.
     * @param id
     * @param data
     * @return
     */
    @Override
    public int Write(int id, byte[] data) {
        return 0;
    }
}
