package team21.flashbackmusic;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.lang.UCharacter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by james on 3/7/2018.
 */

public class AlbumDownloadManager implements ContentDownload {

    File rootpath = new File("storage/emulated/0/Music");
    ArrayList<String> songFiles = new ArrayList<String>();
    Context context;
    DownloadManager downloadManager;
    MainActivity activity;
    long downloadRef;

    public AlbumDownloadManager (Context context){

        this.context = context;
        activity = (MainActivity) context;
        this.downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

    }


    public String checkStatus(){

        return Check_Status();
    }

    public String checkType(){

        return "Album";
    }


    public void updateList() {


        Uri fileuri = downloadManager.getUriForDownloadedFile(downloadRef);

        Log.i("download file uri",""+fileuri);
        Log.i("download file ID",""+downloadRef);


        String fileName = activity.getFileName(fileuri);
        SharedPreferences sharedPreferences = activity.getSharedPreferences("plays", MODE_PRIVATE);
        Gson gson = new Gson();

        Log.i("DownloadZIp","unzipping");

        Log.i("Unzip",fileName);

        try {
            unzip(new File("storage/emulated/0/Music/" + fileName), new File("storage/emulated/0/Music"));
        }
        catch(IOException e){

        }

        int count;

        File[] files = new File[songFiles.size()];

        Log.i("unzip files",""+songFiles.size());

        for(count = 0; count < songFiles.size(); count ++){


            files[count] = (new File("storage/emulated/0/Music/"+songFiles.get(count)));

        }

        try {
            activity.loadListofFiles(files, sharedPreferences, gson);
        }catch (IllegalAccessException e){


        }

        activity.downloadAlbum = false;

        Log.i("DownloadZIp","unzip success");




    }

    private void unzip(File downloadFile, File targetDirectory) throws IOException {

        ZipInputStream zipInputStream = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(downloadFile)));
        try {
            String songName;
            ZipEntry zipEntry;
            int count;
            byte[] buffer = new byte[8192];
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                songName = zipEntry.getName();
                songFiles.add(songName);
                File file = new File(targetDirectory, zipEntry.getName());
                File dir = zipEntry.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (zipEntry.isDirectory())
                    continue;
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                try {
                    while ((count = zipInputStream.read(buffer)) != -1)
                        fileOutputStream.write(buffer, 0, count);
                } finally {
                    fileOutputStream.close();
                }
            /* if time should be restored as well
            long time = zipEntry.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zipInputStream.close();
        }
    }

    public void download (String url){


        Uri uri = Uri.parse(url);
        Log.i("download get the uri",uri.toString());

        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setVisibleInDownloadsUi(true);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, URLUtil.guessFileName(url, null,null));
        Log.i("download set destination","storage/emulated/0/Music");




        downloadRef = downloadManager.enqueue(request);

        Log.i("download file ID",""+downloadRef+"url"+url);

        Log.i("download enque",request.toString());

        Toast toast = Toast.makeText(activity, "Download start", Toast.LENGTH_LONG);
        toast.show();

        Check_Status();




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
                break;
        }


        Log.i("Download Status: ", statusText + "\n" + reasonText);



        //return statusText+" " +reasonText;
        return statusText;


    }





}
