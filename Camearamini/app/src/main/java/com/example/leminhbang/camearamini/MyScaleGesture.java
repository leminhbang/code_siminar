package com.example.leminhbang.camearamini;

import android.view.ScaleGestureDetector;

/**
 * Created by LE MINH BANG on 10/10/2017.
 */

public class MyScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    static float scale = 1.0f;
    float onScaleStart = 0;
    float onScaleEnd = 0;

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        scale *= detector.getScaleFactor();
        if (scale > 2)
            scale = 2;
        if (scale < 0.5f)
            scale = 0.5f;
        //imgMainPicture.setScaleX(scale);
        //imgMainPicture.setScaleY(scale);
        return super.onScale(detector);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        onScaleStart = scale;
        return super.onScaleBegin(detector);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        onScaleEnd = scale;
        super.onScaleEnd(detector);
    }

    public static float getScaleValue() {
        return scale;
    }
}
