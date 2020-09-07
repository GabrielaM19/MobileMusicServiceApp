package com.example.gabim.myapp2;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by gabim on 01.05.2018.
 */

public class MusicService extends Service{

    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    int numer1=1,numer2=2,numer3=3,inf;


    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_PLAY_FIRST = 3;
    static final int MSG_PLAY_SECOND = 4;
    static final int MSG_PLAY_THIRD = 5;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_PLAY_FIRST:
                    inf=3;
                    Play(msg);
                    break;
                case MSG_PLAY_SECOND:
                    inf=4;
                    Play(msg);
                    break;
                case MSG_PLAY_THIRD:
                    inf=5;
                    Play(msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public MusicService() {
    }

    public void Play(Message msg){
        try {
            Message msg1;
            if(inf==3){
                msg1 = Message.obtain(null, MusicService.MSG_PLAY_FIRST);
                msg1.arg1 = numer1;
                mClients.get(mClients.indexOf(msg.replyTo)).send(msg1);
            }
            else if(inf==4){
                msg1 = Message.obtain(null, MusicService.MSG_PLAY_SECOND);
                msg1.arg1 = numer2;
                mClients.get(mClients.indexOf(msg.replyTo)).send(msg1);
            }
            else if(inf==5){
                msg1 = Message.obtain(null, MusicService.MSG_PLAY_THIRD);
                msg1.arg1 = numer3;
                mClients.get(mClients.indexOf(msg.replyTo)).send(msg1);
            }
        } catch (RemoteException e) {
            Toast.makeText(MusicService.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onCreate() {
        Toast.makeText(MusicService.this, "MyService On Create", Toast.LENGTH_LONG).show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(MusicService.this, "MyService On Start", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(MusicService.this, "MyService On Destroy", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

}

