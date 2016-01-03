package kr.ac.kaist.mr.biolab_myco;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BleObserver extends IntentService {
    private BluetoothHelper mBluetoothHelper;
    public static final int TIME_TO_SCAN = 5000;        //in ms
    private static final int SCAN_PERIOD = 120;        //in s
    public static long LastScanDate = (new java.util.Date()).getTime();
    private boolean finish = false;
    public Integer Count = 0;
    public static ArrayList<BleData> scanResult;
    public BleObserver() {
        super("");
    }
    public BleObserver(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (!finish) {
            boolean perform = false;
            synchronized (Count) {
                if (Count <= 0) {
                    perform = true;
                    Count = SCAN_PERIOD;
                } else
                    Count--;
            }
            if (perform)
                performScan();
            else {
                try{
                    Thread.sleep(1000);
                }catch (Exception ex){}
            }
            //Log.e("thread Loop");
        }
    }
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    private void performScan(){

        Log.e(getClass().getName(), "Bluetooth start.");
        mBluetoothHelper.startScan();
        Runnable task = new Runnable() {
            public void run() {
                mBluetoothHelper.stopScan();
                scanResult = new ArrayList<>();
                synchronized (scanResult){
                    if(mBluetoothHelper.btData!=null)
                        scanResult.addAll(mBluetoothHelper.btData);
                }
                LastScanDate = (new java.util.Date()).getTime();
                Log.e(getClass().getName(), "Bluetooth stop.");
                Log.e("Service", "Total " + scanResult.size() + " Entries Found.");
            }
        };
        worker.schedule(task, TIME_TO_SCAN, TimeUnit.MILLISECONDS);



//        Intent intent = new Intent("kr.ac.kaist.mr.biolab_myco.MainActivity$ScanReceiver");
//        intent.putExtra("btScanResult", scanResult.size());
//        Log.e(getClass().getName(), "Ready to Send Broadcast");
////
////        sendBroadcast(intent);
//        Intent i = new Intent("kr.ac.kaist.mr.biolab_myco.ScanReceiver");
//        Log.e(getClass().getName(), "Ready to Send Broadcast to " + i.getAction());
//        sendBroadcast(i);
//        Log.e(getClass().getName(), "Sent Broadcast");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mBluetoothHelper = new BluetoothHelper(this);
        if(intent.getExtras()!=null && intent.getExtras().getBoolean("force", false)){
            performScan();
            stopSelf();
        }
        Log.e("Service", "Service has been Started.");
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
