package dbekinalkar.lib.pathfinding;

import battlecode.common.Direction;

public class DirectionsTable {
    public static int[] distances = new int[]{
            0, 1, 2, 3, 4, 3, 2, 1, 5
    };

    public static int distanceTo(Direction d1, Direction d2) {
        if(d1 == Direction.CENTER || d2 == Direction.CENTER) return -1;
        return distances[(d1.compareTo(d2) + 8) % 8];
    }
}
