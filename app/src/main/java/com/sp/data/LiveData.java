package com.sp.data;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LiveData extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "Live Data";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private boolean newBool = true;
    Button Logs,stop,recordPause;
    private AccelDataCollector accelDataCollector = null;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        setContentView(R.layout.activity_live_data);
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(int i=0; i<sensors.size(); i++){
            Log.d(TAG, "onCreate: Sensor "+ i + ": " + sensors.get(i).toString());
        }
        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        mChart = (LineChart) findViewById(R.id.chart1);
        // enable description text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        // add empty data
        mChart.setData(data);
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
        feedMultiple();
        Logs = findViewById(R.id.button5);
        Logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LiveData.this,LogViewData.class);
                startActivity(intent);
            }
        });

        stop = findViewById(R.id.button4);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logs.setClickable(true);
                Logs.setEnabled(true);
                recordPause.setEnabled(true);
                accelDataCollector = null;
                Toast toast = Toast.makeText(context, "File writing success", duration);
                toast.show();
            }
        });

        recordPause = findViewById(R.id.recordButton);
        recordPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accelDataCollector = new AccelDataCollector(getApplicationContext());
                Logs.setClickable(false);
                Logs.setEnabled(false);
                recordPause.setEnabled(false);
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();
//                newBool = true;
//                try {
//                    String content   = "are you happy";
//                    File path = getApplicationContext().getFilesDir();
//                    System.out.println(path);
//                    FileOutputStream writer = null;
//                    writer = new FileOutputStream(new File(path,"fileName.txt"));
//                    writer.write(content.getBytes(StandardCharsets.UTF_8));
//                    writer.close();
//                    Toast toast1 = Toast.makeText(context, "text", duration);
//                    toast1.show();
//                    fileWrite("text");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    private void addEntry(SensorEvent event) {
        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
            data.notifyDataChanged();
            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();
            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);
            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    plotData = true;
                    newBool = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(newBool){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            System.out.println("x"+x+","+"y"+y+","+"z"+z+"\n");
            newBool = false;
        }
        if(plotData){
            addEntry(event);
            plotData = false;
        }
        if(accelDataCollector != null) {
            accelDataCollector.onSensorUpdate(event);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(LiveData.this);
        thread.interrupt();
        super.onDestroy();
    }

}