package reverse_dbekinalkar.lib.map;

import reverse_dbekinalkar.lib.BitShift;

public class BitGrid {
    public long[] grid;

    public BitGrid() {
        grid = new long[60];
    }

    public void set(int x, int y) {
        grid[x] |= BitShift.bitMasks[y];
    }

    public void unset(int x, int y) {
        grid[x] &= ~BitShift.bitMasks[y];
    }
}
