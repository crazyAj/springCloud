package com.example.demo.extra.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 模拟死锁
 */
public class DidThread {

    private String a = "a";
    private String b = "b";

    /**
     * join 子线程加入到主线程上，主线程等待子线程结束
     */
    public void join() throws InterruptedException {
        Thread thread = new Thread(() -> {
            synchronized (a) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (b) {
                    System.out.println(">>> 111");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (b) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (a) {
                    System.out.println(">>> 222");
                }
            }
        });

        //启动线程
        thread.start();
        thread2.start();

        //子线程加入主线程，等待子线程完成
        thread.join();
        thread2.join();
    }

    /**
     * 计数锁，当 CountDownlatch 减到0，线程继续运行
     */
    public void countDownLatch() throws InterruptedException {
        //分配计数锁
        final CountDownLatch count = new CountDownLatch(2);

        Thread thread = new Thread(() -> {
            synchronized (a) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (b) {
                    System.out.println(">>> 111");
                }
            }

            //锁减一
            count.countDown();
        });

        Thread thread2 = new Thread(() -> {
            synchronized (b) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (a) {
                    System.out.println(">>> 222");
                }
            }

            //锁减一
            count.countDown();
        });

        //启动线程
        thread.start();
        thread2.start();

        //等待计数锁为0
        count.await();
    }

    /**
     * 珊栏锁，当所有线程到达时，才放行
     */
    public void cyclicBarrier() throws InterruptedException {
        //设置计数珊栏
        final CyclicBarrier barrier = new CyclicBarrier(3);

        Thread thread = new Thread(() -> {
            synchronized (a) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (b) {
                    System.out.println(">>> 111");
                }

                //等待所有线程处理完毕
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (b) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (a) {
                    System.out.println(">>> 222");
                }
            }

            //等待所有线程处理完毕
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });

        //启动线程
        thread.start();
        thread2.start();

        //等待所有线程处理完毕
        try {
            barrier.await();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}

