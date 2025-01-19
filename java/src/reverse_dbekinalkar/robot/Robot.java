package reverse_dbekinalkar.robot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import dbekinalkar.Entity;
import reverse_dbekinalkar.lib.map.MapContext;
import reverse_dbekinalkar.lib.task_manager.TaskManager;

public class Robot extends Entity {

    public MapLocation anchor;
    public MapContext context;
    public TaskManager tm;

    public Robot(RobotController rc) {
        super(rc);

        this.anchor = rc.getLocation();
        this.context = new MapContext(rc);
        this.tm = new TaskManager();

        // Temporary tasks
    }

    public void run() throws GameActionException {
        // Parse map content and load into map context
        System.out.println("running");
        this.context.parseMap();

        // TODO: Parse messages

        // Do task
        this.tm.executeTask();

    }
}
