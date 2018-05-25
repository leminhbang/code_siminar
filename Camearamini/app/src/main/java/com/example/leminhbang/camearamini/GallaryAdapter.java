package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.StyleableRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static com.example.leminhbang.camearamini.MainActivity.thumbPaths;

/*
 * Created by LE MINH BANG on 21/10/2017.
 */

public class GallaryAdapter extends BaseAdapter {

    private int GalItemBg;
    private Context cont;
    private Integer[] imgId;
    private List<String> paths = new ArrayList<String>();
    private int REQUEST_CODE;

    public GallaryAdapter(Context c, int REQUEST) {
        cont = c;
        REQUEST_CODE = REQUEST;
        if (REQUEST == 1) {
            imgId = new Integer[]{R.drawable.frame_2, R.drawable.frame_5,
                    R.drawable.frame_6, R.drawable.frame_7, R.drawable.frame_14};
        }
        if (REQUEST == 2) {
            /*imgId = new Integer[]{R.drawable.image_main, R.drawable.image_gray,
                    R.drawable.image_negative, R.drawable.image_black_white,
                    R.drawable.image_fade,R.drawable.image_classic};*/
            paths = thumbPaths;
        }
        TypedArray typArray = cont.obtainStyledAttributes(R.styleable.GalleryTheme);
        GalItemBg = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
        typArray.recycle();
    }

    public final TypedArray obtainStyledAttributes(@StyleableRes int[] attrs) {
        return cont.getTheme().obtainStyledAttributes(attrs);
    }

    @Override
    public int getCount() {
        if (REQUEST_CODE == 1)
            return imgId.length;
        else
            return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        if (REQUEST_CODE == 1)
            return imgId[position];
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imgView = new ImageView(cont);
        imgView.setLayoutParams(new Gallery.LayoutParams(180, 150));
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        imgView.setBackgroundResource(GalItemBg);
        if (REQUEST_CODE == 1)
            imgView.setImageResource(imgId[position]);
        else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap mBitmap = BitmapFactory.decodeFile(paths.get(position),
                    options);
            imgView.setImageBitmap(mBitmap);
        }

        return imgView;
    }

    /*private void saveThumbnailBitmap() {
        Cursor mCursor;
        mCursor = context.getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                null, null, null, null);
        mCursor.moveToFirst();
        int image_column_index = mCursor.getColumnIndex(
                MediaStore.Images.Media._ID);
        long id = mCursor.getLong(image_column_index);
        Bitmap b = MediaStore.Images.Thumbnails.getThumbnail(
                context.getContentResolver(), id,
                MediaStore.Images.Thumbnails.MINI_KIND, null);
    }*/
}
