package com.lixd.example.bezier.utils;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * 贝塞尔曲线估值器
 */
public class BezierEvaluator implements TypeEvaluator<PointF> {
    /**
     * 控制点
     */
    private PointF controlPoint1;
    private PointF controlPoint2;

    public BezierEvaluator(PointF controlPoint1, PointF controlPoint2) {
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
    }

    @Override
    public PointF evaluate(float v, PointF startPoint, PointF endPoint) {
        PointF point = new PointF();
        point.x = startPoint.x * (1 - v) * (1 - v) * (1 - v)
                + 3 * controlPoint1.x * v * (1 - v) * (1 - v)
                + 3 * controlPoint2.x * v * v * (1 - v)
                + endPoint.x * v * v * v;
        point.y = startPoint.y * (1 - v) * (1 - v) * (1 - v)
                + 3 * controlPoint1.y * v * (1 - v) * (1 - v)
                + 3 * controlPoint2.y * v * v * (1 - v)
                + endPoint.y * v * v * v;

        return point;
    }
}