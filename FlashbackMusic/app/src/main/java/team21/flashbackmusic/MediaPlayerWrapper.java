package team21.flashbackmusic;

import android.content.Context;
import android.media.MediaPlayer;

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

    public MediaPlayerWrapper(List<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
        this.index = 0;
        playing = false;

        Song start = songs.get(0);
        loadMedia(start, mediaPlayer);
    }

    public void stopAndStart() {
        if (playing) {
            playing = false;
            mediaPlayer.pause();
        }
        else {
            playing = true;
            newSong(index);
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
            prevSongIdx = (index - 1) < 0 ? songs.size() - 1 : index - 1;
            s = songs.get(prevSongIdx);
        }

        index = prevSongIdx;
        newSong(prevSongIdx);
    }

    public void newSong(int index) {
        Song s = songs.get(index);
        this.index = index;

        if (s.getFavorite() == -1) return;

        mediaPlayer.reset();
        loadMedia(s, mediaPlayer);
        playing = true;
        mediaPlayer.start();
    }

    public void loadMedia(Song song, MediaPlayer mediaPlayer) {
        if (mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            try {
                this.mediaPlayer.setDataSource(context, song.getUri());
                this.mediaPlayer.prepare();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        try {
            mediaPlayer.setDataSource(context, song.getUri());
            mediaPlayer.prepare();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    public int getIndex() {
        return index;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public boolean isPlaying() {
        return playing;
    }

}
