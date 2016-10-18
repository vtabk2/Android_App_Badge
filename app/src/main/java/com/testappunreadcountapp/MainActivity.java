package com.testappunreadcountapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.appbadge.ShortcutBadger;
import com.testappunreadcountapp.qqapp.CommonBadgeUtilImpl;

public class MainActivity extends AppCompatActivity {

    private int i;

    private int j;
    private int nintyEightAdd = 98;

    private Button mButton;
    private Button mBtnClear;
    private Button mBtnSendNotification;
    private Button mBtnSend98Add;

    private Button mBtnLibSend;
    private Button mBtnLibRemove;

    private Button mBtnQQAppSend;
    private Button mBtnQQAppSend99;
    private Button mBtnQQAppRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.btn_send);
        mButton = null;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                print("send i  " + i);
                BadgeHelper.setBadgeCount(MainActivity.this, i);
            }
        });

        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BadgeHelper.resetBadgeCount(MainActivity.this);
                i = 0;
            }
        });


        mBtnSendNotification = (Button) findViewById(R.id.btn_send_native_notifi);
        mBtnSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BadgeHelper.sendNativeNotification(MainActivity.this, j++);
            }
        });



        mBtnSend98Add = (Button) findViewById(R.id.btn_send_98);
        mBtnSend98Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BadgeHelper.setBadgeCount(MainActivity.this, nintyEightAdd);
                print("send count:"+nintyEightAdd);
                nintyEightAdd++;
            }
        });


        mBtnLibSend = (Button) findViewById(R.id.btn_lib_send);
        i = 97;
        mBtnLibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                print("send i  " + i);
                boolean success = ShortcutBadger.applyCount(MainActivity.this, i);
                print("applycount success ? :"+success);
            }
        });


        mBtnLibRemove = (Button) findViewById(R.id.btn_lib_remove);
        mBtnLibRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = ShortcutBadger.removeCount(MainActivity.this);
                print("remove success ? :"+success);
            }
        });


        mBtnQQAppSend = (Button) findViewById(R.id.btn_qqapp_send);
        mBtnQQAppSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                CommonBadgeUtilImpl.setBadge(MainActivity.this, i);
                print("send i  " + i);
            }
        });

        mBtnQQAppSend99 = (Button) findViewById(R.id.btn_qqapp_send_99);
        mBtnQQAppSend99.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i < 98) {
                    i = 98;
                }
                CommonBadgeUtilImpl.setBadge(MainActivity.this, i);
                print("send i  " + i);
                i++;
            }
        });

        mBtnQQAppRemove = (Button) findViewById(R.id.btn_qqapp_remove);
        mBtnQQAppRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 0;
                CommonBadgeUtilImpl.removeBadge(MainActivity.this);
            }
        });

//        boolean supportBadge = CommonBadgeUtilImpl.isSupportBadge(MainActivity.this);
//        print("supportBadge:"+supportBadge);
    }


    private void print(String msg) {
        Log.d("dbs", msg);
    }
}
