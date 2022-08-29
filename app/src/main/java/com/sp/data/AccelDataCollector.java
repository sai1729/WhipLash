package com.sp.data;

import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AccelDataCollector implements IAccelerometer{
    private int count = 0;
    private Context context;
    private final int MAX_EVENT_SIZE = 100;
    StringBuilder sensorData = new StringBuilder();
    private final String TAG = "AccelDataCollector";

    public AccelDataCollector(Context ctx) {
        context = ctx;
    }
    @Override
    public void onSensorUpdate(SensorEvent event) {
        synchronized (sensorData) {
            sensorData.append(event.timestamp).append(",").append(event.values[0]).append(",").append(event.values[1]).append(",").append(event.values[2]).append("\n");
            count++;

            if(count>=MAX_EVENT_SIZE) {
                Log.d("TEST_SD_DATA", "Initiating write data to the file, current Thread = " + Thread.currentThread().getName());
                new Thread(new WriteToFile(new String(sensorData), context)).start();
                count = 0;
                sensorData.setLength(0);

            }
        }
    }


}
