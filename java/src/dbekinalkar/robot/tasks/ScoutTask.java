package dbekinalkar.robot.tasks;

import battlecode.common.*;
import dbekinalkar.lib.task_manager.Task;
import dbekinalkar.robot.Robot;

public class ScoutTask extends Task {
// If in top part of map

    public ScoutTask(Robot r) {
        super(r);
    }

    @Override
    public boolean setup() {
        return true;
    }
    public void run() throws GameActionException {
        //if (this.r.rc.getMapHeight() > 20) {
        //
        //}
        if (this.r.rc.getRoundNum() > 20) {
            return;
        }

    }
}

