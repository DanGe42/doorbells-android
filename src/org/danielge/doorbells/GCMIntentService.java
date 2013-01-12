package org.danielge.doorbells;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import com.google.android.gcm.GCMBaseIntentService;
import org.danielge.doorbells.utils.Utils;

public class GCMIntentService extends GCMBaseIntentService {
    public GCMIntentService() {
        super(Utils.SENDER_ID);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
    }

    @Override
    protected void onError(Context context, String s) {
    }

    @Override
    protected void onRegistered(Context context, String regId) {
    }

    private static class RegistrationTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    @Override
    protected void onUnregistered(Context context, String s) {
    }
}
