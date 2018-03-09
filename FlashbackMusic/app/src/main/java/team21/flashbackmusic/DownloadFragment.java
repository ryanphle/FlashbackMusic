package team21.flashbackmusic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ryanle on 3/4/18.
 */

public class DownloadFragment extends android.support.v4.app.DialogFragment {

    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        view=inflater.inflate(R.layout.download_body, null) ;

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);

        final EditText url = (EditText) view.findViewById(R.id.Music_URL);




        builder.setMessage(R.string.download_prompt)
                .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        //Toast toast = Toast.makeText(((MainActivity) getActivity()), "Music Download Complete", Toast.LENGTH_LONG);
                        //toast.show();

                        Log.i("Song URL","" + url.getText());

                        //Uri uri = Uri.parse(url.getText().toString());

                        Log.i("Song URL","test");

                        String fileextension = url.getText().toString().substring( url.getText().toString().lastIndexOf('.')+1,
                                                                                   url.getText().toString().length() );

                        String type;

                        if(fileextension.equals("zip"))
                            type = "Album";
                        else
                            type = "Song";



                        ((MainActivity) getActivity()).startDownload(url.getText().toString(), type);

                        Log.i("download type",type);

                        // DOWNLOAD
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
