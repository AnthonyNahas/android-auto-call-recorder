package com.anthonynahas.autocallrecorder.models;

import com.anthonynahas.autocallrecorder.providers.RecordDbContract;

import org.chalup.microorm.annotations.Column;

/**
 * Created by anahas on 02.06.2017.
 *
 * @author Anthony Nahas
 * @since 02.06.17
 * @version 1.0
 */

public class RecordExtended extends Record {

    @Column(RecordDbContract.Extended.COLUMN_TOTAL_INCOMING_CALLS)
    private int mTotalIncomingCalls;

    @Column(RecordDbContract.Extended.COLUMN_TOTAL_OUTGOING_CALLS)
    private int mTotalOutgoingCall;

    @Column(RecordDbContract.Extended.COLUMN_TOTAL_CALLS)
    private int mTotalCalls;

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

    public int getTotalCalls() {
        return mTotalCalls;
    }

    public void setTotalCalls(int mTotalCalls) {
        this.mTotalCalls = mTotalCalls;
    }
}
