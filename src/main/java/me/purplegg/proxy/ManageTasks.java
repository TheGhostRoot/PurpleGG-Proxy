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
        executor.submit(this::checker);
    }

    private void checker() {
        List<Future<?>> toRemove = new ArrayList<>();
        while (!executor.isShutdown()) {
            for (Future<?> task : futures) {
                if (task.isDone() || task.isCancelled()) {
                    toRemove.add(task);
                }
            }
            futures.removeAll(toRemove);
            toRemove.clear();
        }
    }

    public void submitTask(Consumer<?> task) {
        futures.add(executor.submit(this::processTasks));
        taskQueue.offer(task);
    }

    public boolean checkPrivateAllTasksDone() {
        for (Future<?> future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    public void shutdown() {
        try {
            futures.clear();
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
