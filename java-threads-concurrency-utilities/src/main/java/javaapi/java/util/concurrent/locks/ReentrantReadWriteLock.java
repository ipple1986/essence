package javaapi.java.util.concurrent.locks;

import java.util.concurrent.locks.*;
import java.util.concurrent.TimeUnit;
import java.util.Collection;

/**
 * An implementation of {@link ReadWriteLock} supporting similar
 * semantics to {@link ReentrantLock}.
 * <p>This class has the following properties:
 *
 * <ul>
 * <li><b>Acquisition order</b>
 *
 * <p>This class does not impose a reader or writer preference
 * ordering for lock access.  However, it does support an optional
 * <em>fairness</em> policy.
 *
 * <dl>
 * <dt><b><i>Non-fair mode (default)</i></b>
 * <dd>When constructed as non-fair (the default), the order of entry
 * to the read and write lock is unspecified, subject to reentrancy
 * constraints.  A nonfair lock that is continuously contended may
 * indefinitely postpone one or more reader or writer threads, but
 * will normally have higher throughput than a fair lock.
 *
 * <dt><b><i>Fair mode</i></b>
 * <dd>When constructed as fair, threads contend for entry using an
 * approximately arrival-order policy. When the currently held lock
 * is released, either the longest-waiting single writer thread will
 * be assigned the write lock, or if there is a group of reader threads
 * waiting longer than all waiting writer threads, that group will be
 * assigned the read lock.
 *
 * <p>A thread that tries to acquire a fair read lock (non-reentrantly)
 * will block if either the write lock is held, or there is a waiting
 * writer thread. The thread will not acquire the read lock until
 * after the oldest currently waiting writer thread has acquired and
 * released the write lock. Of course, if a waiting writer abandons
 * its wait, leaving one or more reader threads as the longest waiters
 * in the queue with the write lock free, then those readers will be
 * assigned the read lock.
 *
 * <p>A thread that tries to acquire a fair write lock (non-reentrantly)
 * will block unless both the read lock and write lock are free (which
 * implies there are no waiting threads).  (Note that the non-blocking
 * {@link ReadLock#tryLock()} and {@link WriteLock#tryLock()} methods
 * do not honor this fair setting and will immediately acquire the lock
 * if it is possible, regardless of waiting threads.)
 * <p>
 * </dl>
 *
 * <li><b>Reentrancy</b>
 *
 * <p>This lock allows both readers and writers to reacquire read or
 * write locks in the style of a {@link ReentrantLock}. Non-reentrant
 * readers are not allowed until all write locks held by the writing
 * thread have been released.
 *
 * <p>Additionally, a writer can acquire the read lock, but not
 * vice-versa.  Among other applications, reentrancy can be useful
 * when write locks are held during calls or callbacks to methods that
 * perform reads under read locks.  If a reader tries to acquire the
 * write lock it will never succeed.
 *
 * <li><b>Lock downgrading</b>
 * <p>Reentrancy also allows downgrading from the write lock to a read lock,
 * by acquiring the write lock, then the read lock and then releasing the
 * write lock. However, upgrading from a read lock to the write lock is
 * <b>not</b> possible.
 *
 * <li><b>Interruption of lock acquisition</b>
 * <p>The read lock and write lock both support interruption during lock
 * acquisition.
 *
 * <li><b>{@link Condition} support</b>
 * <p>The write lock provides a {@link Condition} implementation that
 * behaves in the same way, with respect to the write lock, as the
 * {@link Condition} implementation provided by
 * {@link ReentrantLock#newCondition} does for {@link ReentrantLock}.
 * This {@link Condition} can, of course, only be used with the write lock.
 *
 * <p>The read lock does not support a {@link Condition} and
 * {@code readLock().newCondition()} throws
 * {@code UnsupportedOperationException}.
 *
 * <li><b>Instrumentation</b>
 * <p>This class supports methods to determine whether locks
 * are held or contended. These methods are designed for monitoring
 * system state, not for synchronization control.
 * </ul>
 *
 * <p>Serialization of this class behaves in the same way as built-in
 * locks: a deserialized lock is in the unlocked state, regardless of
 * its state when serialized.
 *
 * <p><b>Sample usages</b>. Here is a code sketch showing how to perform
 * lock downgrading after updating a cache (exception handling is
 * particularly tricky when handling multiple locks in a non-nested
 * fashion):
 *
 * <pre> {@code
 * class CachedData {
 *   Object data;
 *   volatile boolean cacheValid;
 *   final javaapi.java.util.concurrent.locks.ReentrantReadWriteLock rwl = new javaapi.java.util.concurrent.locks.ReentrantReadWriteLock();
 *
 *   void processCachedData() {
 *     rwl.readLock().lock();
 *     if (!cacheValid) {
 *       // Must release read lock before acquiring write lock
 *       rwl.readLock().unlock();
 *       rwl.writeLock().lock();
 *       try {
 *         // Recheck state because another thread might have
 *         // acquired write lock and changed state before we did.
 *         if (!cacheValid) {
 *           data = ...
 *           cacheValid = true;
 *         }
 *         // Downgrade by acquiring read lock before releasing write lock
 *         rwl.readLock().lock();
 *       } finally {
 *         rwl.writeLock().unlock(); // Unlock write, still hold read
 *       }
 *     }
 *
 *     try {
 *       use(data);
 *     } finally {
 *       rwl.readLock().unlock();
 *     }
 *   }
 * }}</pre>
 *
 * ReentrantReadWriteLocks can be used to improve concurrency in some
 * uses of some kinds of Collections. This is typically worthwhile
 * only when the collections are expected to be large, accessed by
 * more reader threads than writer threads, and entail operations with
 * overhead that outweighs synchronization overhead. For example, here
 * is a class using a TreeMap that is expected to be large and
 * concurrently accessed.
 *
 *  <pre> {@code
 * class RWDictionary {
 *   private final Map<String, Data> m = new TreeMap<String, Data>();
 *   private final javaapi.java.util.concurrent.locks.ReentrantReadWriteLock rwl = new javaapi.java.util.concurrent.locks.ReentrantReadWriteLock();
 *   private final Lock r = rwl.readLock();
 *   private final Lock w = rwl.writeLock();
 *
 *   public Data get(String key) {
 *     r.lock();
 *     try { return m.get(key); }
 *     finally { r.unlock(); }
 *   }
 *   public String[] allKeys() {
 *     r.lock();
 *     try { return m.keySet().toArray(); }
 *     finally { r.unlock(); }
 *   }
 *   public Data put(String key, Data value) {
 *     w.lock();
 *     try { return m.put(key, value); }
 *     finally { w.unlock(); }
 *   }
 *   public void clear() {
 *     w.lock();
 *     try { m.clear(); }
 *     finally { w.unlock(); }
 *   }
 * }}</pre>
 *
 * <h3>Implementation Notes</h3>
 *
 * <p>This lock supports a maximum of 65535 recursive write locks
 * and 65535 read locks. Attempts to exceed these limits result in
 * {@link Error} throws from locking methods.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {
    private static final long serialVersionUID = -6992448646407690164L;
    //内部类提供读写锁 同步器
    private final ReentrantReadWriteLock.ReadLock readerLock;
    private final ReentrantReadWriteLock.WriteLock writerLock;
    //执行所有的同步操作
    final Sync sync;

    //是否公平锁
    public ReentrantReadWriteLock() {
        this(false);
    }

    public ReentrantReadWriteLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
        readerLock = new ReadLock(this);
        writerLock = new WriteLock(this);
    }

    public ReentrantReadWriteLock.WriteLock writeLock() { return writerLock; }
    public ReentrantReadWriteLock.ReadLock  readLock()  { return readerLock; }

    /**
     * Synchronization implementation for javaapi.java.util.concurrent.locks.ReentrantReadWriteLock.
     * Subclassed into fair and nonfair versions.
     */
    //ReentrantReadWriteLock类的同步器
    abstract static class Sync extends javaapi.java.util.concurrent.locks.AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 6317671515068378041L;

        // 读写计数器常量
        //高16位为共享读锁的计数器，低16位为排它锁计数器
        static final int SHARED_SHIFT   = 16;
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT);//32
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;//31
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;//31

        //共享计数 高16位
        static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
        //排它计数，低16位
        static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }

        //每个线程的对应的计数据器
        static final class HoldCounter {
            int count = 0;
            // 使用id避免 垃圾回收有残余
            final long tid = getThreadId(Thread.currentThread());
        }

        // ThreadLocal 子类. 显式地定义利于反序列化
        static final class ThreadLocalHoldCounter
            extends ThreadLocal<HoldCounter> {
            public HoldCounter initialValue() {
                return new HoldCounter();
            }
        }

        //当前线程读锁 数
        //当数减到0时，清空
        //在构造器或readObject方法被清除
        private transient ThreadLocalHoldCounter readHolds;

        private transient HoldCounter cachedHoldCounter;

        private transient Thread firstReader = null;
        private transient int firstReaderHoldCount;

        Sync() {
            readHolds = new ThreadLocalHoldCounter();
            setState(getState()); //0 确保readHolds的可见性
        }

        //公平不平公锁，体现在非空队列时，是否允许线程插队
        //不同公平程序的同步器 有不同 实现
        abstract boolean readerShouldBlock();
        abstract boolean writerShouldBlock();

        protected final boolean tryRelease(int releases) {
            if (!isHeldExclusively())//非占有写锁的线程释放 写锁 抛出异常
                throw new IllegalMonitorStateException();
            int nextc = getState() - releases; //减计数
            boolean free = exclusiveCount(nextc) == 0;
            if (free)//当前线程释放写锁，设置此同步器owner=null
                setExclusiveOwnerThread(null);
            setState(nextc);//更新status字段
            return free;  //返回是否当前线程释放所获得的排它/写锁
        }

        protected final boolean tryAcquire(int acquires) {
            Thread current = Thread.currentThread();
            int c = getState();
            int w = exclusiveCount(c);
            if (c != 0) {//同步器写锁或读锁已经被占用，判断是否当前线程获有独占写锁，是的话，可重入
                //写锁未被释放 或 当前线程不是占写锁/排它 线程，获写锁 返回false，放入等待队列
                if (w == 0 || current != getExclusiveOwnerThread())
                    return false;
                if (w + exclusiveCount(acquires) > MAX_COUNT)//超数检查，最多31次 重入
                    throw new Error("Maximum lock count exceeded");
                // 重入获取
                setState(c + acquires);//获得写锁的线程，再次重入获取锁
                return true;
            }
            //c=0,即 写锁 未 被线程占用的情况
            //不公平锁 writerShouldBlock = false时，非公平同步，独占写锁可以闯入，直接compareAndSetState
            //公平锁writerShouldBlock =true时，表明等待队列有线程等待，返回false 放入等待队列
            if (writerShouldBlock() ||
                !compareAndSetState(c, c + acquires))//添加排它/写锁 计数失败，返回false
                return false;
            setExclusiveOwnerThread(current);//抢夺成功的线程，设置自己成为owner
            return true;
        }

        protected final boolean tryReleaseShared(int unused) {
            Thread current = Thread.currentThread();
            if (firstReader == current) {
                // assert firstReaderHoldCount > 0;
                if (firstReaderHoldCount == 1)
                    firstReader = null;
                else
                    firstReaderHoldCount--;
            } else {
                HoldCounter rh = cachedHoldCounter;
                if (rh == null || rh.tid != getThreadId(current))
                    rh = readHolds.get();
                int count = rh.count;
                if (count <= 1) {
                    readHolds.remove();
                    if (count <= 0)
                        throw unmatchedUnlockException();
                }
                --rh.count;
            }
            for (;;) {
                int c = getState();
                int nextc = c - SHARED_UNIT;
                if (compareAndSetState(c, nextc))
                    // Releasing the read lock has no effect on readers,
                    // but it may allow waiting writers to proceed if
                    // both read and write locks are now free.
                    return nextc == 0;
            }
        }

        private IllegalMonitorStateException unmatchedUnlockException() {
            return new IllegalMonitorStateException(
                "attempt to unlock read lock, not locked by current thread");
        }

        protected final int tryAcquireShared(int unused) {

            Thread current = Thread.currentThread();
            int c = getState();
            if (exclusiveCount(c) != 0 &&//其他线程占用 写锁，并且没释放，入等待队列
                getExclusiveOwnerThread() != current)//即，只有占用写锁的线程 或 写锁释放下的所有线程，才能继续往下走
                return -1;
            int r = sharedCount(c);
            //不公平锁 =false时，!readerShouldBlock()=true,直接compareAndSetState
            //公平锁 时，根据等待队列第一节点是否排它线程，如果是共享节点（无所谓公不公平，因为是共享节点），直接compareAndSetState
            if (!readerShouldBlock() && //公平度+超数检查+更新status(最高16位+1)
                r < MAX_COUNT &&
                compareAndSetState(c, c + SHARED_UNIT)) {//下面代码只有一个线程能够执行成功compareAndSetState
                if (r == 0) {//记录第一个读线程及计数1
                    firstReader = current;
                    firstReaderHoldCount = 1;
                } else if (firstReader == current) {//第一个读线程存在，更新
                    firstReaderHoldCount++;
                } else {//其他线程走缓存或ThreadLocal计数+1
                    HoldCounter rh = cachedHoldCounter;
                    if (rh == null || rh.tid != getThreadId(current))
                        cachedHoldCounter = rh = readHolds.get();
                    else if (rh.count == 0)
                        readHolds.set(rh);
                    rh.count++;
                }
                return 1;//执行成功的线程返回
            }
            //不公平锁CAS失败
            //公平锁并且等待队列第一个节点为排它 CAS失败
            return fullTryAcquireShared(current);//未成功的线程往里走
        }

        /**
         * Full version of acquire for reads, that handles CAS misses
         * and reentrant reads not dealt with in tryAcquireShared.
         */
        final int fullTryAcquireShared(Thread current) {

            HoldCounter rh = null;
            for (;;) {//循环让每个线程都compareAndSetState最高16位+1 成功，返回
                int c = getState();
                if (exclusiveCount(c) != 0) {//获共享读锁时，有线程 占用 写锁
                    if (getExclusiveOwnerThread() != current)//如果非当前线程，返回等待
                        return -1;
                    // else we hold the exclusive lock; blocking here
                    // would cause deadlock.
                } else if (readerShouldBlock()) {//公平锁且第一等待节点是排它节点进入；不公平跳过
                    //能进入此处，说明当前线程先占用排它写锁，此时想要获取共享/读锁
                    // Make sure we're not acquiring read lock reentrantly
                    //对于当前线程节点，不处理
                    //对于非当前线点，如果计数为0，说明第一个尝试获取共享读锁，返回等待
                    /*
                    例子：
                    线程A   writeLock > readLock  往下走
                    线程B   readLock              返回等待
                     */
                    if (firstReader == current) {//保证当前线程获得写占用锁的同时，不阻塞
                        // assert firstReaderHoldCount > 0;
                    } else {//其他线程阻塞，在第一次尝试获取共享读锁时，阻塞掉
                        if (rh == null) {
                            rh = cachedHoldCounter;
                            if (rh == null || rh.tid != getThreadId(current)) {
                                rh = readHolds.get();
                                if (rh.count == 0)
                                    readHolds.remove();
                            }
                        }
                        if (rh.count == 0)//第一次获取共享读锁的其他线程阻塞
                            return -1;
                    }
                }
                if (sharedCount(c) == MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                //几种情况：
                //不公平，无写锁被占有，所有线程
                //不公平，有写锁的当前线程
                //公平且第一个节点为排它节点的第一个节点线程，即公平锁，有写锁的当前线程
                //公平且第一个节点为共享节点的所有节点线程，即公平锁，无写锁被占有，所有线程
                //其他情况 无法进到此处
                if (compareAndSetState(c, c + SHARED_UNIT)) {//CAS失败的话，继续循环，总会成功的
                    if (sharedCount(c) == 0) {
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    } else if (firstReader == current) {
                        firstReaderHoldCount++;
                    } else {
                        if (rh == null)
                            rh = cachedHoldCounter;
                        if (rh == null || rh.tid != getThreadId(current))
                            rh = readHolds.get();
                        else if (rh.count == 0)
                            readHolds.set(rh);
                        rh.count++;
                        cachedHoldCounter = rh; // cache for release
                    }
                    return 1;
                }
            }
        }

        final boolean tryWriteLock() {//代码跟tryAcquire()类似，不进入等待队列
            Thread current = Thread.currentThread();
            int c = getState();
            if (c != 0) {//当前同步器存在读锁的当前线程
                int w = exclusiveCount(c);
                /*
                    acquireShared(1) -> tryWriteLock 阻塞
                 */
                if (w == 0 || current != getExclusiveOwnerThread())// 只有写锁 或 有读锁非当前线程 挂起
                    return false;
                if (w == MAX_COUNT)//超数抛出ERROR
                    throw new Error("Maximum lock count exceeded");
            }
            //当前无读/写锁
            //当前只有读锁的当前线程
            /*读锁当前线程
                acquire(1) -> tryWriteLock
             */
            if (!compareAndSetState(c, c + 1))//不公平，CAS更新失败返回
                return false;
            setExclusiveOwnerThread(current);//CAS成功，当前线程占有写锁
            return true;
        }

        final boolean tryReadLock() {//不进行等待队列，成功的更新state，失败的循环
            Thread current = Thread.currentThread();
            for (;;) {
                int c = getState();
                if (exclusiveCount(c) != 0 &&
                    getExclusiveOwnerThread() != current) //写锁被占用的非当前线程，挂起
                    return false;
                //无写锁的所有线程
                /*
                tryReadLock() -> tryReadLock()
                 */
                //有写锁的本地线程
                /*current
                    tryWriteLock()-> tryReadLock
                 */
                int r = sharedCount(c);
                if (r == MAX_COUNT)//超数检查
                    throw new Error("Maximum lock count exceeded");
                if (compareAndSetState(c, c + SHARED_UNIT)) {//循环直到更新成功
                    if (r == 0) {
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    } else if (firstReader == current) {
                        firstReaderHoldCount++;
                    } else {
                        HoldCounter rh = cachedHoldCounter;
                        if (rh == null || rh.tid != getThreadId(current))
                            cachedHoldCounter = rh = readHolds.get();
                        else if (rh.count == 0)
                            readHolds.set(rh);
                        rh.count++;
                    }
                    return true;
                }
            }
        }
        //判断当前线程是否为写锁/排它 线程
        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        //提供多条件锁ConditionObject，从同步器创建ConditionObject，创建条件队列
        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        //获得排它/写锁的线程 ；
        // 排它数为0，表示没有线程获得写锁 返回null
        final Thread getOwner() {
            // Must read state before owner to ensure memory consistency
            return ((exclusiveCount(getState()) == 0) ?
                    null :
                    getExclusiveOwnerThread());
        }
        //读锁共享数
        final int getReadLockCount() {
            return sharedCount(getState());
        }
        //判断写锁数是否为0
        final boolean isWriteLocked() {
            return exclusiveCount(getState()) != 0;
        }
        //如果当前获得写锁，则返回排它/写锁计数
        final int getWriteHoldCount() {
            return isHeldExclusively() ? exclusiveCount(getState()) : 0;
        }
        //如果内存读锁/共享数为0，返回0
        //判断是否第一个读锁/共享线程 或 缓存计数hold 或 readHolds（为0清空） 从中取出数
        final int getReadHoldCount() {
            if (getReadLockCount() == 0)
                return 0;

            Thread current = Thread.currentThread();
            if (firstReader == current)
                return firstReaderHoldCount;

            HoldCounter rh = cachedHoldCounter;
            if (rh != null && rh.tid == getThreadId(current))
                return rh.count;

            int count = readHolds.get().count;
            if (count == 0) readHolds.remove();
            return count;
        }

        //反序列化时调用
        private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            readHolds = new ThreadLocalHoldCounter();
            setState(0); // reset to unlocked state
        }
        //获取status内存值
        final int getCount() { return getState(); }
    }

    //不公平同步器 版本
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = -8159625535654395037L;
        final boolean writerShouldBlock() {
            //可以让独占锁 可以闯入
            return false;
        }
        final boolean readerShouldBlock() {
            //如果第一等待节点是排它节点，返回true，不可闯入
            return apparentlyFirstQueuedIsExclusive();
        }
    }

    //公平锁版本
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -2274990926593161451L;
        final boolean writerShouldBlock() {
            //有排除线程就不让独占排它/写锁 可以闯入
            return hasQueuedPredecessors();
        }
        final boolean readerShouldBlock() {
            //有排除线程就不让共享读锁 可以闯入
            return hasQueuedPredecessors();
        }
    }

    /**
     * The lock returned by method {@link ReentrantReadWriteLock#readLock}.
     */
    public static class ReadLock implements Lock, java.io.Serializable {
        private static final long serialVersionUID = -5992448646407690164L;
        private final Sync sync;

        protected ReadLock(ReentrantReadWriteLock lock) {
            sync = lock.sync;
        }

        //调用AQS,间接调用sync.tryAcquireShared(arg)方法
        public void lock() {
            sync.acquireShared(1);
        }
        //调用AQS,间接调用sync.tryAcquireShared(arg)方法
        public void lockInterruptibly() throws InterruptedException {
            sync.acquireSharedInterruptibly(1);
        }
        //调用sync.tryReadLock()，扩展AQS功能
        public boolean tryLock() {
            return sync.tryReadLock();
        }

        //调用AQS,间接调用sync.tryAcquireShared(arg)方法
        public boolean tryLock(long timeout, TimeUnit unit)
                throws InterruptedException {
            return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
        }

        //调用AQS,间接调用sync.tryReleaseShared(arg)方法
        public void unlock() {
            sync.releaseShared(1);
        }

        //读锁 是共享锁， 不支持 排它 操作
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            int r = sync.getReadLockCount();
            return super.toString() +
                "[Read locks = " + r + "]";
        }
    }

    //写锁类 定义
    public static class WriteLock implements Lock, java.io.Serializable {
        private static final long serialVersionUID = -4992448646407690164L;
        private final Sync sync;
        //传入父类同步器
        protected WriteLock(ReentrantReadWriteLock lock) {
            sync = lock.sync;
        }

        //调用AQS,间接调用sync.tryAcquire(args)方法
        public void lock() {
            sync.acquire(1);
        }

        //调用AQS,间接调用sync.tryAcquire(args)方法
        public void lockInterruptibly() throws InterruptedException {
            sync.acquireInterruptibly(1);
        }

        //调用sync.tryWriteLock()方法
        public boolean tryLock( ) {
            return sync.tryWriteLock();
        }

        //调用AQS,间接调用sync.tryAcquire(args)方法
        public boolean tryLock(long timeout, TimeUnit unit)
                throws InterruptedException {
            return sync.tryAcquireNanos(1, unit.toNanos(timeout));
        }

        //调用AQS,间接调用sync.tryRelease(args)方法
        public void unlock() {
            sync.release(1);
        }

        //调用sync.newCondition()，创建ConditionObject实例
        public Condition newCondition() {
            return sync.newCondition();
        }

        //查看锁定的线程名
        public String toString() {
            Thread o = sync.getOwner();
            return super.toString() + ((o == null) ?
                                       "[Unlocked]" :
                                       "[Locked by thread " + o.getName() + "]");
        }
        //下面一大波工具类方法，提供检测使用，不累述
        /**
         * Queries if this write lock is held by the current thread.
         * Identical in effect to {@link
         * ReentrantReadWriteLock#isWriteLockedByCurrentThread}.
         *
         * @return {@code true} if the current thread holds this lock and
         *         {@code false} otherwise
         * @since 1.6
         */
        public boolean isHeldByCurrentThread() {
            return sync.isHeldExclusively();
        }

        /**
         * Queries the number of holds on this write lock by the current
         * thread.  A thread has a hold on a lock for each lock action
         * that is not matched by an unlock action.  Identical in effect
         * to {@link ReentrantReadWriteLock#getWriteHoldCount}.
         *
         * @return the number of holds on this lock by the current thread,
         *         or zero if this lock is not held by the current thread
         * @since 1.6
         */
        public int getHoldCount() {
            return sync.getWriteHoldCount();
        }
    }

    // Instrumentation and status

    /**
     * Returns {@code true} if this lock has fairness set true.
     *
     * @return {@code true} if this lock has fairness set true
     */
    public final boolean isFair() {
        return sync instanceof FairSync;
    }

    /**
     * Returns the thread that currently owns the write lock, or
     * {@code null} if not owned. When this method is called by a
     * thread that is not the owner, the return value reflects a
     * best-effort approximation of current lock status. For example,
     * the owner may be momentarily {@code null} even if there are
     * threads trying to acquire the lock but have not yet done so.
     * This method is designed to facilitate construction of
     * subclasses that provide more extensive lock monitoring
     * facilities.
     *
     * @return the owner, or {@code null} if not owned
     */
    protected Thread getOwner() {
        return sync.getOwner();
    }

    /**
     * Queries the number of read locks held for this lock. This
     * method is designed for use in monitoring system state, not for
     * synchronization control.
     * @return the number of read locks held
     */
    public int getReadLockCount() {
        return sync.getReadLockCount();
    }

    /**
     * Queries if the write lock is held by any thread. This method is
     * designed for use in monitoring system state, not for
     * synchronization control.
     *
     * @return {@code true} if any thread holds the write lock and
     *         {@code false} otherwise
     */
    public boolean isWriteLocked() {
        return sync.isWriteLocked();
    }

    /**
     * Queries if the write lock is held by the current thread.
     *
     * @return {@code true} if the current thread holds the write lock and
     *         {@code false} otherwise
     */
    public boolean isWriteLockedByCurrentThread() {
        return sync.isHeldExclusively();
    }

    /**
     * Queries the number of reentrant write holds on this lock by the
     * current thread.  A writer thread has a hold on a lock for
     * each lock action that is not matched by an unlock action.
     *
     * @return the number of holds on the write lock by the current thread,
     *         or zero if the write lock is not held by the current thread
     */
    public int getWriteHoldCount() {
        return sync.getWriteHoldCount();
    }

    /**
     * Queries the number of reentrant read holds on this lock by the
     * current thread.  A reader thread has a hold on a lock for
     * each lock action that is not matched by an unlock action.
     *
     * @return the number of holds on the read lock by the current thread,
     *         or zero if the read lock is not held by the current thread
     * @since 1.6
     */
    public int getReadHoldCount() {
        return sync.getReadHoldCount();
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire the write lock.  Because the actual set of threads may
     * change dynamically while constructing this result, the returned
     * collection is only a best-effort estimate.  The elements of the
     * returned collection are in no particular order.  This method is
     * designed to facilitate construction of subclasses that provide
     * more extensive lock monitoring facilities.
     *
     * @return the collection of threads
     */
    protected Collection<Thread> getQueuedWriterThreads() {
        return sync.getExclusiveQueuedThreads();
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire the read lock.  Because the actual set of threads may
     * change dynamically while constructing this result, the returned
     * collection is only a best-effort estimate.  The elements of the
     * returned collection are in no particular order.  This method is
     * designed to facilitate construction of subclasses that provide
     * more extensive lock monitoring facilities.
     *
     * @return the collection of threads
     */
    protected Collection<Thread> getQueuedReaderThreads() {
        return sync.getSharedQueuedThreads();
    }

    /**
     * Queries whether any threads are waiting to acquire the read or
     * write lock. Note that because cancellations may occur at any
     * time, a {@code true} return does not guarantee that any other
     * thread will ever acquire a lock.  This method is designed
     * primarily for use in monitoring of the system state.
     *
     * @return {@code true} if there may be other threads waiting to
     *         acquire the lock
     */
    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    /**
     * Queries whether the given thread is waiting to acquire either
     * the read or write lock. Note that because cancellations may
     * occur at any time, a {@code true} return does not guarantee
     * that this thread will ever acquire a lock.  This method is
     * designed primarily for use in monitoring of the system state.
     *
     * @param thread the thread
     * @return {@code true} if the given thread is queued waiting for this lock
     * @throws NullPointerException if the thread is null
     */
    public final boolean hasQueuedThread(Thread thread) {
        return sync.isQueued(thread);
    }

    /**
     * Returns an estimate of the number of threads waiting to acquire
     * either the read or write lock.  The value is only an estimate
     * because the number of threads may change dynamically while this
     * method traverses internal data structures.  This method is
     * designed for use in monitoring of the system state, not for
     * synchronization control.
     *
     * @return the estimated number of threads waiting for this lock
     */
    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire either the read or write lock.  Because the actual set
     * of threads may change dynamically while constructing this
     * result, the returned collection is only a best-effort estimate.
     * The elements of the returned collection are in no particular
     * order.  This method is designed to facilitate construction of
     * subclasses that provide more extensive monitoring facilities.
     *
     * @return the collection of threads
     */
    protected Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

    /**
     * Queries whether any threads are waiting on the given condition
     * associated with the write lock. Note that because timeouts and
     * interrupts may occur at any time, a {@code true} return does
     * not guarantee that a future {@code signal} will awaken any
     * threads.  This method is designed primarily for use in
     * monitoring of the system state.
     *
     * @param condition the condition
     * @return {@code true} if there are any waiting threads
     * @throws IllegalMonitorStateException if this lock is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this lock
     * @throws NullPointerException if the condition is null
     */
    public boolean hasWaiters(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof javaapi.java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.hasWaiters((javaapi.java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    /**
     * Returns an estimate of the number of threads waiting on the
     * given condition associated with the write lock. Note that because
     * timeouts and interrupts may occur at any time, the estimate
     * serves only as an upper bound on the actual number of waiters.
     * This method is designed for use in monitoring of the system
     * state, not for synchronization control.
     *
     * @param condition the condition
     * @return the estimated number of waiting threads
     * @throws IllegalMonitorStateException if this lock is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this lock
     * @throws NullPointerException if the condition is null
     */
    public int getWaitQueueLength(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof javaapi.java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    /**
     * Returns a collection containing those threads that may be
     * waiting on the given condition associated with the write lock.
     * Because the actual set of threads may change dynamically while
     * constructing this result, the returned collection is only a
     * best-effort estimate. The elements of the returned collection
     * are in no particular order.  This method is designed to
     * facilitate construction of subclasses that provide more
     * extensive condition monitoring facilities.
     *
     * @param condition the condition
     * @return the collection of threads
     * @throws IllegalMonitorStateException if this lock is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this lock
     * @throws NullPointerException if the condition is null
     */
    protected Collection<Thread> getWaitingThreads(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof javaapi.java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitingThreads((javaapi.java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    /**
     * Returns a string identifying this lock, as well as its lock state.
     * The state, in brackets, includes the String {@code "Write locks ="}
     * followed by the number of reentrantly held write locks, and the
     * String {@code "Read locks ="} followed by the number of held
     * read locks.
     *
     * @return a string identifying this lock, as well as its lock state
     */

    public String toString() {
        int c = sync.getCount();
        int w = Sync.exclusiveCount(c);
        int r = Sync.sharedCount(c);

        return super.toString() +
            "[Write locks = " + w + ", Read locks = " + r + "]";
    }

    //获取线程ID
    static final long getThreadId(Thread thread) {
        return UNSAFE.getLongVolatile(thread, TID_OFFSET);
    }

    // Unsafe 机制，提供内存读/写线程tid
    private static final sun.misc.Unsafe UNSAFE;
    private static final long TID_OFFSET;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> tk = Thread.class;
            TID_OFFSET = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("tid"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
