package reverse_dbekinalkar.lib.pathfinding;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import reverse_dbekinalkar.lib.BitShift;
import reverse_dbekinalkar.lib.map.BitGrid;
import reverse_dbekinalkar.robot.Robot;

public class BFS {
    public static BFSQ q = null;
    public static BitGrid visited;

    public static MapLocation start;
    public static Robot r;

    public static Goal g = null;
    public static int[] softLoc = new int[2];

    public static int lastRound;

    public static int[] BfsWithoutGoal(Robot r) {
        setup(r);

        int x, y;
        long ret, mask;

        for (Direction d : DirectionsTable.ALL_DIRECTIONS) {
            x = start.x + d.dx;
            y = start.y + d.dy;

            if (x < 0 || x >= r.rc.getMapWidth() || y < 0 || y >= r.rc.getMapHeight() || r.context.impassable.get(x, y) || visited.get(x, y)) {
                continue;
            }

            visited.set(x, y);

            q.add(x, y, x, y);
        }

        int[][] loc = new int[2][2];

        for (int i = 0; i < 60 * 60; i++) {
            if(q.round >= lastRound) {
                return softLoc;
            }

            q.peek(loc);

            mask = BitShift.bitMasks[loc[0][1]];

            ret = (mask & r.context.buildable[loc[0][0]]);

            if (ret != 0) {
                return loc[1];
            }

            ret = (mask & r.context.paintable[loc[0][0]]);

            if (ret != 0) {
                softSelect(loc[1][0], loc[1][1]);
            }

            for (Direction d : DirectionsTable.ALL_DIRECTIONS) {
                x = loc[0][0] + d.dx;
                y = loc[0][1] + d.dy;

                if (x < 0 || x >= r.rc.getMapWidth() || y < 0 || y >= r.rc.getMapHeight() || r.context.impassable.get(x, y) || visited.get(x, y)) {
                    continue;
                }

                q.add(x, y, loc[1][0], loc[1][1]);
            }

            q.pop(loc);
        }

        return new int[]{0, 0};
    }

    private static void setup(Robot r) {
        BFS.r = r;

        if (q == null) q = new BFSQ();
        q.setup();

        start = r.rc.getLocation();

        visited = new BitGrid();
        visited.set(start.x, start.y);

        lastRound = Integer.MAX_VALUE;
    }

    private static void softSelect(int x, int y) {
        softLoc[0] = x;
        softLoc[1] = y;
        g = Goal.PAINTABLE;

        lastRound = q.round + 2;
    }

    public enum Goal {
        SCOUT, PAINTABLE, BUILDABLE
    }
}
