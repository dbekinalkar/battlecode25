package dbekinalkar.lib.pathfinding;

import battlecode.common.*;
import dbekinalkar.robot.Robot;

public class BugNav {

    public static boolean navigate(Robot r, MapLocation target) throws GameActionException {
        Direction d = getDirection(r, target);

        try {
            r.rc.move(d);
        } catch (GameActionException e) {
            return false;
        }
        return true;
    }

    public static Direction getDirection(Robot r, MapLocation target) throws GameActionException {
            MapLocation myLocation = r.rc.getLocation();
            Direction[] directions = Direction.DIRECTION_ORDER;
            int[] weights = new int[9];
            weights[0] = 100000;

            Direction toTarget = myLocation.directionTo(target);

            // Penalize last location
            Direction lastDir = r.context.lastLoc.directionTo(myLocation);

            for (int i = 1; i < directions.length; i++) {
                Direction dir = directions[i];
                MapLocation loc = myLocation.add(dir);
                MapInfo mi;
                try {
                    mi = r.rc.senseMapInfo(loc);
                }
                catch(GameActionException e) {
                    weights[i] += 2000;
                    continue;
                }

                // Default weight
                int weight = 100;

                // Encourage the correct direction
                weight += 50 * DirectionsTable.distanceTo(toTarget, dir);

                // Paint type on the ground
                switch(mi.getPaint()) {
                    case ALLY_PRIMARY:
                    case ALLY_SECONDARY:
                        weight -= 20; // Favor ally paint
                        break;
                    case EMPTY:
                        weight += 10; // Neutral paint
                        break;
                    case ENEMY_PRIMARY:
                    case ENEMY_SECONDARY:
                        weight += 20;
                        break;
                }

                // Impassable objects
                if (!mi.isPassable()) {
                    weight += 1000; // Treat as impassable
                }

                // Enemy units
                RobotInfo[] enemies = r.rc.senseNearbyRobots(loc, 20, r.rc.getTeam().opponent());
                weight += enemies.length * 100; // Penalize based on number of nearby enemies

                // Ally units
//                RobotInfo[] allies = r.rc.senseNearbyRobots(loc, 20, r.rc.getTeam());
//                for (RobotInfo ally : allies) {
//                    // 80% chance to treat ally square as impassable
//                    if (Math.random() < 0.8) {
//                        weight += 500; // High penalty for treating square as impassable
//                    } else {
//                        weight -= 20; // Small bonus for treating square as passable
//                    }
//                }

                // Penalize last location
                if(dir == lastDir) {
                    weight += 100000;
                }

                // Store weight
                weights[i] = weight;
            }

            // Find direction with lowest weight
            int minWeight = Integer.MAX_VALUE;
            Direction bestDirection = null;

            for (int i = 0; i < directions.length; i++) {
                if (weights[i] < minWeight) {
                    minWeight = weights[i];
                    bestDirection = directions[i];
                }
            }

            return bestDirection;
    }
}
