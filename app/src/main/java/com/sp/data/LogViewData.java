package com.sp.data;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;

public class LogViewData extends AppCompatActivity {

    Button logValueOne;
    Button logValueTwo;
    Button startNewActivityValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view_data);
        logValueOne = findViewById(R.id.button2);
        logValueTwo = findViewById(R.id.button3);
        startNewActivityValue = findViewById(R.id.button4);
//        ConstraintLayout rl = (ConstraintLayout)findViewById(R.id.activity_log_view_data);
//        rl.setBackgroundColor(Color.RED);

//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        graph.addSeries(series);

        startNewActivityValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogViewData.this,LiveData.class);
                startActivity(intent);
            }
        });

        logValueOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filePath = getApplicationContext().getExternalFilesDir(null) + File.separator + "Accelerometer" + File.separator + "accelerometer_data.csv";
                System.out.println(filePath);
                List<String[]> rows = new ArrayList<>();
                CSVReader csvReader = new CSVReader(LogViewData.this, filePath);
                try {
                    rows = csvReader.readCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < rows.size(); i++) {
                    System.out.println(String.format("row %s: %s, %s", i, rows.get(i)[0], rows.get(i)[1]));
                }
            }
        });

        logValueTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogViewData.this,LiveData.class);
                startActivity(intent);
            }
        });
    }
}