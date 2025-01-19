package reverse_dbekinalkar.lib.map;

import battlecode.common.*;
import reverse_dbekinalkar.lib.BitShift;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;

public class MapContext {
    public RobotController rc;

    public long[] buildable;
    public long[] unbuildable;

    public long[] enemyTowers;
    public long[] allyTowers;

    public long[] paintable;
    public long[] unpaintable;
    public PaintType[][] paint;

    public MapContext(RobotController rc) {
        this.rc = rc;

        buildable = new long[60];
        unbuildable = new long[60];

        enemyTowers = new long[60];
        allyTowers = new long[60];

        paintable = new long[60];
        unpaintable = new long[60];

        paint = new PaintType[this.rc.getMapWidth()][this.rc.getMapHeight()];
    }

    public void parseMap() throws GameActionException {
        System.out.println("Parsing MapContext");

        MapLocation[] locs = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 20);
        MapInfo[] mis = rc.senseNearbyMapInfos();
        RobotInfo[] ris = rc.senseNearbyRobots();

        for(RobotInfo ri: ris) {
            UnitType t = ri.getType();
            switch(t) {
                case MOPPER, SPLASHER, SOLDIER:
                    break;
                default:
                    Team team = ri.getTeam();
                    MapLocation loc = ri.getLocation();
                    if(team == this.rc.getTeam()) {
                        allyTowers[loc.x] &= BitShift.bitMasks[loc.y];
                    }
                    else {
                        enemyTowers[loc.x] &= BitShift.bitMasks[loc.y];
                    }
            }
        }

        for(int i = 0; i < locs.length; i++) {
            MapLocation loc = locs[i];
            MapInfo mi = mis[i];

            paint[loc.x][loc.y] = mi.getPaint();
            if(!mi.isPassable()) paint[loc.x][loc.y] = null;

            if(paint[loc.x][loc.y] != null) {
                switch(paint[loc.x][loc.y]) {
                    case PaintType.EMPTY:
                        paintable[loc.x] &= BitShift.bitMasks[loc.y];
                        unpaintable[loc.x] &= ~BitShift.bitMasks[loc.y];
                        break;
                    case PaintType.ALLY_PRIMARY:
                    case PaintType.ALLY_SECONDARY:
                        paintable[loc.x] &= ~BitShift.bitMasks[loc.y];
                        break;
                    case PaintType.ENEMY_PRIMARY:
                    case PaintType.ENEMY_SECONDARY:
                        unpaintable[loc.x] &= BitShift.bitMasks[loc.y];
                        paintable[loc.x] &= ~BitShift.bitMasks[loc.y];
                        break;
                }
            }

            if(mi.hasRuin()) {
                long exists = allyTowers[loc.x] & enemyTowers[loc.x] & BitShift.bitMasks[loc.y];
                if(exists != 0) {
                    buildable[loc.x] &= ~BitShift.bitMasks[loc.y];
                }
                else {
                    if(isBuildable(loc)) {
                        buildable[loc.x] &= BitShift.bitMasks[loc.y];
                    }
                    else {
                        buildable[loc.x] &= ~BitShift.bitMasks[loc.y];
                    }
                }
            }
        }

    }

    private boolean isBuildable(MapLocation ruinLoc) {
        if((unbuildable[ruinLoc.x] & BitShift.bitMasks[ruinLoc.y]) != 0) return false;

//        for(int i = -2; i < 3; i++) {
//            for(int j = -2; j < 3; j++) {
//                if(i == 0 && j == 0) continue;
//                int x = ruinLoc.x + i;
//                int y = ruinLoc.y + j;
//                PaintType p = this.paint[ruinLoc.x][ruinLoc.y];
//
//                if(p != null && p.isEnemy()) {
//                    unbuildable[ruinLoc.x] &= BitShift.bitMasks[ruinLoc.y];
//                    return false;
//                }
//            }
//        }

        long mask = BitShift.towerWidth << (ruinLoc.y - 2);

        for(int i = -2; i < 3; i++) {
            int x = ruinLoc.x + i;
            long ret = unpaintable[x] & mask;
            if(ret != 0) {
                unbuildable[ruinLoc.x] &= BitShift.bitMasks[ruinLoc.y];
                return false;
            }
        }

        return true;
    }

//    public Optional<MapLocation> closest(HashSet<MapLocation> locs, MapLocation loc) {
//        return locs.stream().min(Comparator.comparingInt(a -> loc.distanceSquaredTo(a)));
//    }
}
