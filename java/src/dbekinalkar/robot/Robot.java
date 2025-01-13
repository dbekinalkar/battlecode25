package dbekinalkar.robot;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import dbekinalkar.Entity;

public class Robot extends Entity {

    MapLocation anchor;

    public Robot(RobotController rc) {
        super(rc);

        this.anchor = rc.getLocation();
    }

    public void run() {
        // Parse map content and load into map context

        // Parse messages

        // Do task


    }
}
