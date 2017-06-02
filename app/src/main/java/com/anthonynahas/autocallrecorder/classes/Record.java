package com.anthonynahas.autocallrecorder.classes;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.anthonynahas.autocallrecorder.providers.RecordDbContract;

import org.chalup.microorm.MicroOrm;
import org.chalup.microorm.annotations.Column;

import java.io.Serializable;

/**
 * Class that deal the record table location in content provider.
 *
 * @author Anthony Nahas
 * @version 0.1
 * @see <https://github.com/chalup/microorm
 * @since 25.04.2016
 */
public class Record implements Serializable, Parcelable {

    //implements Serializable
    private static final long serialVersionUID = -5060910544141464421L;

    @Column(RecordDbContract.RecordItem.COLUMN_ID)
    private String m_ID; //like mDate + number

    @Column(RecordDbContract.RecordItem.COLUMN_PATH)
    private String mPath;

    @Column(RecordDbContract.RecordItem.COLUMN_NUMBER)
    private String mNumber; //like +49 151 20 55555 2

    @Column(RecordDbContract.RecordItem.COLUMN_CONTACT_ID)
    private long mContactID;

    @Column(RecordDbContract.RecordItem.COLUMN_DATE)
    private long mDate;

    @Column(RecordDbContract.RecordItem.COLUMN_SIZE)
    private int mSize;

    @Column(RecordDbContract.RecordItem.COLUMN_DURATION)
    private int mDuration;

    @Column(RecordDbContract.RecordItem.COLUMN_IS_INCOMING)
    private boolean mIsIncoming; //like outgoing, incoming, custom

    @Column(RecordDbContract.RecordItem.COLUMN_IS_LOVE)
    private boolean mIsLove; // favorite or not

    @Column(RecordDbContract.RecordItem.COLUMN_IS_LOCKED)
    private boolean mIsLocked; // if locked --> audio to base64 in DB = safe and will be not deleted

    @Column(RecordDbContract.RecordItem.COLUMN_IS_TO_DELETE)
    private boolean mIsToDelete; // the audio file is in the recycle bin and will be soon deleted

//    @Column(RecordDbContract.Extended.COLUMN_TOTAL_INCOMING_CALLS)
    private int mTotalIncomingCalls;

//    @Column(RecordDbContract.Extended.COLUMN_TOTAL_OUTGOING_CALLS)
    private int mTotalOutgoingCall;

//    @Column(RecordDbContract.Extended.COLUMN_TOTAL_CALLS)
    private int mTotalCalls;

    private String mName;
    private int mRank;

    //base64 // TODO: 01.06.2017

    public Record() {
        super();
    }

