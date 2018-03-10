package team21.flashbackmusic;

import java.net.URL;

/**
 * Created by james on 3/7/2018.
 */

public interface ContentDownload {

    public void download (String url);

    public String checkStatus();

    public String checkType();

    public void updateList();

}
