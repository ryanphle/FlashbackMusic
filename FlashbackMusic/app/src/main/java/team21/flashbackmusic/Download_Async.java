package team21.flashbackmusic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by petternarvhus on 15/03/2018.
 */

public class Download_Async extends AsyncTask<String,Integer,String> {



    private Context context;
    private  String url;
    MainActivity activity;
    private Fragment fragment;

    public Download_Async(Context context,String url, Fragment fragment){


        this.context = context;
        this.url = url;
        activity = (MainActivity) context;
        this.fragment = fragment;


    }

    @Override
    protected String doInBackground(String... strings) {


        while (!activity.sorted_songs.get(0).isDownloaded()){
            String type = activity.fileExtension(url);
            activity.startDownload(activity.sorted_songs.get(0).getUrl(),type);
            activity.sorted_songs.get(0).setIsDownloaded(true);
        }

        return null;
    }


    @Override protected void onPreExecute(){super.onPreExecute();}

    @Override
    protected void onPostExecute(String result){

        super.onPostExecute(result);
        Bundle bundle = new Bundle();

        if (activity.songListEmpty){
            activity.songListEmpty = false;
            activity.mediaPlayerWrapper = new MediaPlayerWrapper(activity.sorted_songs, context,activity);
        }
        activity.mediaPlayerWrapper.setSongs(activity.sorted_songs);
        activity.mediaPlayerWrapper.setIndex(0);
        activity.mediaPlayerWrapper.newSong(activity.mediaPlayerWrapper.getIndex());

        bundle.putParcelableArrayList("songs", activity.sorted_songs);
        fragment.setArguments(bundle);

        if (activity.mProgressDialog != null && activity.mProgressDialog.isShowing()) {
            activity.mProgressDialog.dismiss();
        }




    }


}
