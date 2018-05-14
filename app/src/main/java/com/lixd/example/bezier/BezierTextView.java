package com.lixd.example.bezier;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Administrator on 2018/5/14.
 */

public class BezierTextView implements BezierLayout.IBezierView {
    private Random random = new Random();
    private int[] colors = {
            Color.rgb(255, 0, 0),       //赤色
            Color.rgb(255, 165, 0),     //橙色
            Color.rgb(255, 255, 0),     //黄色
            Color.rgb(0, 255, 0),       //绿色
            Color.rgb(0, 127, 255),     //青色
            Color.rgb(0, 0, 255),       //蓝色
            Color.rgb(139, 0, 255)      //紫色
    };

    @Override
    public View createView(Context context) {
        TextView textView = new TextView(context);
        textView.setText("LOVE");
        textView.setTextColor(colors[random.nextInt(colors.length)]);
        textView.setTextSize(14);
        return textView;
    }
}
