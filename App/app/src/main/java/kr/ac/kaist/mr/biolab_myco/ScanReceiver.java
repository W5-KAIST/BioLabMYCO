package kr.ac.kaist.mr.biolab_myco;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScanReceiver extends BroadcastReceiver {
    public ScanReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(getClass().getName(), "Somthing Received");
        if(intent.getAction()!=null && intent.getAction().equals("com.test.test")){
            Log.e(getClass().getName(), "Confirmed as I sent");
            //MainActivity main = (MainActivity) context;
        }
    }
}
