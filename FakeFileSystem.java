import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device {
    // Variable declaration.
    private RandomAccessFile[] files;

    /**
     * Constructor.
     */
    public FakeFileSystem() {
        files = new RandomAccessFile[10];
    }

    /**
     * This method takes a filename and creates a RandomAccessFile object and sets in an index in the array.
     * @param fileName
     * @return i
     * @throws Exception
     */
    @Override
    public int Open(String fileName) throws Exception {
        if (fileName.equals(null) || fileName.isEmpty()) {
            throw new Exception("File is empty or null!");
        } else {
            for (int i = 0; i < files.length; i++) {
                // Creates RandomAccessFile with read and write access.
                if (files[i] == null) {
                    files[i] = new RandomAccessFile(fileName, "rw");
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * This method nulls the device entry.
     * @param id
     */
    @Override
    public void Close(int id) throws IOException {
        // Check if id isn't -1 and files[id] isn't null.
        if (files[id] != null && id != -1) {
            files[id].close();
            files[id] = null;
        } else {
            throw new IOException("File doesn't exist!");
        }
    }

    /**
     * This method calls read() from RandomDevice class.
     * @param id
     * @param size
     * @return readArray
     * @throws IOException
     */
    @Override
    public byte[] Read(int id, int size) throws IOException {
        // Check if id isn't -1 and files[id] isn't null.
        byte[] readArray = new byte[10];

        if (files[id] != null && id != -1) {
            files[id].read(readArray);
        } else {
            throw new IOException("File doesn't exist!");
        }
        return readArray;
    }

    /**
     * This method calls seek() from RandomDevice class.
     * @param id
     * @param to
     */
    @Override
    public void Seek(int id, int to) throws IOException {
        // Check if id isn't -1 and files[id] isn't null.
        if (files[id] != null && to != -1 && id != -1) {
            files[id].seek(to);
        } else {
            throw new IOException("File doesn't exist and offset is below 0!");
        }
    }

    /**
     * This method calls write() from RandomDevice class.
     * @param id
     * @param data
     * @return
     * @throws IOException
     */
    @Override
    public int Write(int id, byte[] data) throws IOException {
        if (files[id] != null && id != -1) {
            files[id].write(data);
        } else {
            throw new IOException("File doesn't exist!");
        }
        return data.length;
    }
}
