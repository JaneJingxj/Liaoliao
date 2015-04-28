package com.xxj.liaoliao.adapter;

/**
 * Created by Administrator on 15-4-24.
 */

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xxj.liaoliao.R;
import com.xxj.liaoliao.bean.ChatMessage;

import java.util.List;

/**
 * 主要用于聊天信息展示中 listview的显示，分为左侧和右侧两部分
 */
public class ChatMessageAadapter extends BaseAdapter{
    /**
     * 当前聊天信息列表     我改变了它
     *
     *
     * 我又改了
     */
    private List<ChatMessage> messages;

    private Context context;
    private LayoutInflater inflater;

    public ChatMessageAadapter(List<ChatMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    /**
     * 获取所有的数据个数
     * @return
     */
    @Override
    public int getCount() {
        int ret=0;
        if (messages!=null){
            return messages.size();
        }
        return ret;
    }

    /**
     * 获取制定索引实际数据对象
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        Object ret=null;
        if (messages!=null){
            ret=messages.get(position);
        }
        return ret;
    }

    /**
     * 获取数据的ID，对于CursorAdapter这个方法  返回的是数据库记录的ID
     * 另外一种应用就是ListView 设置为可以多选的情况
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 告诉listview 内部的布局一共有多少种
     * @return
     */
    @Override
    public int getViewTypeCount() {
        //对于2，主要是聊天主要有发送和接收的两种布局
        //左侧接收的 ，右侧发送的
        return 2;
    }

    /**
     * 每次listview显示item的时候，都问一下adapter指定位置的item
     * 是什么类型
     * getView的时候获得
     * @param position  根据位置，获取数据的类型
     * @return  int 注意：返回的数值必须是从 0到 getViewTypeCount-1
     */
    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = messages.get(position);
        int ret=0;
        int sourcetype = chatMessage.getSourcetype();

        //对于发送出去的消息，显示在右侧，制定返回类型为1
        if (sourcetype==ChatMessage.SOURCE_TYPE_SEND){
            ret=1;
        }else if (sourcetype==ChatMessage.SOURCE_TYPE_RECEIVED){
            //  对于收到的消息，显示在左侧，返回的类型为0
            ret=0;
        }
        return ret;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View ret = null;

        //TODO 1:获取消息，判断消息的类型，根据类型来进行内容的设置

        ChatMessage chatMessage = messages.get(position);

        //获取来源类型，根据来源类型，进行不同布局的加载于显示
        int sourcetype = chatMessage.getSourcetype();

        String body=chatMessage.getBody();
        if (sourcetype==ChatMessage.SOURCE_TYPE_RECEIVED){
            //TODO 收到的，就在左侧

            if (convertView!=null){
                ret=convertView;

            }else{
                //LayoutInflater
                ret=inflater.inflate(R.layout.item_chat_left,parent,false);
            }
            //TODO 显示消息内容
            //左侧的TextView  id chat_message
            TextView textMessage = (TextView) ret.findViewById(R.id.chat_message);
            textMessage.setText(chatMessage.getBody());

            //聊天消息表情
            //1.找到字符串中所有的[] 包含的内容
            SpannableString str=new SpannableString(body);

            //【偷笑】【察汗】
            // 0    3  4    7

            //正则表达式 如何查找 [] 包含的内容


        }else if (sourcetype==ChatMessage.SOURCE_TYPE_SEND){
            //TODO 发送的就显示在右侧
            if (convertView!= null) {
                ret=convertView;

            }else {
                ret=inflater.inflate(R.layout.item_chat_right,parent,false);
            }
            // 显示消息的右侧
            TextView textMessage = (TextView) ret.findViewById(R.id.chat_message_right);
            textMessage.setText(chatMessage.getBody());
        }
        return ret;
    }
}
