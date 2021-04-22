package com.example.connection.libs;

import com.example.connection.libs.DataEmitter;

/**
 * Created by koush on 5/28/13.
 */
public interface DataTrackingEmitter extends com.example.connection.libs.DataEmitter {
    interface DataTracker {
        void onData(int totalBytesRead);
    }
    void setDataTracker(DataTracker tracker);
    DataTracker getDataTracker();
    int getBytesRead();
    void setDataEmitter(DataEmitter emitter);
}
