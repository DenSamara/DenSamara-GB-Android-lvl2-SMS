package ru.home.denis.konovalov.mysmsapplication;

import android.app.IntentService;
import android.content.Intent;

public class HeadlessSmsSendService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HeadlessSmsSendService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