    public Record(Cursor cursor, Object object) {
        if (cursor != null && cursor.getCount() > 0) {
            m_ID = cursor.getString(cursor
                    .getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID));
            mPath = cursor.getString(cursor
                    .getColumnIndex(RecordDbContract.RecordItem.COLUMN_PATH));
            mContactID = cursor.getLong(cursor
                    .getColumnIndex(RecordDbContract.RecordItem.COLUMN_CONTACT_ID));
            mNumber = cursor.getString(cursor
                    .getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER));
            mDuration = cursor.getInt(cursor
                    .getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION));
            mIsIncoming = cursor.getInt(cursor
                    .getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_INCOMING)) == 1;

        }
    }

    public Record(String m_ID,
                  String mNumber,
                  long mContactID,
                  long mDate, int mSize,
                  int mDuration,
                  boolean mIsIncoming,
                  boolean mIsLove) {

        super();
        this.m_ID = m_ID;
        this.mNumber = mNumber;
        this.mContactID = mContactID;
        this.mDate = mDate;
        this.mSize = mSize;
        this.mDuration = mDuration;
        this.mIsIncoming = mIsIncoming;
        this.mIsLove = mIsLove;
    }

    public String get_ID() {
        return m_ID;
    }

    public void set_ID(String m_ID) {
        this.m_ID = m_ID;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public long getContactID() {
        return mContactID;
    }

    public void setContactID(long mContactID) {
        this.mContactID = mContactID;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int mSize) {
        this.mSize = mSize;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public boolean isIsIncoming() {
        return mIsIncoming;
    }

    public void setIsIncoming(boolean mIsIncoming) {
        this.mIsIncoming = mIsIncoming;
    }

    public void setIsIncoming(int mIsIncoming) {
        this.mIsIncoming = mIsIncoming == 1;
    }

    public boolean isIsLove() {
        return mIsLove;
    }

    public void setIsLove(boolean mIsLove) {
        this.mIsLove = mIsLove;
    }

    public void setIsLove(int mIsLove) {
        this.mIsLove = mIsLove == 1;
    }

    public boolean isIsLocked() {
        return mIsLocked;
    }

    public void setIsLocked(boolean mIsLocked) {
        this.mIsLocked = mIsLocked;
    }

    public void setIsLocked(int mIsLocked) {
        this.mIsLocked = mIsLocked == 1;
    }

    public boolean isIsToDelete() {
        return mIsToDelete;
    }

    public void setIsToDelete(boolean mIsToDelete) {
        this.mIsToDelete = mIsToDelete;
    }

    public void setIsToDelete(int mIsToDelete) {
        this.mIsToDelete = mIsToDelete == 1;
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

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(RecordDbContract.RecordItem.COLUMN_ID, m_ID); //string
        values.put(RecordDbContract.RecordItem.COLUMN_PATH, mPath); //String
        values.put(RecordDbContract.RecordItem.COLUMN_NUMBER, mNumber); //string
        values.put(RecordDbContract.RecordItem.COLUMN_CONTACT_ID, mContactID); //long
        values.put(RecordDbContract.RecordItem.COLUMN_DATE, mDate); //long
        values.put(RecordDbContract.RecordItem.COLUMN_SIZE, mSize); //int
        values.put(RecordDbContract.RecordItem.COLUMN_DURATION, mDuration); //int
        values.put(RecordDbContract.RecordItem.COLUMN_IS_INCOMING, mIsIncoming ? 1 : 0); //int
        values.put(RecordDbContract.RecordItem.COLUMN_IS_LOVE, mIsLove ? 1 : 0); //int
        values.put(RecordDbContract.RecordItem.COLUMN_IS_LOCKED, mIsLocked ? 1 : 0); //int
        values.put(RecordDbContract.RecordItem.COLUMN_IS_TO_DELETE, mIsToDelete ? 1 : 0); //int

        return values;
    }

    public static Record newInstance(Cursor cursor) {
        MicroOrm uOrm = new MicroOrm();
        return uOrm.fromCursor(cursor, Record.class);
    }

    public static final Parcelable.Creator<Record> CREATOR = new Parcelable.Creator<Record>() {

        @Override
        public Record createFromParcel(Parcel parcel) {
            Record record = new Record();
            record.m_ID = parcel.readString();
            record.mPath = parcel.readString();
            record.mNumber = parcel.readString();
            record.mContactID = parcel.readLong();
            record.mDate = parcel.readLong();
            record.mSize = parcel.readInt();
            record.mDuration = parcel.readInt();
            record.mIsIncoming = parcel.readByte() != 0;
            record.mIsLove = parcel.readByte() != 0;
            record.mIsLocked = parcel.readByte() != 0;
            record.mIsToDelete = parcel.readByte() != 0;
            record.mName = parcel.readString();
            record.mRank = parcel.readInt();
            record.mTotalIncomingCalls = parcel.readInt();
            record.mTotalOutgoingCall = parcel.readInt();
            record.mTotalCalls = parcel.readInt();

            return record;
        }

        @Override
        public Record[] newArray(int i) {
            return new Record[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(m_ID);
        parcel.writeString(mPath);
        parcel.writeString(mNumber);
        parcel.writeLong(mContactID);
        parcel.writeLong(mDate);
        parcel.writeInt(mSize);
        parcel.writeInt(mDuration);
        parcel.writeByte((byte) (mIsIncoming ? 1 : 0));
        parcel.writeByte((byte) (mIsLove ? 1 : 0));
        parcel.writeByte((byte) (mIsLocked ? 1 : 0));
        parcel.writeByte((byte) (mIsToDelete ? 1 : 0));
        parcel.writeString(mName);
        parcel.writeInt(mRank);
        parcel.writeInt(mTotalIncomingCalls);
        parcel.writeInt(mTotalOutgoingCall);
        parcel.writeInt(mTotalCalls);
    }

}
