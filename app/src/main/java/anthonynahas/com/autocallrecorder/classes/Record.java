package anthonynahas.com.autocallrecorder.classes;

/**
 * Created by A on 25.04.16.
 *
 * @author Anthony Nahas
 * @version 0.1
 * @since 25.04.2016
 */
public class Record extends ContactRecord {

    private String m_ID; //like mDate + number
    private String mNumber; //like +49 151 20 55555 2
    private long mContactID;
    private long mDate;
    private int mSize;
    private int mDuration;
    private boolean mIsIncoming; //like outgoing, incoming, custom
    private boolean mIsLove; // favorite or not


    public Record() {
        super();
    }

    public Record(String m_ID,
                  String mNumber,
                  long mContactID,
                  long mDate, int mSize,
                  int mDuration,
                  boolean mIsIncoming,
                  boolean mIsLove) {

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

    public void setize(int mSize) {
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

    public boolean isIsLove() {
        return mIsLove;
    }

    public void setIsLove(boolean mIsLove) {
        this.mIsLove = mIsLove;
    }
}
