package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Created by LE MINH BANG on 21/10/2017.
 */

public class GallaryAdapter extends BaseAdapter {

    int GalItemBg;
    private Context cont;
    private Integer[] imgId;

    public GallaryAdapter(Context c, int REQUEST) {
        cont = c;

        if (REQUEST == 1) {
            Integer[] imgIdTmp = {R.drawable.frame_2, R.drawable.frame_5,
                    R.drawable.frame_6, R.drawable.frame_7, R.drawable.frame_14};
            imgId = imgIdTmp;
        }
        if (REQUEST == 2) {
            Integer[] imgIdTmp = {R.drawable.image_main, R.drawable.image_gray,
                    R.drawable.image_negative, R.drawable.image_fade,
                    R.drawable.image_classic};
            imgId = imgIdTmp;
        }
        TypedArray typArray = cont.obtainStyledAttributes(R.styleable.GalleryTheme);
        GalItemBg = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
        typArray.recycle();
    }

    /*public final TypedArray obtainStyledAttributes(@StyleableRes int[] attrs) {
        return cont.getTheme().obtainStyledAttributes(attrs);
    }*/

    @Override
    public int getCount() {
        return imgId.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imgView = new ImageView(cont);
        imgView.setImageResource(imgId[position]);
        imgView.setLayoutParams(new Gallery.LayoutParams(110, 100));
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        imgView.setBackgroundResource(GalItemBg);
        return imgView;
    }
}
