package com.anthonynahas.autocallrecorder.utilities.helpers;

import android.content.AsyncQueryHandler;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.dagger.annotations.ApplicationContext;
import com.anthonynahas.autocallrecorder.models.Record;
import com.anthonynahas.autocallrecorder.providers.cursors.CursorLogger;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Helper class that deals with contacts, like android's contact api and more
 * --> insert, retrieve, delete...
 *
 * @author Anthony Nahas
 * @version 1.0
 * @see //developer.android.com/reference/android/provider/ContactsContract.PhoneLookup.html
 * @since 03.06.2016
 */
@Singleton
public class ContactHelper {

    private Context mContext;
    private ContentResolver mContentResolver;
    private MemoryCacheHelper mMemoryCacheHelper;
    private CursorLogger mCursorLogger;

    @Inject
    public ContactHelper
            (@ApplicationContext Context mContext,
             ContentResolver mContentResolver,
             MemoryCacheHelper mMemoryCacheHelper,
             CursorLogger mCursorLogger) {
        this.mContext = mContext;
        this.mContentResolver = mContentResolver;
        this.mMemoryCacheHelper = mMemoryCacheHelper;
        this.mCursorLogger = mCursorLogger;
    }

    private static final String TAG = ContactHelper.class.getSimpleName();

    /**
     * Get a contact by name
     *
     * @param startsWith - the string to query contacts by name
     * @return
     */
    public Cursor getContactCursorByName(String startsWith) {

        Cursor cursor = null;

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection =
                {
                        ContactsContract.Contacts._ID,
                        ContactsContract.Data.CONTACT_ID,
                        ContactsContract.Data.RAW_CONTACT_ID,
                        ContactsContract.PhoneLookup._ID,
                        ContactsContract.PhoneLookup.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
        String selection = null;
        String[] selectionArgs = null;
        String orderBy = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        try {
            if (startsWith != null && !startsWith.equals("")) {
                selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like \"" + startsWith + "%\"";
                //cursor = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like \"" + startsWith + "%\"", null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            }
            cursor = mContentResolver.query(uri, projection, selection, selectionArgs, orderBy);

            if (cursor != null) {
                cursor.moveToFirst();
                mCursorLogger.log(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: ", e);
        }
        return cursor;
    }


    public String[] getContactIDsByName(Cursor cursor) {

        ArrayList<String> idsList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String contact_id;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.CONTACT_ID));
                } else {
                    contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                }

                if (!idsList.contains(contact_id)) {
                    Log.d(TAG, "on getContactIDsByName --> add: " + contact_id);
                    idsList.add(contact_id);
                }

            } while (cursor.moveToNext());
        }

        return idsList.toArray(new String[idsList.size()]);
    }

    public boolean insertContact(ContentResolver contactAdder, String firstName, String mobileNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName).build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void deleteContact(String number) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String[] args = new String[]{String.valueOf(getContactID(number))};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public long getContactID(String number) {

        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup._ID}; // TODO: 05.05.2017 phone lookpo contact id and not _id
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(contactUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                return cursor.getLong(personID);
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }


    public String getContactName(String number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(contactUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                mCursorLogger.log(cursor);
                int contactNameRow = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                return cursor.getString(contactNameRow);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public void getContactNameAsyncAndPost(@NonNull final Record record, final TextView textView) {

        String cachedContactName = mMemoryCacheHelper.getMemoryCacheForContactsName(record.getNumber());

        if (cachedContactName != null) {
            textView.setText(cachedContactName);
            record.setName(cachedContactName);

        } else {

            AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(mContentResolver) {
                @Override
                protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            mCursorLogger.log(cursor);
                            int contactNameRow = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                            String contactName = cursor.getString(contactNameRow);

                            if (contactName != null && contactName.length() > 0) {
                                record.setName(contactName);
                                textView.setText(contactName);
                                record.setName(contactName);
                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Error: --> ", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            };

            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(record.getNumber()));
            String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};

            asyncQueryHandler.startQuery(0, null, contactUri, projection, null, null, null);
        }
    }

    public void getContactUriForPhotoAsyncAndPost(@NonNull Record record, final Boolean big, final ImageView imageView) {

        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(mContentResolver) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        int contactIDColumn = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                        long contactID = cursor.getLong(contactIDColumn);

                        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);

                        Uri photoUri = Uri.withAppendedPath(contactUri,
                                big ?
                                        ContactsContract.Contacts.Photo.DISPLAY_PHOTO //large
                                        :
                                        ContactsContract.Contacts.Photo.CONTENT_DIRECTORY //thumbnails
                        );

                        Picasso.with(mContext)
                                .load(photoUri)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.custompic3)
                                .error(R.drawable.custmtranspprofpic150px)
                                .into(imageView);

                    }
                } catch (Exception e) {
                    Log.d(TAG, "Error: --> ", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        };

        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(record.getNumber()));
        String[] projection = {ContactsContract.PhoneLookup._ID}; //

        asyncQueryHandler.startQuery(0, null, contactUri, projection, null, null, null);

    }

    /*
    * A read-only sub-directory of a single contact that contains the contact's primary photo.
    * The photo may be stored in up to two ways - the default "photo" is a thumbnail-sized image
    * stored directly in the data row, while the "display photo", if present, is a larger version
    * stored as a file.*/

    /**
     * @return the photo URI
     */
    public Uri getContactPhotoUri(long id) {
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Uri contact = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        return Uri.withAppendedPath(contact, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    /**
     * Retrieving the larger photo version
     *
     * @param contactId
     * @return
     */
    public InputStream openLargeDisplayPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    mContentResolver.openAssetFileDescriptor(displayPhotoUri, "r");
            assert fd != null;
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * Retrieving the thumbnail-sized photo
     *
     * @param contactId
     * @return
     */
    public InputStream openThumbnailPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = mContentResolver.query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * Get the profile pic of a contact by contact id as bitmap
     *
     * @param mode      - whether in large mode (0) or thumbnails (1)
     * @param contactID - the id of the target contact
     * @return - the bitmap of the contact if it's available
     */
    public Bitmap getBitmapForContactID(int mode, long contactID) {

        InputStream in = null;
        Bitmap bitmap = null;
        BufferedInputStream buf = null;

        try {
            switch (mode) {
                case 0: //large
                    in = openLargeDisplayPhoto(contactID);
                    break;
                case 1: //thumbnail
                    in = openThumbnailPhoto(contactID);
                    break;
            }
            if (in != null) {
                buf = new BufferedInputStream(in);
                bitmap = BitmapFactory.decodeStream(buf);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error reading file", e);
        } finally {

            try {
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error: ", e);
            }
        }
        return bitmap;
    }
}
