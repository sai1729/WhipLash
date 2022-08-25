package com.sp.data;

import android.hardware.SensorEvent;

public interface IAccelerometer {
    void onSensorUpdate(SensorEvent event);
}
