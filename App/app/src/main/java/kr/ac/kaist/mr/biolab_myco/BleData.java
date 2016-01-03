package kr.ac.kaist.mr.biolab_myco;

import java.util.List;

/**
 * Created by wjuni on 15. 11. 20..
 */
public class BleData {
    private int R;
    private int count;
    private int temperature;
    private boolean isMotorDone;
    private boolean valid = false;
    private float batteryVoltage;
    private String deviceName;
    private String RAW;

    public BleData(String deviceName, int R, int count, int temp, boolean isMotorDone, float battVolt){
        this.R = R; this.count = count; this.temperature = temp; this.isMotorDone = isMotorDone; this.batteryVoltage = battVolt;
        this.deviceName = deviceName; this.valid = true; this.RAW = "TEST_RAW_DATA";
    }

    public BleData(String name, byte[] scanRecord){
        deviceName = name;
        if (scanRecord.length >= 26) {
            try{
                R = Integer.parseInt(new String(scanRecord).substring(18, 20));
            } catch (Exception ex) {
                R = 0;
            }
            isMotorDone = (scanRecord[20] == '1');
            temperature = (scanRecord[21] < 0 ? 256 + scanRecord[21] : scanRecord[21]);
            batteryVoltage = (float)(scanRecord[22] < 0 ? 256 + scanRecord[22] : scanRecord[22])/10f;
            count = (scanRecord[23] < 0 ? 256 + scanRecord[23] : scanRecord[23]);
            valid = true;
            RAW = "";
            //RAW = new String(scanRecord).substring(18, 21) + "_[" + temperature + "]_[" + batteryVoltage + "]_[" + (float)scanRecord[22] + "]_[" + count+ "]";
        }
    }

    public int findAtList(List<BleData> lst){
        for(int i=0;i<lst.size();i++){
            if(lst.get(i).deviceName != null && lst.get(i).deviceName.equals(deviceName))
                return i;
        }
        return -1;
    }

    public int getR() {
        return R;
    }

    public int getCount() {
        return count;
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean isMotorDone() {
        return isMotorDone;
    }

    public boolean isValid() {
        return valid;
    }

    public float getBatteryVoltage() {
        return batteryVoltage;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getRAW() { return RAW; }
}
