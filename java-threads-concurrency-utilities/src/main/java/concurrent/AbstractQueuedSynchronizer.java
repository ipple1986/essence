package concurrent;/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */
//package java.util.concurrent.locks;
import java.util.concurrent.locks.*;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import sun.misc.Unsafe;

/**
 * Provides a framework for implementing blocking locks and related
 * synchronizers (semaphores, events, etc) that rely on
 * first-in-first-out (FIFO) wait queues.  This class is designed to
 * be a useful basis for most kinds of synchronizers that rely on a
 * single atomic {@code int} value to represent state. Subclasses
 * must define the protected methods that change this state, and which
 * define what that state means in terms of this object being acquired
 * or released.  Given these, the other methods in this class carry
 * out all queuing and blocking mechanics. Subclasses can maintain
 * other state fields, but only the atomically updated {@code int}
 * value manipulated using methods {@link #getState}, {@link
 * #setState} and {@link #compareAndSetState} is tracked with respect
 * to synchronization.
 *
 * <p>Subclasses should be defined as non-public internal helper
 * classes that are used to implement the synchronization properties
 * of their enclosing class.  Class
 * {@code concurrent.AbstractQueuedSynchronizer} does not implement any
 * synchronization interface.  Instead it defines methods such as
 * {@link #acquireInterruptibly} that can be invoked as
 * appropriate by concrete locks and related synchronizers to
 * implement their public methods.
 *
 * <p>This class supports either or both a default <em>exclusive</em>
 * mode and a <em>shared</em> mode. When acquired in exclusive mode,
 * attempted acquires by other threads cannot succeed. Shared mode
 * acquires by multiple threads may (but need not) succeed. This class
 * does not &quot;understand&quot; these differences except in the
 * mechanical sense that when a shared mode acquire succeeds, the next
 * waiting thread (if one exists) must also determine whether it can
 * acquire as well. Threads waiting in the different modes share the
 * same FIFO queue. Usually, implementation subclasses support only
 * one of these modes, but both can come into play for example in a
 * {@link ReadWriteLock}. Subclasses that support only exclusive or
 * only shared modes need not define the methods supporting the unused mode.
 *
 * <p>This class defines a nested {@link ConditionObject} class that
 * can be used as a {@link Condition} implementation by subclasses
 * supporting exclusive mode for which method {@link
 * #isHeldExclusively} reports whether synchronization is exclusively
 * held with respect to the current thread, method {@link #release}
 * invoked with the current {@link #getState} value fully releases
 * this object, and {@link #acquire}, given this saved state value,
 * eventually restores this object to its previous acquired state.  No
 * {@code concurrent.AbstractQueuedSynchronizer} method otherwise creates such a
 * condition, so if this constraint cannot be met, do not use it.  The
 * behavior of {@link ConditionObject} depends of course on the
 * semantics of its synchronizer implementation.
 *
 * <p>This class provides inspection, instrumentation, and monitoring
 * methods for the internal queue, as well as similar methods for
 * condition objects. These can be exported as desired into classes
 * using an {@code concurrent.AbstractQueuedSynchronizer} for their
 * synchronization mechanics.
 *
 * <p>Serialization of this class stores only the underlying atomic
 * integer maintaining state, so deserialized objects have empty
 * thread queues. Typical subclasses requiring serializability will
 * define a {@code readObject} method that restores this to a known
 * initial state upon deserialization.
 *
 * <h3>Usage</h3>
 *
 * <p>To use this class as the basis of a synchronizer, redefine the
 * following methods, as applicable, by inspecting and/or modifying
 * the synchronization state using {@link #getState}, {@link
 * #setState} and/or {@link #compareAndSetState}:
 *
 * <ul>
 * <li> {@link #tryAcquire}
 * <li> {@link #tryRelease}
 * <li> {@link #tryAcquireShared}
 * <li> {@link #tryReleaseShared}
 * <li> {@link #isHeldExclusively}
 * </ul>
 *
 * Each of these methods by default throws {@link
 * UnsupportedOperationException}.  Implementations of these methods
 * must be internally thread-safe, and should in general be short and
 * not block. Defining these methods is the <em>only</em> supported
 * means of using this class. All other methods are declared
 * {@code final} because they cannot be independently varied.
 *
 * <p>You may also find the inherited methods from {@link
 * AbstractOwnableSynchronizer} useful to keep track of the thread
 * owning an exclusive synchronizer.  You are encouraged to use them
 * -- this enables monitoring and diagnostic tools to assist users in
 * determining which threads hold locks.
 *
 * <p>Even though this class is based on an internal FIFO queue, it
 * does not automatically enforce FIFO acquisition policies.  The core
 * of exclusive synchronization takes the form:
 *
 * <pre>
 * Acquire:
 *     while (!tryAcquire(arg)) {
 *        <em>enqueue thread if it is not already queued</em>;
 *        <em>possibly block current thread</em>;
 *     }
 *
 * Release:
 *     if (tryRelease(arg))
 *        <em>unblock the first queued thread</em>;
 * </pre>
 *
 * (Shared mode is similar but may involve cascading signals.)
 *
 * <p id="barging">Because checks in acquire are invoked before
 * enqueuing, a newly acquiring thread may <em>barge</em> ahead of
 * others that are blocked and queued.  However, you can, if desired,
 * define {@code tryAcquire} and/or {@code tryAcquireShared} to
 * disable barging by internally invoking one or more of the inspection
 * methods, thereby providing a <em>fair</em> FIFO acquisition order.
 * In particular, most fair synchronizers can define {@code tryAcquire}
 * to return {@code false} if {@link #hasQueuedPredecessors} (a method
 * specifically designed to be used by fair synchronizers) returns
 * {@code true}.  Other variations are possible.
 *
 * <p>Throughput and scalability are generally highest for the
 * default barging (also known as <em>greedy</em>,
 * <em>renouncement</em>, and <em>convoy-avoidance</em>) strategy.
 * While this is not guaranteed to be fair or starvation-free, earlier
 * queued threads are allowed to recontend before later queued
 * threads, and each recontention has an unbiased chance to succeed
 * against incoming threads.  Also, while acquires do not
 * &quot;spin&quot; in the usual sense, they may perform multiple
 * invocations of {@code tryAcquire} interspersed with other
 * computations before blocking.  This gives most of the benefits of
 * spins when exclusive synchronization is only briefly held, without
 * most of the liabilities when it isn't. If so desired, you can
 * augment this by preceding calls to acquire methods with
 * "fast-path" checks, possibly prechecking {@link #hasContended}
 * and/or {@link #hasQueuedThreads} to only do so if the synchronizer
 * is likely not to be contended.
 *
 * <p>This class provides an efficient and scalable basis for
 * synchronization in part by specializing its range of use to
 * synchronizers that can rely on {@code int} state, acquire, and
 * release parameters, and an internal FIFO wait queue. When this does
 * not suffice, you can build synchronizers from a lower level using
 * {@link java.util.concurrent.atomic atomic} classes, your own custom
 * {@link java.util.Queue} classes, and {@link LockSupport} blocking
 * support.
 *
 * <h3>Usage Examples</h3>
 *
 * <p>Here is a non-reentrant mutual exclusion lock class that uses
 * the value zero to represent the unlocked state, and one to
 * represent the locked state. While a non-reentrant lock
 * does not strictly require recording of the current owner
 * thread, this class does so anyway to make usage easier to monitor.
 * It also supports conditions and exposes
 * one of the instrumentation methods:
 *
 *  <pre> {@code
 * class Mutex implements Lock, java.io.Serializable {
 *
 *   // Our internal helper class
 *   private static class Sync extends concurrent.AbstractQueuedSynchronizer {
 *     // Reports whether in locked state
 *     protected boolean isHeldExclusively() {
 *       return getState() == 1;
 *     }
 *
 *     // Acquires the lock if state is zero
 *     public boolean tryAcquire(int acquires) {
 *       assert acquires == 1; // Otherwise unused
 *       if (compareAndSetState(0, 1)) {
 *         setExclusiveOwnerThread(Thread.currentThread());
 *         return true;
 *       }
 *       return false;
 *     }
 *
 *     // Releases the lock by setting state to zero
 *     protected boolean tryRelease(int releases) {
 *       assert releases == 1; // Otherwise unused
 *       if (getState() == 0) throw new IllegalMonitorStateException();
 *       setExclusiveOwnerThread(null);
 *       setState(0);
 *       return true;
 *     }
 *
 *     // Provides a Condition
 *     Condition newCondition() { return new ConditionObject(); }
 *
 *     // Deserializes properly
 *     private void readObject(ObjectInputStream s)
 *         throws IOException, ClassNotFoundException {
 *       s.defaultReadObject();
 *       setState(0); // reset to unlocked state
 *     }
 *   }
 *
 *   // The sync object does all the hard work. We just forward to it.
 *   private final Sync sync = new Sync();
 *
 *   public void lock()                { sync.acquire(1); }
 *   public boolean tryLock()          { return sync.tryAcquire(1); }
 *   public void unlock()              { sync.release(1); }
 *   public Condition newCondition()   { return sync.newCondition(); }
 *   public boolean isLocked()         { return sync.isHeldExclusively(); }
 *   public boolean hasQueuedThreads() { return sync.hasQueuedThreads(); }
 *   public void lockInterruptibly() throws InterruptedException {
 *     sync.acquireInterruptibly(1);
 *   }
 *   public boolean tryLock(long timeout, TimeUnit unit)
 *       throws InterruptedException {
 *     return sync.tryAcquireNanos(1, unit.toNanos(timeout));
 *   }
 * }}</pre>
 *
 * <p>Here is a latch class that is like a
 * {@link java.util.concurrent.CountDownLatch CountDownLatch}
 * except that it only requires a single {@code signal} to
 * fire. Because a latch is non-exclusive, it uses the {@code shared}
 * acquire and release methods.
 *
 *  <pre> {@code
 * class BooleanLatch {
 *
 *   private static class Sync extends concurrent.AbstractQueuedSynchronizer {
 *     boolean isSignalled() { return getState() != 0; }
 *
 *     protected int tryAcquireShared(int ignore) {
 *       return isSignalled() ? 1 : -1;
 *     }
 *
 *     protected boolean tryReleaseShared(int ignore) {
 *       setState(1);
 *       return true;
 *     }
 *   }
 *
 *   private final Sync sync = new Sync();
 *   public boolean isSignalled() { return sync.isSignalled(); }
 *   public void signal()         { sync.releaseShared(1); }
 *   public void await() throws InterruptedException {
 *     sync.acquireSharedInterruptibly(1);
 *   }
 * }}</pre>
 *
 * @since 1.5
 * @author Doug Lea
 */
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {

    private static final long serialVersionUID = 7373984972572414691L;

    //创建队列同步器，state初始值=0
    protected AbstractQueuedSynchronizer() { }

    /**
     * 等待线程队列 节点类 定义
     *
     * <p>The wait queue is a variant of a "CLH" (Craig, Landin, and
     * Hagersten) lock queue. CLH locks are normally used for
     * spinlocks.  We instead use them for blocking synchronizers, but
     * use the same basic tactic of holding some of the control
     * information about a thread in the predecessor of its node.  A
     * "status" field in each node keeps track of whether a thread
     * should block.  A node is signalled when its predecessor
     * releases.  Each node of the queue otherwise serves as a
     * specific-notification-style monitor holding a single waiting
     * thread. The status field does NOT control whether threads are
     * granted locks etc though.  A thread may try to acquire if it is
     * first in the queue. But being first does not guarantee success;
     * it only gives the right to contend.  So the currently released
     * contender thread may need to rewait.
     *
     * <p>To enqueue into a CLH lock, you atomically splice it in as new
     * tail. To dequeue, you just set the head field.
     * <pre>
     *      +------+  prev +-----+       +-----+
     * head |      | <---- |     | <---- |     |  tail
     *      +------+       +-----+       +-----+
     * </pre>
     *
     * <p>Insertion into a CLH queue requires only a single atomic
     * operation on "tail", so there is a simple atomic point of
     * demarcation from unqueued to queued. Similarly, dequeuing
     * involves only updating the "head". However, it takes a bit
     * more work for nodes to determine who their successors are,
     * in part to deal with possible cancellation due to timeouts
     * and interrupts.
     *
     * <p>The "prev" links (not used in original CLH locks), are mainly
     * needed to handle cancellation. If a node is cancelled, its
     * successor is (normally) relinked to a non-cancelled
     * predecessor. For explanation of similar mechanics in the case
     * of spin locks, see the papers by Scott and Scherer at
     * http://www.cs.rochester.edu/u/scott/synchronization/
     *
     * <p>We also use "next" links to implement blocking mechanics.
     * The thread id for each node is kept in its own node, so a
     * predecessor signals the next node to wake up by traversing
     * next link to determine which thread it is.  Determination of
     * successor must avoid races with newly queued nodes to set
     * the "next" fields of their predecessors.  This is solved
     * when necessary by checking backwards from the atomically
     * updated "tail" when a node's successor appears to be null.
     * (Or, said differently, the next-links are an optimization
     * so that we don't usually need a backward scan.)
     *
     * <p>Cancellation introduces some conservatism to the basic
     * algorithms.  Since we must poll for cancellation of other
     * nodes, we can miss noticing whether a cancelled node is
     * ahead or behind us. This is dealt with by always unparking
     * successors upon cancellation, allowing them to stabilize on
     * a new predecessor, unless we can identify an uncancelled
     * predecessor who will carry this responsibility.
     *
     * <p>CLH queues need a dummy header node to get started. But
     * we don't create them on construction, because it would be wasted
     * effort if there is never contention. Instead, the node
     * is constructed and head and tail pointers are set upon first
     * contention.
     *
     * <p>Threads waiting on Conditions use the same nodes, but
     * use an additional link. Conditions only need to link nodes
     * in simple (non-concurrent) linked queues because they are
     * only accessed when exclusively held.  Upon await, a node is
     * inserted into a condition queue.  Upon signal, the node is
     * transferred to the main queue.  A special value of status
     * field is used to mark which queue a node is on.
     *
     * <p>Thanks go to Dave Dice, Mark Moir, Victor Luchangco, Bill
     * Scherer and Michael Scott, along with members of JSR-166
     * expert group, for helpful ideas, discussions, and critiques
     * on the design of this class.
     */
    //等待线程队列 节点类 定义
    //等待队列/条件队列都使用此节点
    static final class Node {
        //共享/排它 模式 定义，创建等待节点时，构建器需要这2个参数之一
        static final Node SHARED = new Node();
        static final Node EXCLUSIVE = null;

        //枚举节点 等待状态 waitStatus 的值
        //表示 节点已被取消
        static final int CANCELLED =  1;

        //表示 节点的后继节点线程 需要 唤醒/启动unparking
        static final int SIGNAL    = -1;

        //表示 节点的线程正在等待一个条件
        static final int CONDITION = -2;

        //表示 下一次 acquireShared获取共享应该无条件地传播
        static final int PROPAGATE = -3;

        // 初始值0或CONDITION
        //非负值代表不需要发出信号
        // 可选值有以下
        // SIGNAL:-1表示 下个节点（或很快）被阻塞住，在本节点在取消（超时/中断）或释放时必须启动后继节点线程；
                //为避免竞争，acquire方法必须首先表明他们需要一个信号，然后重试原子acquire，然后失败就阻塞
        //CANCELLED:1,表示 节点因线程超时或中断而被取消。节点永远离不开这个状态，尤其取消状态的节点线程 不再 阻塞
        //CONDITION:-2 表示 在一个条件队列上的节点，不是同步队列节点。直到它被转移成同步队列节点，waitStatus设置成0（无其他用途）
        //PROPAGATE:-3 释放共享需要被传播到其他节点，在doReleaseShared方法内（只针对头节点）设置这个状态，
                // 以确保传播继续下去，即使其他操作已介入
        //内存可见性+写
        volatile int waitStatus;


        //指向上一个节点，当前节点线程需要检查上节点的waitStatus
        //入队时分配，出队时设置null，利于GC
        //短路查找上一个非取消状态的节点（这个节点永远存在，因为头节点不可能是取消节点：成功获得锁的节点成为头节点）
        //取消状态的节点永远不能成功获得锁，只有线程自身可以取消自己，而不是其他节点
        volatile Node prev;

        //指向当前节点/线程,调用释放方法时，需要唤醒unpark下个节点。入队时初始化，出队时置空
        // next等于null时，从队尾反过来又向检查
        //CANCELLED节点将next指向自己 为什么？保证将队列中间的CANCELLED节点还在isOnSyncQueue同步队列上
        volatile Node next;

        //本节点关联的线程
        volatile Thread thread;


        //* 条件线程队列后继节点 NODE.CONDITION，需要转移到等待队列并重新获取re-acquire
        //* 共享模式 NODE.SHARED
        //* 排它模式 NODE.EXCLUSIVE
        Node nextWaiter;


        //判断本节点是否共享 等待节点
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        //返回本节点的先驱节点，不存在抛出空指针
        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }
        //用于创建 初始化头节点 或 共享标记节点（）
        Node() {    // Used to establish initial head or SHARED marker
        }
        //用于添加等待节点，等待节点模式：排它/共享 2种
        Node(Thread thread, Node mode) {
            this.nextWaiter = mode;
            this.thread = thread;
        }
        //用于添加新节点到 条件队列
        Node(Thread thread, int waitStatus) {
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

    //等待队列的队头指针，延迟初始化，修改（初始化时+调用setHead方法时）头指针的等待状态保证不能是取消节点
    // （waitStatus!=Node.CANCELLED)
    private transient volatile Node head;


    //等待队列的队尾指针，延迟初始化，入队添加新节点时被修改
    private transient volatile Node tail;

    //定义同步状态
    private volatile int state;

    //返回当前的同步状态值，volatile内存记忆+写
    protected final int getState() {
        return state;
    }

    //设置当前的同步状态值，volatile内存可见+写
    protected final void setState(int newState) {
        state = newState;
    }

    //CAS 原子修改 同步状态state的值，如果内存值 与 期望值相同，则更新成功返回true，否则更新失败返回false
    protected final boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    // 队列工具方法

    /**
     * The number of nanoseconds for which it is faster to spin
     * rather than to use timed park. A rough estimate suffices
     * to improve responsiveness with very short timeouts.
     */
    //带超时纳秒时间的acquire方法，当超过下面设置的最大超时时间时，定时挂起线程
    static final long spinForTimeoutThreshold = 1000L;

    //插入节点，返回上个节点
    private Node enq(final Node node) {
        for (;;) {//自旋
            Node t = tail;
            if (t == null) { // 头节点在此处初始化，发现无头节点
                if (compareAndSetHead(new Node())) //多个线程并发，总有一个设置头节点会成功
                    tail = head;
            } else {//头节点存在后
                node.prev = t;
                if (compareAndSetTail(t, node)) {//多线程并发，每一次只有一个线程会成功，失败的线程自旋（继续循环）
                    t.next = node;//成功后，设置队列上个节点的next指向新节点
                    return t;//返回新添节点的上一个节点
                }
            }
        }
    }

    //根据给定模式（排它或共享），创建当前线程的等待节点并入队（先快速路径CAS入列，失败就自旋重试）
    //返回新建的节点
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        if (pred != null) {//确保当前队列至少有个头节点（刚开始head,tail指向同一个空节点）
            node.prev = pred;
            //CAS入队，有可能成功也有可能失败
            // 线程B执行到这里有可能成功：线程B进入此处（原因线程A创建了队头），但线程A还没将自身节点入队，此时线程B成功入队
            //也线程B有可能失败：线程B进到这里（原因线程A创建了队头），但线程A此时修改了队尾，导致线程B此处CASE失败
            if (compareAndSetTail(pred, node)) {
                pred.next = node;//设置队尾成功后，上个节点的next指向新加节点
                return node;
            }
        }
        enq(node);//进入自旋重试入队，for(;;)
        return node;//返回创建的节点
    }

    //acquire获锁方法调用，出队列时设置新头节点，即为了GC，清空原来非头节点，将它（非空节点）作为头节点
    //避免不必要的信号与遍历
    private void setHead(Node node) {
        head = node;
        node.thread = null;
        node.prev = null;//从队列中移除，isOnSyncQueue()通过此判断
    }

/*
ZZW
unparkSuccessor逻辑
1。将当前节点waitStatus(-1,-2)设为0
2。如果是队尾结点或后继节点是CANCELLED节点，从队尾反向找到第一个非CANCELLED唤醒。
3。反之唤醒后继结点线程

 */
    //唤醒给定等待节点的后继节点
    private void unparkSuccessor(Node node) {

        //可能是SIGNAL -2/ CANCELLED 1/ 0
        int ws = node.waitStatus;
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);

        //双向检查
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {//如果后继节点是CANCELLED或队尾
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)//反向从队尾
                if (t.waitStatus <= 0)//找到离队头最近的非CANCELLED节点// 0 -1 -2 -3
                    s = t;
        }
        if (s != null)//如果节点找到
            LockSupport.unpark(s.thread);//唤醒对应节点上线程
    }

    /**
     * Release action for shared mode -- signals successor and ensures
     * propagation. (Note: For exclusive mode, release just amounts
     * to calling unparkSuccessor of head if it needs signal.)
     */
    private void doReleaseShared() {
        // 尝试从队头head节点唤醒需要SIGNAL的后继节点(head.waitStatus=0)，如果不存在则设置队头head.waitStatus=PROPAGATE
        for (;;) {//防止操作时，有新加点加入，所以循环
            Node h = head;
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                if (ws == Node.SIGNAL) {
                    //如果队头为SIGNAL，并更新成功，unparkSuccessor队头后继节点
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0)) //修改队头waitStatus(SIGNAL->0),
                        continue;            // CAS失败重试
                    unparkSuccessor(h);//触发队头后继节点，让此循环不跳出
                }
                else if (ws == 0 &&
                         !compareAndSetWaitStatus(h, 0, Node.PROPAGATE)) //设置队头节点的waitStatus=PROPAGATE
                    continue;                // CAS失败重试
            }
            if (h == head)                   // 操作过程中，队头被修改过，重新循环
                break;
        }
    }

    /**
     * Sets head of queue, and checks if successor may be waiting
     * in shared mode, if so propagating if either propagate > 0 or
     * PROPAGATE status was set.
     *
     * @param node the node
     * @param propagate the return value from a tryAcquireShared
     */
    private void setHeadAndPropagate(Node node, int propagate) {
        Node h = head; // Record old head for check below
        setHead(node);
        /*
         * Try to signal next queued node if:
         *   Propagation was indicated by caller,
         *     or was recorded (as h.waitStatus either before
         *     or after setHead) by a previous operation
         *     (note: this uses sign-check of waitStatus because
         *      PROPAGATE status may transition to SIGNAL.)
         * and
         *   The next node is waiting in shared mode,
         *     or we don't know, because it appears null
         *
         * The conservatism in both of these checks may cause
         * unnecessary wake-ups, but only when there are multiple
         * racing acquires/releases, so most need signals now or soon
         * anyway.
         */
        //两次检查head和head.waitStatus //sign-check
        if (propagate > 0 || h == null || h.waitStatus < 0 ||
            (h = head) == null || h.waitStatus < 0) {//传播值>0或者当前队头且状态为PROPAGATE/SIGNAL
            Node s = node.next;
            if (s == null || s.isShared())//无等待节点或后继节点是共享节点
                doReleaseShared();//真正实现传播机制
        }
    }

    // Utilities for various versions of acquire


