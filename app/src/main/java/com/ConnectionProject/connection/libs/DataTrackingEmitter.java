package com.ConnectionProject.connection.libs;

/**
 * Created by koush on 5/28/13.
 */
public interface DataTrackingEmitter extends com.ConnectionProject.connection.libs.DataEmitter {
    interface DataTracker {
        void onData(int totalBytesRead);
    }
    void setDataTracker(DataTracker tracker);
    DataTracker getDataTracker();
    int getBytesRead();
    void setDataEmitter(DataEmitter emitter);
}
