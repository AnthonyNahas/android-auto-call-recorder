package com.anthonynahas.autocallrecorder.utilities.asyncTasks.contactAPI;

import android.net.Uri;
import android.os.AsyncTask;

import com.anthonynahas.autocallrecorder.utilities.helpers.ContactHelper;

import javax.inject.Inject;

/**
 * Created by A on 17.06.17.
 *
 * @param String --> contact number
 * @param Void --> progress
 * @param Uri --> photo uri
 */

public class ContactUriAsyncTask extends AsyncTask<String,Void, Uri> {

    private ContactHelper mContactHelper;



    @Override
    protected Uri doInBackground(String... params) {
        return null;
    }


}
