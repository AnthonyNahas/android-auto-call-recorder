package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by A on 04.06.16.
 */
public class UploadAudioFile extends AsyncTask<Void,Void,Boolean> {

    private DropboxAPI dropboxApi;
    private Context context;
    private String dropbox_path;
    private String filePath;
    private String fileName;

    public UploadAudioFile(Context context, DropboxAPI dropboxApi, String dropbox_path, String filePath, String fileName) {
        super();
        this.dropboxApi = dropboxApi;
        this.dropbox_path = dropbox_path;
        this.context = context;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        //File tmpFile = new File(filePath,fileName);
        File tmpFile = new File(filePath);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(tmpFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            dropboxApi.putFileOverwrite(dropbox_path + fileName, fis, tmpFile.length(), null);
            return true;
        } catch (DropboxUnlinkedException e) {
            Log.e("DbExampleLog", "User has unlinked.");
        } catch (DropboxException e) {
            Log.e("DbExampleLog", "Something went wrong while uploading.");
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(result) {
            Toast.makeText(context, "File has been uploaded!",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Error occured while processing the upload request",
                    Toast.LENGTH_LONG).show();
        }
    }
}
