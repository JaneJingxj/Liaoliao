package com.xxj.liaoliao;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity implements ServiceConnection {
    /**
     * 从service中，取到的内容
     */
    private ChatService.ChatController controller;
    private EditText textUserName;
    private EditText textPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = new Intent(this, ChatService.class);
        startService(intent);

        //启动之后，再绑定一下      绑定服务
        bindService(intent, this, BIND_AUTO_CREATE);

        textUserName = ((EditText) findViewById(R.id.login_username));
        textPassword = ((EditText) findViewById(R.id.login_password));
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    public void btnLoginOnClick(View view) {
        //登录按钮
        if (controller!=null){
            String userName=textUserName.getText().toString().trim();
            String password=textPassword.getText().toString().trim();
            String userJID = controller.login(userName, password);

            if (userJID!=null){
                //TODO 登录成功
                Intent intent=new Intent(this,MainActivity.class);
                intent.putExtra("userJID",userJID);
                startActivity(intent);
                finish();
            }else{
                //TODO 提示错误
                Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        //取service
        controller = ((ChatService.ChatController) service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        controller = null;
    }
}
