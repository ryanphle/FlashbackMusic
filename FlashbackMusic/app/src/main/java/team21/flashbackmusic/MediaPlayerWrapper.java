package team21.flashbackmusic;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanle on 3/4/18.
 */

public class MediaPlayerWrapper {

    private MediaPlayer mediaPlayer;
    private List<Song> songs;
    private Context context;
    private boolean playing;
    private int index;
    private MainActivity activity;

    public MediaPlayerWrapper(List<Song> songs, final Context context, final MainActivity activity) {
        this.songs = songs;
        this.context = context;
        this.index = 0;
        this.activity = activity;

        this.mediaPlayer = new MediaPlayer();

        Song start = songs.get(0);
        loadMedia(start, mediaPlayer);
        newSong(index);
        playing = false;
        mediaPlayer.pause();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                index = (index + 1) % getSongs().size();
                loadMedia(getSongs().get(index), mediaPlayer);
                updateAndStore(index, activity.songPlayingFrag, true);
                mediaPlayer.start();
            }
        });
    }

    public void stopAndStart() {
        if (playing) {
            playing = false;
            mediaPlayer.pause();
        }
        else {
            playing = true;
            mediaPlayer.start();
        }
    }

    public void next() {
        int nextSongIdx = (index + 1) % songs.size();

        Song s = songs.get(nextSongIdx);
        while (s.getFavorite() == -1) {
            nextSongIdx = (nextSongIdx + 1) % songs.size();
            s = songs.get(nextSongIdx);
        }

        index = nextSongIdx;
        newSong(nextSongIdx);
    }


    public void prev() {
        int prevSongIdx = (index - 1) < 0 ? songs.size() - 1 : index - 1;

        Song s = songs.get(prevSongIdx);
        while (s.getFavorite() == -1) {
            prevSongIdx = (prevSongIdx - 1) < 0 ? songs.size() - 1 : prevSongIdx - 1;
            s = songs.get(prevSongIdx);
        }

        index = prevSongIdx;
        newSong(prevSongIdx);
    }

    public void newSong(int index) {
        Song s = songs.get(index);

        if (s.getFavorite() == -1) return;
        this.index = index;

        mediaPlayer.reset();
        loadMedia(s, mediaPlayer);
        playing = true;
        mediaPlayer.start();
    }

    public void loadMedia(Song song, MediaPlayer mediaPlayer) {
        try {
            mediaPlayer.setDataSource(context, song.getUri());
            mediaPlayer.prepare();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Song getSong() { return songs.get(index); }

    public void setIndex(int index) { this.index = index; }

    public int getIndex() {
        return index;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Song> getSongs() { return this.songs; }

    public boolean isPlaying() {
        return playing;
    }

    public void forcePause() {
        mediaPlayer.pause();
        playing = false;
    }

    public void release() {
        mediaPlayer.release();
    }

    public void updateAndStore(int index, int mode, boolean songChange) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        activity.storePlayInformation(songs.get(index), activity.lastLocation, "plays",
                                        time);
        activity.updateSongMetaData(index, mode, songChange);
    }

}
