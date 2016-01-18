package com.yu.wangy.mydemoentry.event;

/**
 * Created by wangyu on 2016/1/18.
 */
public class DownloadEvent {

    private boolean mResult;

    public DownloadEvent(boolean mResult) {
        this.mResult = mResult;
    }

    public boolean ismResult() {
        return mResult;
    }

    public void setmResult(boolean mResult) {
        this.mResult = mResult;
    }
}
