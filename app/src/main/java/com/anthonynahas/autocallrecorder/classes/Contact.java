package com.anthonynahas.autocallrecorder.classes;

import android.provider.ContactsContract;

import org.chalup.microorm.annotations.Column;

/**
 * Custom contact class that reflects the cursor received from a query done using the
 * Contact API 's content provider
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 08.06.17
 */

public class Contact {

    @Column(ContactsContract.PhoneLookup.CONTACT_ID)
    private long _mID;

    @Column(ContactsContract.CommonDataKinds.Phone.NUMBER)
    private String mNumber;

    @Column(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
    private String mName;

    public long get_ID() {
        return _mID;
    }

    public void set_ID(long _mID) {
        this._mID = _mID;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
