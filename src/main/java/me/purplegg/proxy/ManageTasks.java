package me.purplegg.proxy;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ManageTasks {
    private final ExecutorService executor;
    private final Queue<Consumer<?>> taskQueue = new ConcurrentLinkedQueue<>();
    private boolean isRunning = true;
    private int max_threads = 1;
    private int current_threads = 0;

    public ManageTasks(int threads) {
        this.max_threads = threads;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void submitTask(Consumer<?> task) {
        if (current_threads < max_threads) {
            executor.submit(this::processTasks);
            current_threads++;
        }
        taskQueue.offer(task);
    }

    public void shutdown() {
        isRunning = false;
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processTasks() {
        while (isRunning) {
            Consumer<?> task = taskQueue.poll();
            if (task == null) {
                Thread.interrupted();
                break;
            }
            task.accept(null);
        }
    }
}
