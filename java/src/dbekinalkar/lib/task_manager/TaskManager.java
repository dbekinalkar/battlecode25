package dbekinalkar.lib.task_manager;

import battlecode.common.GameActionException;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TaskManager {

    public PriorityQueue<PriorityTask> q;
    int taskCount = 0;

    public TaskManager() {
        this.q = new PriorityQueue<PriorityTask>(Comparator.comparingInt((PriorityTask t) -> t.priority).thenComparingInt((PriorityTask t) -> t.taskNo));
    }

    public void addTask(Task t, int priority) {
        q.add(new PriorityTask(t, priority, taskCount));
        taskCount++;
    }

    public void executeTask() throws GameActionException {
        Task t = q.peek();

        while(!t.setup()) {
            q.poll();
            t = q.peek();
        }

        t.run();
    }

    public void clear() {
        q.clear();
        taskCount = 0;
    }
}
