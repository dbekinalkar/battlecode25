package dbekinalkar.robot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.UnitType;
import dbekinalkar.Entity;
import dbekinalkar.lib.map.MapContext;
import dbekinalkar.lib.task_manager.TaskManager;
import dbekinalkar.robot.tasks.BomberTask;
import dbekinalkar.robot.tasks.PainterTask;

public class Robot extends Entity {

    public MapLocation anchor;
    public MapContext context;
    public TaskManager tm;

    public Robot(RobotController rc) {
        super(rc);

        this.anchor = rc.getLocation();
        this.context = new MapContext(rc);
        this.tm = new TaskManager();
        switch  (this.rc.getType()) {
            case UnitType.SPLASHER:
                this.tm.addTask(new BomberTask(this), 5);

                break;
            case UnitType.SOLDIER:
                this.tm.addTask(new PainterTask(this), 5);

                break;
        }
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
