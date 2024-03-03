package com.lkd;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalTime;
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
    private int producerID;
    boolean accidentClosedFlag=false;
    InputStream is=null;
    BufferedReader br =null;
    boolean sendFlag=true;

    public Producer(int producerID) {
        this.producerID=producerID;
    }

    @Override
    public void run() {

        int i=0;
        try {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (producerSendSocket != null || !producerSendSocket.isClosed()) {
                        pw.println("生产者"+producerID+"建立的连接已主动关闭");
                        pw.flush();
                        sendFlag=false;
                        producerSendSocket.close(); // 关闭 Socket 连接
                        System.out.println("生产者"+producerID+"建立的连接已主动关闭");
                        if(os!=null)
                            os.close();
                        if(osw!=null)
                            osw.close();
                        if(pw!=null)
                            pw.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            producerSendSocket =new Socket("localhost",33333);
            os=producerSendSocket.getOutputStream();//字字节liu
            osw=new OutputStreamWriter(os,"UTF_16LE");//字符解码为字节流
            pw=new PrintWriter(osw);//写入字符
            Runnable receiver = () -> {// 接收线程
                try {
                    // 初始化接收服务器信息的 BufferedReader
                    is = producerSendSocket.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16LE));
                    String receivedMessage;
                    while ((receivedMessage = br.readLine()) != null) {
                        System.out.println("Received from server: " + receivedMessage);
                        String[] parts = receivedMessage.split(" ");
                        if(parts[0].equals("end"))
                        {
                            sendFlag=false;
                            break;
                        }
                        // 根据接收到的信息进行相应处理
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // 关闭连接或资源释放等操作
                    try {
                        if (br != null) br.close();
                        if (is != null) is.close();
                        if (producerSendSocket != null) producerSendSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread receiverThread = new Thread(receiver);
            receiverThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true)
        {
try {
    int sendID=-1;
    //判断项目开始时间
//    if(Main.getIDcnt()==1)
//    {
        if(!Files.exists(Main.pathTimeLine))
        {
            try {
                Files.createFile(Main.pathTimeLine);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        long startTime=System.currentTimeMillis();
        String stringStartTime=String.valueOf(startTime);
        try (FileWriter fileWriter = new FileWriter(Main.PERSISTENCE_TIMELINE)) {
            fileWriter.write("开始时间"+stringStartTime);
            fileWriter.write("\n"); // 添加空行
            System.out.println("Content with a new line has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //初始判断
//    }
    for (i = 0; i < 500000; i++) {
        if(sendFlag==true)
        {
            randNumData = 2000000000 + (Math.abs(rand.nextInt(1000000)));
//            randNumData=i;
            randTagNum=rand.nextInt(4);//生成0 1 2 3
            if(randTagNum==0)
            {
                randTag="我是零";
            }
            else if(randTagNum==1)
            {
                randTag="我是一";
            }
            else if(randTagNum==2)
            {
                randTag="我是二";
            } else if (randTagNum==3) {
                randTag="我是三";
            }
//                StructureMessage structureMessage=new StructureMessage(Main.IDcnt,randTag,randNumData);
            sendID=Main.getIDcnt();//锁住取得

            String producerSendMessage="send "+sendID+" "+randTag+" "+randNumData;
            Main.incrementIDcnt();//锁住自增
            pw.println(producerSendMessage);
            pw.flush();
//            Main.IDcnt++;
            System.out.println(producerSendMessage+"这是i当前的数值"+i);
//                try {
//                    Thread.sleep(20000);
//                }
//                catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
        }
        else if(sendFlag==false)
        {
            break;
        }


    }
    if(sendFlag==false||producerSendSocket.isClosed()||producerSendSocket==null)
    {
        System.out.println("求求你了真的别再发了");
        break;
    }
    if(i>=249997||sendFlag==false)
    {
        System.out.println("生产者"+producerID+"已经生产完数据或者中途暂停了");
        break;
    }

} catch (Exception e) {
    throw new RuntimeException(e);
}
        break;//结束发送
        }


    }
}

