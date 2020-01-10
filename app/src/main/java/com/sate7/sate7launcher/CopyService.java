package com.sate7.sate7launcher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.sate7.sate7launcher.uitl.FileUtil;

public class CopyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onCreate() {
        super.onCreate();
        FileUtil.CopyDirectory(this);
    }
}
