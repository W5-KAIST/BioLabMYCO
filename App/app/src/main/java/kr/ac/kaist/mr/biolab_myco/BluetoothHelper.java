package kr.ac.kaist.mr.biolab_myco;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by wjuni on 15. 11. 20..
 */
public class BluetoothHelper {
    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    public ArrayList<BleData> btData;
    public ArrayList<String> btLog;

    BluetoothHelper(Context context){
        this.mContext = context;
        btData = new ArrayList<>();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null){
            //emulator mode
            Log.e(getClass().getName(), "Bluetooth Adapter Not found. Entering Simulator Mode.");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            BluetoothDevice dev = device;
            if (dev == null || dev.getName() == null || !dev.getName().startsWith("BM-"))
                return;
            BleData bledata = new BleData(device.getName(), scanRecord);
            int storedIndex;
            if((storedIndex = bledata.findAtList(btData))>=0)
                btData.set(storedIndex, bledata);   //perform Update
            else
                btData.add(bledata);
        }
    };

    @SuppressWarnings("deprecation")
    public void startScan(){
        if(mBluetoothAdapter==null){
            Log.e(getClass().getName(), "LE Scan Start (Emulated)");
            btData.clear();
            return;
        }
        Log.e(this.getClass().getName(), "LE Scan Start");
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }


    @SuppressWarnings("deprecation")
    public void stopScan(){
        if(mBluetoothAdapter==null){
            Log.e(getClass().getName(), "LE Scan Stop (Emulated)");
            btData.add(new BleData("BM-DUMMY01", 50, 3, 29, true, 3.8f));
            btData.add(new BleData("BM-DUMMY02", 30, 1, 26, false, 4.1f));
            btData.add(new BleData("BM-DUMMY03", 30, 5, 22, true, 3.3f));
            btData.add(new BleData("BM-DUMMY04", 50, 0, 23, false, 4.2f));
            return;
        }
        Log.e(this.getClass().getName(), "LE Scan Stop");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}
