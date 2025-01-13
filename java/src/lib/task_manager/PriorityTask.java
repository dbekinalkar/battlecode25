package lib.task_manager;

public class PriorityTask extends Task {
    public int priority;
    public int taskNo;
    Task t;

    public PriorityTask(Task t, int priority, int taskNo) {
        this.priority = priority;
        this.taskNo = taskNo;

        this.t = t;
    }

    public boolean setup() {
        return this.t.setup();
    }

    public void run() {
        this.t.run();
    }
}