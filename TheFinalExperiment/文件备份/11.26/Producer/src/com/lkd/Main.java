package com.lkd;

public class Main {
    public static int IDcnt=1;
    public static void main(String[] args) throws InterruptedException {
        Producer producer1=new Producer();
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
}
