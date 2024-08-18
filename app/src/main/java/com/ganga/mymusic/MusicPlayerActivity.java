package com.ganga.mymusic;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView titleTV,currentTimeTV,totalTimeTV;
    SeekBar seekBar;
    ImageView pausePlay,previousBtn,nextBtn,musicIcon;
    ArrayList<AudioModel> songList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();
    int x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_player);

        titleTV=findViewById(R.id.song_title);

        previousBtn=findViewById(R.id.previous);
        pausePlay=findViewById(R.id.pause_play);
        nextBtn=findViewById(R.id.next);
        musicIcon=findViewById(R.id.music_icon_big);
        seekBar=findViewById(R.id.seek_bar);
        currentTimeTV=findViewById(R.id.current_time);
        totalTimeTV=findViewById(R.id.total_time);
        //for marquee
        titleTV.setSelected(true);
        songList=(ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTV.setText(covertToMMSS(mediaPlayer.getCurrentPosition()+""));
                }
                if(mediaPlayer.isPlaying())
                {
                    pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                    musicIcon.setImageResource(R.drawable.music);
                    musicIcon.setRotation(x++);

                }
                else {
                    pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                    musicIcon.setImageResource(R.drawable.music_image);
                }

                //change in each second
                new Handler().postDelayed(this,100);

            }
        });
        // if user want to change seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer!=null&& b)
                {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    void setResourcesWithMusic(){
        currentSong=songList.get(MyMediaPlayer.currentIndex);

        titleTV.setText(currentSong.getTitle());

        totalTimeTV.setText(covertToMMSS(currentSong.getDuration()));
        pausePlay.setOnClickListener(v->pausePlay());
        nextBtn.setOnClickListener(v->playNextSong());
        previousBtn.setOnClickListener(v->playPreviousSong());


        playMusic();
    }

    private void playMusic()
    {
// we want media play player instance
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    private void playNextSong()
    {
        if(MyMediaPlayer.currentIndex==songList.size()-1) {
            return;
        }

        MyMediaPlayer.currentIndex+=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }
    private void playPreviousSong()
    {
        if(MyMediaPlayer.currentIndex==0) {
            return;
        }

        MyMediaPlayer.currentIndex-=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }
    private void pausePlay()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        else
        {
           mediaPlayer.start();
        }

    }



    public static String covertToMMSS(String duration)
    {
        Long millis=Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));


    }
}