/*
ZZW
成为CANCELLED节点步骤
1。将绑定的线程引用=null，标记自己为CANCELLED
2。如果自己已经是队尾，从队列移除，并将前驱next字段置空
3。关联前驱非CANCELLED节点 到后驱非CANCELLED节点
4。如前/后驱非CANCELLED有一个为null，则调用unparkSuccessor唤醒下一个节点
5。将自身next字段 指向自己


 */
    //标记 尝试获取锁的失败节点为 CANCELLED
    private void cancelAcquire(Node node) {
        if (node == null)//判空，跳过
            return;

        node.thread = null;//GC

        Node pred = node.prev;//跳过CANCELLED节点，将当前节点的前驱指针指向"非"CANCELLED节点
        while (pred.waitStatus > 0)
            node.prev = pred = pred.prev;//pred此时是node的前驱，即  pred <- node

        // 一个明显不拼接在等待队列的节点predNext
        Node predNext = pred.next;

        //不需要通过CAS写，因为此处原子线程安全
        node.waitStatus = Node.CANCELLED;

        //队尾等待节点，从等待队列 中移除
        if (node == tail && compareAndSetTail(node, pred)) {//如果是node是队尾，直接队尾为前一个节点
            compareAndSetNext(pred, predNext, null);//将前一个节点的next置空
        } else {
            // If successor needs signal, try to set pred's next-link
            // so it will get one. Otherwise wake it up to propagate.
            int ws;
            if (pred != head &&
                ((ws = pred.waitStatus) == Node.SIGNAL ||
                 (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
                pred.thread != null) {//存在非头节点、非CANCELLED节点的waitStatus为SIGNAL时
                Node next = node.next;
                if (next != null && next.waitStatus <= 0)//如果取消节点的后继非CANCELLED节点，设置
                    compareAndSetNext(pred, predNext, next);//非CANCELLED前驱 指向非CANCELLED后继
            } else {//否则唤醒下一个节点
                unparkSuccessor(node);
            }

            node.next = node; // forGC，将要CANCELLED节点的next指向自己
        }
    }

    //判断前驱节点的waitStatus是否等于SIGNAL，返回TRUE说明可以当前节点node所属线程可被park挂起
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)//如果前驱节点是SIGNAL，直接返回
            return true;
        if (ws > 0) {
            //CANCELLED节点，找到SIGNAL前驱节点
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;//修改next指针，返回false
        } else {//waitStatus 为0或PROPAGATE
            //表示当前节点需要SIGNAL前驱，但自己还不打算挂起
            //调用者通过重试确保无法获得，才挂起
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);//CAS 尝试修改前驱节点为SIGNAL
        }
        return false;//除了前驱刚好是SIGNAL返回true,其他返回false
    }

    //当前线程 自我中断
    static void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    //挂起等待被 唤醒，返回中断标记+清空标记
    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
    //节点已经入同步队列，可能提入前队员，也可能条件结果入队
    //返回中断标记，中断或无中断唤醒
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;//标注是否需要清除节点，竞争失败的节点最后是true
        try {
            boolean interrupted = false;//标注是否竞争失败，队头指向输到它时，返回标记给调用者
            for (;;) {
                final Node p = node.predecessor();
                //多个线程访问到这里，只有当tryAcquire结果为true，且该线程对应节点刚好在队头，这个线程才能获得锁,其他线程都被标记中断
                if (p == head && tryAcquire(arg)) {//等待队列 头节点处理
                    setHead(node); //更新头节点指针（清空thread等字段），让其他未获得锁的线程竞争
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;//返回中断标记给调用者
                }
                // 非head头等待节点处理：当前驱是SIGNAL节点时，返回true;否则做调后返回false
                //非head头等待节点经过for (;;)多轮调用shouldParkAfterFailedAcquire，将所有非头节点挂起
                //总结：
                // 1.阻塞等待唤醒【前驱是SIGNAL返回TRUE，唤醒后标记中断情况】
                // 2.继续循环【找到前驱非CANCELLED节点，next指向node ,如SIGNAL-->--CANCELED-->--node】
                // 3.继续循环 将前驱状态改成SIGNAL，前驱可能是0或PROPAGATE
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt()) //执行挂起+清除中断标记,等待Release相关方法来唤醒
                    //被 线程本身自我中断selfInterrupt()-> Thread.currentThread().interrupt()
                    interrupted = true;
            }
        } finally {
            if (failed)//此处有必要？？
                //failed局部变量线程安全
                // 挂起的线程，受中断后interrupted=true，但还是处于for(;;)里边
                // 正常执行返回时failed=false，不会进入此处
                cancelAcquire(node);//标记成CANCELLED节点
        }
    }

    //可被中断排它模式
    private void doAcquireInterruptibly(int arg)
        throws InterruptedException {
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    throw new InterruptedException();//多了一个中断异常，如果线程被中断，逻辑交给线程处理
            }
        } finally {
            if (failed)//线程被中断
                cancelAcquire(node);
        }
    }

    //带时间排它模式
    private boolean doAcquireNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (nanosTimeout <= 0L)
            return false;
        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return true;
                }
                nanosTimeout = deadline - System.nanoTime();
                if (nanosTimeout <= 0L) //超时返回false
                    return false;
                if (shouldParkAfterFailedAcquire(p, node) &&
                    nanosTimeout > spinForTimeoutThreshold)//nanosTimeout 大于 自旋超时阀值，定时挂起，唤醒继续循环
                    LockSupport.parkNanos(this, nanosTimeout);
                if (Thread.interrupted()) //被中断抛出中断异常
                    throw new InterruptedException();
            }
        } finally {
            if (failed)//超时/中断处理
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared uninterruptible mode.
     * @param arg the acquire argument
     */
    private void doAcquireShared(int arg) {
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);//排它锁暴露接口的返回值是一个整型变量
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        if (interrupted)
                            selfInterrupt();
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)//同问？
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared interruptible mode.
     * @param arg the acquire argument
     */
    private void doAcquireSharedInterruptibly(int arg)
        throws InterruptedException {
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)//中断时处理
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared timed mode.
     *
     * @param arg the acquire argument
     * @param nanosTimeout max wait time
     * @return {@code true} if acquired
     */
    private boolean doAcquireSharedNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (nanosTimeout <= 0L)
            return false;
        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return true;
                    }
                }
                nanosTimeout = deadline - System.nanoTime();
                if (nanosTimeout <= 0L)
                    return false;
                if (shouldParkAfterFailedAcquire(p, node) &&
                    nanosTimeout > spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if (Thread.interrupted())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)//中断/超时处理
                cancelAcquire(node);
        }
    }

    // 主要暴露的方法

    /**
     * 在排它模式下，尝试获取. 查询对象的许可state状态是否可以被获取。
     *
     * <p>This method is always invoked by the thread performing
     * acquire.  If this method reports failure, the acquire method
     * may queue the thread, if it is not already queued, until it is
     * signalled by a release from some other thread. This can be used
     * to implement method {@link Lock#tryLock()}.
     *
     * <p>The default
     * implementation throws {@link UnsupportedOperationException}.
     *
     * @param arg the acquire argument. This value is always the one
     *        passed to an acquire method, or is the value saved on entry
     *        to a condition wait.  The value is otherwise uninterpreted
     *        and can represent anything you like.
     * @return {@code true} if successful. Upon success, this object has
     *         been acquired.
     * @throws IllegalMonitorStateException if acquiring would place this
     *         synchronizer in an illegal state. This exception must be
     *         thrown in a consistent fashion for synchronization to work
     *         correctly.
     * @throws UnsupportedOperationException if exclusive mode is not supported
     */
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in exclusive
     * mode.
     *
     * <p>This method is always invoked by the thread performing release.
     *
     * <p>The default implementation throws
     * {@link UnsupportedOperationException}.
     *
     * @param arg the release argument. This value is always the one
     *        passed to a release method, or the current state value upon
     *        entry to a condition wait.  The value is otherwise
     *        uninterpreted and can represent anything you like.
     * @return {@code true} if this object is now in a fully released
     *         state, so that any waiting threads may attempt to acquire;
     *         and {@code false} otherwise.
     * @throws IllegalMonitorStateException if releasing would place this
     *         synchronizer in an illegal state. This exception must be
     *         thrown in a consistent fashion for synchronization to work
     *         correctly.
     * @throws UnsupportedOperationException if exclusive mode is not supported
     */
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to acquire in shared mode. This method should query if
     * the state of the object permits it to be acquired in the shared
     * mode, and if so to acquire it.
     *
     * <p>This method is always invoked by the thread performing
     * acquire.  If this method reports failure, the acquire method
     * may queue the thread, if it is not already queued, until it is
     * signalled by a release from some other thread.
     *
     * <p>The default implementation throws {@link
     * UnsupportedOperationException}.
     *
     * @param arg the acquire argument. This value is always the one
     *        passed to an acquire method, or is the value saved on entry
     *        to a condition wait.  The value is otherwise uninterpreted
     *        and can represent anything you like.
     * @return a negative value on failure; zero if acquisition in shared
     *         mode succeeded but no subsequent shared-mode acquire can
     *         succeed; and a positive value if acquisition in shared
     *         mode succeeded and subsequent shared-mode acquires might
     *         also succeed, in which case a subsequent waiting thread
     *         must check availability. (Support for three different
     *         return values enables this method to be used in contexts
     *         where acquires only sometimes act exclusively.)  Upon
     *         success, this object has been acquired.
     * @throws IllegalMonitorStateException if acquiring would place this
     *         synchronizer in an illegal state. This exception must be
     *         thrown in a consistent fashion for synchronization to work
     *         correctly.
     * @throws UnsupportedOperationException if shared mode is not supported
     */
    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in shared mode.
     *
     * <p>This method is always invoked by the thread performing release.
     *
     * <p>The default implementation throws
     * {@link UnsupportedOperationException}.
     *
     * @param arg the release argument. This value is always the one
     *        passed to a release method, or the current state value upon
     *        entry to a condition wait.  The value is otherwise
     *        uninterpreted and can represent anything you like.
     * @return {@code true} if this release of shared mode may permit a
     *         waiting acquire (shared or exclusive) to succeed; and
     *         {@code false} otherwise
     * @throws IllegalMonitorStateException if releasing would place this
     *         synchronizer in an illegal state. This exception must be
     *         thrown in a consistent fashion for synchronization to work
     *         correctly.
     * @throws UnsupportedOperationException if shared mode is not supported
     */
    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if synchronization is held exclusively with
     * respect to the current (calling) thread.  This method is invoked
     * upon each call to a non-waiting {@link ConditionObject} method.
     * (Waiting methods instead invoke {@link #release}.)
     *
     * <p>The default implementation throws {@link
     * UnsupportedOperationException}. This method is invoked
     * internally only within {@link ConditionObject} methods, so need
     * not be defined if conditions are not used.
     *
     * @return {@code true} if synchronization is held exclusively;
     *         {@code false} otherwise
     * @throws UnsupportedOperationException if conditions are not supported
     */
    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }

    //提供给AQS子类调用
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
    //提供给AQS子类调用
    //tryAcquire尝试获取失败，就调用doAcquireInterruptibly
    public final void acquireInterruptibly(int arg)
            throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();//如果线程被中断，抛出中断异常
        if (!tryAcquire(arg))
            doAcquireInterruptibly(arg);
    }
    //提供给AQS子类调用
    public final boolean tryAcquireNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();//如果当前线程被中断，清除标记+抛出异常
        return tryAcquire(arg) ||
            doAcquireNanos(arg, nanosTimeout);
    }
    //提供给AQS子类调用
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)//排它模式，等待队头被处理后，队头后继状态基本上SIGNAL/CANCELLED
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
    //提供给AQS子类调用
    public final void acquireShared(int arg) {
        if (tryAcquireShared(arg) < 0)
            doAcquireShared(arg);
    }
    //提供给AQS子类调用
    public final void acquireSharedInterruptibly(int arg)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (tryAcquireShared(arg) < 0)
            doAcquireSharedInterruptibly(arg);
    }
    //提供给AQS子类调用
    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquireShared(arg) >= 0 ||
            doAcquireSharedNanos(arg, nanosTimeout);
    }
    //提供给AQS子类调用
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }

    // 队列检查方法Queue inspection methods

    //不可靠，查看等待队列是否还有线程在等待
    public final boolean hasQueuedThreads() {
        return head != tail;
    }

    //判断是否曾经有线程要获取这把同步器锁，说明队列清空后，头节点还在。
    public final boolean hasContended() {
        return head != null;
    }

    //获取队头线程
    public final Thread getFirstQueuedThread() {
        // handle only fast path, else relay
        return (head == tail) ? null : fullGetFirstQueuedThread();
    }

    //获取队列第一个线程
    private Thread fullGetFirstQueuedThread() {
        Node h, s;
        Thread st;
        //判断队头不为空，有第一个等待结果，反向第一节点前驱是队头，第一节点的线程不会空
        //并发的原因，两次检查
        if (((h = head) != null && (s = h.next) != null &&
             s.prev == head && (st = s.thread) != null) ||
            ((h = head) != null && (s = h.next) != null &&
             s.prev == head && (st = s.thread) != null))
            return st;

        //从队尾查找第一个线程
        Node t = tail;
        Thread firstThread = null;
        while (t != null && t != head) {
            Thread tt = t.thread;
            if (tt != null)
                firstThread = tt;
            t = t.prev;
        }
        return firstThread;
    }

    //判断给定线程是否入队列了，给null抛出空指针
    public final boolean isQueued(Thread thread) {
        if (thread == null)
            throw new NullPointerException();
        for (Node p = tail; p != null; p = p.prev)
            if (p.thread == thread)
                return true;
        return false;
    }

    /**
     * Returns {@code true} if the apparent first queued thread, if one
     * exists, is waiting in exclusive mode.  If this method returns
     * {@code true}, and the current thread is attempting to acquire in
     * shared mode (that is, this method is invoked from {@link
     * #tryAcquireShared}) then it is guaranteed that the current thread
     * is not the first queued thread.  Used only as a heuristic in
     * concurrent.ReentrantReadWriteLock.
     */
    //判断 第一个等待节点是否排它线程
    final boolean apparentlyFirstQueuedIsExclusive() {
        Node h, s;
        return (h = head) != null &&
            (s = h.next)  != null &&
            !s.isShared()         &&
            s.thread != null;
    }

    /**
     * Queries whether any threads have been waiting to acquire longer
     * than the current thread.
     *
     * <p>An invocation of this method is equivalent to (but may be
     * more efficient than):
     *  <pre> {@code
     * getFirstQueuedThread() != Thread.currentThread() &&
     * hasQueuedThreads()}</pre>
     *
     * <p>Note that because cancellations due to interrupts and
     * timeouts may occur at any time, a {@code true} return does not
     * guarantee that some other thread will acquire before the current
     * thread.  Likewise, it is possible for another thread to win a
     * race to enqueue after this method has returned {@code false},
     * due to the queue being empty.
     *
     * <p>This method is designed to be used by a fair synchronizer to
     * avoid <a href="concurrent.AbstractQueuedSynchronizer#barging">barging</a>.
     * Such a synchronizer's {@link #tryAcquire} method should return
     * {@code false}, and its {@link #tryAcquireShared} method should
     * return a negative value, if this method returns {@code true}
     * (unless this is a reentrant acquire).  For example, the {@code
     * tryAcquire} method for a fair, reentrant, exclusive mode
     * synchronizer might look like this:
     *
     *  <pre> {@code
     * protected boolean tryAcquire(int arg) {
     *   if (isHeldExclusively()) {
     *     // A reentrant acquire; increment hold count
     *     return true;
     *   } else if (hasQueuedPredecessors()) {
     *     return false;
     *   } else {
     *     // try to acquire normally
     *   }
     * }}</pre>
     *
     * @return {@code true} if there is a queued thread preceding the
     *         current thread, and {@code false} if the current thread
     *         is at the head of the queue or the queue is empty
     * @since 1.7
     */
    //判断当前线程是否有前驱等待线程
    public final boolean hasQueuedPredecessors() {
        // The correctness of this depends on head being initialized
        // before tail and on head.next being accurate if the current
        // thread is first in queue.
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t &&
            ((s = h.next) == null || s.thread != Thread.currentThread());
    }


    // Instrumentation and monitoring methods

    //不可靠，评估队列线程数
    public final int getQueueLength() {
        int n = 0;
        for (Node p = tail; p != null; p = p.prev) {
            if (p.thread != null)
                ++n;
        }
        return n;
    }

    //不可靠，评估 获取当前队列集合
    public final Collection<Thread> getQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            Thread t = p.thread;
            if (t != null)
                list.add(t);
        }
        return list;
    }

    //获取排它线程集合
    public final Collection<Thread> getExclusiveQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (!p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    //获取共享线程集合
    public final Collection<Thread> getSharedQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    //显示当前status，和队列是否空的情况
    public String toString() {
        int s = getState();
        String q  = hasQueuedThreads() ? "non" : "";
        return super.toString() +
            "[State = " + s + ", " + q + "empty queue]";
    }


    // Internal support methods for Conditions

    /**
     * Returns true if a node, always one that was initially placed on
     * a condition queue, is now waiting to reacquire on sync queue.
     * @param node the node
     * @return true if is reacquiring
     */
    final boolean isOnSyncQueue(Node node) {
        if (node.waitStatus == Node.CONDITION || node.prev == null)//条件CONDITION节点或头节点返
            return false;
        if (node.next != null) // next不为空，返回true
            return true;

        return findNodeFromTail(node);
    }

    //从队尾查找给定的节点，找到返回true，反之false
    private boolean findNodeFromTail(Node node) {
        Node t = tail;
        for (;;) {
            if (t == node)
                return true;
            if (t == null)
                return false;
            t = t.prev;
        }
    }

    /**
     * Transfers a node from a condition queue onto sync queue.
     * Returns true if successful.
     * @param node the node
     * @return true if successfully transferred (else the node was
     * cancelled before signal)
     */
    final boolean transferForSignal(Node node) {

        //返回false,过滤掉因中断/超时在await方法时提前入同步队列的节点
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;

        //拼接进等待队列，返回前驱p
        Node p = enq(node);
        int ws = p.waitStatus;
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
            //前驱是CANCELLED或
            // 无法修改成SIGNAL(说明前驱节点当前正在被处理中)直接唤醒自己
            LockSupport.unpark(node.thread);//就唤醒本节点
        return true;
    }

    /**
     * Transfers node, if necessary, to sync queue after a cancelled wait.
     * Returns true if thread was cancelled before being signalled.
     *
     * @param node the node
     * @return true if cancelled before the node was signalled
     */
    //取消等待 之后 再做 转移
    final boolean transferAfterCancelledWait(Node node) {
        if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            enq(node);//修改节点waitStatus CONDITION->0
            return true;
        }
        //没有signal唤醒处理，必须要等到入同步队列后才能返回
        //在未完成signal转移（很少出现）时，取消等待
        //所以要自旋循环
        while (!isOnSyncQueue(node))//循环保证node入队成功后，才返回false
            Thread.yield();
        return false;
    }

    final int fullyRelease(Node node) {
        boolean failed = true;
        try {
            int savedState = getState();
            if (release(savedState)) {
                failed = false;
                return savedState;
            } else {
                throw new IllegalMonitorStateException();
            }
        } finally {
            if (failed)
                node.waitStatus = Node.CANCELLED;
        }
    }

    // 关于条件的 检测方法Instrumentation methods for conditions


    //判断给定条件对象是否使用 当前队列同步器类 作为锁
    public final boolean owns(ConditionObject condition) {
        return condition.isOwnedBy(this);
    }


    //查询给定条件对象ConditionObject上是否存在CONDITION等待节点
    public final boolean hasWaiters(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.hasWaiters();
    }

    //获取给定ConditionObject条件对象的等待结点长度
    public final int getWaitQueueLength(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitQueueLength();
    }

    //获取给定条件对象ConditionObject上的等待线程集合
    public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitingThreads();
    }

    //条件对象类 定义 ZZW
    public class ConditionObject implements Condition, java.io.Serializable {
        private static final long serialVersionUID = 1173984872572414699L;
        //同步队列队头节点
        private transient Node firstWaiter;
        //同步队列队尾节点
        private transient Node lastWaiter;

        public ConditionObject() { }

        // 内部方法

        /**
         * Adds a new waiter to wait queue.
         * @return its new wait node
         */
        private Node addConditionWaiter() {
            Node t = lastWaiter;
            // 最后一个等待结果为CANCELLED时，清除掉
            if (t != null && t.waitStatus != Node.CONDITION) { //0
                unlinkCancelledWaiters();
                t = lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), Node.CONDITION);//不像同步队列有个空队头
            if (t == null)
                firstWaiter = node;
            else
                t.nextWaiter = node;
            lastWaiter = node;
            return node;
        }

        /**
         * Removes and transfers nodes until hit non-cancelled one or
         * null. Split out from signal in part to encourage compilers
         * to inline the case of no waiters.
         * @param first (non-null) the first node on condition queue
         */
        private void doSignal(Node first) {
            do {
                if ( (firstWaiter = first.nextWaiter) == null)
                    lastWaiter = null;
                first.nextWaiter = null;
            } while (!transferForSignal(first) &&
                     (first = firstWaiter) != null);
        }

        /**
         * Removes and transfers all nodes.
         * @param first (non-null) the first node on condition queue
         */
        private void doSignalAll(Node first) {
            lastWaiter = firstWaiter = null;
            do {//将整个条件队列 ，每个节点从队列中逐个做转移 transferForSignal
                Node next = first.nextWaiter;
                first.nextWaiter = null;//从条件队列 移出
                transferForSignal(first);
                first = next;//指针修改为后继节点
            } while (first != null);
        }

        //清除队列中CANCELLED节点
        private void unlinkCancelledWaiters() {
            Node t = firstWaiter;
            Node trail = null;
            while (t != null) {
                Node next = t.nextWaiter;
                if (t.waitStatus != Node.CONDITION) {//CANCELLED节点
                    t.nextWaiter = null;
                    if (trail == null)
                        firstWaiter = next;
                    else
                        trail.nextWaiter = next;
                    if (next == null)
                        lastWaiter = trail;
                }
                else
                    trail = t;
                t = next;
            }
        }

        // 公开方法public methods

        /**
         * Moves the longest-waiting thread, if one exists, from the
         * wait queue for this condition to the wait queue for the
         * owning lock.
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        public final void signal() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;
            if (first != null)
                doSignal(first);
        }

        /**
         * Moves all threads from the wait queue for this condition to
         * the wait queue for the owning lock.
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        public final void signalAll() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;
            if (first != null)
                doSignalAll(first);
        }

        /**
         * Implements uninterruptible condition wait.
         * <ol>
         * <li> Save lock state returned by {@link #getState}.
         * <li> Invoke {@link #release} with saved state as argument,
         *      throwing IllegalMonitorStateException if it fails.
         * <li> Block until signalled.
         * <li> Reacquire by invoking specialized version of
         *      {@link #acquire} with saved state as argument.
         * </ol>
         */
        public final void awaitUninterruptibly() {
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean interrupted = false;
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if (Thread.interrupted())
                    interrupted = true;
            }
            if (acquireQueued(node, savedState) || interrupted)
                selfInterrupt();
        }


        //中断返回类型枚举
        private static final int REINTERRUPT =  1;
        private static final int THROW_IE    = -1;

        //中断时，尝试将节点入同步队列
        private int checkInterruptWhileWaiting(Node node) {
            return Thread.interrupted() ?
                (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) :
                0;
        }

        //中断异常处理
        private void reportInterruptAfterWait(int interruptMode)
            throws InterruptedException {
            if (interruptMode == THROW_IE)
                throw new InterruptedException();
            else if (interruptMode == REINTERRUPT)
                selfInterrupt();
        }


        public final void await() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);//释放同步队列前面的线程
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {//等待CONDITION节点进行队列，即condition.signal触发
                LockSupport.park(this);//等待被 唤醒
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)//中断时跳出，节点尝试入列
                    //interruptMode = THROW_IE 说明 是await此处线程被唤醒 并先于signal将node加入同步节点，抛出异常
                    //interruptMode = REINTERRUPT 说明由signal修改了waitStatus，合理，最后通过reportInterruptAfterWait实现自我中断
                    break;
            }
            //节点已经在同步队列上，判断是否轮到自己线程节点，可能被挂起
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null) // 同步队列无后继节点，清空CANCELLED(waitStatus=0)的节点
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);//中断标记的处理，自我中断还是抛出中断异常
        }

        public final long awaitNanos(long nanosTimeout)
                throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            final long deadline = System.nanoTime() + nanosTimeout;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {//跳出必要保证条件节点已经在同步队列上
                if (nanosTimeout <= 0L) {
                    transferAfterCancelledWait(node);//超时插入节点或确保节点在同步队列上，取消等待
                    break;
                }
                if (nanosTimeout >= spinForTimeoutThreshold)//超过阀值，定时挂起
                    LockSupport.parkNanos(this, nanosTimeout);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)//中断处理
                    break;
                nanosTimeout = deadline - System.nanoTime();
            }
            //节点已经在同步队列上，判断是否轮到自己线程节点，可能被挂起
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return deadline - System.nanoTime();
        }

        public final boolean awaitUntil(Date deadline)
                throws InterruptedException {
            long abstime = deadline.getTime();
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean timedout = false;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (System.currentTimeMillis() > abstime) {
                    timedout = transferAfterCancelledWait(node);//超时处理
                    break;
                }
                LockSupport.parkUntil(this, abstime);//挂起到指定时间唤醒
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            //节点在同步队列上，判断是否轮到自己,以便移动队列head指针,或再次挂起
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return !timedout;
        }

        public final boolean await(long time, TimeUnit unit)
                throws InterruptedException {
            long nanosTimeout = unit.toNanos(time);
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            final long deadline = System.nanoTime() + nanosTimeout;
            boolean timedout = false;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (nanosTimeout <= 0L) {
                    timedout = transferAfterCancelledWait(node);//超时处理，入列
                    break;
                }
                if (nanosTimeout >= spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);//超过最大阀值，挂起
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)//中断处理，入列
                    break;
                nanosTimeout = deadline - System.nanoTime();
            }
            //节点在同步队列上，判断是否轮到自己，以便移动队列head指针
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return !timedout;
        }

        //  support for instrumentation

        //判断 给定同步器是否当前ConditionObject的父亲实例
        final boolean isOwnedBy(AbstractQueuedSynchronizer sync) {
            return sync == AbstractQueuedSynchronizer.this;
        }


        //不可靠，判断条件队列是否有等待节点
        protected final boolean hasWaiters() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION)
                    return true;
            }
            return false;
        }

        //不可靠，评估获取条件队列的等待结点数
        protected final int getWaitQueueLength() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            int n = 0;
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION)
                    ++n;
            }
            return n;
        }

        //获取条件队列的等待线程集合
        protected final Collection<Thread> getWaitingThreads() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            ArrayList<Thread> list = new ArrayList<Thread>();
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION) {
                    Thread t = w.thread;
                    if (t != null)
                        list.add(t);
                }
            }
            return list;
        }
    }

    //提升性能，利用JVM内在API，本地化实现原子CAS
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            stateOffset = unsafe.objectFieldOffset
                (AbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset
                (AbstractQueuedSynchronizer.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset
                (AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset
                (Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset
                (Node.class.getDeclaredField("next"));

        } catch (Exception ex) { throw new Error(ex); }
    }

    //CAS 修改节点的head指针,入队时初始化创建头节点
    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }
    //CAS 修改节点的tail指针,入队时使用
    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }
    //CAS 修改节点的waitStatus字段
    private static final boolean compareAndSetWaitStatus(Node node,int expect,int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset,expect, update);
    }
    //CAS 修改节点的next指针
    private static final boolean compareAndSetNext(Node node, Node expect,Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }
}