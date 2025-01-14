package ShlobberAttack;
import battlecode.common.*;
import java.util.*;



public strictfp class RobotPlayer {
    static RobotController rc;
    static MapLocation TARGET_MONEY_TOWER;
    static boolean moneyTowerDestroyed = false;
    static MapLocation destroyedTowerLocation;
    static int stationaryTurns = 0;
    static int eastMoves = 0;
    static boolean forceEastMovement = false;
    static int spiralStep = 0;
    static int spiralRadius = 1;
    static int stepsTaken = 0;
    static int amountSteps = 1;
    static Direction[] spiralDirections = {Direction.SOUTHEAST, Direction.SOUTHWEST, Direction.NORTHWEST, Direction.NORTHEAST};

    static int layerLoop = 0;
    static int directionCount = 0;

    //Andrew's stupid variables
    static Direction[] lateralDirections = {Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.SOUTH};
    static int counter = 0;
    static int lateralDir = 0;
    static boolean True = true;
    static boolean False = false;



    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;
        if (rc.getLocation().y < rc.getMapHeight() / 2) {
            TARGET_MONEY_TOWER = new MapLocation(9, 17);
        } else {
            TARGET_MONEY_TOWER = new MapLocation(10, 2);
        }

        // Check if TARGET_MONEY_TOWER has a ruin safely
        if (rc.canSenseLocation(TARGET_MONEY_TOWER) && rc.senseMapInfo(TARGET_MONEY_TOWER).hasRuin()) {
            System.out.println("TARGET_MONEY_TOWER has a ruin!");
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
                    case LEVEL_ONE_MONEY_TOWER:
                        runMoneyTower();
                        break;
                    case SPLASHER:
                        runSpiralSplasher();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }

    static void runSoldier() throws GameActionException {
        if (rc.getRoundNum() > 200) {
            paintAndMoveStraight();
        } else if (!moneyTowerDestroyed) {
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
        if (rc.getRoundNum() > 50) {
            spawnSplasherOnly();
        } else {
            spawnSoldierOnly();
        }
    }

    static void runMoneyTower() throws GameActionException {
        // Logic for money tower
        if (rc.isActionReady()) {
            for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam().opponent())) {
                rc.attack(enemy.getLocation());
                return;
            }
        }
        if (rc.getRoundNum() > 50) {
            spawnSplasherOnly();
        } else {
            spawnSoldierOnly();
        }
    }

    static boolean atXEdge(int x) {
        if (x == 0 || x == rc.getMapWidth() - 1) {
            return True;
        }
        return False;
    }
    static void runSpiralSplasher() throws GameActionException {
        if (!rc.isActionReady()) return;
        if (!rc.senseMapInfo(rc.getLocation()).getPaint().isAlly() && counter == 5) {
            if (rc.canPaint(rc.getLocation())) {
                rc.attack(rc.getLocation());
                counter = 0;
                System.out.println("Painting");
            }
        }
        else if (counter == 5) {
            counter = 0;
            System.out.println("Resetting counter");
        }
        if (rc.senseMapInfo(rc.getLocation().add(lateralDirections[lateralDir])).getPaint().isAlly()) {
            lateralDir = (lateralDir + 1) % 4;
            counter = 0;
        }
        if (rc.canMove(lateralDirections[lateralDir])) {
            rc.move(lateralDirections[lateralDir]);
            counter += 1;
        }
//        else {
//            lateralDir = (lateralDir + 1) % 4;
//            counter = 0;
//            System.out.println("Setting dir to down");
//            System.out.println(lateralDirections[lateralDir].name());
//        }



//        // Attack only if the location is unpainted or has enemy paint
//        if (!rc.senseMapInfo(rc.getLocation()).getPaint().isAlly()) {
//            rc.attack(rc.getLocation());
//        }
    }
//
//        if (rc.isMovementReady()) {
//            //Direction moveDir = [Direction.NORTH];
//            Direction moveDir = spiralDirections[spiralStep];
//            if (rc.canMove(moveDir)) {
//
//                if(directionCount <= layerLoop){
//                    rc.move(moveDir);
//
//                    directionCount++;
//                }
//                if(directionCount > layerLoop){
//                    directionCount = 0;
//                    spiralStep++;
//                }
//                if (spiralStep == 4) {
//                    spiralStep = 0;
//                    layerLoop++;
//                }
//
//            } else {
//            for (Direction dir : Direction.allDirections()) {
//                if (rc.canMove(dir)) {
//                    rc.move(dir);
//                    //layerLoop--;
//                    return;
//                }
//                }
//            }
//
///**
//
// /**/
//        }
//
//    }



    static int countUnpaintedOrEnemyPaint(MapLocation loc) throws GameActionException {
        int count = 0;
        for (Direction dir : Direction.allDirections()) {
            MapLocation adj = loc.add(dir);
            if (rc.canSenseLocation(adj)) {
                MapInfo mapInfo = rc.senseMapInfo(adj);
                if (!mapInfo.getPaint().isAlly()) {
                    count++;
                }
            }
        }
        return count;
    }





    static void paintAndMoveStraight() throws GameActionException {
        if (!rc.isActionReady()) return;

        MapLocation myLocation = rc.getLocation();
        for (Direction dir : Direction.allDirections()) {
            MapLocation targetLoc = myLocation.add(dir);
            if (rc.canPaint(targetLoc)) {
                rc.attack(targetLoc, true);
            }
        }

        if (rc.isMovementReady()) {
            Direction moveDir = Direction.NORTH;
            if (rc.canMove(moveDir)) {
                rc.move(moveDir);
            }
        }
    }

    static void paintAroundDestroyedTower() throws GameActionException {
        if (!rc.isActionReady()) return;

        for (Direction dir : Direction.allDirections()) {
            MapLocation targetLoc = destroyedTowerLocation.add(dir);
            if (rc.canPaint(targetLoc)) {
                rc.attack(targetLoc, true);
            }
        }
    }

    static void tryMoveTowardsTarget() throws GameActionException {
        if (!rc.isMovementReady()) return;
        Pathfinding.navigateTo(TARGET_MONEY_TOWER);
    }

    static boolean tryAttack() throws GameActionException {
        if (rc.isActionReady()) {
            for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam().opponent())) {
                if (enemy.getType() == UnitType.LEVEL_ONE_MONEY_TOWER || enemy.getType() == UnitType.LEVEL_ONE_PAINT_TOWER) {
                    rc.attack(enemy.getLocation());
                    return true;
                }
            }
        }
        return false;
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

    static void spawnSplasherOnly() throws GameActionException {
        if (rc.isActionReady()) {
            rc.buildRobot(UnitType.SPLASHER,rc.getLocation().add(Direction.SOUTH));
            /**for (Direction dir : Direction.allDirections()) {
                if (rc.canBuildRobot(UnitType.SPLASHER, rc.getLocation().add(dir))) {
                    rc.buildRobot(UnitType.SPLASHER, rc.getLocation().add(dir));
                    return;
                }
            }**/
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
