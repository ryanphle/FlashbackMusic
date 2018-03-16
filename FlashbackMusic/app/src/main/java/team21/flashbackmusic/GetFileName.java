package team21.flashbackmusic;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by James on 3/14/2018.
 */

public class GetFileName extends AsyncTask<String, Integer, String> {

        public interface GetFileNameListener {
            void onTaskCompleted(String result);
        }

    private final GetFileNameListener mListener;

    public GetFileName(GetFileNameListener listener) {
        mListener = listener;
    }

    protected String doInBackground(String[] urls)
    {
        URL url;
        String filename = null;
        try {
            Log.i("Fileurl", "real file url is " + urls[0]);

            url = new URL(urls[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            conn.setInstanceFollowRedirects(false);

            String depo = conn.getHeaderField("Content-Disposition");
            String type = conn.getHeaderField("Content-Type");


            Log.i("Fileurl", "Content-Type is " + type);

            Log.i("Fileurl", "Content-Disposition is " + depo);

            if (depo != null)
                filename = depo.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
            else
                filename = URLUtil.guessFileName(urls[0], depo, null);

            conn.disconnect();

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
        }
        return filename;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (mListener != null)
            mListener.onTaskCompleted(result);
    }

}
