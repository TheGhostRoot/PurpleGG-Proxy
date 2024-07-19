package me.purplegg.proxy;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class ManageTasks {
    private final ExecutorService executor;
    private final Queue<Consumer<?>> taskQueue = new ConcurrentLinkedQueue<>();
    private List<Future<?>> futures = new ArrayList<>();

    public ManageTasks(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void submitTask(Consumer<?> task) {
        futures.add(executor.submit(this::processTasks));
        taskQueue.offer(task);
    }

    public boolean checkAllTasksDone() {
        boolean allDone = true;
        for(Future<?> future : futures){
            allDone &= future.isDone(); // check if future is done
        }
        return allDone;
    }

    public void shutdown() {
        try {
            executor.shutdown();
            executor.shutdownNow();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processTasks() {
        if (taskQueue.isEmpty()) {
            return;
        }
        Consumer<?> task = taskQueue.remove();
        while (task != null) {
            task.accept(null);
            if (taskQueue.isEmpty()) {
                break;
            }
            task = taskQueue.remove();
        }
    }
}
