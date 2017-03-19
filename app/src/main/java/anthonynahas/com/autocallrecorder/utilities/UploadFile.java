package anthonynahas.com.autocallrecorder.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class UploadFile extends AsyncTask<Void, Void, Boolean>{

	private DropboxAPI dropboxApi;
	private String path;
	private Context context;
	
	public UploadFile(Context context, DropboxAPI dropboxApi, String path) {
		super();
		this.dropboxApi = dropboxApi;
		this.path = path;
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		
		final File tempDropboxDirectory = context.getCacheDir();
		File tempFileToUpload;
		FileWriter fileWriter = null;
		
		try {
			
			tempFileToUpload = File.createTempFile("file", ".txt",
					tempDropboxDirectory);
			
			fileWriter = new FileWriter(tempFileToUpload);
			fileWriter.write("Hello world");
			fileWriter.close();
			
			FileInputStream fileInputStream = new FileInputStream(tempFileToUpload);
			dropboxApi.putFile(path + "hello_world.txt", fileInputStream,
					tempFileToUpload.length(), null, null);
			tempFileToUpload.delete();
			
			return true;
		} catch (IOException ioe) {
			
		} catch (DropboxException de) {
			// TODO: handle exception
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if(result) {
			Toast.makeText(context, "File has been uploaded!",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "Error occured while processing the upload request",
					Toast.LENGTH_LONG).show();
		}
	}
}
