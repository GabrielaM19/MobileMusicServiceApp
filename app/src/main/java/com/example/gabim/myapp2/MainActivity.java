package com.example.gabim.myapp2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Messenger mService = null;
    boolean mIsBound = false;
    MediaPlayer mp;
    int numer,first,second=0;
    ArrayList<Integer> playlist;
    String title1="Exo - Call me Baby",title2="Human",title3="One Republic - Secrets";

    private TextView textViewConnect,textViewTitle;
    private Button bConnect,bStartSong,bPauseSong,bNextSong,bStopService;
    private ImageView image;


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MusicService.MSG_PLAY_FIRST:
                    numer=msg.arg1;
                    PlaySong();
                    break;
                case MusicService.MSG_PLAY_SECOND:
                    numer=msg.arg1;
                    PlaySong();
                    break;
                case MusicService.MSG_PLAY_THIRD:
                    numer=msg.arg1;
                    PlaySong();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger  mMessenger = new Messenger(new IncomingHandler());

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);
            textViewConnect.setText("Connected to Server");
            try {
                Message msg = Message.obtain(null,
                        MusicService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

            } catch (RemoteException e) {
            }
            Toast.makeText(MainActivity.this, "Connected To Remote MyService",
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            textViewConnect.setText("Disconnected to server");

            Toast.makeText(MainActivity.this, "Disconnected",
                    Toast.LENGTH_SHORT).show();

        }
    };

    void doBindMyService() {
        bindService(new Intent(MainActivity.this, MusicService.class),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void StopMyServiceListener() {
        bStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.release();
                textViewConnect.setText("Service Stopped");
                //doUnbindMyService();
                getApplicationContext().stopService(
                        new Intent(getApplicationContext(),MusicService.class));
            }
        });
    }

    public void StartMyServiceListener() {
        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsBound==false){
                    doBindMyService();}
                textViewConnect.setText("Service Started");
                Intent intent = new Intent(getApplicationContext(), MusicService.class);
                intent.putExtra("arg1", "valueOfArg1");
                getApplicationContext().startService(intent);
            }
        });
    }

    /*void doUnbindMyService() {
        if (mIsBound) {
            if(mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            MusicService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {

                }
            }
            mIsBound = false;
        }
    }*/

    public void StartSongListener() {
        bStartSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message msg = Message.obtain(null,
                            MusicService.MSG_PLAY_FIRST);
                    msg.replyTo = mMessenger;
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    public void PlaySong(){
        if(numer==1) {
            mp = MediaPlayer.create(this,playlist.get(0));
            textViewTitle.setText(title1);
            mp.start();
        }
        else if(numer==2){
            mp = MediaPlayer.create(this,playlist.get(1));
            textViewTitle.setText(title2);
            mp.start();
        }
        else if(numer==3){
            mp = MediaPlayer.create(this,playlist.get(2));
            textViewTitle.setText(title3);
            mp.start();
        }
    }

    public void PauseSongListener() {
        bPauseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(first==0){
                mp.pause();
                first=1;}
                else{
                    mp.start();
                    first=0;
                }
            }
        });
    }

    public void NextSongListener() {
        bNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.reset();
                if(second==0){
                    Message msg = Message.obtain(null,
                        MusicService.MSG_PLAY_SECOND);
                    msg.replyTo = mMessenger;
                    second=1;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }}
                else if(second==1){
                    second=2;
                    Message msg = Message.obtain(null,
                            MusicService.MSG_PLAY_THIRD);
                    msg.replyTo = mMessenger;
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                else if(second==2)
                {
                    second=0;
                    Message msg = Message.obtain(null,
                            MusicService.MSG_PLAY_FIRST);
                    msg.replyTo = mMessenger;
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bConnect = (Button) findViewById(R.id.buttonConnect);
        bNextSong = (Button) findViewById(R.id.buttonNextSong);
        bStartSong = (Button) findViewById(R.id.buttonStartSong);
        bStopService = (Button) findViewById(R.id.buttonStopService);
        bPauseSong = (Button) findViewById(R.id.buttonPauseSong);
        textViewConnect = (TextView) findViewById(R.id.textViewConnect);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);

        image = (ImageView) findViewById(R.id.imageViewSong);
        image.setImageResource(R.drawable.notes);

        StartMyServiceListener();
        StopMyServiceListener();
        StartSongListener();
        PauseSongListener();
        NextSongListener();

        playlist = new ArrayList<>();
        playlist.add(R.raw.exo);
        playlist.add(R.raw.human);
        playlist.add(R.raw.secrets);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
