package com.xxj.liaoliao;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.xxj.liaoliao.adapter.ChatMessageAadapter;
import com.xxj.liaoliao.bean.ChatMessage;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;

/**
 * 聊天界面，从其他Activity传递的参数：userJID ,代表聊天的对象
 */

public class ChatActivity extends ActionBarActivity implements ServiceConnection, MessageListener, PacketListener {
    private String userJID;
    private ChatService.ChatController controller;
    private Chat chat;
    /**
     * 发送的信息输入框
     */

    private EditText textContent;
    private String thread;
    private String body;
    private ArrayList<ChatMessage> chatMessages;
    private ChatMessageAadapter adapter;

    /**
     * 从服务获取的binder,用于进行消息的发送
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //接受目标联系人
        Intent intent = getIntent();

        userJID = intent.getStringExtra("userJID");

        //在Activity上显示标题
        setTitle(userJID);

        //获取 Chat主题，可能为空，因为点击自己进入ChatAactivity时，是没有的
        thread = intent.getStringExtra("thread");

        //只有收到消息的时候，才会有
        body = intent.getStringExtra("body");

        //绑定服务，用于发送消息
        Intent service = new Intent(this, ChatService.class);
        //参数1：Intent代表服务
        //2:服务绑定的回调接口
        //3:
        bindService(service, this, BIND_ABOVE_CLIENT);

        textContent = ((EditText) findViewById(R.id.chat_message_content));

        //listView 显示，实现聊天的样式，左侧都是收到的消息，右侧都是发出的消息
        ListView listView = ((ListView) findViewById(R.id.chat_message_list));

        chatMessages = new ArrayList<>();

        adapter = new ChatMessageAadapter(chatMessages, this);

        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//点击事件代码部分

    public void btnFaceClick(View view) {
        //TODO 处理表情的点击

        //EditText.setText();
        //1.获取输入框现有的文本内容
        Editable text = textContent.getText();

        //2.准备一个字符串：这个字符串类型 不再是String
        //而是SpannableString
        SpannableStringBuilder sb = new SpannableStringBuilder(text);

        //TODO 添加笑脸

        //1.表情的文本显示（例如[偷笑]  :)  :(  /偷笑）

        //获取即将添加的字符串的起始位置
        int start=sb.length();
        switch (view.getId()) {
            case R.id.chat_image1:
                sb.append("[微笑]");

                ImageSpan face1 = new ImageSpan(this, R.drawable.face1);

                //第一个参数 what 就是各种span对象，也就是需要给字符串设置的样式
                //第二个参数 设置的字符串的起始位置 例如“I LOVE ANDROID "如果给love设置样式，那么 起始2
                //第三个参数  通常可以指定为 起始位置 + 需要设置样式的字符长度，因为第四个参数，直接影响到这个值的设置
                //第四个参数  代表第二个参数和第三个参数的使用方式
                //通常第四个参数 采用Spanned.SPAN_INCLUSIVE_EXCLUSIVE INCLUSIVE包含起始位置   EXCLUSIVE不包含结束位置
                //                   最终范围是从起始位置 到 结束位置-1
                sb.setSpan(face1,start,start+4,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
            case R.id.chat_image2:
                sb.append("[撇嘴]");
                ImageSpan face2 = new ImageSpan(this, R.drawable.face2);

                //第一个参数 what 就是各种span对象，也就是需要给字符串设置的样式
                //第二个参数 设置的字符串的起始位置 例如“I LOVE ANDROID "如果给love设置样式，那么 起始2
                //第三个参数  通常可以指定为 起始位置 + 需要设置样式的字符长度，因为第四个参数，直接影响到这个值的设置
                //第四个参数  代表第二个参数和第三个参数的使用方式
                //通常第四个参数 采用Spanned.SPAN_INCLUSIVE_EXCLUSIVE INCLUSIVE包含起始位置   EXCLUSIVE不包含结束位置
                //                   最终范围是从起始位置 到 结束位置-1
                sb.setSpan(face2,start,start+4,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
            case R.id.chat_image3:
                sb.append("[色]");
                ImageSpan face3= new ImageSpan(this, R.drawable.face3);

                //第一个参数 what 就是各种span对象，也就是需要给字符串设置的样式
                //第二个参数 设置的字符串的起始位置 例如“I LOVE ANDROID "如果给love设置样式，那么 起始2
                //第三个参数  通常可以指定为 起始位置 + 需要设置样式的字符长度，因为第四个参数，直接影响到这个值的设置
                //第四个参数  代表第二个参数和第三个参数的使用方式
                //通常第四个参数 采用Spanned.SPAN_INCLUSIVE_EXCLUSIVE INCLUSIVE包含起始位置   EXCLUSIVE不包含结束位置
                //                   最终范围是从起始位置 到 结束位置-1
                sb.setSpan(face3,start,start+3,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
            case R.id.chat_image4:
                sb.append("[流汗]");
                ImageSpan face4= new ImageSpan(this, R.drawable.face4);

                //第一个参数 what 就是各种span对象，也就是需要给字符串设置的样式
                //第二个参数 设置的字符串的起始位置 例如“I LOVE ANDROID "如果给love设置样式，那么 起始2
                //第三个参数  通常可以指定为 起始位置 + 需要设置样式的字符长度，因为第四个参数，直接影响到这个值的设置
                //第四个参数  代表第二个参数和第三个参数的使用方式
                //通常第四个参数 采用Spanned.SPAN_INCLUSIVE_EXCLUSIVE INCLUSIVE包含起始位置   EXCLUSIVE不包含结束位置
                //                   最终范围是从起始位置 到 结束位置-1
                sb.setSpan(face4,start,start+4,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
        }
        textContent.setText(sb);

        //将输入框中的光标移动到文字的最后。
        textContent.setSelection(sb.length());
    }

    /**
     * 发送按钮点击事件
     *
     * @param view
     */
    public void btnSendOnClick(View view) {
        String content = textContent.getText().toString();

        if (chat != null) {
            try {
                chat.sendMessage(content);

                //TODO 创建消息实体，显示在ListView 上面
                ChatMessage msg = new ChatMessage();
                //设置显示的文本
                msg.setBody(content);

                //类型是发送的类型
                msg.setSourcetype(ChatMessage.SOURCE_TYPE_SEND);

                chatMessages.add(msg);

                adapter.notifyDataSetChanged();

            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //接收的要有接口回调，发送的chat就可以管理


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        controller = ((ChatService.ChatController) service);

        //TODO 1.绑定成功之后，进行聊天会话的创建 Chat 对象
        chat = controller.openChat(userJID, null, this);

        //TODO 2:controller 要向内部的 XMPPTCPConnection 添加一个 PacketListener
        controller.addPacketListener(this);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        //TODO 删除/停止 监听数据包的内容
        controller.removePacketListener(this);

        if (chat != null) {
            chat.close();//关闭
        }
        controller = null;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        //TODO 处理消息的发送和接收

        String from = message.getFrom();

        String to = message.getTo();

        String body = message.getBody();

        //显示信息，用于判断，发送出去的消息，方法是否回调
        //                    接收的消息能否取到
        Log.d("ChatActivity", "message from" + from + "to" + to + "" + body);

    }

    /**
     * 接收消息，显示在ListView 上面
     *
     * @param packet
     * @throws SmackException.NotConnectedException
     */
    @Override
    public void processPacket(Packet packet) throws SmackException.NotConnectedException {

        if (packet instanceof Message) {

            Message msg = ((Message) packet);

            //1.检查消息的来源是否是当前的会话人
            String from = msg.getFrom();

            //因为PacketListener会接收所有消息
            //对于会话界面而言，就需要检查消息的来源
            //是否当前聊天人
            if (from.startsWith(userJID)) {
                //TODO 显示ListView 信息
                ChatMessage chatMessage = new ChatMessage();

                chatMessage.setBody(msg.getBody());

                chatMessage.setFrom(from);

                chatMessage.setTo(msg.getTo());

                chatMessage.setSourcetype(ChatMessage.SOURCE_TYPE_RECEIVED);

                chatMessage.setTime(System.currentTimeMillis());

                //添加消息，更新ListView adapter
                chatMessages.add(chatMessage);

//                adapter.notifyDataSetChanged();
                //因为 processPacket 执行在子线程中
                //listview的更新应该在主线程中
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
