package team21.flashbackmusic;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.lang.UCharacter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

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

        //unZip("storage/emulated/0/Music/",fileName);

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

    /*
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

                 Log.i("zip song name",songName);

                 if(zipEntry.isDirectory()){


                     //songFiles.remove(songFiles.size()-1);

                     File fmd = new File(path+fileName);
                     fmd.mkdir();
                     continue;

                 }

                 Log.i("zip file name",path+fileName);


                 FileOutputStream fileOutputStream = new FileOutputStream(path+fileName);

                 int i = 0;

                 while((count = zipInputStream.read(buffer)) != -1){

                     Log.i("zip file count",""+count);
                     i = i + count;
                     Log.i("zip file total count",""+i);

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


*/

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
