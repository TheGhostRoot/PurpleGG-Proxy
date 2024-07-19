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
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processTasks() {
        Consumer<?> task = taskQueue.remove();
        while (task != null) {
            task.accept(null);
            task = taskQueue.remove();
        }
    }
}
