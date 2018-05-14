package com.lixd.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lixd.example.bezier.BezierLayout;
import com.lixd.example.bezier.BezierTextView;
import com.lixd.example.bezier.BezierView;

public class MainActivity extends AppCompatActivity {

    private BezierLayout.IBezierView[] bezierViews = {
            new BezierView(), new BezierTextView()
    };
    private BezierLayout bezierLayout;
    private boolean isDestory;
    private boolean isStartThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bezierLayout = (BezierLayout) findViewById(R.id.activity_main);
        UpdateRunnable runnable = new UpdateRunnable();
        final Thread thread = new Thread(runnable);
        bezierLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStartThread) {
                    thread.start();
                    isStartThread = true;
                }
            }
        });
    }

    class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            while (!isDestory) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bezierLayout.start(bezierViews[1]);
                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestory = true;
    }
}
