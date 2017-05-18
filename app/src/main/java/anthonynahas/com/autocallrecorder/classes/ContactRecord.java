package anthonynahas.com.autocallrecorder.classes;

/**
 * Created by anahas on 17.05.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 17.05.2017
 */

public class ContactRecord {

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
}
