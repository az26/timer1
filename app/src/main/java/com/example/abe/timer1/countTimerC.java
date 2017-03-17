package com.example.abe.timer1;

import android.content.Context;
import android.os.Handler;

/**
 * Created by abe on 17/03/05.
 */

public class countTimerC extends countTimer {
    protected Handler handler = new Handler();

    public countTimerC(Context context, int tid, int totalid, long count, long total, int pid){
        super(context,tid,totalid,count,total,pid);
    }

    @Override
    public void run() {
        super.run();
        handler.post(new Runnable() {
            @Override
            public void run() {
                count();
                long p = (count*100)/(count+total);
                ptext.setText(String.format("%d %%",p));
            }
        });
    }
}
