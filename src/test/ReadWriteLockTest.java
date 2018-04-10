import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.IntStream;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class ReadWriteLockTest {
    @Test
    public void reentrantLock() throws InterruptedException {
        Lock reentrantLock = new ReentrantLock();
        Thread thread1 = new Thread(() -> {
            reentrantLock.lock();
            for (int i = 0; i < 10; i++) {
                System.out.println("thread1：" + i);
            }
            reentrantLock.unlock();
        });
        Thread thread2 = new Thread(() -> {
            reentrantLock.lock();
            System.out.println("thread2");
            reentrantLock.unlock();
        });
        thread1.start();
        thread2.start();
        Thread.sleep(1000);
    }

    @Test
    public void reentrantReadWriteLock() throws InterruptedException {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        ReadLock readLock = reentrantReadWriteLock.readLock();
        Thread thread1 = new Thread(() -> {
            readLock.lock();
            for (int j = 0; j < 10; j++) {
                System.out.println("read:" + j);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            readLock.unlock();
        });
        WriteLock writeLock = reentrantReadWriteLock.writeLock();
        Thread thread2 = new Thread(() -> {
            writeLock.lock();
            for (int j = 0; j < 10; j++) {
                System.out.println("write1:" + j);
            }
            writeLock.unlock();
        });

        IntStream.range(0,1000).boxed().forEach(i->{
            Thread thread3 = new Thread(() -> {
                writeLock.lock();
                System.out.println(Thread.currentThread().getName());
                writeLock.unlock();
            });
            thread3.setName("thread"+i);
            thread3.start();
        });
        Thread.sleep(100000);
    }
}
