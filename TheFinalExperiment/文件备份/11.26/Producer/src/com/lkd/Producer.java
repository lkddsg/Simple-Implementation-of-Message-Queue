package com.lkd;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Producer implements Runnable{
    public static Random rand = new Random();
    public static int randNumData = 0;
    public static int randTagNum=0;
    public static String randTag=null;
    Socket producerSendSocket=null;
    OutputStream os=null;
    PrintWriter pw=null;
    OutputStreamWriter osw =null;
    @Override
    public void run() {
        try {
            producerSendSocket =new Socket("localhost",33333);
            os=producerSendSocket.getOutputStream();//字字节liu
            osw=new OutputStreamWriter(os,"UTF_16LE");//字符解码为字节流
            pw=new PrintWriter(osw);//写入字符
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true)
        {

            for (int i = 0; i < 500000; i++) {

//                randNum = 2000000000 + (Math.abs(rand.nextInt(1000000)));
            randNumData=i;
                randTagNum=rand.nextInt(3);//生成0 1 2
                if(randTagNum==0)
                {
                    randTag="我是零";
                }
                else if(randTagNum==1)
                {
                    randTag="我是一";
                }
                if(randTagNum==2)
                {
                    randTag="我是二";
                }
//                StructureMessage structureMessage=new StructureMessage(Main.IDcnt,randTag,randNumData);
                String producerSendMessage="send "+Main.IDcnt+" "+randTag+" "+randNumData;
                pw.println(producerSendMessage);
                pw.flush();
                Main.IDcnt++;
                System.out.println(producerSendMessage);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }


    }
}

