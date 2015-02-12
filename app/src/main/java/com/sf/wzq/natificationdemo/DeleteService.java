package com.sf.wzq.natificationdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DeleteService extends Service {
    public DeleteService() {
        System.out.println("delete the notification and start DeleteService");
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
