package com.example.bou.blinkinglight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class MyReceiver extends BroadcastReceiver {

    int lastState = TelephonyManager.CALL_STATE_IDLE;
    Date callStartTime;
    boolean isIncoming;
    String savedNumber;
    CameraManager cameraManager;
    String[] cameraIdList;
    boolean on = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraIdList = cameraManager.getCameraIdList();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }


        //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

        //because the passed incoming is only valid in ringing


        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                onCallStateChanged(context, state, number);
            }
        }
    }


    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onCallStateChanged(final Context context, int state, String number) {
        if (lastState == state) {
            Toast.makeText(context, "ended", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraIdList[0], false);
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                for (int i = 0; i < 10; i++) {

                                    try {
                                        cameraManager.setTorchMode(cameraIdList[0], true);

                                        Thread.sleep(1000);

                                        cameraManager.setTorchMode(cameraIdList[0], false);

                                        Thread.sleep(500);

                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                        }

                    };


                    Thread thread = new Thread(runnable);
                    thread.start();
                }
                //  onIncomingCallStarted(context, number, callStartTime);
                Toast.makeText(context, " onIncomingCallStarted", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                    for (int i = 0; i < 10; i++) {

                                        try {
                                            cameraManager.setTorchMode(cameraIdList[0], true);

                                            Thread.sleep(1000);

                                            cameraManager.setTorchMode(cameraIdList[0], false);

                                            Thread.sleep(500);

                                        } catch (CameraAccessException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }
                        };

                        Thread thread = new Thread(runnable);
                        thread.start();
                    }
                    Toast.makeText(context, " onOutgoingCallStarted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"Received",Toast.LENGTH_SHORT).show();
                }
                // onOutgoingCallStarted(context, savedNumber, callStartTime);

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    //  onMissedCall(context, savedNumber, callStartTime);
                    Toast.makeText(context, " onMissedCall", Toast.LENGTH_SHORT).show();
                } else if (isIncoming) {
                    // onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                    Toast.makeText(context, " onIncomingCallEnded ", Toast.LENGTH_SHORT).show();
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                cameraManager.setTorchMode(cameraIdList[0], false);
                            }
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }


                    }

                    //  onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                    Toast.makeText(context, "  onOutgoingCallEnded ", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        lastState = state;
    }
}
