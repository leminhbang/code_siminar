package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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

import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MyScaleGesture.scale;


public class InsertFrameActivity extends AppCompatActivity implements View.OnTouchListener, AdapterView.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage,imgFrame;
    private Gallery galleryImageFrame;
    private BottomNavigationView btmnBottomMenu;
    private int REQUEST = 1;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private Bitmap bitmapFrame,bitmapTemp;
    private int originalWidth,originalHeight, width, height;
    private boolean isFirst = true;

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
        if (filePath != null && bitmapMain != null) {
            //imgMainImage.setImageURI(fileUri);
            bitmapTemp = bitmapMain;
            imgMainImage.setImageBitmap(bitmapMain);
            originalWidth = bitmapMain.getWidth();
            originalHeight = bitmapMain.getHeight();
        }
        /*imgMainImage.buildDrawingCache();
        bitmapMain = imgMainImage.getDrawingCache();*/
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
            case R.id.action_cancel_2:
                cancelAction();
                break;
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
                if (!isFirst) {
                    float scale = MyScaleGesture.getScaleValue();
                    imgMainImage.setScaleX(scale);
                    imgMainImage.setScaleY(scale);
                } else {
                    isFirst = false;
                    MyScaleGesture.setScaleValue();
                }

                width = Math.round(originalWidth*scale);;
                height = Math.round(originalHeight*scale);;

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
        bitmapFrame = BitmapFactory.decodeResource(getResources(),(int) id);
        int w = imgFrame.getWidth();
        int h = imgFrame.getHeight();
        Bitmap tmp = Bitmap.createScaledBitmap(bitmapFrame, w, h, false);
        bitmapFrame = Bitmap.createBitmap(tmp);
        imgFrame.setImageBitmap(bitmapFrame);
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
                break;
            case R.id.action_cut_image:
                intent = new Intent(context,CutImageActivity.class);
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
        if (bmp1 == null) {
            return bmp2;
        }
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
                bmp1.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        paint.setDither(false);
        paint.setAntiAlias(false);
        paint.setFilterBitmap(false);

        Canvas canvas = new Canvas(bmOverlay);
        float w = (bmp1.getWidth() - bmp2.getWidth())/2;
        float h = (bmp1.getHeight() - bmp2.getHeight())/2;
        canvas.drawBitmap(bmp1,0,0, paint);
        canvas.drawBitmap(bmp2,w,h, paint);
        return bmOverlay;
    }

    public void saveImage() {
        Bitmap tmp = Bitmap.createScaledBitmap(bitmapMain,width,height,false);
        bitmapMain = Bitmap.createBitmap(tmp);
        bitmapMain = overlayBitmap(bitmapFrame,bitmapMain);
        bitmapFrame = bitmapMain;

        //luu anh vao bo nho
        //saveImageFile(fileUri,bitmapMain);

        imgFrame.setImageBitmap(null);
        imgMainImage.setScaleX(1);
        imgMainImage.setScaleY(1);
        imgMainImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgMainImage.setImageBitmap(bitmapMain);
    }

    private void cancelAction() {
        bitmapMain = bitmapTemp;
        imgFrame.setImageBitmap(null);
        imgMainImage.setImageBitmap(bitmapMain);
    }
}

