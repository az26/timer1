package com.example.abe.timer1;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import java.util.TimerTask;

/**
 * Created by abe on 17/02/19.
 */

public class countTimer extends TimerTask {
    protected int tid,totalid;
    protected long count,total;
    protected long hh,mm,ss,p;
    protected Context context;
    protected TextView totaltext,ptext;
    protected Button timertext;


    public countTimer(Context context, int tid, int totalid, long count, long total, int pid){
        this.tid = tid;
        this.totalid = totalid;
        this.total = (long)((int)((total - count)/10))*10;
        this.count = count;
        this.context = context;
        timertext = (Button) ((Activity)context).findViewById(tid);
        totaltext = (TextView)((Activity)context).findViewById(totalid);
        ptext = (TextView)((Activity)context).findViewById(pid);
    }

    @Override
    public void run(){
    }

    public void count(){
        count++;
        hh = count/36000;
        mm = (count%36000)/600;
        ss = (count-36000*hh-600*mm)/10;
        long th = (total+count)/36000;
        long tm = ((total+count)%36000)/600;
        long ts = ((total+count)-36000*th-600*tm)/10;
        timertext.setText(String.format("%02d:%02d:%02d",hh,mm,ss));
        totaltext.setText(String.format("%02d:%02d:%02d",th,tm,ts));
    }

    public long getCount(){return count;}
}
