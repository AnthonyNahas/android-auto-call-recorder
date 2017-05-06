package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.OperationApplicationException;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Helper class that deals with contacts, like android's contact api and more
 * --> insert, retrieve, delete...
 *
 * @author Anthony Nahas
 * @version 1.0
 * @see //developer.android.com/reference/android/provider/ContactsContract.PhoneLookup.html
 * @since 03.06.2016
 */
public class ContactHelper {

    private static final String TAG = ContactHelper.class.getSimpleName();

    /**
     * Get a contact by name
     *
     * @param contactHelper - the used content resolver
     * @param startsWith    - the string to query contacts by name
     * @return
     */
    public static Cursor getContactCursorByName(ContentResolver contactHelper, String startsWith) {

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
                //cursor = contactHelper.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like \"" + startsWith + "%\"", null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            }
            cursor = contactHelper.query(uri, projection, selection, selectionArgs, orderBy);

            if (cursor != null) {
                cursor.moveToFirst();
                logContactCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: ", e);
        }
        return cursor;
    }

    public static boolean insertContact(ContentResolver contactAdder, String firstName, String mobileNumber) {
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

    public static void deleteContact(ContentResolver contactHelper, String number) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String[] args = new String[]{String.valueOf(getContactID(contactHelper, number))};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public static long getContactID(ContentResolver contactHelper, String number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup._ID}; // TODO: 05.05.2017 phone lookpo contact id and not _id
        Cursor cursor = null;
        try {
            cursor = contactHelper.query(contactUri, projection, null, null, null);
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

    public static String getContactName(ContentResolver contactHelper, String number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = null;
        try {
            cursor = contactHelper.query(contactUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
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

    /*
    * A read-only sub-directory of a single contact that contains the contact's primary photo.
    * The photo may be stored in up to two ways - the default "photo" is a thumbnail-sized image
    * stored directly in the data row, while the "display photo", if present, is a larger version
    * stored as a file.*/

    /**
     * @return the photo URI
     */
    public static Uri getContactPhotoUri(ContentResolver contactHelper, long id) {
        Cursor cursor = null;
        try {
            cursor = contactHelper.query(
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
     * @param contactHelper
     * @param contactId
     * @return
     */
    public static InputStream openLargeDisplayPhoto(ContentResolver contactHelper, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    contactHelper.openAssetFileDescriptor(displayPhotoUri, "r");
            assert fd != null;
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * Retrieving the thumbnail-sized photo
     *
     * @param contactHelper
     * @param contactId
     * @return
     */
    public static InputStream openThumbnailPhoto(ContentResolver contactHelper, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = contactHelper.query(photoUri,
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

    public static Bitmap getBitmapForContactID(ContentResolver contentResolver, int mode, long contactID) {

        InputStream in = null;
        Bitmap bitmap = null;
        BufferedInputStream buf = null;

        try {
            switch (mode) {
                case 0: //large
                    in = ContactHelper.openLargeDisplayPhoto(contentResolver, contactID);
                    break;
                case 1: //thumbnail
                    in = ContactHelper.openThumbnailPhoto(contentResolver, contactID);
                    break;
            }
            buf = new BufferedInputStream(in);
            bitmap = BitmapFactory.decodeStream(buf);

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

    private static void logContactCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                long Contact_contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                long data_raw_contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                long phone_lookup_contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup.CONTACT_ID)); //this is the right onw
                long phone_lookup_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                String contact_display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contact_number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d(TAG,
                        "ContactsContract.Contacts._ID "
                                + Contact_contact_id
                                + " Data.CONTACT_ID = "
                                + data_raw_contact_id
                                + " PhoneLookup._ID = "
                                + phone_lookup_contact_id
                                + " contact id = "
                                + phone_lookup_id
                                + " --> display name = "
                                + contact_display_name
                                + " --> number = "
                                + contact_number);
            } while (cursor.moveToNext());

            cursor.moveToFirst();
        }
    }
}
