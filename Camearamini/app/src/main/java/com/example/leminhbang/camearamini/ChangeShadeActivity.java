package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.leminhbang.camearamini.MainActivity.context;

public class ChangeShadeActivity extends AppCompatActivity implements View.OnTouchListener, AdapterView.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage;
    private Gallery galleryChangeShade;
    private BottomNavigationView btmnBottomMenu;
    private int REQUEST = 2;
    GestureDetector gestureDetector;
    ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_shade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextTmp = context;
        mapView();
        context = this;
        gestureDetector = new GestureDetector(this,new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void mapView() {
        findViewById(R.id.linearlayout_main).setOnTouchListener(this);
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        galleryChangeShade = (Gallery) findViewById(R.id.gallery_change_shade);
        galleryChangeShade.setAdapter(new GallaryAdapter(this,REQUEST));
        galleryChangeShade.setOnItemClickListener(this);
        btmnBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btmnBottomMenu.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (id) {
            case R.id.img_main_image:
                scaleGestureDetector.onTouchEvent(event);
                float scale = MyScaleGesture.getScaleValue();
                //imgMainImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imgMainImage.setScaleX(scale);
                imgMainImage.setScaleY(scale);

                gestureDetector.onTouchEvent(event);

                break;
            case R.id.linearlayout_main:
                gestureDetector.onTouchEvent(event);
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        imgMainImage.buildDrawingCache();
        Bitmap bitmap = imgMainImage.getDrawingCache();
        int[][][] pixelMat;
        switch (position) {
            case 0:
                Toast.makeText(context,"Không có",
                        Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(context,"Ảnh xám",
                        Toast.LENGTH_SHORT).show();
                //imgMainImage.setImageBitmap(convertToGray(bitmap));
                Bitmap b = convertToGray(bitmap);
                pixelMat = MyCameraHelper.convertBitmapToMatrix(b);
                imgMainImage.setImageBitmap(MyCameraHelper.
                        convertMatrixToBitmap(pixelMat));
                break;
            case 2:
                Toast.makeText(context,"Ảnh âm bản",
                        Toast.LENGTH_SHORT).show();
                imgMainImage.setImageBitmap(convertToNegative(bitmap));
                break;
            case 3:
                Toast.makeText(context,"Ảnh mờ",
                        Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(context,"Ảnh cổ điển",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_insert_text:
                Intent intent = new Intent(context,InsertTextActivity.class);
                startActivity(intent);
                break;
            case R.id.action_insert_frame:
                intent = new Intent(ChangeShadeActivity.this, InsertFrameActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        context = contextTmp;
        super.onBackPressed();
    }

    public Bitmap convertToGray(Bitmap colorBitmap) {
        Bitmap grayBitmap = Bitmap.createBitmap(colorBitmap.getWidth(),
                colorBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int pixel, alpha, r, g, b, gray;
        for (int i = 0; i < colorBitmap.getWidth(); i++) {
            for (int j = 0; j < colorBitmap.getHeight(); j++) {
                pixel = colorBitmap.getPixel(i,j);
                alpha = Color.alpha(pixel);
                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);
                gray = (int) (r*0.298 + g*0.588 + b*0.114);
                grayBitmap.setPixel(i,j,Color.argb(alpha,gray,gray,gray));
            }
        }
        return grayBitmap;
    }
    public Bitmap convertToNegative(Bitmap colorBitmap) {
        Bitmap negativeBitmap = Bitmap.createBitmap(colorBitmap.getWidth(),
                colorBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int pixel, alpha, r, g, b;
        for (int i = 0; i < colorBitmap.getWidth(); i++) {
            for (int j = 0; j < colorBitmap.getHeight(); j++) {
                pixel = colorBitmap.getPixel(i,j);
                alpha = Color.alpha(pixel);
                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);
                negativeBitmap.setPixel(i,j,Color.argb(alpha, 255 - r,
                        255 - g, 255 - b));
            }
        }
        return negativeBitmap;
    }
}
