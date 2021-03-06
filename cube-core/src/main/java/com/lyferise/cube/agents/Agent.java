package com.lyferise.cube.agents;

import com.lyferise.cube.components.ComponentState;
import lombok.SneakyThrows;

import static com.lyferise.cube.components.ComponentState.STARTED;
import static com.lyferise.cube.components.ComponentState.STOPPED;

public abstract class Agent {
    private final Thread thread = new Thread(this::run);
    private volatile ComponentState state;

    public Agent() {
        thread.start();
        state = STARTED;
    }

    public ComponentState getState() {
        return state;
    }

    @SneakyThrows
    public void stop() {
        state = STOPPED;
        thread.interrupt();
        thread.join();
    }

    protected abstract void execute();

    protected abstract void waitForSignal();

    protected abstract void onError(Exception e);

    private void run() {
        thread.setName(getClass().getName() + "-" + thread.getId());

        while (state != STOPPED){
            try {
                execute();
                waitForSignal();
            } catch (final Exception e) {
                switch (state) {
                    case STARTED -> onError(e);
                    case STOPPED -> {
                        return;
                    }
                }
            }
        }
    }
}