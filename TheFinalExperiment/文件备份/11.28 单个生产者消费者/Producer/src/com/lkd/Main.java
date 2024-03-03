package com.lkd;

public class Main {
    public static int IDcnt=1;
    private static final Object lock = new Object();
    public static void main(String[] args) throws InterruptedException {
        Producer producer1=new Producer(1);
        Thread producerThread=new Thread(producer1);
        producerThread.start();
//        Producer producer2=new Producer();
//        Thread producerThread2=new Thread(producer2);
//        producerThread2.start();
//        for(int i=0;i<5000000;i++)
//        {
//Thread.sleep(2000);
//        }
    }
    public static int getIDcnt() {
        synchronized (lock) { // 使用synchronized确保对IDcnt的安全访问
            return IDcnt;
        }
    }

    // 提供一个静态方法用于修改IDcnt的值
    public static void incrementIDcnt() {
        synchronized (lock) { // 使用synchronized确保对IDcnt的安全访问
            IDcnt++;
        }
    }
}
