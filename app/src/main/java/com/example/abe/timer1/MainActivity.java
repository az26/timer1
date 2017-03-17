package com.example.abe.timer1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private Button st,btnc,btnd;
    private Timer ctimer = null;
    private Timer dtimer = null;
    private countTimerC ctask;
    private countTimerD dtask;
    private TextView tview,pview;
    private boolean sw = true;
    private long before;
    private long countc = 0;
    private long countd = 0;
    private long countt = 0;
    private boolean first = true;
    private boolean add = false;
    private String dateText;

    private int period = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tview = (TextView) findViewById(R.id.total);
        pview = (TextView) findViewById(R.id.percentage);

        btnc = (Button) findViewById(R.id.timerc);
        btnd = (Button) findViewById(R.id.timerd);
        st = (Button) findViewById(R.id.st);

        st.setSelected(false);
        btnc.setEnabled(false);
        btnd.setEnabled(false);
        btnc.setSelected(true);
        btnd.setSelected(true);

        File file = this.getFileStreamPath("lastdata.csv");
        boolean f = file.exists();
        if(f == true) {lastData();}

        st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (st.isSelected() == false) {
                    if (first == true) {
                        Date date = new Date(System.currentTimeMillis());
                        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                        dateText = format.format(date);
                        first = false;
                    }

                    if (sw == true) {
                        btnc.setEnabled(true);
                        countUpC();
                        cChangeTextSize(true,btnd);
                    } else {
                        countUpD();
                        btnd.setEnabled(true);
                    }
                    btnc.setSelected(false);
                    btnd.setSelected(false);
                    st.setSelected(true);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                }else{
                    if(sw == true){
                        cancelC();
                        btnc.setSelected(true);
                        btnc.setEnabled(false);
                    }else{
                        cancelD();
                        btnd.setSelected(true);
                        btnd.setEnabled(false);
                    }
                    st.setSelected(false);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });

        btnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelC();
                btnc.setEnabled(false);
                btnd.setEnabled(true);
                cChangeTextSize(false,btnd);
                cChangeTextSize(true,btnc);
                countUpD();
                sw = false;
            }
        });

        btnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelD();
                btnd.setEnabled(false);
                btnc.setEnabled(true);
                cChangeTextSize(false,btnc);
                cChangeTextSize(true,btnd);
                countUpC();
                sw = true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countt == 0) {
        }else {
            save();
        }
    }

    public void cChangeTextSize(boolean change, Button btn){
        if(change == true){
            btn.setTextSize(92);
        }else {
            btn.setTextSize(100);
        }
    }

    public void countUpC(){
        ctimer = new Timer();
        ctask = new countTimerC(MainActivity.this, R.id.timerc, R.id.total, countc,countt,R.id.percentage);
        ctimer.schedule(ctask,0,period);
    }

    public void countUpD(){
        dtimer = new Timer();
        dtask = new countTimerD(MainActivity.this, R.id.timerd, R.id.total, countd,countt,R.id.percentage);
        dtimer.schedule(dtask,0,period);
    }

    public void cancelC(){
        ctimer.cancel();
        before = countc;
        countc = ctask.getCount();
        countt += (countc-before);
    }

    public void cancelD(){
        dtimer.cancel();
        before = countd;
        countd = dtask.getCount();
        countt += (countd-before);
    }

    public void stop(){
        if(st.isSelected() == true) {
            if (sw == true) {
                cancelC();
                btnc.setSelected(true);
                btnc.setEnabled(false);
            } else {
                cancelD();
                btnd.setSelected(true);
                btnd.setEnabled(false);
            }
            st.setSelected(false);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void outputCsv(){
        OutputStream out =null;
        OutputStreamWriter writer = null;
        BufferedWriter bw = null;
        long d = 0;
        long c;
        if(add == true){
            if(ctimer != null){
                c = ctask.getCount();
            }else{
                c = countc;
            }
            if(dtimer != null){
                d = dtask.getCount();
            }else{
                d = countd;
            }
        }else{
            if(dtimer != null){
                d = dtask.getCount();
            }
            c = ctask.getCount();
        }

        double p = (double)(c*100)/(c + d);


        BigDecimal bd =  new BigDecimal(String.valueOf(p));
        double per = bd.setScale(1, RoundingMode.HALF_UP).doubleValue();

        try{
            out = openFileOutput("data.csv",MODE_APPEND);
            writer = new OutputStreamWriter(out);
            bw = new BufferedWriter(writer);

            String text = dateText+","+c+","+d+","+per;

            bw.write(text);
            bw.newLine();
            bw.close();

        }catch (IOException e){
            Log.e("Internal", "IO Exception " + e.getMessage(), e);
        } finally {
            try {
                if (bw != null) { bw.close(); }
                if (writer != null) { writer.close(); }
                if (out != null) { out.close(); }
            } catch (IOException e) {
                Log.e("Internal", "IO Exception " + e.getMessage(), e);
            }
        }
    }

    public void lastData(){
        InputStream in = null;
        InputStreamReader sr = null;
        BufferedReader br = null;

        Date date = new Date(System.currentTimeMillis());
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        dateText = format.format(date);

        try {
            in = openFileInput("lastdata.csv");
            sr = new InputStreamReader(in);
            br = new BufferedReader(sr);
            String line;

            while ((line = br.readLine()) != null){
                StringTokenizer st = new StringTokenizer(line,",");
                String valdate = st.nextToken();

                if(valdate.equals(dateText)){
                    syncDialog(line);
                }
                break;
            }

        } catch (IOException e) {

        } finally {
            try {
                if (br != null) { br.close(); }
                if (sr != null) { sr.close(); }
                if (in != null) { in.close(); }
            } catch (IOException e) {
                Toast.makeText(this, "E", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void syncDialog(final String line){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("同じ日付のデータが存在します")
                .setMessage("同期しますか?")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataSync(line);
                        add = true;
                    }
                })
                .setNegativeButton("いいえ", null)
                .show();
    }

    public void dataSync(String line){
        StringTokenizer st = new StringTokenizer(line,",");
        st.nextToken();
        countc = Long.parseLong(st.nextToken());
        countd = Long.parseLong(st.nextToken());
        st.nextToken();
        int s = Integer.parseInt(st.nextToken());

        long c[] = timeCount(countc);
        btnc.setText(String.format("%02d:%02d:%02d",c[0],c[1],c[2]));

        long d[] = timeCount(countd);
        btnd.setText(String.format("%02d:%02d:%02d",d[0],d[1],d[2]));

        long t[] = timeCount(countt = countc + countd);
        tview.setText(String.format("%02d:%02d:%02d",t[0],t[1],t[2]));

        pview.setText(String.format("%d %%",(countc*100)/(countt)));

        if(s == 1){
            sw = true;
            btnd.setSelected(false);
            cChangeTextSize(true,btnd);
        }else{
            sw = false;
            btnc.setSelected(false);
            cChangeTextSize(true,btnc);
        }
        first = false;
    }

    public long[] timeCount(long c){
        int countMin = 1000/period;
        int hms[] = {3600*countMin,60*countMin,countMin};
        long time[] = new long[3];
        time[0]  = c/hms[0];
        time[1] = (c%hms[0])/hms[1];
        time[2]= (c-hms[0]*time[0]-hms[1]*time[1])/hms[2];
        return time;
    }

    public void generatelastdata(){
        OutputStream out =null;
        OutputStreamWriter writer = null;
        BufferedWriter bw = null;
        double d = 0;
        double c;
        if(add == true){
            if(ctimer != null){
                c = (double)ctask.getCount();
            }else{
                c = countc;
            }
            if(dtimer != null){
                d = (double)dtask.getCount();
            }else{
                d = countd;
            }
        }else{
            cancelC();
            if(dtimer != null){
                d = (double)dtask.getCount();
            }
            c = (double)ctask.getCount();
        }
        double p = (c*100)/(c + d);

        /*adb pull /data/data/com.example.abe.timertest4/files/data.csv /home/abe/Documents/Android*/

        BigDecimal bd =  new BigDecimal(String.valueOf(p));
        double per = bd.setScale(1, RoundingMode.HALF_UP).doubleValue();

        int s;
        if(sw == true){
            s = 1;
        }else {
            s = 0;
        }

        deleteFile("lastData.csv");
        try{
            out = openFileOutput("lastdata.csv",MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            bw = new BufferedWriter(writer);

            String text = dateText+","+countc+","+countd+","+per+","+s;

            bw.write(text);
            bw.newLine();
            bw.close();

        }catch (IOException e){
            Log.e("Internal", "IO Exception " + e.getMessage(), e);
        } finally {
            try {
                if (bw != null) { bw.close(); }
                if (writer != null) { writer.close(); }
                if (out != null) { out.close(); }
            } catch (IOException e) {
                Log.e("Internal", "IO Exception " + e.getMessage(), e);
            }
        }
    }

    public void csvEditing(){
        InputStream in = null;
        InputStreamReader sr = null;
        BufferedReader br = null;
        ArrayList<String> str = new ArrayList<>();

        try {
            in = openFileInput("data.csv");
            sr = new InputStreamReader(in);
            br = new BufferedReader(sr);
            String line;
            int ln = 0;

            while ((line = br.readLine()) != null){
                str.add(line);
                ln++;
            }

            deleteFile("data.csv");

            OutputStream out =null;
            OutputStreamWriter writer = null;
            BufferedWriter bw = null;

            try{
                out = openFileOutput("data.csv",MODE_APPEND);
                writer = new OutputStreamWriter(out);
                bw = new BufferedWriter(writer);

                for(int i = 0; i < ln-1;i++){
                    String text = str.get(i);
                    bw.write(text);
                    bw.newLine();
                }
                bw.close();

            }catch (IOException e){
                Log.e("Internal", "IO Exception " + e.getMessage(), e);
            } finally {
                try {
                    if (bw != null) { bw.close(); }
                    if (writer != null) { writer.close(); }
                    if (out != null) { out.close(); }
                } catch (IOException e) {
                    Log.e("Internal", "IO Exception " + e.getMessage(), e);
                }
            }

        } catch (IOException e) {

        } finally {
            try {
                if (br != null) { br.close(); }
                if (sr != null) { sr.close(); }
                if (in != null) { in.close(); }
            } catch (IOException e) {
                Toast.makeText(this, "E", Toast.LENGTH_SHORT).show();
            }
        }
        str = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.graph:
                stop();
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                startActivity(intent);
                break;

            case R.id.save:
                if (countt != 0) {save();}
                break;

            case R.id.exit:
                if (countt != 0) {save();}
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void save(){
        stop();
        if(add == true){
            csvEditing();
            outputCsv();
        }else {
            outputCsv();
            add = true;
        }
        generatelastdata();
    }
}
