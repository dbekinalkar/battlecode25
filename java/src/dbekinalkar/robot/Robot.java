package dbekinalkar.robot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import dbekinalkar.Entity;
import lib.map.MapContext;
import lib.task_manager.TaskManager;

public class Robot extends Entity {

    MapLocation anchor;
    MapContext context;
    TaskManager tm;

    public Robot(RobotController rc) {
        super(rc);

        this.anchor = rc.getLocation();
        this.context = new MapContext(rc);
        this.tm = new TaskManager();

        // Temporary tasks
    }

    public void run() throws GameActionException {
        // Parse map content and load into map context
        this.context.parseMap();

        // TODO: Parse messages

        // Do task


    }
}
