package ShlobberAttack;
import battlecode.common.*;
import java.util.*;

public strictfp class RobotPlayer {
    static RobotController rc;
    static final Random rng = new Random(2025);
    static int turnCount = 0;

    static MapLocation TARGET_MONEY_TOWER;
    static MapLocation PAINT_MAKER_1000;
    static boolean moneyTowerDestroyed = false;
    static boolean hasVoyaged = false;
    static boolean earthIsTilled = false;
    static MapLocation destroyedTowerLocation;
    static int giveUpTurn;


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
        System.out.println(rc.getRoundNum());
        if (rc.getRoundNum() % 3 == 0) {
            hasVoyaged = true;
        }
        RobotPlayer.rc = rc;
        if (rc.getMapWidth() < 21) { //small map
            giveUpTurn = 50;
            if (rc.getLocation().y < rc.getMapHeight() / 2) {
                TARGET_MONEY_TOWER = new MapLocation(9, 17);
                PAINT_MAKER_1000 = new MapLocation(5 , 2);
            } else {
                TARGET_MONEY_TOWER = new MapLocation(10, 2);
                PAINT_MAKER_1000 = new MapLocation(14,17);
            }
        } else if (rc.getMapWidth() < 36){ // medium map
            giveUpTurn = 60;
            if (rc.getLocation().x < rc.getMapWidth() / 2) {
                TARGET_MONEY_TOWER = new MapLocation(27, 7);
                PAINT_MAKER_1000 = new MapLocation(7,27);
            } else {
                TARGET_MONEY_TOWER = new MapLocation(7, 7);
                PAINT_MAKER_1000 = new MapLocation(27,27);
            }
        } else if (rc.getMapWidth() < 51) { //large map
            giveUpTurn = 70;
            if (rc.getLocation().x < rc.getMapWidth() / 2) {
                TARGET_MONEY_TOWER = new MapLocation(41, 17);
                PAINT_MAKER_1000 = new MapLocation(12,12);
            } else {
                TARGET_MONEY_TOWER = new MapLocation(8, 17);
                PAINT_MAKER_1000 = new MapLocation(37,12);
            }
        } else { //huge map
            giveUpTurn = 80;
            if (rc.getLocation().y < rc.getMapHeight() / 2) {
                TARGET_MONEY_TOWER = new MapLocation(31, 54);
                PAINT_MAKER_1000 = new MapLocation(25,4);
            } else {
                TARGET_MONEY_TOWER = new MapLocation(31, 4);
                PAINT_MAKER_1000 = new MapLocation(25,54);
            }
        }


        while (true) {
            turnCount += 1;
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
        } else if(earthIsTilled && !hasVoyaged) {
            Pathfinding.navigateTo(new MapLocation(rc.getMapWidth()/2,rc.getMapHeight()/2));
        } else {
            paintAroundDestroyedTower();
        }
        if (rc.getLocation().isWithinDistanceSquared(new MapLocation(rc.getMapWidth()/2,rc.getMapHeight()/2),4)){
            hasVoyaged = true;
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
            if(rc.getLocation().isWithinDistanceSquared(TARGET_MONEY_TOWER,1) || rc.getLocation().isWithinDistanceSquared(PAINT_MAKER_1000,2) || rc.getRoundNum() > giveUpTurn) {
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
        if (turnCount < 100 || turnCount > 200 || turnCount % 10 == 0) {
            Direction dir = directions[rng.nextInt(directions.length)];
            MapLocation nextLoc = rc.getLocation().add(dir);
            boolean the_move = true;
            if (rc.canMove(dir) && the_move) {
                for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam().opponent())) {
                    if (enemy.getType() == UnitType.LEVEL_ONE_MONEY_TOWER) {
                        rc.attack(enemy.getLocation());

                    } else if (enemy.getType() == UnitType.LEVEL_ONE_PAINT_TOWER) {
                        rc.attack(enemy.getLocation());
                    }
                }
                for (RobotInfo ally : rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam())) {
                    if (ally.getType() == UnitType.LEVEL_ONE_MONEY_TOWER && turnCount > 100 && rc.getLocation().isWithinDistanceSquared(PAINT_MAKER_1000,64)) {
                        earthIsTilled = true;
                    } else if (ally.getType() == UnitType.LEVEL_ONE_PAINT_TOWER && turnCount > 100 && rc.getLocation().isWithinDistanceSquared(PAINT_MAKER_1000,64)) {
                        earthIsTilled = true;
                    }
                }
                rc.move(dir);
            } else {
                rc.move(Direction.SOUTH);
            }
            if (rc.canMopSwing(dir)) {
                rc.mopSwing(dir);
                System.out.println("Mop Swing! Booyah!");
            }
            MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
            if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())) {
                rc.attack(rc.getLocation());
            }
        }else {
            // Sense information about all visible nearby tiles.
            MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();
            // Search for a nearby ruin to complete.
            MapInfo curRuin = null;
            for (MapInfo tile : nearbyTiles){
                if (tile.hasRuin()){
                    curRuin = tile;
                }
            }
            if (curRuin != null){
                MapLocation targetLoc = curRuin.getMapLocation();
                Direction dir = rc.getLocation().directionTo(targetLoc);
                if (rc.canMove(dir))
                    if(rc.getLocation().isWithinDistanceSquared(targetLoc,4)){
                        Pathfinding.rotateAround(targetLoc);
                    }else {
                        Pathfinding.navigateTo(targetLoc);
                    }

                // Mark the pattern we need to draw to build a tower here if we haven't already.
                MapLocation shouldBeMarked = curRuin.getMapLocation().subtract(dir);
                if (rc.senseMapInfo(shouldBeMarked).getMark() == PaintType.EMPTY && rc.canMarkTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, targetLoc)){
                    rc.markTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, targetLoc);
                    System.out.println("Trying to build a tower at " + targetLoc);
                }
                // Fill in any spots in the pattern with the appropriate paint.
                for (MapInfo patternTile : rc.senseNearbyMapInfos(targetLoc, 20)){
                    if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY){
                        boolean useSecondaryColor = patternTile.getMark() == PaintType.ALLY_SECONDARY;
                        if (rc.canAttack(patternTile.getMapLocation()))
                            rc.attack(patternTile.getMapLocation(), useSecondaryColor);
                    }
                }
                // Complete the ruin if we can.
                if (rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, targetLoc)){
                    rc.completeTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, targetLoc);
                    rc.setTimelineMarker("Tower built", 0, 255, 0);
                    System.out.println("Built a tower at " + targetLoc + "!");
                }

                MapLocation nextLoc = rc.getLocation().add(dir);
                if (rc.canMove(dir)){
                    rc.move(dir);
                }
                // Try to paint beneath us as we walk to avoid paint penalties.
                // Avoiding wasting paint by re-painting our own tiles.
                MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
                if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())){
                    rc.attack(rc.getLocation());
                }
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
        static void rotateAround(MapLocation target) throws GameActionException{
            Queue<MapLocation> queue = new LinkedList<>();
            Set<MapLocation> visited = new HashSet<>();
            queue.add(rc.getLocation());
            while (!queue.isEmpty()) {
                MapLocation current = queue.poll();
                Direction dir = current.directionTo(target);

                if (rc.canMove(dir)) {
                    rc.move(dir);
                    queue.add(current.add(dir));
                    return;
                } else {
                    boolean nextMOVE = false;
                    for (Direction attempt : directions) {
                        if(nextMOVE){
                            dir = attempt;
                        }
                        if(attempt == dir) {
                            nextMOVE = true;
                        }
                    }
                    queue.add(current.add(dir));
                    rc.move(dir);
                    return;
                }

            }
        }
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
