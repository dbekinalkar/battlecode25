package dbekinalkar.tower;

import battlecode.common.*;
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
        if (this.rc.getChips() - this.rc.getNumberTowers() * 250 < 1000) return;
        if (rc.getChips() > 4000 && rc.getType() != UnitType.LEVEL_THREE_PAINT_TOWER && rc.getType() != UnitType.LEVEL_THREE_MONEY_TOWER) {
            rc.upgradeTower(rc.getLocation());
            System.out.println("Tower upgraded");
        } else if (rc.getRoundNum() < 500 && (rc.canBuildRobot(UnitType.SOLDIER, rc.getLocation().add(Direction.NORTH)))) {
            rc.buildRobot(UnitType.SOLDIER, rc.getLocation().add(Direction.NORTH));
        } else if (rc.getRoundNum() > 500  && (rc.canBuildRobot(UnitType.SPLASHER, rc.getLocation().add(Direction.NORTH)))) {
            rc.buildRobot(UnitType.SPLASHER, rc.getLocation().add(Direction.NORTH));
        }

        //if(this.rc.getRoundNum() != 2) return;
        //if(rc.canBuildRobot(UnitType.SOLDIER, this.robotSpawnLoc)) {
        //    rc.buildRobot(UnitType.SOLDIER, this.robotSpawnLoc);
//        if (rc.canBuildRobot(UnitType.SOLDIER, rc.getLocation().add(Direction.NORTH))) {
//            rc.buildRobot(UnitType.SOLDIER, rc.getLocation().add(Direction.NORTH));
//        }
    }
}