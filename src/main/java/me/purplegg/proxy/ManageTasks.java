package me.purplegg.proxy;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ManageTasks {
    private final ExecutorService executor;
    private List<Future<?>> futures = Collections.synchronizedList(new ArrayList<>());

    public ManageTasks(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    synchronized public void submitTask(Consumer<?> task) {
        futures.add(executor.submit(() -> task.accept(null)));
    }

    public void shutdown() {
        Iterator<Future<?>> iterator = futures.iterator();
        while (iterator.hasNext()) {
            Future<?> future = iterator.next();
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
            } finally {
                iterator.remove();
            }
        }
        futures.clear();
        try {
            executor.shutdownNow();
        } catch (Exception e) {}
    }
}
