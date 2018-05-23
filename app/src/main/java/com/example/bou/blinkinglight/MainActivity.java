package com.example.bou.blinkinglight;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MyReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiver = new MyReceiver();
//
//        this.registerReceiver(receiver, new IntentFilter(
//                "android.intent.action.PHONE_STATE"));
//
//        this.registerReceiver(receiver, new IntentFilter(
//                "android.intent.action.NEW_OUTGOING_CALL"));
        Toast.makeText(getApplicationContext(), "Enabled broadcast receiver", Toast.LENGTH_SHORT).show();
    }


//    protected void onStop() {
//        super.onStop();
//        this.unregisterReceiver(receiver);
//    }
}
