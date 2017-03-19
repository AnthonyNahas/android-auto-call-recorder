package anthonynahas.com.autocallrecorder.classes;

/**
 * Created by A on 25.04.16.
 */
public class Record {

    private String mID; //like mDate + number
    private String mType; //like outgoing, incoming, custom
    private long mDate;
    private String mNumber; //like +49 151 20 55555 2
    private String mFilePath;

    public Record() {
    }

    public Record(String ID, String type, long date, String filePath, String number) {
        mID = ID;
        mType = type;
        mDate = date;
        mFilePath = filePath;
        mNumber = number;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }
}
