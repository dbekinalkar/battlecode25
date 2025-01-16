package dbekinalkar.lib.map;

import battlecode.common.*;

import java.util.*;

public class MapContext {
    public RobotController rc;

    public HashSet<MapLocation> buildable;
    public HashSet<MapLocation> unbuildable;

    public HashSet<MapLocation> enemyTowers;
    public HashSet<MapLocation> allyTowers;

    public HashSet<MapLocation> paintable;
    public HashSet<MapLocation> unpaintable;
    public PaintType[][] paint;

    public MapContext(RobotController rc) {
        this.rc = rc;

        buildable = new HashSet<MapLocation>();
        unbuildable = new HashSet<MapLocation>();

        enemyTowers = new HashSet<MapLocation>();
        allyTowers = new HashSet<MapLocation>();

        paintable = new HashSet<MapLocation>();
        unpaintable = new HashSet<MapLocation>();

        paint = new PaintType[this.rc.getMapHeight()][this.rc.getMapWidth()];
    }

    public void parseMap() throws GameActionException {
        MapLocation[] locs = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 20);

        for(MapLocation loc: locs) {
            MapInfo mi = rc.senseMapInfo(loc);
            RobotInfo ri = rc.senseRobotAtLocation(loc);

            paint[loc.y][loc.x] = mi.getPaint();
            if(!mi.isPassable()) paint[loc.y][loc.x] = null;

            switch(paint[loc.y][loc.x]) {
                case PaintType.EMPTY:
                    paintable.add(loc);
                    unpaintable.remove(loc);
                case PaintType.ENEMY_PRIMARY:
                case PaintType.ENEMY_SECONDARY:
                    unpaintable.add(loc);
                    paintable.remove(loc);
            }



            if(mi.hasRuin() ) {
                if(ri != null) {
                    if (ri.getTeam() == rc.getTeam()) {
                        allyTowers.add(loc);
                    }
                    else {
                        enemyTowers.add(loc);
                    }
                }
                else {
                    if(isBuildable(loc)) {
                        buildable.add(loc);
                    }
                    else {
                        buildable.remove(loc);
                    }
                }
            }
        }

    }

    private boolean isBuildable(MapLocation ruinLoc) {

        for(int i = -2; i < 3; i++) {
            for(int j = -2; j < 3; j++) {
                try {
                    if(i == 0 && j == 0) continue;
                    MapLocation loc = ruinLoc.translate(i, j);

                    MapInfo mi = rc.senseMapInfo(loc);

                    if(mi.getPaint().isEnemy()) {
                        unbuildable.add(ruinLoc);
                        return false;
                    }
                } catch (GameActionException ignored) {}
            }
        }

        return true;
    }

    public Optional<MapLocation> closest(HashSet<MapLocation> locs, MapLocation loc) {
        return locs.stream().min(Comparator.comparingInt(loc::distanceSquaredTo));
    }
}
