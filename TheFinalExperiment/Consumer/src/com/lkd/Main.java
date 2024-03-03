package com.lkd;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static final String  PERSISTENCE_TIMELINE="consumerTimeline.txt";//时间线
    public static Path pathTimeLine = Paths.get(PERSISTENCE_TIMELINE);
    public static void main(String[] args) {
        System.out.println("请输入您选的编号");
        Scanner sc=new Scanner(System.in);
        int bian=sc.nextInt();
        Consumer consumer1=new Consumer(bian);
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
