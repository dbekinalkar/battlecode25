package ShlobberAttack;
import battlecode.common.*;
import java.util.*;

public strictfp class RobotPlayer {
    static RobotController rc;
    static MapLocation TARGET_MONEY_TOWER;
    static boolean moneyTowerDestroyed = false;
    static MapLocation destroyedTowerLocation;

    static final Random rng = new Random(2025);

    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

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
                    case MOPPER:
                        runMopper();
                        break;
                    case LEVEL_ONE_MONEY_TOWER:
                        runTower();
                        break;
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
    public static void runDefaultTower() throws GameActionException{
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        // Pick a random robot type to build.


        rc.buildRobot(UnitType.SOLDIER, nextLoc);


        // Read incoming messages


        // TODO: can we attack other bots?
    }

    static boolean tryAttack() throws GameActionException {
        if (rc.isActionReady()) {
            for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam().opponent())) {
                if (enemy.getType() == UnitType.LEVEL_ONE_MONEY_TOWER) {
                    rc.attack(enemy.getLocation());
                    return true;
                } else if (enemy.getType() == UnitType.LEVEL_ONE_PAINT_TOWER) {
                    rc.attack(enemy.getLocation());
                    return true;
                }
            }
            if(rc.getLocation().isWithinDistanceSquared(TARGET_MONEY_TOWER,1) || rc.getLocation().isWithinDistanceSquared(new MapLocation(10,2),2) || rc.getRoundNum() > 50) {
                moneyTowerDestroyed = true;

            }
        }
        return false;
    }

    static void tryMoveTowardsTarget() throws GameActionException {
        if (!rc.isMovementReady()) return;
        Pathfinding.navigateTo(TARGET_MONEY_TOWER);
    }

    static void paintAroundDestroyedTower() throws GameActionException {
        // Move and attack randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        boolean the_move = true;
        if (rc.canMove(dir) && the_move){
            rc.move(dir);
            the_move = true;
        } else {
            rc.move(Direction.SOUTH);
        }
        if (rc.canMopSwing(dir)){
            rc.mopSwing(dir);
            System.out.println("Mop Swing! Booyah!");
        }
        MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
        if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())){
            rc.attack(rc.getLocation());
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

    public static void runMopper() throws GameActionException{
        // Move and attack randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        if (rc.canMove(dir)){
            rc.move(dir);
        }
        if (rc.canMopSwing(dir)){
            rc.mopSwing(dir);
            System.out.println("Mop Swing! Booyah!");
        }
        else if (rc.canAttack(nextLoc)){
            rc.attack(nextLoc);
        }
        // We can also move our code into different methods or classes to better organize it!
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
