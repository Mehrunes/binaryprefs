package com.ironz.binaryprefs.lock;

import com.ironz.binaryprefs.exception.LockOperationException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class ProcessFileLock implements Lock {

    private static final String RWD_MODE = "rwd";

    private final File lockFile;

    private RandomAccessFile randomAccessFile;
    private FileChannel channel;
    private FileLock lock;

    ProcessFileLock(File lockFile) {
        this.lockFile = lockFile;
    }

    @Override
    public void lock() {
        try {
            randomAccessFile = new RandomAccessFile(lockFile, RWD_MODE);
            channel = randomAccessFile.getChannel();
            if (!lockFile.exists()) {
                randomAccessFile.seek(0);
                randomAccessFile.write(0);
            }
            lock = channel.lock();
        } catch (Exception e) {
            throw new LockOperationException(e);
        }
    }

    @Override
    public void unlock() {
        try {
            lock.release();
        } catch (Exception e) {
            throw new LockOperationException(e);
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Not implemented!");
    }
}