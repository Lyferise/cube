package com.lyferise.cube.agents;

import com.lyferise.cube.components.ComponentState;

import static com.lyferise.cube.components.ComponentState.STARTED;
import static com.lyferise.cube.components.ComponentState.STOPPED;

public abstract class Agent {
    private final Thread thread = new Thread(this::run);
    private volatile ComponentState state;

    public Agent() {
        thread.start();
        state = STARTED;
    }

    public void stop() {
        state = STOPPED;
    }

    protected abstract void execute();

    protected abstract void waitForSignal();

    protected abstract void onError(Exception e);

    private void run() {
        thread.setName(getClass().getName() + "-" + thread.getId());

        while (true) {
            try {
                execute();
            } catch (final Exception e) {
                if (state == STARTED) onError(e);
            }

            waitForSignal();
        }
    }
}