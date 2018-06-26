package com.example.leminhbang.camearamini.ImageViewUtils;

/*
 * Created by LE MINH BANG on 6/19/2018.
 */

import android.widget.ImageView;

import com.imagezoom.ImageAttacher;

public class ImageViewUtil {
    public static void usingSimpleImage(ImageView imageView) {
        ImageAttacher mAttacher = new ImageAttacher(imageView);
        ImageAttacher.MAX_ZOOM = 2.0f; // Double the current Size
        ImageAttacher.MIN_ZOOM = 0.5f; // Half the current Size
        MatrixChangeListener mMaListener = new MatrixChangeListener();
        mAttacher.setOnMatrixChangeListener(mMaListener);
        PhotoTapListener mPhotoTap = new PhotoTapListener();
        mAttacher.setOnPhotoTapListener(mPhotoTap);
        ABC abc = new ABC(imageView, mAttacher);
    }
}
