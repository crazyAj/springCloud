package com.example.demo.extra.thread;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * ReentrantLock 重入锁
 *      效果和synchronized一样，都可以同步执行，lock方法获得锁，unlock方法释放锁
 *      await方法：通过创建Condition对象来使线程wait，必须先执行lock.lock方法获得锁
 *      signal方法：condition对象的signal方法可以唤醒wait线程
 */
public class ReentrantLock {

    private Lock lock = new java.util.concurrent.locks.ReentrantLock();
    private Condition condition = lock.newCondition();

    @Test
    public void reentrantLock() {
        ExecutorService executorService = null;
        List<Future<String>> list = new ArrayList<>();
        try {
            executorService = Executors.newFixedThreadPool(2);
            for(int i=0; i<2; i++) {
                Thread.sleep(1000);
                final int t = i;
                list.add(executorService.submit(() -> {
                    if (t == 0)
                        testLock();
                    else
                        testLock2();
                    return "SUCCESS";
                }));
            }
            for(Future<String> future:list) {
                future.get();
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("------- e ------- " + e);
        }
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            executorService.shutdown();
        }
    }

    public void testLock() throws Exception {
        System.out.println("----- 1 1 ---- " + Thread.currentThread());
        lock.lock();
        try {
            System.out.println("----- 1 2 ---- " + Thread.currentThread());
            condition.await();
            System.out.println("----- 1 3 ---- " + Thread.currentThread());
        }finally{
            System.out.println("1 sleep 1s");
            Thread.sleep(1000);
            System.out.println("----- 1 4 ---- " + Thread.currentThread());
            lock.unlock();
            System.out.println("----- 1 5 ---- " + Thread.currentThread());
        }
    }

    public void testLock2() throws Exception {
        System.out.println("----- 2 1 ---- " + Thread.currentThread());
        lock.lock();
        try {
            String str = null;
            str.length();
            System.out.println("----- 2 2 ---- " + Thread.currentThread());
        }finally{
            System.out.println("2 sleep 1s");
            Thread.sleep(1000);
            System.out.println("----- 2 3 ---- " + Thread.currentThread());
            condition.signal();
            System.out.println("----- 2 4 ---- " + Thread.currentThread());
            lock.unlock();
            System.out.println("----- 2 5 ---- " + Thread.currentThread());
        }
    }

}



