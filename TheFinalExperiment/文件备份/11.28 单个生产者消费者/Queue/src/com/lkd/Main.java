package com.lkd;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static final String  PERSISTENCE_SUBSCRIBERELATION="subscribeRelationQueue.txt";//包含用户ID和订阅Tag
    public static final String PERSISTENCE_FILEZERO = "message_queueZero.txt"; // 文件名
    public static final String PERSISTENCE_FILEONE = "message_queueOne.txt"; // 文件名
    public static final String PERSISTENCE_FILETWO = "message_queueTwo.txt"; // 文件名
    public static final String PERSISTENCE_FILETTHREE = "message_queueThree.txt"; // 文件名
    public static LinkedBlockingQueue<StructureSubscribeRelation> subscribeRelationQueue=new LinkedBlockingQueue<>();
   public static LinkedBlockingQueue<StructureMessage> myQueue=new LinkedBlockingQueue<>();
    public static LinkedBlockingQueue<StructureMessage> myQueueZero=new LinkedBlockingQueue<>();
    public static LinkedBlockingQueue<StructureMessage> myQueueOne=new LinkedBlockingQueue<>();
    public static LinkedBlockingQueue<StructureMessage> myQueueTwo=new LinkedBlockingQueue<>();
    public static LinkedBlockingQueue<StructureMessage> myQueueThree=new LinkedBlockingQueue<>();
   public static Path pathZero= Paths.get(PERSISTENCE_FILEZERO);
    public static   Path pathOne= Paths.get(PERSISTENCE_FILEONE);
    public static   Path pathTwo= Paths.get(PERSISTENCE_FILETWO);
    public static   Path pathThree= Paths.get(PERSISTENCE_FILETTHREE);
    public static   Path pathSubscribe= Paths.get(PERSISTENCE_SUBSCRIBERELATION);
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(!Files.exists(pathZero))
            {
                try {
                    Files.createFile(pathZero);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            ClientHandler.saveStructureMessageQueueToFile(myQueueZero,PERSISTENCE_FILEZERO);

            if(!Files.exists(pathOne))
            {
                try {
                    Files.createFile(pathOne);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            ClientHandler.saveStructureMessageQueueToFile(myQueueOne,PERSISTENCE_FILEONE);

            if(!Files.exists(pathTwo))
            {
                try {
                    Files.createFile(pathTwo);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            ClientHandler.saveStructureMessageQueueToFile(myQueueTwo,PERSISTENCE_FILETWO);

            if(!Files.exists(pathThree))
            {
                try {
                    Files.createFile(pathThree);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            ClientHandler.saveStructureMessageQueueToFile(myQueueThree,PERSISTENCE_FILETTHREE);
            if(!Files.exists(pathSubscribe))
            {
                try {
                    Files.createFile(pathSubscribe);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            ClientHandler.saveStructureSubscribeRelationQueueToFile(subscribeRelationQueue,PERSISTENCE_SUBSCRIBERELATION);
            System.out.println("已执行完保存");

        }));
        {

            if(Files.exists(pathZero))
            ClientHandler.readStructureMessageQueueToFile(myQueueZero,PERSISTENCE_FILEZERO);//读取
            if(Files.exists(pathOne))
            ClientHandler.readStructureMessageQueueToFile(myQueueOne,PERSISTENCE_FILEONE);//读取
            if(Files.exists(pathTwo))
            ClientHandler.readStructureMessageQueueToFile(myQueueTwo,PERSISTENCE_FILETWO);//读取
            if(Files.exists(pathThree))
                ClientHandler.readStructureMessageQueueToFile(myQueueThree,PERSISTENCE_FILETTHREE);//读取
            if(Files.exists(pathSubscribe))
            ClientHandler.readStructureSubscribeRelationQueueToFile(subscribeRelationQueue,PERSISTENCE_SUBSCRIBERELATION);
            System.out.println("已执行完读取");
        }
//        Iterator<StructureMessage> iterator = myQueue.iterator();//测试读取
//        while (iterator.hasNext()) {
//            StructureMessage message = iterator.next();
//            // 处理 message，可以输出、处理等操作
//            System.out.println(message.getID()+" "+message.getTag()+" "+message.getData());
//        }
        int port = 33333;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread =new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//初步设定：如果是收到数据，发过来的开头为send；若是发出数据，则要队列发出send

//线程设定①welcome线程，监听是否有连接。
//②收取数据线程，如果是send就要收
//③发送数据线程，如果是take就要发送
class ClientHandler implements Runnable {
    private Socket clientSocket;
    OutputStream os = null;
    PrintWriter pw = null;
    InputStreamReader isr = null;
    OutputStreamWriter osw = null;
    BufferedReader br = null;
    DataInputStream dis = null;
    ByteArrayInputStream byteArrayInputStream = null;

    InputStream is = null;

    DataInputStream dataInputStream = null;
    BufferedReader reader = null;
    String[] parts = null;

    // 存储分割后的不同部分到不同的字符串中
    String command = null;
    String ID = null;
    String tag = null;
    String data = null;

    byte byteData[] = new byte[65536];

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("程序即将关闭，执行清理工作...");
            // 在这里编写需要在程序关闭时执行的代码，比如关闭资源、保存数据等
            if (clientSocket != null && !clientSocket.isClosed()) {
                try {
                    pw.println("end accidently");
                    pw.flush();
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
                    clientSocket.close(); // 关闭 Socket 连接
                    System.out.println("Socket连接已关闭");
                    if(!clientSocket.isClosed())
                    {
                        System.out.println("其实并没有关闭！");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }));
        try {

            is = clientSocket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16LE));
            os = clientSocket.getOutputStream();//字字节liu
            osw = new OutputStreamWriter(os, "UTF_16LE");//字符解码为字节流
            pw = new PrintWriter(osw);//写入字符
            String request;
            // 读取字符流并在控制台输出
            while ((request = reader.readLine()) != null) {
                System.out.println("接收到的数据：" + request);
//                System.out.println("接受到消息"+request+",截取内容0-4:"+request.substring(0,4)+",5:"+request.substring(5));
                String responseTypeSend = "send";//如果接受到的是send，就要往队列里放
                String responseTypeTake = "take";//如果接受到的是take，就要一次性取走给人家
                String responseTypeSubscribe= "subscribe";


                String[] parts = request.split(" ");
//                int partsLength= parts.length;
//                System.out.println("收到的字符串后，放入数组数组大小为"+partsLength);
                // 存储分割后的不同部分到不同的字符串中
                command = parts[0];

//                receivedInt=Integer.parseInt((request.substring(5)).trim());
                if (command.equals(responseTypeSend)) {
                    //此为接受生产者信息
                    Main.myQueue.put(new StructureMessage(ID, tag, data));
                    ID = parts[1];
                    tag = parts[2];
                    data = parts[3];
                    if(tag.equals("我是零"))
                    {
                        Main.myQueueZero.put(new StructureMessage(ID, tag, data));
                    }
                    else if(tag.equals("我是一"))
                    {
                        Main.myQueueOne.put(new StructureMessage(ID, tag, data));
                    }
                    else if(tag.equals("我是二"))
                    {
                        Main.myQueueTwo.put(new StructureMessage(ID, tag, data));
                    }
                    else if(tag.equals("我是三"))
                    {
                        Main.myQueueThree.put(new StructureMessage(ID, tag, data));
                    }

//                    saveIntegerToFile(receivedInt);
                }
                //一下部分属于与消费者的交互
                else if (command.equals(responseTypeTake)) {
//先处理主动的批量读取
                    //先判定是否已经提前订阅
                    String messageSend=null;
                    int receivedInt = 0;
                    String receivedType=null;

                    receivedType=parts[2];
                    ID = parts[1];
                    if(parts.length==4)
                    {//这是拿多少信息
                        receivedInt = Integer.parseInt(parts[3].trim());
                    }
                    boolean flagSubscribeMessage=false;
                    Iterator<StructureSubscribeRelation> iterator = Main.subscribeRelationQueue.iterator();//测试读取
                    while (iterator.hasNext()) {
                        StructureSubscribeRelation message = iterator.next();
                        System.out.println("正在遍历队列：  "+"这一用户"+message.getConsumerID()+"已订阅"+message.getConsumerTag());
                        // 处理 message，可以输出、处理等操作
//                        if(message.getConsumerID().equals("1"))System.out.println("这个对应字符串1");
//                        if(message.getConsumerTag().equals("我是零"))System.out.println("这个对应字符串我是零");
//                        System.out.println(message.getConsumerID()+" "+message.getConsumerTag());
                        if(message.getConsumerID().equals(ID)&&message.getConsumerTag().equals(receivedType))
                        {
                            flagSubscribeMessage=true;//确实订阅了，放给你吧
                            System.out.println("这一用户"+message.getConsumerID()+"已订阅"+message.getConsumerTag());
                            break;
                        }

                    }

                    if(flagSubscribeMessage)//确实有订阅，那么接下来要进行判断发什么了
                    {
                        if(parts.length==4)//用户自行输入有多少个
                        {
                            List<StructureMessage> elements = new ArrayList<>();


                            System.out.println("进入我是零推送判断");
                            if(receivedType.equals("我是零")&&Main.myQueueZero.size()>=receivedInt)
                            {//做判定，如果没这么多，那就不取了
                                System.out.println("接下来取元素零");
                                Main.myQueueZero.drainTo(elements, receivedInt);
                                System.out.println("取出的元素为:");
                                for (StructureMessage element : elements) {
                                    System.out.println(element.getmessageID() + " " + element.getTag() + " " + element.getData());
                                    messageSend = element.getmessageID() + " " + element.getTag() + " " + element.getData();
                                    pw.println(messageSend);
                                    pw.flush();
                                }
                            }
                            else if(receivedType.equals("我是零")&&Main.myQueueZero.size()<receivedInt)
                            {
                                messageSend="您订阅的我是零内容数量并没有那么多！请稍候再申请！";
                                pw.println(messageSend);
                                pw.flush();
                            }
                            if(receivedType.equals("我是一")&&Main.myQueueOne.size()>=receivedInt)
                            {//做判定，如果没这么多，那就不取了
                                System.out.println("接下来取元素一");
                                Main.myQueueOne.drainTo(elements, receivedInt);
                                System.out.println("取出的元素为:");
                                for (StructureMessage element : elements) {
                                    System.out.println(element.getmessageID() + " " + element.getTag() + " " + element.getData());
                                    messageSend = element.getmessageID() + " " + element.getTag() + " " + element.getData();
                                    pw.println(messageSend);
                                    pw.flush();
                                }
                            }
                            else if(receivedType.equals("我是一")&&Main.myQueueOne.size()<receivedInt)
                            {
                                messageSend="您订阅的我是一内容数量并没有那么多！请稍候再申请！";
                                pw.println(messageSend);
                                pw.flush();
                            }
                            if(receivedType.equals("我是二")&&Main.myQueueTwo.size()>=receivedInt)
                            {//做判定，如果没这么多，那就不取了
                                System.out.println("接下来取元素二");
                                Main.myQueueTwo.drainTo(elements, receivedInt);
                                System.out.println("取出的元素为:");
                                for (StructureMessage element : elements) {
                                    System.out.println(element.getmessageID() + " " + element.getTag() + " " + element.getData());
                                    messageSend = element.getmessageID() + " " + element.getTag() + " " + element.getData();
                                    pw.println(messageSend);
                                    pw.flush();
                                }
                            }
                            else if(receivedType.equals("我是二")&&Main.myQueueTwo.size()<receivedInt)
                            {
                                messageSend="您订阅的我是二内容数量并没有那么多！请稍候再申请！";
                                pw.println(messageSend);
                                pw.flush();
                            }

                            if(receivedType.equals("我是三")&&Main.myQueueThree.size()>=receivedInt)
                            {//做判定，如果没这么多，那就不取了
                                System.out.println("接下来取元素三");
                                Main.myQueueThree.drainTo(elements, receivedInt);
                                System.out.println("取出的元素为:");
                                for (StructureMessage element : elements) {
                                    System.out.println(element.getmessageID() + " " + element.getTag() + " " + element.getData());
                                    messageSend = element.getmessageID() + " " + element.getTag() + " " + element.getData();
                                    pw.println(messageSend);
                                    pw.flush();
                                }
                            }
                            else if(receivedType.equals("我是三")&&Main.myQueueThree.size()<receivedInt)
                            {
                                messageSend="您订阅的我是三内容数量并没有那么多！请稍候再申请！";
                                pw.println(messageSend);
                                pw.flush();
                            }
                            //首先应该判断是要取出哪一个队列！！！！！！！！！！！！！！！注意！！！！！！！！！！！！！
                            //回来做！
                            // 假设您想要一次性取出 5 个元素
                        }
                        else if(parts.length==3)//用户想要一直不断地接受订阅信息
                        {
                            //处理元素零
                            Thread consumerThread = new Thread(() -> {
                                while (true) {
                                    try {
                                        ID=parts[1];
                                        tag=parts[2];
                                        if(clientSocket==null||clientSocket.isClosed())
                                        {
                                            //如果关闭了，那么退出循环

                                            System.out.println("用户"+ID+"对"+tag+"订阅的连接已关闭");
                                            break;
                                        }
                                        // 检查队列是否为空，如果不为空，则取出元素
                                        if (!Main.myQueueZero.isEmpty()&&tag.equals("我是零")) {
                                            System.out.println("接下来取元素零");
                                            StructureMessage message = Main.myQueueZero.take();
                                            // 对取出的元素进行处理
                                            // 这里可以根据具体需求对 message 进行操作
                                            System.out.println("取出的元素零为: " + message.getmessageID() + " "+message.getTag()+" "+message.getData());
                                            String myMessageSend=null;
                                            myMessageSend=message.getmessageID() + " "+message.getTag()+" "+message.getData();
                                            pw.println(myMessageSend);
                                            pw.flush();
                                        }
                                        else if (!Main.myQueueOne.isEmpty()&&tag.equals("我是一")) {
                                            System.out.println("接下来取元素一");
                                            StructureMessage message = Main.myQueueOne.take();
                                            // 对取出的元素进行处理
                                            // 这里可以根据具体需求对 message 进行操作
                                            System.out.println("取出的元素一为: " + message.getmessageID() + " "+message.getTag()+" "+message.getData());
                                            String myMessageSend=null;
                                            myMessageSend=message.getmessageID() + " "+message.getTag()+" "+message.getData();
                                            pw.println(myMessageSend);
                                            pw.flush();
                                        }
                                        else if (!Main.myQueueTwo.isEmpty()&&tag.equals("我是二")) {
                                            System.out.println("接下来取元素二");
                                            StructureMessage message = Main.myQueueTwo.take();
                                            // 对取出的元素进行处理
                                            // 这里可以根据具体需求对 message 进行操作
                                            System.out.println("取出的元素二为: " + message.getmessageID() + " "+message.getTag()+" "+message.getData());
                                            String myMessageSend=null;
                                            myMessageSend=message.getmessageID() + " "+message.getTag()+" "+message.getData();
                                            pw.println(myMessageSend);
                                            pw.flush();
                                        }
                                        else if (!Main.myQueueThree.isEmpty()&&tag.equals("我是三")) {
                                            System.out.println("接下来取元素三");
                                            StructureMessage message = Main.myQueueThree.take();
                                            // 对取出的元素进行处理
                                            // 这里可以根据具体需求对 message 进行操作
                                            System.out.println("取出的元素三为: " + message.getmessageID() + " "+message.getTag()+" "+message.getData());
                                            String myMessageSend=null;
                                            myMessageSend=message.getmessageID() + " "+message.getTag()+" "+message.getData();
                                            pw.println(myMessageSend);
                                            pw.flush();
                                        }
                                    } catch (InterruptedException e) {
                                        // 处理可能出现的中断异常
                                        e.printStackTrace();
                                    }
                                }
                            });

                            // 启动消费者线程
                            consumerThread.start();
                        }




                    }
                    else {
                        messageSend="有用户试图取出没有订阅的内容！";
                        System.out.println("有用户试图取出没有订阅的内容！");
                        pw.println(messageSend);
                        pw.flush();
//                        System.out.println("这一用户"+message.getConsumerID()+"没有订阅"+message.getConsumerTag());
                    }


                } else if (command.equals(responseTypeSubscribe)) {
                    //处理订阅
                    ID = parts[1];
                    tag = parts[2];
//                    data = parts[3];
                    //先检查是否已经订阅
                    boolean flagPutInMessage=true;
                    Iterator<StructureSubscribeRelation> iterator = Main.subscribeRelationQueue.iterator();//测试读取
                    while (iterator.hasNext()) {
                        StructureSubscribeRelation message = iterator.next();
                        // 处理 message，可以输出、处理等操作
//                        System.out.println(message.getConsumerID()+" "+message.getConsumerTag());
                        if(message.getConsumerID().equals(ID)&&message.getConsumerTag().equals(tag))
                        {
                            flagPutInMessage=false;//找到了重的，那么就无需再放入
                            System.out.println("已经有订阅，无需再订阅");
                            pw.println("已经有订阅，无需再订阅");
                            pw.flush();
                        }
                    }
                    if(flagPutInMessage)//没重复，放
                    {
                        Main.subscribeRelationQueue.put(new StructureSubscribeRelation(ID,tag));//放进
                        System.out.println("用户"+ID+"已经订阅"+tag+"成功");
                        pw.println("用户"+ID+"已经订阅"+tag+"成功");
                        pw.flush();
                    }


                }
            }
        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);

        }

        catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (osw != null)
                    osw.close();
                if (pw != null)
                    pw.close();
                if (reader != null)
                    reader.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    public static void saveStructureMessageQueueToFile(LinkedBlockingQueue<StructureMessage> messageQueue,String FilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FilePath))) {

            for (StructureMessage message : messageQueue) {
                writer.write(message.getmessageID() + " " + message.getTag() + " " + message.getData());
                writer.newLine();
            }
        } catch (IOException e) {
            // 处理文件写入异常
            e.printStackTrace();
        }
    }
    public static void saveStructureSubscribeRelationQueueToFile(LinkedBlockingQueue<StructureSubscribeRelation> messageQueue,String FilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FilePath))) {

            for (StructureSubscribeRelation message : messageQueue) {
                writer.write(message.getConsumerID()+ " " + message.getConsumerTag());
                writer.newLine();
            }
        } catch (IOException e) {
            // 处理文件写入异常
            e.printStackTrace();
        }
    }


