package pool;

public interface Pool<T> {
    void init(int maxConnections,long maxWaitMillis);
    T get() throws Exception;
    void release(T t);
}
