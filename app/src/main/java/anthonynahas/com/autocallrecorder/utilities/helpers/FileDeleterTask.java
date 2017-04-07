package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by A on 25.11.16.
 */

public class FileDeleterTask extends AsyncTask<ArrayList<String>, Void, Boolean> {

    private static final String Tag = FileDeleterTask.class.getSimpleName();
    ProgressDialog mProgressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //send broadcast to show progress circular
    }

    @SafeVarargs
    @Override
    protected final Boolean doInBackground(ArrayList<String>... params) {

        ArrayList<String> paths = params[0];

        for(String path : paths){
            try{
                File f = new File(path);
                if(f.delete()){
                    Log.d(Tag,"file with path " + path + " has been successfully deleted");
                }
            }
            catch (Exception e){
                Log.e(Tag,"Error with file deleting",e);
            }

        }

        return true;
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }



    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        //send broadcast to show progress circular
        Log.d(Tag,"on post Execite: " + aBoolean);
    }


}