public static void readStructureSubscribeRelationQueueToFile(LinkedBlockingQueue<StructureSubscribeRelation> messageQueue,String filePath) {
    try {
        // 创建 BufferedReader 读取文件
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // 读取文件信息
        String line;
        while ((line = reader.readLine()) != null) {
            // 处理每一行的内容

            // 在这里可以对读取的内容进行处理，存储到数据结构中等操作
            String[] parts = line.split(" ");

            // 存储分割后的不同部分到不同的字符串中


//                receivedInt=Integer.parseInt((request.substring(5)).trim());

            String ID = parts[0];
            String tag = parts[1];
//            String data = parts[2];
            messageQueue.put(new StructureSubscribeRelation(ID,tag));
            System.out.println("订阅用户内容："+ID+" "+tag);

        }

        // 关闭文件流
        reader.close();
    } catch (IOException e) {
        // 处理异常
        e.printStackTrace();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
public static void readStructureMessageQueueToFile(LinkedBlockingQueue<StructureMessage> messageQueue,String filePath) {
        try {
            // 创建 BufferedReader 读取文件
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            // 读取文件信息
            String line;
            while ((line = reader.readLine()) != null) {
                // 处理每一行的内容
                System.out.println(line);
                // 在这里可以对读取的内容进行处理，存储到数据结构中等操作
                String[] parts = line.split(" ");

                // 存储分割后的不同部分到不同的字符串中


//                receivedInt=Integer.parseInt((request.substring(5)).trim());

                   String ID = parts[0];
                   String tag = parts[1];
                    String data = parts[2];
                    messageQueue.put(new StructureMessage(ID, tag, data));

            }

            // 关闭文件流
            reader.close();
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
