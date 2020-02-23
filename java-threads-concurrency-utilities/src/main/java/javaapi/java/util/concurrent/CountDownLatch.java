package javaapi.java.util.concurrent;
import javaapi.java.util.concurrent.locks.AbstractQueuedSynchronizer;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

// 1.5 Doug Lea
public class CountDownLatch {

    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;
        //设置同步状态state
        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }
        //state不为0时，永远等待
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }
        //
        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

    private final Sync sync;//内部同步器

    //构造器，传入state值
    public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }
    //可中断
    //间接调tryAcquireShared(arg),state不为0时，挂起
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    //带时间可中断
    //间接调tryAcquireShared(arg),state不为0时，挂起
    public boolean await(long timeout, TimeUnit unit)
        throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    //间接调tryReleaseShared(arg),state减1
    public void countDown() {
        sync.releaseShared(1);
    }

    //获取当前state值
    public long getCount() {
        return sync.getCount();
    }


    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }
}
