package com.lkd;

import jdk.nashorn.internal.ir.Symbol;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    boolean canAlwaysTake=false;//这是持续永久发送的判断，假定为不能取

    public Consumer(int consumerID) {
        this.consumerID=consumerID;
    }

    @Override
    public void run() {
        {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (consumerSocket != null && !consumerSocket.isClosed()) {
                        pw.println("消费者端"+consumerID+" 已自行关闭Socket连接");
                        pw.flush();
                        consumerSocket.close(); // 关闭 Socket 连接
                        System.out.println("消费者端"+consumerID+" 已自行关闭Socket连接");
                    }
                    if(is!=null)
                        is.close();
                    if(br!=null)
                        br.close();
                    if(os!=null)
                        os.close();
                    if(osw!=null)
                        osw.close();
                    if(pw!=null)
                        pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            while (true)
            {
                try {
                    System.out.println("第"+consumerID+"个消费者提醒：请输入您的操作");
                    Scanner sc=new Scanner(System.in);
                    String myInput=sc.nextLine();
                    String[] sendParts = myInput.split(" ");
                    try {
                        if (consumerSocket != null && !consumerSocket.isClosed()) {
                            pw.println("消费者端"+consumerID+" 已自行关闭Socket连接");
                            pw.flush();
                            consumerSocket.close(); // 关闭 Socket 连接
                            System.out.println("消费者端"+consumerID+" 已自行关闭Socket连接");
                        }
                        if(is!=null)
                            is.close();
                        if(br!=null)
                            br.close();
                        if(os!=null)
                            os.close();
                        if(osw!=null)
                            osw.close();
                        if(pw!=null)
                            pw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    consumerSocket=new Socket("localhost",33333);
                    is=consumerSocket.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16LE));
                    os=consumerSocket.getOutputStream();//字字节liu
                    osw=new OutputStreamWriter(os,"UTF_16LE");//字符解码为字节流
                    pw=new PrintWriter(osw);//写入字符
                    //订阅：(consumerID) subscribe tag
                    //取走：take Tag tag待定
                    //结束socket连接：end socket

                    if(sendParts[0].equals("subscribe"))
                    {
                        myInput=sendParts[0]+" "+this.consumerID+" "+sendParts[1];
                    }
                    else if(sendParts[0].equals("take"))
                    {
                        if(sendParts.length==3)//这是自己决定有多少个拉取
                        myInput=sendParts[0]+" "+this.consumerID+" "+sendParts[1]+" "+sendParts[2];
                        else if(sendParts.length==2)//这是一直拉取
                        {
                            myInput=sendParts[0]+" "+this.consumerID+" "+sendParts[1];
                            canAlwaysTake=true;
                        }
                    }
                    else //错误输入
                    {
                        System.out.println("您的输入不符规范！请重新输入符合标准的输入！");
                        continue;
                    }
//                    int myInput=5;
                    //暂时还没把输入请求改善完毕
                    String consumerRequest=myInput;
                    pw.println(consumerRequest);
                    pw.flush();
//                     String consumerRequestTake="take "+myInput;
                    while (true)
                    {
//                        if(consumerSocket.isClosed())
//                        {
//                            System.out.println("检测到服务器好像意外停止了");
//                        }
                        String response;
                        boolean nextInFlag=false;
                        // 读取字符流并在控制台输出
                        while ((response = br.readLine()) != null) {
                            System.out.println("第"+consumerID+"消费者接收到的数据：" + response);
                            String[] receviedParts = response.split(" ");
                            if(receviedParts.length>=3)//收到了正常的需要判断信息
                            {
                                if(canAlwaysTake!=true)//此为判断是否持续拉取，不是，而是接受批量信息
                                {
                                    cnt=JudgePrime(Integer.parseInt(receviedParts[2]));
                                    if(cnt>=Integer.parseInt(sendParts[2]))
                                    {
                                        System.out.println("这一组判断结束");
                                        nextInFlag=true;
                                        if(Integer.parseInt((receviedParts[0]))==1000000)
                                        {
                                            System.out.println("已完成最后一个的判断");
                                            long startTime=System.currentTimeMillis();
                                            String stringStartTime=String.valueOf(startTime);
                                            System.out.println("结束时间"+stringStartTime);
                                        }
                                        break;//判断结束，进入下一次接受批量信息
                                    }
                                }
                        else if (canAlwaysTake==true)//我要一直拉取
                                {
                                    cnt=JudgePrime(Integer.parseInt(receviedParts[2]));
                                    if(Integer.parseInt((receviedParts[0]))==1000000)
                                    {
                                        System.out.println("已完成最后一个的判断");
                                        if(Integer.parseInt((receviedParts[0]))==1000000)
                                        {
                                            System.out.println("已完成最后一个的判断");
                                            long startTime=System.currentTimeMillis();
                                            String stringStartTime=String.valueOf(startTime);
                                            System.out.println("结束时间"+stringStartTime);
                                        }
                                    }
                                    break;
                                }

                            }
                            else {//我接收到了异常信息
                                nextInFlag=true;
                                System.out.println("接受到异常信息");
                                break;
                            }
                            }
                        if(nextInFlag==true)break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    if (consumerSocket != null && !consumerSocket.isClosed()) {
                        pw.println("消费者端"+consumerID+" 已自行关闭Socket连接");
                        pw.flush();
                        consumerSocket.close(); // 关闭 Socket 连接
                        System.out.println("消费者端"+consumerID+" 已自行关闭Socket连接");
                    }
                    if(is!=null)
                        is.close();
                    if(br!=null)
                        br.close();
                    if(os!=null)
                        os.close();
                    if(osw!=null)
                        osw.close();
                    if(pw!=null)
                        pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
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

