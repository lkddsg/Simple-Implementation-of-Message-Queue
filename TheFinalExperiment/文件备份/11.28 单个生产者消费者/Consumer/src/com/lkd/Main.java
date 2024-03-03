package com.lkd;

public class Main {
    public static void main(String[] args) {
        Consumer consumer1=new Consumer(1);
        Thread consumerThread1=new Thread(consumer1);
//        Consumer consumer2=new Consumer(2);
//        Thread consumerThread2=new Thread(consumer2);
//        Consumer consumer3=new Consumer(3);
//        Thread consumerThread3=new Thread(consumer3);
//        Consumer consumer4=new Consumer(4);
//        Thread consumerThread4=new Thread(consumer4);
        consumerThread1.start();
//        consumerThread2.start();
//        consumerThread3.start();
//        consumerThread4.start();
    }
}
