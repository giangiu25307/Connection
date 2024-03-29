package com.ConnectionProject.connection.libs.util;

import com.ConnectionProject.connection.libs.ByteBufferList;

import java.nio.ByteBuffer;

/**
 * Created by koush on 6/28/14.
 */
public class Allocator {
    final int maxAlloc;
    int currentAlloc = 0;
    int minAlloc = 2 << 11;

    public Allocator(int maxAlloc) {
        this.maxAlloc = maxAlloc;
    }

    public Allocator() {
        maxAlloc = ByteBufferList.MAX_ITEM_SIZE;
    }

    public ByteBuffer allocate() {
        return allocate(currentAlloc);
    }

    public ByteBuffer allocate(int currentAlloc) {
        return ByteBufferList.obtain(Math.min(Math.max(currentAlloc, minAlloc), maxAlloc));
    }

    public void track(long read) {
        currentAlloc = (int)read * 2;
    }

    public int getMaxAlloc() {
        return maxAlloc;
    }

    public void setCurrentAlloc(int currentAlloc) {
        this.currentAlloc = currentAlloc;
    }

    public int getMinAlloc() {
        return minAlloc;
    }

    public Allocator setMinAlloc(int minAlloc ) {
        this.minAlloc = Math.max(0, minAlloc);
        return this;
    }
}

