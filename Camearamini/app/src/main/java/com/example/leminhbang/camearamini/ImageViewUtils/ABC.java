package com.example.leminhbang.camearamini.ImageViewUtils;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.leminhbang.camearamini.MyGesture;
import com.imagezoom.ImageAttacher;

import static com.example.leminhbang.camearamini.MainActivity.context;

/*
 * Created by LE MINH BANG on 6/26/2018.
 */

public class ABC implements View.OnTouchListener {
    ImageView img;
    ImageAttacher imageAttacher;
    GestureDetector gestureDetector;
    public ABC(ImageView imageView, ImageAttacher imgA) {
        imageAttacher = imgA;
        gestureDetector = new GestureDetector(context,new MyGesture());
        img = imageView;
        img.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        imageAttacher.onTouch(v, event);
        return true;
    }
}
