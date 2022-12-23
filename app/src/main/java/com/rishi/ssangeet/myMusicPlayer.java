package com.rishi.ssangeet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;

import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class myMusicPlayer extends AppCompatActivity {
//    MediaPlayer player = null ;
    BarVisualizer visualizer = null;
    final Handler handler2 = new Handler();
    @Override
    protected void onDestroy() {
        handler2.removeCallbacksAndMessages(null);
        if (visualizer != null)
        {
            visualizer.release();
            visualizer = null;
            Log.d("myTag1", "Visualizer released");
        }
        super.onDestroy();
    }
    String sng_name;
    ArrayList<File> songs;
    TextView name, start, end;
    Button playbtn, nextbtn;
    int pos, play;
    SeekBar seek;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pos = 0;
        play = 0;
        Intent intent = getIntent();
        sng_name = intent.getStringExtra(MainActivity.MSG1);
        songs = (ArrayList<File>) getIntent().getSerializableExtra(MainActivity.MSG2);
        for (File f : songs)
        {
            if (f.getName().equalsIgnoreCase(sng_name))
                break;
            pos ++ ;
        }
        Uri uri = Uri.parse(songs.get(pos).toString());

        name = findViewById(R.id.song_name_text);
        name.setSelected(true);
        name.setText(sng_name);

        if (CommonMethod.player != null) {
            CommonMethod.player.stop();
            CommonMethod.player.release();
            CommonMethod.player = null;
            Log.d("myTag2", "Player released");
        }
//        player = MediaPlayer.create(getApplicationContext(), uri);
        CommonMethod.SoundPlayer(getApplicationContext(), uri);
        CommonMethod.player.start();
//        player.start();
        play = 1;
        playbtn = (Button)findViewById(R.id.playbtn);

        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation();
                handler1.postDelayed(this, 5000);
            }
        },5000);
        start = findViewById(R.id.timer_start);
        end = findViewById(R.id.timer_end);

        seek = findViewById(R.id.seekbar);
        seek.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.MULTIPLY);

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                seek.setProgress(CommonMethod.player.getCurrentPosition());
                if (play == 1) {
                    start.setSelected(false);
                    start.setText(mili_to_text(CommonMethod.player.getCurrentPosition()));
                    start.setSelected(false);
                }
                handler2.postDelayed(this, 1000);
            }
        }, 1000);

        visualizer = findViewById(R.id.blast);
        nextbtn = findViewById(R.id.next);

        seek_bar_music();
    }
    public void playpause (View v)
    {
        if (play == 1)
        {
            CommonMethod.player.pause();
            play = 0;
            playbtn.setBackgroundResource(R.drawable.ic_play);
        }
        else
        {
            CommonMethod.player.start();
            play = 1;
            playbtn.setBackgroundResource(R.drawable.ic_pause);
        }
    }
    public void startAnimation()
    {
        ImageView img = findViewById(R.id.music_icon);
        ObjectAnimator animator = ObjectAnimator.ofFloat(img, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public void seek_bar_music()
    {
        end.setText(mili_to_text(CommonMethod.player.getDuration()));
        seek.setMax(CommonMethod.player.getDuration());

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                CommonMethod.player.seekTo(seekBar.getProgress());
            }
        });
        int audiosessionId = CommonMethod.player.getAudioSessionId();
        if (audiosessionId != -1)
        {
            visualizer.setAudioSessionId(audiosessionId);
        }

        CommonMethod.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                start.setText(mili_to_text(CommonMethod.player.getDuration()));
                nextbtn.performClick();
            }
        });
    }
    public String mili_to_text(int x)
    {
        String str = "";
        x = (int)(x/1000);
        int k = (x % 60);
        x = (int)(x/60);
        if (k >= 10)
            str = Integer.toString(x) + ":" + Integer.toString(k);
        else
            str = Integer.toString(x) + ":0" + Integer.toString(k);
        return str;
    }
    public void next_song(View v)
    {
        CommonMethod.player.stop();
        CommonMethod.player.release();

        seek.setProgress(0);
        pos = ((pos + 1)% songs.size());
        Uri u = Uri.parse(songs.get(pos).toString());
        CommonMethod.player = MediaPlayer.create(getApplicationContext(),u);

        sng_name = songs.get(pos).getName();
        name.setSelected(true);
        name.setText(sng_name);

        CommonMethod.player.start();
        play = 1;
        playbtn.setBackgroundResource(R.drawable.ic_pause);

        end.setText(mili_to_text(CommonMethod.player.getDuration()));
        seek.setMax(CommonMethod.player.getDuration());

        int audiosessionId = CommonMethod.player.getAudioSessionId();
        if (audiosessionId != -1)
        {
            visualizer.setAudioSessionId(audiosessionId);
            CommonMethod.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    start.setText(mili_to_text(CommonMethod.player.getDuration()));
                    nextbtn.performClick();
                }
            });
        }
    }
    public void prev_song(View v)
    {
        CommonMethod.player.stop();
        CommonMethod.player.release();

        seek.setProgress(0);
        pos = ((pos - 1) < 0)?(songs.size() - 1):(pos-1);
        Uri u = Uri.parse(songs.get(pos).toString());
        CommonMethod.player = MediaPlayer.create(getApplicationContext(),u);

        sng_name = songs.get(pos).getName();
        name.setSelected(true);
        name.setText(sng_name);

        CommonMethod.player.start();
        play = 1;
        playbtn.setBackgroundResource(R.drawable.ic_pause);

        end.setText(mili_to_text(CommonMethod.player.getDuration()));
        seek.setMax(CommonMethod.player.getDuration());

        int audiosessionId = CommonMethod.player.getAudioSessionId();
        if (audiosessionId != -1)
        {
            visualizer.setAudioSessionId(audiosessionId);
        }
//        Toast.makeText(this, "hehe", Toast.LENGTH_SHORT).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void fast(View v)
    {
        float speed = CommonMethod.player.getPlaybackParams().getSpeed();
        if (speed >= 0.25f && speed <= 1.75f) {
            speed = speed + 0.25f;
            CommonMethod.player.setPlaybackParams(CommonMethod.player.getPlaybackParams().setSpeed(speed));
            Toast.makeText(this, "Current Playback Speed : "+speed+"x", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Audio is already in its fastest playback speed.", Toast.LENGTH_SHORT).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void slow(View v){
        float speed = CommonMethod.player.getPlaybackParams().getSpeed();
        if (speed <= 2.0f && speed >= 0.50f) {
            speed = speed - 0.25f;
            CommonMethod.player.setPlaybackParams(CommonMethod.player.getPlaybackParams().setSpeed(speed));
            Toast.makeText(this, "Current Playback Speed : "+speed+"x", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Audio is already in its slowest playback speed.", Toast.LENGTH_SHORT).show();
    }
    public void about (View v)
    {
        Toast.makeText(this, "Developer : Soumyadev Saha", Toast.LENGTH_SHORT).show();
    }
}