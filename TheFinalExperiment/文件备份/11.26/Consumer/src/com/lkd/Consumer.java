package com.lkd;

import jdk.nashorn.internal.ir.Symbol;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class Consumer implements Runnable {
    private Socket consumerSocket=null;
    private int consumerID=0;
    int cnt=0;
    int[] myIntBuffer=new int[1024];
    OutputStream os=null;
    PrintWriter pw=null;
    OutputStreamWriter osw =null;
    InputStream is=null;
    byte[] data=null;
    BufferedReader br=null;
    boolean canTake=false;//假定为不能取

    public Consumer(int consumerID) {
        this.consumerID=consumerID;
    }

    @Override
    public void run() {
        {
            try {
                consumerSocket=new Socket("localhost",33333);
                is=consumerSocket.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16LE));
                os=consumerSocket.getOutputStream();//字字节liu
                osw=new OutputStreamWriter(os,"UTF_16LE");//字符解码为字节流
                pw=new PrintWriter(osw);//写入字符
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (true)
            {
                try {
                    System.out.println("第"+consumerID+"个消费者提醒：请输入您的操作");
                    //订阅：(consumerID) subscribe tag
                    //取走：take Tag tag待定
                    //结束socket连接：end socket
                    Scanner sc=new Scanner(System.in);
                    String myInput=sc.nextLine();
                    String[] sendParts = myInput.split(" ");
                    if(sendParts[0].equals("subscribe"))
                    {
                        myInput=sendParts[0]+" "+this.consumerID+" "+sendParts[1];
                    }
                    else if(sendParts[0].equals("take"))
                    {
                        myInput=sendParts[0]+" "+this.consumerID+" "+sendParts[1]+" "+sendParts[2];
                    }
//                    int myInput=5;
                    //暂时还没把输入请求改善完毕
                    String consumerRequest=myInput;
                    pw.println(consumerRequest);
                    pw.flush();
//                     String consumerRequestTake="take "+myInput;
                    while (true)
                    {
                        String response;
                        boolean nextInFlag=false;
                        // 读取字符流并在控制台输出
                        while ((response = br.readLine()) != null) {
                            System.out.println("第"+consumerID+"消费者接收到的数据：" + response);
                            String[] receviedParts = response.split(" ");
                            if(receviedParts.length>=3)
                            {
                                cnt=JudgePrime(Integer.parseInt(receviedParts[2]));
                                if(cnt>=Integer.parseInt(sendParts[2]))
                                {
                                    System.out.println("这一组判断结束");
                                    nextInFlag=true;
                                    break;
                                }
                            }
                            else {
                                nextInFlag=true;
                                break;
                            }
                            }
                        if(nextInFlag==true)break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }
    }
    private int JudgePrime(int judgeNumber)
    {

        boolean isPrime=true;
        for(int j=2;j<judgeNumber/j;j++)
        {
            if(judgeNumber%j==0)
            {
                isPrime=false;
                break;
            }
        }
        if(isPrime)
        {
            System.out.println("第"+consumerID+"消费者判断："+judgeNumber+"是素数");
        }
        else
        {
            System.out.println("第"+consumerID+"消费者判断："+judgeNumber+"不是素数");
        }
        cnt++;
        return cnt;
    }
}

