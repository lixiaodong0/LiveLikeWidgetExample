package com.lixd.example.bezier;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.lixd.example.R;

import java.util.Random;

/**
 * Created by Administrator on 2018/5/14.
 */

public class BezierView implements BezierLayout.IBezierView {

    private int[] icons = {
            R.mipmap.heart_1,
            R.mipmap.heart_2,
            R.mipmap.heart_3,
            R.mipmap.heart_4,
            R.mipmap.heart_5,
            R.mipmap.heart_6,
            R.mipmap.heart_7,
    };

    private Random random = new Random();

    @Override
    public View createView(Context context) {
        ImageView targetView = new ImageView(context);
        targetView.setImageResource(icons[random.nextInt(icons.length)]);
        return targetView;
    }
}
