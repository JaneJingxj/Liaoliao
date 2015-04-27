package com.xxj.liaoliao;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * smack客户端的开发步骤
 * 1.创建XMPPTCPConnection 连接
 * 2.用户登录
 * 3.添加联系人
 */
public class ChatService extends Service {
    // 1.创建xmpp连接  HttpURLConnection
    //                 XMPPTCPConnection   XMPP协议 TCP网络 Connection连接

    private static XMPPTCPConnection conn;


    public class ChatController extends Binder {//把连接变为service 的成员变量

        /**
         * 停止监听器，不再接收消息（对于外部界面而言）
         * @param listener
         */
        public void removePacketListener(PacketListener listener){
            if (listener!=null){
                if (conn!=null){
                    conn.removePacketListener(listener);
                }
            }
        }

        /**
         * 添加监听器接口（外部界面要接收消息的时候 设置）
         * @param listener
         */
        public void addPacketListener(PacketListener listener){
            if (listener!=null){
                if (conn!=null){
                    conn.addPacketListener(listener,new MessageTypeFilter(Message.Type.chat));
                }
            }
        }

        /**
         * 用于开启聊天会话，主要在ChatActivity界面上使用，用于发送和接收消息
         *
         * @param target   需要和谁聊天
         * @param thread
         *@param listener MessageListener用来监听消息的  @return Chat 对象，可以通过Chat调用sendMessage 发送消息
         */
        public Chat openChat(String target, String thread, MessageListener listener) {
            Chat ret = null;
            if (target != null) {
                if (conn != null) {
                    if (conn.isAuthenticated()) {
                        //已经登录的情况
                        ChatManager chatManager = ChatManager.getInstanceFor(conn);
                        //创建聊天会话
                        ret = chatManager.createChat(target,thread, listener);

                    }
                }
            }
            return ret;
        }

