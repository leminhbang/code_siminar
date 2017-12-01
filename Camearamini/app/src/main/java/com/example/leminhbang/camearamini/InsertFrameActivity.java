package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

import java.io.ByteArrayOutputStream;

import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;


public class InsertFrameActivity extends AppCompatActivity implements View.OnTouchListener, AdapterView.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage,imgFrame;
    private Gallery galleryImageFrame;
    private BottomNavigationView btmnBottomMenu;
    private int REQUEST = 1;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private Bitmap bitmapMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextTmp = context;
        context = this;
        mapView();

        gestureDetector = new GestureDetector(this,new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (filePath != null) {
            imgMainImage.setImageURI(fileUri);
        }
        imgMainImage.buildDrawingCache();
        bitmapMain = imgMainImage.getDrawingCache();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save_2:
                saveImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void mapView() {
        findViewById(R.id.relativelayout_insert_frame).setOnTouchListener(this);
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        imgFrame = (ImageView) findViewById(R.id.imgFrame);
        galleryImageFrame = (Gallery) findViewById(R.id.gallery_image_frame);
        galleryImageFrame.setAdapter(new GallaryAdapter(this,REQUEST));
        galleryImageFrame.setOnItemClickListener(this);
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
                Matrix matrix = new Matrix();
                matrix.postScale(scale,scale);
                Bitmap b = Bitmap.createBitmap(bitmapMain,0,0,bitmapMain.getWidth(),
                        bitmapMain.getHeight(), matrix,false);
                bitmapMain = b;
                imgMainImage.setImageBitmap(b);
                /*imgMainImage.setScaleX(scale);
                imgMainImage.setScaleY(scale);*/

                gestureDetector.onTouchEvent(event);

                break;
            case R.id.relativelayout_insert_frame:
                gestureDetector.onTouchEvent(event);
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView img = (ImageView) view;
        img.buildDrawingCache();
        //imgFrame.setImageBitmap(img.getDrawingCache());
        Bitmap bitmapFrame = img.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFrame.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        int w1 = imgFrame.getMeasuredWidth();
        int h1 = imgFrame.getMeasuredHeight();
        imgFrame.setImageBitmap(Bitmap.createScaledBitmap(b,w1,h1,false));
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
                intent = new Intent(InsertFrameActivity.this, InsertFrameActivity.class);
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

    public Bitmap overlayBitmap(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
                bmp1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOverlay);
        float w = (bmp1.getWidth() - bmp2.getWidth())/2;
        float h = (bmp1.getHeight() - bmp2.getHeight())/2;
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2,new Matrix(), null);
        return bmOverlay;


        /*Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(imgFrame.getMeasuredWidth(),
                    imgFrame.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            Resources res = getResources();
            Drawable drawable1 = new BitmapDrawable(bmp1);
            Drawable drawable2 = new BitmapDrawable(bmp2);
            drawable1.setBounds(0, 0, bmp1.getWidth(), bmp1.getHeight());
            int w = (bmp1.getWidth() - bmp2.getWidth())/2;
            int h = (bmp1.getHeight() - bmp2.getHeight())/2;
            drawable2.setBounds(w,h,bmp2.getWidth() + w,bmp2.getHeight() + h);
            drawable1.draw(c);
            drawable2.draw(c);
        } catch (Exception e) {

        }
        return bitmap;*/
    }

    public void saveImage() {
        imgFrame.buildDrawingCache();
        Bitmap bmp1 = imgFrame.getDrawingCache();
        imgMainImage.buildDrawingCache();
        Bitmap bmp2 = imgMainImage.getDrawingCache();
        Bitmap bitmapOverlay = overlayBitmap(bmp1,bmp2);
        imgFrame.setImageBitmap(null);
        imgMainImage.setImageBitmap(bitmapOverlay);
    }
}
