package com.gxw.bluetoothconn;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxw.bluetoothconn.utils.ReceiveMessageUtil;
import com.gxw.bluetoothconn.utils.SendMessageUtil;

public class DeviceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = DeviceDetailActivity.class.getName().toString();

    private EditText et_detail;
    private Button btn_detail_send_text, btn_detail_send_file;
    private LinearLayout ll_content;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://文本消息
                    if (TextUtils.isEmpty(msg.obj.toString())) return;
                    ll_content.addView(getLeftTextView(msg.obj.toString()));
                    break;
                case 2://文件消息
                    if (TextUtils.isEmpty(msg.obj.toString())) return;
                    ll_content.addView(getLeftTextView(msg.obj.toString()));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        initView();
        initReceive();

    }

    private void initView() {
        et_detail = (EditText) findViewById(R.id.et_detail);
        btn_detail_send_file = (Button) findViewById(R.id.btn_detail_send_file);
        btn_detail_send_text = (Button) findViewById(R.id.btn_detail_send_text);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);

        btn_detail_send_file.setOnClickListener(this);
        btn_detail_send_text.setOnClickListener(this);
    }

    private void initReceive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReceiveMessageUtil.receiveMessage(mHandler, MyApplication.bluetoothSocket);
            }
        }).start();
    }

    private TextView getLeftTextView(String message) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        textView.setGravity(View.FOCUS_LEFT);
        textView.setBackgroundResource(android.R.color.darker_gray);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setLayoutParams(layoutParams);
        textView.setText(message);
        return textView;
    }

    private TextView getRightTextView(String message) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        textView.setGravity(View.FOCUS_RIGHT);
        textView.setBackgroundResource(android.R.color.white);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setLayoutParams(layoutParams);
        textView.setText(message);
        return textView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_detail_send_file:
                break;
            case R.id.btn_detail_send_text:
                if (et_detail.getText().toString().trim().equalsIgnoreCase("")) return;
                SendMessageUtil.sendMessage(et_detail.getText().toString().trim(), MyApplication.bluetoothSocket);
                ll_content.addView(getRightTextView(et_detail.getText().toString().trim()));
                break;
        }
    }
}
