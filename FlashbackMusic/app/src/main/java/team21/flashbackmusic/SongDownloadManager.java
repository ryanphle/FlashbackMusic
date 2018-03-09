package team21.flashbackmusic;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.URLUtil;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by james on 3/7/2018.
 */

class SongDownloadManager implements ContentDownload {

    Context context;
    DownloadManager downloadManager;
    MainActivity activity;
    Long downloadRef;

    public SongDownloadManager (Context context){

        this.context = context;
        activity = (MainActivity) context;
        this.downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

    }


    public String checkStatus(){

        String result = "";


        return result;
    }

    public String checkType(){

        return "Song";
    }


    public void updateList() {


        Uri fileuri = downloadManager.getUriForDownloadedFile(downloadRef);
        SharedPreferences sharedPreferences = activity.getSharedPreferences("plays", MODE_PRIVATE);
        Gson gson = new Gson();
        activity.addSong(sharedPreferences,gson,fileuri);

        Log.i("add download song",""+fileuri);

    }




    public void download (String url){

        //downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);

        //download_uri = uri;
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setVisibleInDownloadsUi(true);

            //MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            //request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(url));


        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, URLUtil.guessFileName(url, null,null));

            //Uri fileuri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString()+"/");

            //request.setDestinationUri(fileuri);

        downloadRef = downloadManager.enqueue(request);

            //Log.i("uri",""+DownloadManager.COLUMN_LOCAL_URI);
            //Log.i("uri",""+DownloadManager.COLUMN_LOCAL_FILENAME);


    }
}
