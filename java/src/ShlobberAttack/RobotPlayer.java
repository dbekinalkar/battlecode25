package ShlobberAttack;
import battlecode.common.*;
import scala.Unit;

import java.nio.ReadOnlyBufferException;
import java.util.*;


public strictfp class RobotPlayer {
    public static final PaintType one = PaintType.ALLY_PRIMARY;
    public static final PaintType two = PaintType.ALLY_SECONDARY;

    public static final PaintType[][] chip_tower = {
            {one, two, two, two, one},
            {two, two, one, two, two},
            {two, one, null, one, two},
            {two, two, one, two, two},
            {one, two, two, two, one},
    };

    public static final PaintType[][] paint_tower = {
            {two, one, one, one, two},
            {one, two, one, two, one},
            {one, one, null, one, one},
            {one, two, one, two, one},
            {two, one, one, one, two},
    };

    public static final PaintType[][] special_resource = {
            {two, one, two, one, two},
            {one, two, one, two, one},
            {two, one, one, one, two},
            {one, two, one, two, one},
            {two, one, two, one, two},
    };

    static RobotController rc;
    static MapLocation TARGET_MONEY_TOWER;
    static boolean moneyTowerDestroyed = false;
    static MapLocation destroyedTowerLocation;
    static int timeAlive = 0;
    static int currentStrategy = 1;
    static final Random rng = new Random(2025);
    static boolean isBottom;
    static int spawnCounter = 0;
    static MapLocation home;
    static MapLocation suspectEnemy;
    static RobotInfo homeName;
    static Direction defaultGo = null;
    static MapInfo curRuin = null;


    static final Direction[] directions = {
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
            Direction.NORTH
    };

    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;
        suspectEnemy = new MapLocation((rc.getMapWidth() - rc.getLocation().x),(rc.getMapHeight() - rc.getLocation().y));


        while (true) {
            timeAlive++;
            try {
                switch (rc.getType()) {
                    case SPLASHER:
                        runSplasher();
                        break;
                    case MOPPER:
                        runMopper();
                        break;
                    case SOLDIER:
                        runSoldier();
                        break;
                    default:
                        runTower();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }

    static void runMopper() throws  GameActionException{

    }

    static void runSplasher() throws GameActionException{

    }

    static void runSoldier() throws GameActionException {

        RobotInfo[] targets = rc.senseNearbyRobots(-1,rc.getTeam().opponent()); //store all enemies
        if(rc.getPaint() < 30 && currentStrategy != 1) {
            if(rc.isActionReady() && home.isWithinDistanceSquared(home,2)){
                rc.transferPaint(home,-170);
            } else {
                Direction dir = rc.getLocation().directionTo(home);
                rc.move(dir);
            }
        }//if the robot is low on paint they run to their home**/
        RobotInfo[] ally = rc.senseNearbyRobots(4,rc.getTeam());

        if(true){
            for(RobotInfo tower : ally){
                if(tower.getType().isTowerType()){
                    home = tower.getLocation();
                }
            }
        }// saves the spawn location to come restock on paint

        if(currentStrategy == 2){ //strategy 1 for a soldier is an attempt at finding enemies by going to the opposite of the board
            if (targets != null) {
                for (RobotInfo victim : targets){
                    if(victim.getType().isTowerType() && rc.canAttack(victim.getLocation())){
                        rc.attack(victim.getLocation());
                    }
                }
            }else if(rc.isMovementReady()){
                Pathfinding.navigate(rc,suspectEnemy);
            }
        } else if(currentStrategy == 1) { //strategy 2 for a soldier will be blatant and shameless tower making
            MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();
            RobotInfo[] nearbyTowers = rc.senseNearbyRobots();

            // Search for a nearby ruin to complete.

            for (MapInfo tile : nearbyTiles){
                if (tile.hasRuin() && !tile.getMapLocation().isWithinDistanceSquared(home,4)){
                    curRuin = tile;
                }
            }

            if(curRuin != null && curRuin.getMapLocation().isAdjacentTo(rc.getLocation())){
                makeTower(curRuin.getMapLocation());
            }else if(curRuin != null) {
                MapLocation targetLoc = curRuin.getMapLocation();
                Direction dir = rc.getLocation().directionTo(targetLoc);
                if (rc.canMove(dir))
                    rc.move(dir);
            }else {
                if(defaultGo == null){
                    defaultGo = directions[rng.nextInt(directions.length)];
                }
                if(rc.canMove(defaultGo)){
                    rc.move(defaultGo);
                }else {
                    defaultGo = null;
                }
            }
        } else if(currentStrategy == 3) { //strategy 3 for a soldier will be special resource patterns

        } //leveling towers is to be discussed later once the bots seems to work smoothly

    }

    static void runTower() throws GameActionException {
        if(currentStrategy == 1){
            Boolean robotBuilt = true;
            while(robotBuilt) {
                int many = 0;
                Direction dir = directions[rng.nextInt(directions.length)];
                MapLocation nextLoc = rc.getLocation().add(dir);
                if (rc.canBuildRobot(UnitType.SOLDIER, nextLoc)) {
                    rc.buildRobot(UnitType.SOLDIER, nextLoc);
                    robotBuilt = false;
                } else if(many == 10){
                    robotBuilt = false;
                }
                many++;
            }
            if(rc.getRoundNum() > 20) {
                currentStrategy = 2;
            }
            /** Strategy two for towers currently consists of building one Mopper if they are a money tower
             *  and then one splasher every five spawns. Otherwise, they try to build soldiers.
             *
             * message 99 is a call for paint
             * **/
        } else if (currentStrategy == 2){
            Direction dir = directions[rng.nextInt(directions.length)];
            MapLocation nextLoc = rc.getLocation().add(dir);
            if(rc.getType() == UnitType.LEVEL_ONE_MONEY_TOWER){
                if(spawnCounter == 0 && rc.canBuildRobot(UnitType.MOPPER, nextLoc)){
                    rc.buildRobot(UnitType.MOPPER, nextLoc);
                    spawnCounter++;
                } else if(spawnCounter % 5 == 0 && rc.canBuildRobot(UnitType.SPLASHER,nextLoc)){
                    rc.buildRobot(UnitType.SPLASHER,nextLoc);
                    spawnCounter++;
                } else if( rc.canBuildRobot(UnitType.SOLDIER,nextLoc)){
                    rc.buildRobot(UnitType.SOLDIER,nextLoc);
                    spawnCounter++;
                } else {
                    RobotInfo[] allies = rc.senseNearbyRobots(-1,rc.getTeam());
                    for (RobotInfo ally : allies){
                        if (rc.canSendMessage(ally.location, 99) && ally.getType() == UnitType.MOPPER){
                            rc.sendMessage(ally.location, 99);
                        }
                    }
                }
            }
            else {
                if(spawnCounter == 0 && rc.canBuildRobot(UnitType.SOLDIER, nextLoc)){
                    rc.buildRobot(UnitType.SOLDIER, nextLoc);
                    spawnCounter++;
                } else if(spawnCounter % 5 == 0 && rc.canBuildRobot(UnitType.SPLASHER,nextLoc)){
                    rc.buildRobot(UnitType.SPLASHER,nextLoc);
                    spawnCounter++;
                } else if( rc.canBuildRobot(UnitType.SOLDIER,nextLoc)) {
                    rc.buildRobot(UnitType.SOLDIER, nextLoc);
                    spawnCounter++;
                }
            }
        }
    }


    static class Pathfinding {
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
            while(i < 8) {
                // TODO: Remove Infinite Loop
                if(rc.canMove(d)) return d;
                i++;
                d = nextDirection(d, i);
            }
            return d;
        }

        static Direction nextDirection(Direction d, int i) {
            if(i > directions.length) return Direction.CENTER;

            int diff = (i  + 1) / 2 * (i % 2 == 0? 1: -1);
            int start = directionMap.getOrDefault(d, 0);

            return directions[(start + diff + directions.length) % directions.length];
        }
    }

    static void makeTower(MapLocation center) throws GameActionException {
        if(rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, center)) {
            rc.completeTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, center);
        } else {
            if(true){
                Pathfinding.navigate(rc,center);
            }
            MapInfo[] patternTile = rc.senseNearbyMapInfos(center, 8);

            for(MapInfo curTile : patternTile) {

                    for (int i = -2; i < 3; i++) {
                        for (int j = -2; j < 3; j++) {
                            MapLocation coordNow = center.translate(i, j);
                            if (curTile.getMapLocation().equals(coordNow)) {
                                if (curTile.getPaint() != chip_tower[i + 2][j + 2] && chip_tower[i + 2][j + 2] != null) {
                                    boolean useSecondaryColor = chip_tower[i + 2][j + 2] == PaintType.ALLY_SECONDARY;
                                    rc.attack(curTile.getMapLocation(), useSecondaryColor);
                                }
                            }
                        }
                    }

            }

        }

    }
}






