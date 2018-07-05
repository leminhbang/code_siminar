package com.example.leminhbang.camearamini;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import static com.example.leminhbang.camearamini.ImageViewUtils.ImageViewUtil.usingSimpleImage;
import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.bitmapTemp;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;
import static com.example.leminhbang.camearamini.MainActivity.showDialogSave;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;
import static com.example.leminhbang.camearamini.MyScaleGesture.scale;


public class InsertFrameActivity extends AppCompatActivity implements View.OnTouchListener, AdapterView.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage, imgFrame;
    private Gallery galleryImageFrame;
    private BottomNavigationView btmnBottomMenu;
    private int REQUEST = 1;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private Bitmap bitmapFrame;
    private int originalWidth, originalHeight, width, height;
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

        gestureDetector = new GestureDetector(this, new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

        //zoom and movee image, pinch to zoom
        usingSimpleImage(imgMainImage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bitmapTemp == null)
            bitmapTemp = bitmapMain;
        imgMainImage.setImageBitmap(bitmapTemp);

        originalWidth = bitmapMain.getWidth();
        originalHeight = bitmapMain.getHeight();
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
        galleryImageFrame.setAdapter(new GallaryAdapter(this, REQUEST));
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
                    MyScaleGesture.setScaleValue(1.0f);
                }

                width = Math.round(originalWidth * scale);
                ;
                height = Math.round(originalHeight * scale);
                ;

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
        bitmapFrame = null;
        bitmapTemp = null;
        bitmapFrame = BitmapFactory.decodeResource(getResources(), (int) id);
        int w = bitmapMain.getWidth();
        int h = bitmapMain.getHeight();
        Bitmap tmp = Bitmap.createScaledBitmap(bitmapFrame, w, h, false);
        bitmapFrame = Bitmap.createBitmap(tmp);
        bitmapTemp = overlayBitmap(bitmapFrame, bitmapMain);
        imgMainImage.setImageBitmap(bitmapTemp);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_insert_text:
                showDialogSave(bitmapTemp, this, InterfaceClass.InsertTextClass);
                break;
            case R.id.action_insert_frame:
                break;
            case R.id.action_cut_image:
                showDialogSave(bitmapTemp, this, InterfaceClass.CutImageClass);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        context = contextTmp;
        showDialogSave(bitmapTemp, this, contextTmp.getClass());
        super.onBackPressed();
    }
    //bmp1 : khung anh, bmp2 anh goc
    public Bitmap overlayBitmap(Bitmap bmp1, Bitmap bmp2) {
        if (bmp1 == null) {
            return bmp2;
        }
        Bitmap bmOverlay = Bitmap.createBitmap(bmp2.getWidth(),
                bmp2.getHeight(), bmp2.getConfig());
        int w = bitmapMain.getWidth();
        int h = bitmapMain.getHeight();
        Mat src1 = new Mat(h, w, CvType.CV_8UC3);
        Mat src2 = new Mat(h, w, CvType.CV_8UC4);
        Mat det =  new Mat(h, w, CvType.CV_8UC4);
        Utils.bitmapToMat(bmp1, src1);;
        Utils.bitmapToMat(bmp2, src2);
        float alpha = 0.5f;
        Core.addWeighted(src1, 0.9f, src2, 1.0f, 0, det);
        Utils.matToBitmap(det, bmOverlay);
        return bmOverlay;
    }

    public void saveImage() {

        bitmapMain = bitmapTemp;

        //luu anh vao bo nho
        saveImageFile(fileUri, bitmapMain);

        imgFrame.setImageBitmap(null);
        imgMainImage.setScaleX(1);
        imgMainImage.setScaleY(1);
        imgMainImage.setImageBitmap(bitmapMain);
    }

    private void cancelAction() {
        bitmapTemp = bitmapMain;
        imgFrame.setImageBitmap(null);
        imgMainImage.setImageBitmap(bitmapMain);
    }
}

