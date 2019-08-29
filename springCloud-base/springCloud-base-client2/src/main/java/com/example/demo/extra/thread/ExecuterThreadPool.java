package com.example.demo.extra.thread;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  四种线程池的使用
 *      ExecutorService executorService = Executors.newFixedThreadPool(n);
 *      创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
 *
 *      ExecutorService executorService = Executors.newCachedThreadPool();
 *      创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
 *
 *      ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(n);
 *      创建一个定长线程池，支持定时及周期性任务执行。
 *
 *      ExecutorService executor = Executors.newSingleThreadExecutor();
 *      创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，相当于线程lock.lock()
 *
 *      1. Callable接口
 *          a. 实现call()方法
 *          b. 返回一个泛型值，方法无返回值则可自定义一个返回值
 *          c. Future<T>.get()方法，如果没有获取到任务的结果，则会一直阻塞等待结果返回
 *          d. 需要通过Future<T>.get()方法，进行捕获异常
 *
 *      2. Runnable接口
 *          a. 实现run()方法
 *          b. 无返回值
 *          c. 只能在run()方法内部进行异常处理，不可以在外部捕获异常
 *          d. Thread只支持Runnable
 *
 *      3. Future<T>接口
 *          a. 方法
 *               get            获得Callable的返回值
 *               cancel         取消Callable的执行，当Callable还没有完成时
 *               isCanceled     判断是否取消了
 *               isDone         判断是否完成
 *          b. executorService.submit()  返回Future<T>类型
 *             executorService.execute() 无返回值
 */
public class ExecuterThreadPool {

    private Lock lock = new ReentrantLock();

    /**
     * ExecutorService executorService = Executors.newFixedThreadPool(n);
     * 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
     */
    @Test
    public void fixedThreadPool() {
        List<Future<String>> futures = new ArrayList<>();
        ExecutorService executorService = null;
        try {
            executorService = Executors.newFixedThreadPool(2);
            for (int i = 0; i < 3; i++) {
                futures.add(executorService.submit(() -> run()));
            }
            for (Future<String> future:futures) {
                System.out.println(future.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            executorService.shutdown();
        }
    }

    /**
     * ExecutorService executorService = Executors.newCachedThreadPool();
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
     */
    @Test
    public void cachedThreadPool(){
        List<Future<String>> futures = new ArrayList<>();
        ExecutorService executorService = null;
        try {
            executorService = Executors.newCachedThreadPool();
            for (int i=0;i<3;i++){
                futures.add(executorService.submit(()->run()));
            }
            for(Future<String> future:futures){
                System.out.println(future.get());
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            executorService.shutdown();
        }
    }

    /**
     * ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(n);
     * 创建一个定长线程池，支持定时及周期性任务执行。
     */
    @Test
    public void sheduledThreadPool(){
        ScheduledExecutorService scheduledExecutorService = null;
        try{
            scheduledExecutorService = Executors.newScheduledThreadPool(3);

            //延迟1s后执行
            scheduledExecutorService.schedule(() -> run(), 1, TimeUnit.SECONDS);
            //延迟5s后每隔1s执行一次
            scheduledExecutorService.scheduleAtFixedRate(()->run(),5,1,TimeUnit.SECONDS);

            Thread.sleep(15000);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            scheduledExecutorService.shutdown();
        }
    }

    /**
     * ExecutorService executor = Executors.newSingleThreadExecutor();
     * 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，相当于线程lock.lock()
     */
    @Test
    public void singleThreadExecutor(){
        ExecutorService executorService = null;
        try {
            executorService = Executors.newSingleThreadExecutor();

            for(int i=0;i<3;i++){
                executorService.submit(() -> run());
            }
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            executorService.shutdown();
        }
    }

    private String run(){
        try {
            System.out.println("---------- 1 --- " + Thread.currentThread().getName());
            Thread.sleep(1000);
            lock.lock();
            System.out.println("---------- 2");
            Thread.sleep(1000);
            System.out.println("---------- 3");
            Thread.sleep(1000);
            lock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return "OK";
        }
    }

}



