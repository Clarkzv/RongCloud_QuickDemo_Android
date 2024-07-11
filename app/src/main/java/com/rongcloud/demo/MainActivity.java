package com.rongcloud.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRoom;
import cn.rongcloud.rtc.api.callback.IRCRTCResultDataCallback;
import cn.rongcloud.rtc.base.RTCErrorCode;
import io.rong.callkit.RongCallKit;
import io.rong.calllib.RongCallCommon;
import io.rong.common.fwlog.FwLog;
import io.rong.imkit.RongIM;
import io.rong.imkit.picture.tools.ToastUtils;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.SendMessageOption;
import io.rong.message.TextMessage;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final String userA = "";
    private final String userB = "";
    private final String tokenA = "==";
    private final String tokenB = "==";
    Button connectBtn, conversationListBtn, conversationBtn, calllibBtn, callkitBtn, sendMessageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        connectBtn = findViewById(R.id.btn_main_connect);
        conversationListBtn = findViewById(R.id.btn_main_conversation_list);
        conversationBtn = findViewById(R.id.btn_main_conversation);
        calllibBtn = findViewById(R.id.btn_main_calllib);
        sendMessageBtn = findViewById(R.id.btn_main_send_message);
        callkitBtn = findViewById(R.id.btn_main_callkit);
        connectBtn.setOnClickListener(v -> rongConnect());
        conversationListBtn.setOnClickListener(v -> conversationList());
        conversationBtn.setOnClickListener(v -> conversation());
        sendMessageBtn.setOnClickListener(v -> sendMessage());
        calllibBtn.setOnClickListener(v -> callLib());
        callkitBtn.setOnClickListener(v -> callKit());
    }


    /**
     * 连接方法
     */
    private void rongConnect() {
        RongIM.connect(tokenA, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String t) {
                ToastUtils.s(MainActivity.this, "连接成功");
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode e) {
                ToastUtils.s(MainActivity.this, "连接失败");
            }

            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {
                ToastUtils.s(MainActivity.this, "数据库打开失败");
            }
        });
    }

    /**
     * 会话列表
     */
    private void conversationList() {
        RouteUtils.routeToConversationListActivity(this, "test");
    }


    /**
     * 会话界面
     */
    private void conversation() {
        if (isSelf()) {
            RouteUtils.routeToConversationActivity(this, Conversation.ConversationType.PRIVATE, userB);
        } else {
            RouteUtils.routeToConversationActivity(this, Conversation.ConversationType.PRIVATE, userA);
        }
    }

    private boolean isSelf() {
        return RongIM.getInstance().getCurrentUserId().equals(userA);
    }


    private void sendMessage() {
        String targetId = isSelf() ? userB : userA;
        Conversation.ConversationType conversationType = Conversation.ConversationType.PRIVATE;
        TextMessage textMessage = TextMessage.obtain("测试消息");
        Message message = Message.obtain(targetId, conversationType, textMessage);
        RongIM.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                Log.d(TAG, "onAttached: ");
            }

            @Override
            public void onSuccess(Message message) {
                ToastUtils.s(MainActivity.this, "发送成功");
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                Log.d(TAG, "onError: " + errorCode.getValue());

            }
        });
    }

    /**
     * CallKit 示例
     */
    private void callKit() {
        RongCallKit.startSingleCall(this, userB, RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO);
    }

    /**
     * CallLib 示例
     */
    private void callLib() {
        CalllibActivity.start(this);
    }
}