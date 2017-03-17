package com.example.abe.timer1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by abe on 17/03/02.
 * gittest
 * branc
 */

public class SubActivity extends AppCompatActivity {
    private CombinedChart mChart;
    private ArrayList<BarEntry> timerData = new ArrayList<>();
    private ArrayList<Entry> pdata = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private int item;
    private float vw = 10f;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.graph);

        mChart = (CombinedChart)findViewById(R.id.comb);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(true);
        mChart.setDrawBarShadow(false);
        mChart.setDescription("");

        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        mChart.getLegend().setEnabled(true);


        final YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        YAxisValueFormatter yAxisValueFormatter = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                long hh = (long)value/36000;
                long mm = (long)(value%36000)/600;
                String d;
                if(mm == 0){
                    d = ""+String.format("%dh",hh);
                }else{
                    d = ""+String.format("%2dh%02dm",hh,mm);
                }
                return d;
            }
        };
        leftAxis.setValueFormatter(yAxisValueFormatter);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMaxValue(110f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        csvopen();

        float p;
        if(item >= vw+1) {
            p = item / vw;
        }else{
            p = 1f;
        }

        mChart.setScaleMinima(p,1f);
        mChart.moveViewToX(item);

        CombinedData data = new CombinedData(date);
        data.setData(generateLineData());
        data.setData(generateBar());

        mChart.setData(data);
        mChart.invalidate();


    }

    private LineData generateLineData() {
        LineData d = new LineData();
        LineDataSet set = new LineDataSet(pdata, "P (%)");

        set.setColor(Color.rgb(0, 0, 255));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(0,0,255));
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(0, 0, 200));

        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                String d = ""+value;
                return d;
            }
        };
        set.setValueFormatter(valueFormatter);

        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBar(){
        BarDataSet set2 = new BarDataSet(timerData, "");
        set2.setStackLabels(new String[]{"C", "D"});
        set2.setColors(new int[]{Color.rgb(0, 240, 0), Color.rgb(220, 0, 0)});
        set2.setValueTextColor(Color.rgb(0, 0, 0));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setDrawValues(true);

        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                long hh = (long)value/36000;
                long mm = (long)(value%36000)/600;
                String d;
                if(mm == 0){
                    d = ""+String.format("%dh",hh);
                }else{
                    d = ""+String.format("%2dh%02dm",hh,mm);
                }
                return d;
            }
        };

        set2.setValueFormatter(valueFormatter);

        BarData d = new BarData(date,set2);

        return d;
    }

    public void csvopen(){
        InputStream in = null;
        InputStreamReader sr = null;
        BufferedReader br = null;

        try {
            in = openFileInput("data.csv");
            sr = new InputStreamReader(in);
            br = new BufferedReader(sr);
            String line;
            int i = 0;

            while ((line = br.readLine()) != null){
                StringTokenizer st = new StringTokenizer(line,",");
                String valdate = st.nextToken();
                float valc = Float.parseFloat(st.nextToken());
                float vald = Float.parseFloat(st.nextToken());
                float valp = Float.parseFloat(st.nextToken());

                st = new StringTokenizer(valdate,"/");
                st.nextToken();
                String d = String.format("%d/",Long.parseLong(st.nextToken()));
                d += String.format("%d",Long.parseLong(st.nextToken()));

                date.add(d);
                timerData.add(new BarEntry(new float[]{valc,vald},i));
                pdata.add(new Entry(valp,i));

                i++;
            }
            item = i;

            if(i<vw) {
                for (int c = 0; c < vw - i; c++) {
                    date.add("");
                }
            }

        } catch (IOException e) {
            Log.e("Internal", "IO Exception " + e.getMessage(), e);
        } finally {
            try {
                if (br != null) { br.close(); }
                if (sr != null) { sr.close(); }
                if (in != null) { in.close(); }
            } catch (IOException e) {
                Log.e("Internal", "IO Exception " + e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;

            case R.id.set1:
                deleteFile("data.csv");
                deleteFile("lastdata.csv");
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sub_menu, menu);
        return true;
    }

}