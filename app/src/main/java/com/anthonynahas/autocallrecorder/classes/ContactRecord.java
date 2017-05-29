package com.anthonynahas.autocallrecorder.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * This class corresponds to each contact that have (been) called.
 * Furthermore, this class implement Serializable and is Parcelable -->
 * Goal: passing the object by intent.
 * <p>
 * <p>
 * https://www.javacodegeeks.com/2014/01/android-tutorial-two-methods-of-passing-object-by-intent-serializableparcelable.html
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 17.05.2017
 */

public class ContactRecord implements Serializable, Parcelable {

    //implements Serializable
    private static final long serialVersionUID = -7060210544600464481L;

    private String mName;
    private int mTotalIncomingCalls;
    private int mTotalOutgoingCall;
    private int mRank;


    public ContactRecord() {
    }

    public ContactRecord(int mTotalIncomingCalls, int mTotalOutgoingCall, int mRank) {
        this.mTotalIncomingCalls = mTotalIncomingCalls;
        this.mTotalOutgoingCall = mTotalOutgoingCall;
        this.mRank = mRank;
    }

    public ContactRecord(String mName, int mTotalIncomingCalls, int mTotalOutgoingCall, int mRank) {
        this.mName = mName;
        this.mTotalIncomingCalls = mTotalIncomingCalls;
        this.mTotalOutgoingCall = mTotalOutgoingCall;
        this.mRank = mRank;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getTotalIncomingCalls() {
        return mTotalIncomingCalls;
    }

    public void setTotalIncomingCalls(int mTotalIncomingCalls) {
        this.mTotalIncomingCalls = mTotalIncomingCalls;
    }

    public int getTotalOutgoingCall() {
        return mTotalOutgoingCall;
    }

    public void setTotalOutgoingCall(int mTotalOutgoingCall) {
        this.mTotalOutgoingCall = mTotalOutgoingCall;
    }

    public int getRank() {
        return mRank;
    }

    public void setRank(int mRank) {
        this.mRank = mRank;
    }


    //implement Parcelable
    public static final Parcelable.Creator<ContactRecord> CREATOR = new Parcelable.Creator<ContactRecord>() {
        @Override
        public ContactRecord createFromParcel(Parcel parcel) {
            ContactRecord contactRecord = new ContactRecord();
            contactRecord.mName = parcel.readString();
            contactRecord.mRank = parcel.readInt();
            contactRecord.mTotalIncomingCalls = parcel.readInt();
            contactRecord.mTotalOutgoingCall = parcel.readInt();
            return contactRecord;
        }

        @Override
        public ContactRecord[] newArray(int i) {
            return new ContactRecord[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mName);
        parcel.writeInt(mRank);
        parcel.writeInt(mTotalIncomingCalls);
        parcel.writeInt(mTotalOutgoingCall);
    }

}
