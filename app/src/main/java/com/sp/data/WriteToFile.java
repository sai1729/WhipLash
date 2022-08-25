package com.sp.data;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class WriteToFile implements Runnable{
    private String sData;
    private Context context;

    public WriteToFile(String _dataToWrite, Context ctx) {
        sData = _dataToWrite;
        context = ctx;
    }

    @Override
    public void run() {
        try {
            String filePath = context.getExternalFilesDir(null) + File.separator + "Accelerometer" + File.separator + "accelerometer_data.csv";
            File accelFile = new File(filePath);
            System.out.println(filePath);
            if(!accelFile.exists()) {
                if(!accelFile.getParentFile().exists()) {
                    accelFile.getParentFile().mkdirs();
                }
                accelFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(filePath, true);
            System.out.println(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(sData);
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
            Log.d("TEST_SD_DATA", "Completed writing file current Thread = " + Thread.currentThread().getName());
        } catch(Exception e) {
            Log.d("TEST_SD_DATA", "Exception " + e.getLocalizedMessage());
        }
    }
}
