package reverse_dbekinalkar.lib.pathfinding;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import reverse_dbekinalkar.lib.BitShift;
import reverse_dbekinalkar.robot.Robot;

public class BFS {
    public enum Goal {
        BUILDABLE, PAINTABLE, NAVIGATE
    }

    public static BFSQ q = null;

    public static int[] BfsWithoutGoal(Robot r) {
        if(q == null) q = new BFSQ();
        q.setup();

        MapLocation myLoc = r.rc.getLocation();

        long[] visited = new long[60];
        visit(visited, myLoc.x, myLoc.y);

        int x, y;

        for(Direction d: DirectionsTable.ALL_DIRECTIONS) {
            x = myLoc.x + d.dx;
            y = myLoc.y + d.dy;

            if(x < 0 || x >= r.rc.getMapWidth() || y < 0 || y >= r.rc.getMapHeight()) {
                continue;
            }

            q.add(x, y, x, y);
        }

        int[][] loc = new int[2][2];

        for(int i = 0; i < 60*60; i++) {
            q.peek(loc);

            long mask = BitShift.bitMasks[loc[0][1]];
            long ret = (mask & r.context.paintable[loc[0][0]]) | (mask & r.context.buildable[loc[0][0]]);

            if(ret != 0) {

            }

            for(Direction d: DirectionsTable.ALL_DIRECTIONS) {
                x = loc[0][0] + d.dx;
                y = loc[0][1] + d.dy;

                if(x < 0 || x >= r.rc.getMapWidth() || y < 0 || y >= r.rc.getMapHeight()) {
                    continue;
                }

                q.add(x, y, loc[1][0], loc[1][1]);
            }
        }

        return new int[]{0, 0};
    }

    private static void visit(long[] visited, int x, int y) {
        visited[x] &= BitShift.bitMasks[y];
    }


}
