package dbekinalkar.lib.pathfinding;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.HashMap;
import java.util.Map;

public class Pathfinder {

    public static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST
    };

    public static final Map<Direction, Integer> directionMap = Map.of(
            Direction.NORTH, 0,
            Direction.NORTHEAST, 1,
            Direction.EAST, 2,
            Direction.SOUTHEAST, 3,
            Direction.SOUTH, 4,
            Direction.SOUTHWEST, 5,
            Direction.WEST, 6,
            Direction.NORTHWEST, 7
    );

    public static boolean navigate(RobotController rc, MapLocation target) throws GameActionException {
        MapLocation loc = rc.getLocation();
        Direction d = loc.directionTo(target);

        d = getDirection(rc, d);

        if(d == Direction.CENTER) return false;

        rc.move(d);
        return true;
    }

    static Direction getDirection(RobotController rc, Direction d) {
        int i = 0;
        while(true) {
            // TODO: Remove Infinite Loop
            if(rc.canMove(d)) return d;
            i++;
            d = nextDirection(d, i);
        }
    }

    static Direction nextDirection(Direction d, int i) {
        if(i > directions.length) return Direction.CENTER;

        int diff = (i  + 1) / 2 * (i % 2 == 0? 1: -1);
        int start = directionMap.getOrDefault(d, 0);

        return directions[(start + diff + directions.length) % directions.length];
    }
}
