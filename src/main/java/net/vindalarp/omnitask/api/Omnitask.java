package net.vindalarp.omnitask.api;

// DRAFT

import net.minecraft.client.Minecraft;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Omnitask {

    protected static final Minecraft minecraft = Minecraft.getInstance();
    protected static final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Runs code concurrently.<br />
     * WARNING: This code does not run on Minecraft's main thread.<br />
     * Accessing or modifying Minecraft state from this thread may cause thread-safety issues.<br />
     * Use {@code Omnitask.runOnMainThread()} when accessing or modifying Minecraft state.
     * @param code Lambda to run. Remember to use <code>runOnMainThread</code> for thread-safety.
     */
    public static void spawn(Runnable code) {
        executor.submit(code);
    }

    /**
     * Schedules code to run on Minecraft's main thread.
     * @param code The code to run
     */
    public static void defer(Runnable code) {
        minecraft.execute(code);
    }

    /**
     * Safely stalls the current thread.<br />
     * @param durationInSeconds The amount (in seconds) to wait.
     */
    public static void wait(float durationInSeconds) {
        var millis = (long) (durationInSeconds * 1000);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
