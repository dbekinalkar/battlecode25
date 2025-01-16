package dbekinalkar.tower;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.UnitType;
import dbekinalkar.Entity;

public class Tower extends Entity {
    MapLocation robotSpawnLoc;

    public Tower(RobotController rc) {
        super(rc);

        int x = this.rc.getMapWidth() / 2;
        int y = this.rc.getMapHeight() / 2;

        MapLocation loc = this.rc.getLocation();

        int dx = x - loc.x;
        dx = Math.min(dx, 2);
        dx = Math.max(dx, -2);

        int dy = y - loc.y;
        dy = Math.min(dy, 2);
        dy = Math.max(dy, -2);

        this.robotSpawnLoc = loc.translate(dx, dy);
    }

    public void run() throws GameActionException {
        if(this.rc.getChips() - this.rc.getNumberTowers() * 250 > 1000) {
            if(rc.canBuildRobot(UnitType.SOLDIER, this.robotSpawnLoc)) {
                rc.buildRobot(UnitType.SOLDIER, this.robotSpawnLoc);
            }
        }
    }
}
