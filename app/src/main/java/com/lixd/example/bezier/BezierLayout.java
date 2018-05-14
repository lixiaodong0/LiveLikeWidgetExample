package com.lixd.example.bezier;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.lixd.example.bezier.utils.BezierEvaluator;

import java.util.Random;

/**
 * 贝塞尔布局
 */

public class BezierLayout extends RelativeLayout {
    private static final String LOG_TAG = BezierLayout.class.getSimpleName();
    private Context context;
    private LayoutParams layoutParams;
    //随机数生成器
    private Random random;
    //宽度
    private int width;
    //高度
    private int height;
    //子控件宽度
    private int childWidth;
    //子控件高度
    private int childHeight;
    //插值器
    private Interpolator[] interpolators = {
            //在开始,结束的时候慢,中间开始加速
            new AccelerateDecelerateInterpolator(),
            //在开始的时候慢,然后开始加速
            new AccelerateInterpolator(),
            //在开始的时候快,然后变慢
            new DecelerateInterpolator()
    };

    public BezierLayout(Context context) {
        this(context, null);
    }

    public BezierLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        context = getContext();
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //居中对齐
        layoutParams.addRule(CENTER_HORIZONTAL);
        //底部对齐
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        random = new Random();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    /**
     * 执行Bezier动画
     *
     * @param iBezierView
     */
    public void start(IBezierView iBezierView) {
        final View targetView = iBezierView.createView(context);
        if (targetView == null) {
            Log.d(LOG_TAG, "targetView not null");
            return;
        }

        targetView.measure(0, 0);
        childWidth = targetView.getMeasuredWidth();
        childHeight = targetView.getMeasuredHeight();

        Log.e(LOG_TAG, "childWidth: " + childWidth);
        Log.e(LOG_TAG, "childHeight: " + childHeight);

        //执行targetView添加到容器时动画
        AnimatorSet set = createTargetViewAnimat(targetView);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                /**
                 动画开始执行的时候,需要将View先添加到容器显示出来
                 否则后续动画无法显示
                 */
                addView(targetView, layoutParams);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 动画执行完毕, 移除View, 防止View数量过多
                 */
                removeView(targetView);
            }
        });
        set.start();
    }

    /**
     * 创建目标View动画
     */
    private AnimatorSet createTargetViewAnimat(View view) {
        //缩放动画
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f);

        //透明度动画
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1f);

        //组合缩放,透明度动画
        AnimatorSet objectSet = new AnimatorSet();
        objectSet.setDuration(300);
        objectSet.setInterpolator(new LinearInterpolator());
        objectSet.playTogether(alphaAnim, scaleXAnim, scaleYAnim);

        ValueAnimator bezierAnim = createBezierAnimat(view);
        //组合objectSet,bezierAnim动画, objectSet先执行,后执行bezierAnim
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(objectSet, bezierAnim);
        return set;
    }


    /**
     * 创建贝塞尔动画
     *
     * @return
     */
    private ValueAnimator createBezierAnimat(final View view) {
        //起点
        PointF startPoint = new PointF((width - childWidth) / 2, height - childHeight);
        //控制点1
        PointF controlPoint1 = getControlPoint(1);
        //控制点2
        PointF controlPoint2 = getControlPoint(2);
        //结束点
        PointF endPoint = new PointF(random.nextInt(width), 0);

        Log.e(LOG_TAG, "P1: " + startPoint);
        Log.e(LOG_TAG, "P2: " + controlPoint1);
        Log.e(LOG_TAG, "P3: " + controlPoint2);
        Log.e(LOG_TAG, "P4: " + endPoint);

        BezierEvaluator evaluator = new BezierEvaluator(controlPoint1, controlPoint2);
        ValueAnimator bezierAnim = ValueAnimator.ofObject(evaluator, startPoint, endPoint);
        bezierAnim.setInterpolator(interpolators[random.nextInt(interpolators.length)]);
        bezierAnim.setDuration(2000);
        bezierAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                //更新View的位置
                view.setX(point.x);
                view.setY(point.y);

                //计算alpha值 1~0之间 (不透明->透明)
                float animatedFraction = animation.getAnimatedFraction();
                float alpha = 1 - animatedFraction;
                view.setAlpha(alpha);
            }
        });
        return bezierAnim;
    }

    /**
     * 获取控制点
     *
     * @return
     */
    private PointF getControlPoint(@IntRange(from = 1, to = 2) int flag) {
        //以Y轴中心点为基础
        int centerY = (height - childHeight) / 2;
        int x = random.nextInt(width - childWidth);
        int y = 0;
        if (flag == 1) {
            //控制点1
            y = centerY + random.nextInt(centerY / 2);
        } else {
            //控制点2
            y = centerY;
        }
        return new PointF(x, y);
    }

    /**
     * 接口,方便扩展业务
     */
    public interface IBezierView {

        /**
         * 创建View
         *
         * @param context 上下文
         * @return
         */
        View createView(Context context);
    }
}
