package com.ConnectionProject.connection.libs;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AsyncSemaphore {

    Semaphore semaphore = new Semaphore(0);

    public void acquire() throws InterruptedException {
        com.ConnectionProject.connection.libs.ThreadQueue threadQueue = com.ConnectionProject.connection.libs.ThreadQueue.getOrCreateThreadQueue(Thread.currentThread());
        AsyncSemaphore last = threadQueue.waiter;
        threadQueue.waiter = this;
        Semaphore queueSemaphore = threadQueue.queueSemaphore;
        try {
            if (semaphore.tryAcquire())
                return;

            while (true) {
                // run the queue
                while (true) {
                    Runnable run = threadQueue.remove();
                    if (run == null)
                        break;
//                        Log.i(LOGTAG, "Pumping for AsyncSemaphore");
                    run.run();
                }

                int permits = Math.max(1, queueSemaphore.availablePermits());
                queueSemaphore.acquire(permits);
                if (semaphore.tryAcquire())
                    break;
            }
        }
        finally {
            threadQueue.waiter = last;
        }
    }

    public boolean tryAcquire(long timeout, TimeUnit timeunit) throws InterruptedException {
        long timeoutMs = TimeUnit.MILLISECONDS.convert(timeout, timeunit);
        com.ConnectionProject.connection.libs.ThreadQueue threadQueue = com.ConnectionProject.connection.libs.ThreadQueue.getOrCreateThreadQueue(Thread.currentThread());
        AsyncSemaphore last = threadQueue.waiter;
        threadQueue.waiter = this;
        Semaphore queueSemaphore = threadQueue.queueSemaphore;

        try {
            if (semaphore.tryAcquire())
                return true;

            long start = System.currentTimeMillis();
            do {
                // run the queue
                while (true) {
                    Runnable run = threadQueue.remove();
                    if (run == null)
                        break;
//                        Log.i(LOGTAG, "Pumping for AsyncSemaphore");
                    run.run();
                }

                int permits = Math.max(1, queueSemaphore.availablePermits());
                if (!queueSemaphore.tryAcquire(permits, timeoutMs, TimeUnit.MILLISECONDS))
                    return false;
                if (semaphore.tryAcquire())
                    return true;
            }
            while (System.currentTimeMillis() - start < timeoutMs);
            return false;
        }
        finally {
            threadQueue.waiter = last;
        }
    }

    public void release() {
        semaphore.release();
        com.ConnectionProject.connection.libs.ThreadQueue.release(this);
    }
}
