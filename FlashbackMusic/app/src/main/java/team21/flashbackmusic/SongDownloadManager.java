package team21.flashbackmusic;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.webkit.URLUtil;
import android.widget.Toast;

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
    long downloadRef;

    public SongDownloadManager (Context context){

        this.context = context;
        activity = (MainActivity) context;
        this.downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

    }


    public String checkStatus(){

        //String result = "";


        return Check_Status();
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
        Log.i("download get the uri",uri.toString());

        //download_uri = uri;
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setVisibleInDownloadsUi(true);

            //MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            //request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(url));


        request.setDestinationInExternalPublicDir("storage/emulated/0/Music", URLUtil.guessFileName(url, null,null));

        Log.i("download set destination","storage/emulated/0/Music");


        //Uri fileuri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString()+"/");

            //request.setDestinationUri(fileuri);

        downloadRef = downloadManager.enqueue(request);
        Log.i("download enque",request.toString());

        Check_Status();


        //Log.i("uri",""+DownloadManager.COLUMN_LOCAL_URI);
            //Log.i("uri",""+DownloadManager.COLUMN_LOCAL_FILENAME);


    }

    private String Check_Status() {

        String result = "";

        DownloadManager.Query DownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        DownloadQuery.setFilterById(downloadRef);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(DownloadQuery);
        if(cursor.moveToFirst()){
            result = DownloadStatus(cursor, downloadRef);
        }

        return result;

    }


    private String DownloadStatus(Cursor cursor, long DownloadId){

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        //int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        //String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                //reasonText = "Filename:\n" + filename;
                break;
        }


            Toast toast = Toast.makeText(activity,
                    "Download Status:" + "\n" + statusText + "\n" +
                            reasonText,
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        return statusText+" " +reasonText;


    }


}
