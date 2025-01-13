package ShlobberAttack;
import battlecode.common.*;
import java.util.*;

public strictfp class RobotPlayer {
    static RobotController rc;
    static MapLocation TARGET_MONEY_TOWER;
    static boolean moneyTowerDestroyed = false;
    static MapLocation destroyedTowerLocation;

    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;
        if (rc.getLocation().y < rc.getMapHeight() / 2) {
            TARGET_MONEY_TOWER = new MapLocation(9, 17);
        } else {
            TARGET_MONEY_TOWER = new MapLocation(10, 2);
        }
        while (true) {
            try {
                switch (rc.getType()) {
                    case SOLDIER:
                        runSoldier();
                        break;
                    case LEVEL_ONE_PAINT_TOWER:
                        runTower();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }

    static void runSoldier() throws GameActionException {
        if (!moneyTowerDestroyed) {
            if (tryAttack()) return;
            tryMoveTowardsTarget();
        } else {
            paintAroundDestroyedTower();
        }
    }

    static void runTower() throws GameActionException {
        if (rc.isActionReady()) {
            for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam().opponent())) {
                rc.attack(enemy.getLocation());
                return;
            }
        }
        spawnSoldierOnly();
    }

    static boolean tryAttack() throws GameActionException {
        if (rc.isActionReady()) {
            for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam().opponent())) {
                if (enemy.getType() == UnitType.LEVEL_ONE_MONEY_TOWER) {
                    rc.attack(enemy.getLocation());
                    return true;
                } else if (enemy.getType() == UnitType.LEVEL_ONE_PAINT_TOWER) {
                    rc.attack(enemy.getLocation());
                    if (enemy.getLocation().equals(TARGET_MONEY_TOWER) && enemy.getHealth() <= 60) {
                        moneyTowerDestroyed = true;
                        destroyedTowerLocation = enemy.getLocation();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    static void tryMoveTowardsTarget() throws GameActionException {
        if (!rc.isMovementReady()) return;
        Pathfinding.navigateTo(TARGET_MONEY_TOWER);
    }

    static void paintAroundDestroyedTower() throws GameActionException {
        if (!rc.isActionReady()) return;

        for (Direction dir : Direction.allDirections()) {
            MapLocation targetLoc = destroyedTowerLocation.add(dir);
            if (rc.canPaint(targetLoc)) {
                rc.attack(targetLoc, true);
            }
        }

        if (rc.isMovementReady()) {
            Direction bestDir = null;
            int maxUnpainted = 0;

            for (Direction dir : Direction.allDirections()) {
                MapLocation newLoc = rc.getLocation().add(dir);
                int unpaintedCount = countUnpaintedTiles(newLoc);
                if (rc.canMove(dir) && unpaintedCount > maxUnpainted) {
                    maxUnpainted = unpaintedCount;
                    bestDir = dir;
                }
            }
            if (bestDir != null) {
                rc.move(bestDir);
            }
        }
    }

    static int countUnpaintedTiles(MapLocation loc) throws GameActionException {
        int count = 0;
        for (Direction dir : Direction.allDirections()) {
            MapLocation adj = loc.add(dir);
            if (!rc.senseMapInfo(adj).getPaint().isAlly()) {
                count++;
            }
        }
        return count;
    }

    static class Pathfinding {
        static void navigateTo(MapLocation target) throws GameActionException {
            Queue<MapLocation> queue = new LinkedList<>();
            Set<MapLocation> visited = new HashSet<>();
            queue.add(rc.getLocation());

            while (!queue.isEmpty()) {
                MapLocation current = queue.poll();
                Direction dir = current.directionTo(target);

                if (rc.canMove(dir)) {
                    rc.move(dir);
                    return;
                }
                for (Direction alternative : Direction.allDirections()) {
                    MapLocation newLoc = current.add(alternative);
                    if (!visited.contains(newLoc) && rc.canMove(alternative)) {
                        queue.add(newLoc);
                        visited.add(newLoc);
                    }
                }
            }
        }
    }

    static void spawnSoldierOnly() throws GameActionException {
        if (rc.isActionReady()) {
            for (Direction dir : Direction.allDirections()) {
                if (rc.canBuildRobot(UnitType.SOLDIER, rc.getLocation().add(dir))) {
                    rc.buildRobot(UnitType.SOLDIER, rc.getLocation().add(dir));
                    return;
                }
            }
        }
    }
}
