package team21.flashbackmusic;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    Long downloadRef;

    public AlbumDownloadManager (Context context){

        this.context = context;
        activity = (MainActivity) context;
        this.downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

    }


    public String checkStatus(){

        String result = "";


        return result;
    }

    public String checkType(){

        return "Album";
    }


    public void updateList() {


        Uri fileuri = downloadManager.getUriForDownloadedFile(downloadRef);
        String fileName = activity.getFileName(fileuri);
        SharedPreferences sharedPreferences = activity.getSharedPreferences("plays", MODE_PRIVATE);
        Gson gson = new Gson();

        Log.i("DownloadZIp","unzipping");

        Log.i("Unzip",fileName);

        unZip("storage/emulated/0/Music/",fileName);

        int count;

       // File[] files = new File[songFiles.size()];

        Log.i("unzip files",""+songFiles.size());
/*
        for(count = 0; count < songFiles.size(); count ++){


            files[count] = (new File("storage/emulated/0/Music/"+songFiles.get(count)));

        }

        try {
            activity.loadListofFiles(files, sharedPreferences, gson);
        }catch (IllegalAccessException e){


        }
*/
        Log.i("DownloadZIp","unzip success");



    }
     private boolean unZip (String path, String fileName){

         InputStream inputStream;
         ZipInputStream zipInputStream;
         try{

             String songName;

             inputStream = new FileInputStream(path+fileName);
             zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));

             ZipEntry zipEntry;

             byte[] buffer = new byte[1024];
             int count;

             while((zipEntry = zipInputStream.getNextEntry())!=null){

                 songName = zipEntry.getName();
                 //songFiles.add(songName);


                 if(zipEntry.isDirectory()){


                     //songFiles.remove(songFiles.size()-1);

                     File fmd = new File(path+fileName);
                     fmd.mkdir();
                     continue;

                 }

                 FileOutputStream fileOutputStream = new FileOutputStream(path+fileName);

                 while((count = zipInputStream.read(buffer)) != -1){

                     fileOutputStream.write(buffer,0,count);

                 }

                 fileOutputStream.close();
                 zipInputStream.closeEntry();
             }

             zipInputStream.close();

         }
         catch (IOException e){

             e.printStackTrace();
             return false;


         }


         return true;



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
