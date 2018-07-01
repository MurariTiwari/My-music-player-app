package com.tiwarithetiger11.murari.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {
    public static NotificationCompat.Builder builder;
    public static NotificationManager notificationManager;
    public static int notificationid;
    public static RemoteViews remoteViews;
    public static Context context;
    public static ArrayList<SongInfo> _song=new ArrayList<SongInfo>();
    RecyclerView recyclerView;
    static SeekBar seekBar;
    SongAdapter songAdapter;
    PhoneStateListener phoneStateListener;
    static SharedPreferences preferences;
    static MediaPlayer  mediaPlayer;
    static TextView title;
    static TextView artist;
    static ImageButton pp;
    ImageButton next;
    ImageButton prev;
    ImageButton shu;
    ImageButton rep;
    static LinearLayout l;
    int i=0;
    static Boolean bp=false;
    static Boolean s=false;
    static Boolean r=false;
    static int sid;
    static Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title=findViewById(R.id.title);
        artist=findViewById(R.id.artist);
        pp=findViewById(R.id.pp);
        next=findViewById(R.id.next);
        prev=findViewById(R.id.prev);
        shu=findViewById(R.id.shu);
        rep=findViewById(R.id.rep);
        context=this;
        rep.setBackgroundResource(R.drawable.ic_repeat_black_24dp);
        shu.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
        l=findViewById(R.id.thumbnail);
        preferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        recyclerView=(RecyclerView) findViewById(R.id.recy);
        seekBar=(SeekBar) findViewById(R.id.seek);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(recyclerView.getContext(),linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkpermission();


         notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
         remoteViews=new RemoteViews(getPackageName(),R.layout.noti);
         shu.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(s) {

                     s = false;
                     shu.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
                 }else
                 {
                     s= true;
                     shu.setBackgroundResource(R.drawable.ic_loop_black_24dp);
                 }
             }
         });rep.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(r) {

                     r = false;
                     rep.setBackgroundResource(R.drawable.ic_repeat_black_24dp);
                 }else
                 {

                     r = true;
                     rep.setBackgroundResource(R.drawable.ic_repeat_one_black_24dp);
                 }
             }
         });
         next.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(sid+1<_song.size())
                 {
                     sid+=1;
                 }
                 SongInfo s= _song.get(sid);
                 title.setText(s.songname);
                 artist.setText(s.artist);
                 bp=true;
                 pp.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                 try {
                     l.setBackground(Drawable.createFromPath(String.valueOf(Uri.parse(s.path))));
                 }catch(NullPointerException e)
                 {}l.getBackground().setAlpha(60);
                 startSong(s,sid);
             }
         });
         prev.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(sid-1>=0)
                 {
                     sid-=1;
                 }
                 SongInfo s= _song.get(sid);
                 title.setText(s.songname);
                 artist.setText(s.artist);
                 bp=true;
                 pp.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                 try {
                     l.setBackground(Drawable.createFromPath(String.valueOf(Uri.parse(s.path))));
                 }catch(NullPointerException e)
                 {}l.getBackground().setAlpha(60);
                 startSong(s,sid);
             }
         });



         pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bp)
                {  if(mediaPlayer!=null) {
                    pp.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                    bp = false;
                    mediaPlayer.pause();
                }
                }else if(i==0)
                {    pp.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                    sogN(sid);i=1;
                }else{
                    if(mediaPlayer!=null)
                    {pp.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                        bp=true;
                        mediaPlayer.start();
                    }}
            }
        });


        if(_song.size()==0) {
            int b = preferences.getInt("b", 0);
            if(b==0)
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("b",1 );
                editor.commit();
            }
            else {
                loadSong();
            }
        }else{
            songAdapter=new SongAdapter(_song,this);
            recyclerView.setAdapter(songAdapter);
            setFirst();
        }

        AudioManager manager= (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        manager.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void sogN(final int position)
{

    Log.e("index", "sogN: "+_song.size() );
    Log.e("index", "sogN: "+position );

    title.setText(_song.get(position).songname);
    artist.setText(_song.get(position).artist);
    bp=true;
    sid=position;
    pp.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
    try {
        l.setBackground(Drawable.createFromPath(String.valueOf(Uri.parse(_song.get(position).path))));

        l.getBackground().setAlpha(60);
    }catch (NullPointerException e)
    {
        e.getCause();
    }
    Runnable r=new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer!=null)
            {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer=null;

                startSong(_song.get(position),position);

            }
            else{
                startSong(_song.get(position),position);
            }
        }
    };
    handler.postDelayed(r,100);
}



    private static void startSong(final SongInfo obj, int position)
    {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("sid", sid);
        editor.commit();

        Runnable r=new Runnable() {
            @Override
            public void run() {
                try {

                    if(mediaPlayer!=null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer=null;
                    }
                    mediaPlayer=new MediaPlayer();
                    mediaPlayer.setDataSource(obj.getSongURL());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(final MediaPlayer mediaPlayer) {

                            mediaPlayer.start();

                               seekBar.setProgress(0);
                            seekBar.setMax(mediaPlayer.getDuration());
                            try{
                              remoteViews.setImageViewUri(R.id.n_icon, Uri.parse(obj.path));
                              remoteViews.setTextViewText(R.id.n_song,obj.songname);
                              remoteViews.setTextViewText(R.id.n_artist,obj.artist);

                                Intent notification_intent=new Intent(context,MainActivity.class);
                                PendingIntent pendingIntent=PendingIntent.getActivity(context,0,notification_intent,0);
                                builder=new NotificationCompat.Builder(context);
                                builder.setSmallIcon(R.drawable.ic_insert_chart_black_24dp)
                                        .setAutoCancel(false)
                                        .setCustomBigContentView(remoteViews)
                                        .setContentIntent(pendingIntent);
                               notificationManager.notify(123,builder.build());
                            }catch(NullPointerException e)
                            {}
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    shurep();
                                }
                            });

                         seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                         @Override
                         public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                         }

                          @Override
                          public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                           @Override
                           public void onStopTrackingTouch(SeekBar seekBar) {

                             mediaPlayer.seekTo(seekBar.getProgress());
                             }
                            });
                            Thread t=new Mythread();

                            t.start();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        };
        handler.postDelayed(r,100);



    }

    private void checkpermission() {
        if(Build.VERSION.SDK_INT>=23)
        {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)!= PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_NOTIFICATION_POLICY},123);
            }

        }
        else
        {
            loadSong();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case 123:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {
                    loadSong();
                }else
                {
                    Toast.makeText(this,"Permission Denied", LENGTH_LONG).show();
                checkpermission();
                }break;


                default: super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    private void loadSong() {
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection=MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                do{
                    if(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)).equals("audio/mpeg") ) {
                        //Log.e("MIME", "loadSong: "+cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)) );
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        String path = null;

                        Cursor cursor1 = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                                MediaStore.Audio.Albums._ID + "=?",
                                new String[]{String.valueOf(albumId)},
                                null);

                        if (cursor1.moveToFirst()) {
                            path = cursor1.getString(cursor1.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

                        }


                        SongInfo s = new SongInfo(name, artist, url, path);
                        _song.add(s);
                    }
                }while (cursor.moveToNext());
            }
            cursor.close();
            songAdapter=new SongAdapter(_song,this);
            recyclerView.setAdapter(songAdapter);
            setFirst();
        }

    }

    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if(mediaPlayer!=null) mediaPlayer.start();break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            if(mediaPlayer!=null) mediaPlayer.pause();break;
        }
    }
    public static class Mythread extends Thread
    {
        @Override
        public void run() {
            super.run();

            try {

                while(mediaPlayer!=null)
                {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private static void shurep() {

        if(r)
        {

        }
        else if(s)
        {
            sid++;
            if(sid==_song.size()) sid=0;
        }
        else if(!s)
        {
            Random rand = new Random();
            sid= rand.nextInt(_song.size());
        }
        SongInfo s= _song.get(sid);
        title.setText(s.songname);
        artist.setText(s.artist);
        bp=true;
        pp.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
        try {
            l.setBackground(Drawable.createFromPath(String.valueOf(Uri.parse(s.path))));
        }catch (NullPointerException e)
        {

        }
       l.getBackground().setAlpha(60);
        startSong(s,sid);
    }

public static void setFirst()
{
    if(_song.size()>0) {
        sid = preferences.getInt("sid", 0);
        title.setText(_song.get(sid).songname);
        artist.setText(_song.get(sid).artist);
        try {
            l.setBackground(Drawable.createFromPath(String.valueOf(Uri.parse(_song.get(sid).path))));
            l.getBackground().setAlpha(60);
            pp.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);

        } catch (NullPointerException e) {
            e.getCause();
        }
        bp = false;

    }

}

}
