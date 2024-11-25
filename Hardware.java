import java.util.Arrays;

public class Hardware {
    // Variable declarations.
    static int[][] TLB = new int[2][2];
    static byte[] memory = new byte[1024 * 1024];
    static boolean mappingExists = false;

    /**
     * This method gets the virtual page, gets the physical page, checks TLB and returns data from memory.
     * @param address
     * @return memory[physicalAddress]
     */
    public static byte Read(int address) {
        // Variable declarations.
        int virtualAddress = address / 1024;
        int offset = virtualAddress % 1024;
        int physicalAddress = FindPhysicalAddress(virtualAddress, offset);

        if (physicalAddress == -1) {
            OS.GetMapping(virtualAddress);
            physicalAddress = FindPhysicalAddress(virtualAddress, offset);
        }
        return memory[physicalAddress];
    }

    /**
     * This method gets virtual page, gets physical page, checks TLB and writes data into memory.
     * @param address
     * @param value
     */
    public static void Write(int address, byte value) {
        // Variable declarations.
        int virtualAddress = address / 1024;
        int offset = virtualAddress % 1024;
        int physicalAddress = FindPhysicalAddress(virtualAddress, offset);

        if (physicalAddress == -1) {
            OS.GetMapping(virtualAddress);
            physicalAddress = FindPhysicalAddress(virtualAddress, offset);
        }
        memory[physicalAddress] = value;
    }

    /**
     * This is a helper method that finds the mapping while saving the physical page number before
     * calculating and returning the physical address.
     * @param address
     * @param offset
     * @return pageLocation * 1024 + offset
     */
    public static int FindPhysicalAddress(int address, int offset) {
        // Variable declarations.
        int pageLocation = 0;

        // Find the virtual page -> physical mapping and save the physical page number.
        for (int i = 0; i < TLB.length; i++) {
            if (TLB[0][i] == address) {
                pageLocation = TLB[1][i];
                mappingExists = true;
            }
        }
        return pageLocation * 1024 + offset;
    }

    /**
     * Helper method to clear TLB when task switching happens.
     */
    public static void ClearTLB() {
        for (int i = 0; i < TLB.length; i++) {
            Arrays.fill(TLB[i], -1);
        }
    }
}
