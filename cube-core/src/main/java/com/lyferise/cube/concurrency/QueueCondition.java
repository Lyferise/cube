package com.lyferise.cube.concurrency;

import com.lyferise.cube.functions.Condition;

import java.util.concurrent.locks.ReentrantLock;

public abstract class QueueCondition implements Condition {
    private final ReentrantLock lock = new ReentrantLock();
    private final java.util.concurrent.locks.Condition condition = lock.newCondition();

    public abstract boolean test();

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (test()) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public void awaitNanos(final long nanosTimeout) throws InterruptedException {
        var t = nanosTimeout;
        lock.lock();
        try {
            while (test() && t > 0) {
                t = condition.awaitNanos(t);
            }
        } finally {
            lock.unlock();
        }
    }

    public void signal() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}