        /**
         * 给外部的LoginActivity提供直接调用的功能
         *
         * @param userName
         * @param password
         * @return
         */
        public String login(String userName, String password) {
            String ret = null;
            if (userName != null && password != null) {
                if (conn != null) {

                    try {
                        if (!conn.isAuthenticated()) {
                            //登录
                            conn.login(userName, password);
                        }

                        ret = conn.getUser();//用来，登录完成之后返回用户名
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return ret;
        }

        /**
         * 获取当前登录帐号中的 联系人信息
         *
         * @return
         */
        public List<RosterEntry> getRosterEntries() {
            List<RosterEntry> ret = null;
            if (conn != null) {
                //如果当前已经登陆过，那么获取
                if (conn.isAuthenticated()) {

                    Roster roster = conn.getRoster();

                    if (roster != null) {
                        //获取联系人列表
                        Collection<RosterEntry> entries = roster.getEntries();

                        ret = new LinkedList<RosterEntry>();
                        //联系人获取出来
                        ret.addAll(entries);

//                        for (RosterEntry entry : entries) {
//
//                        }
                    }
                }
            }
            return ret;
        }
    }

    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");

        return new ChatController();
    }

    private ChatThread chatthread;

    @Override
    public void onCreate() {
        super.onCreate();

        if (conn != null) {
            try {
                conn.disconnect();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
            conn = null;
        }
        //TODO 连接服务器  参数：服务器地址
        conn = new XMPPTCPConnection("10.0.154.195");
    }

    /**
     * 服务的启动
     * <p/>
     * startService 可以执行多次
     * 最多的是service
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // TODO 启动线程
        if (chatthread == null) {
            chatthread = new ChatThread();
            chatthread.start();

        }

        return START_STICKY;//聊天退了还能启动

    }

    @Override
    public void onDestroy() {
        if (chatthread != null) {
            chatthread.stopThread();
            chatthread = null;
        }
        super.onDestroy();
    }

    /**
     * 实际聊天的线程部分
     */
    class ChatThread extends Thread {


        //标志线程
        private boolean running;

        public void stopThread() {
            running = false;
        }

        @Override
        public void run() {
            running = true;
//            //TODO 连接服务器  参数：服务器地址
//            conn=new XMPPTCPConnection("10.0.154.195");
            //进行实际的连接服务器操作
            try {
                // Smark API 当中，大部分方法发生错误的时候直接抛异常
                conn.connect();

////                //账号注册
////                //获取帐号管理器进行注册的操作
////                AccountManager accountManager = AccountManager.getInstance(conn);
////                //参数："用户名","密码"
////                try {
////                    //注册帐号
////                    accountManager.createAccount("xxj","123456");
//
//                }catch (Exception ex){
//                    ex.printStackTrace();
//                }
//
//                // TODO  登录账号，上一个注册成功
//
//                //登录都是在连接打开之后，因此 登录方法在  xmpptcpConnection 中
//                //参数：用户名  密码  第三个用于标注用户
//                conn.login("xxj","123456");
//
//                //获取用户最基本的信息，（检查是否登录成功）
//                //返回已经登录的用户  JID  （也就是发送消息的时候，使用的目标收信人的格式）
//                String user = conn.getUser();
//                Log.d("ChatThread","Login User:"+user);
//
//                //TODO 获取联系人列表
//                // getRoster() 会自动从服务器上面获取当前登录用户的联系人列表，并且返回的Roster类对象
//                //所有的添加修改的操作都会影响到账号实际的联系人内容
//                Roster roster = conn.getRoster();
//
//                //获取联系人的个数
//                int entryCount = roster.getEntryCount();
//                //获取所有的联系人信息
//                Collection<RosterEntry> entries = roster.getEntries();
//
//                //TODO 遍历每个联系人信息
//                for(RosterEntry entry:entries){
//                    //昵称
//                    String name = entry.getName();
//                    //手法信息时用到的内容
//                    String user1 = entry.getUser();
//                    //获取状态
//                    RosterPacket.ItemStatus status = entry.getStatus();
//
////                    Log.d("ChatThread","打印好友信息");
//                    Log.d("ChatThread","Roster:"+user1);
//
//                }
//
////                //TODO 创建联系人
////                //第一个参数 是 JID 形式的，也就是 用户名@域名 方式
////                //第二个参数 ，添加联系人的 备注名称
////                //第三个参数 ，属于那些组
//                roster.createEntry("vhly@10.0.154.195","Zhang sir",null);
////
////                //TODO 接收消息
////                // 向连接中，添加数据包的监听器，当服务器给客户端转发消息的时候，XMPPTCPConnection 会自动调用 PacketListener的回调
////                //两个参数：第一个：数据包监听器，用于处理数据   第二个：监听器要监听哪些类型的数据 因为conn内部所有的操作都是数据包，例如联系人，其实也在发送数据包

                PacketListener listener = new PacketListener() {

                    @Override
                    public void processPacket(Packet packet) throws SmackException.NotConnectedException {//用来接收外部消息，没有进行处理
                        //TODO 处理消息类型的数据包，因为message 类继承了Packet 所以检查是否 Message
                        if (packet instanceof Message) {
                            Message msg = ((Message) packet);

                            //消息内容
                            String body = msg.getBody();
                            //回话的主题
                            String subject = msg.getSubject();
                            //从谁发过来的
                            String from = msg.getFrom();
                            //发给谁
                            String to = msg.getTo();

                            //聊天会话主题，通过这个主题，就可以确定发送者创建的Chat 对象了。
                            //这个thread类似于对讲机之间的联系
                            String thread = msg.getThread();//线程主题

                            Log.d("ChatThread", "has a packet from:" + from + "to:" + to + "content:" + body);

                            //TODO 当收到消息，就模拟一下 QQ 的通知栏信息
                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ChatService.this);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(ChatService.this);

                            builder.setContentTitle("您有新消息");
                            builder.setContentText(body);

                            //设置点击之后，直接进入聊天
                            Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);

                            //如果应用启动了，并且ChatActivity 在任务栈中，那么 直接启动
                            //如果没有启动，那么开一个新的栈
                            chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

                            //使用UserJID给谁回复
                            chatIntent.putExtra("userJID",from);
                            //主题标示，进行连个账号之间的联系
                            chatIntent.putExtra("thread",thread);

                            //内容
                            chatIntent.putExtra("body",body);

                            PendingIntent pendingIntent = PendingIntent.getActivity(ChatService.this, 998, chatIntent, PendingIntent.FLAG_ONE_SHOT);

                            builder.setContentIntent(pendingIntent);

                            builder.setSmallIcon(R.drawable.ic_launcher);

                            Notification notification = builder.build();
                            //发送通知
                            managerCompat.notify((int)(System.currentTimeMillis()),notification);
                        }
                    }
                };
                //!!! 再开始回话之前，进行PacketListener的设置
                conn.addPacketListener(listener, new MessageTypeFilter(Message.Type.chat));

//                //创建回话管理器
//                ChatManager chatManager = ChatManager.getInstanceFor(conn);//管理所有的回话
//
//                //创建回话需要给其他人发消息
//
//                if (entries!=null&&!entries.isEmpty()){
//                    Iterator<RosterEntry> iterator = entries.iterator();
                    //找到第一个联系人
//                    RosterEntry next = iterator.next();
//                    String jid = next.getUser();

//                    String jid="Totoro1@10.0.154.195";
//
//                    Log.d("ChatThread","send to"+jid);
//
//                    //创建聊天会话，有一个叫做chat 的对象，进行回话管理
//                    //当使用chat进行发送消息的时候，会自动的,通过底层的 XMPPTCPConnection
//                    Chat chat=chatManager.createChat(jid,new MessageListener() {
//                        @Override
//                        public void processMessage(Chat chat, Message msg) {
//                            //TODO 要处理回话过程中的消息数据
//                            //消息内容
//                            String body=msg.getBody();
//                            //回话的主题
//                            String subject = msg.getSubject();
//                            //从谁发过来的
//                            String from = msg.getFrom();
//                            //发给谁
//                            String to = msg.getTo();
//
//                            Log.d("ChatThread","has a message from:"+from+"to:"+to+"content:"+body);
//                        }
//                    });
                //发送消息
//                    chat.sendMessage("hihi by xxj client ");
//
//                }


                //进行循环，等待消息内容，以及要进行发送处理
                while (running) {
                    Thread.sleep(300);
                }
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        //关闭连接
                        conn.disconnect();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    conn = null;
                }
            }
        }
    }
}
