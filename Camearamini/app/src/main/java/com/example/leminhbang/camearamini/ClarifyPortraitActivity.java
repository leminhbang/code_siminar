package com.example.leminhbang.camearamini;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.SeekBar;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.bitmapTemp;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;
import static com.example.leminhbang.camearamini.MainActivity.showDialogSave;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;

public class ClarifyPortraitActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener, SeekBar.OnSeekBarChangeListener {
    private ImageView imgMainImage;
    private SeekBar sekClarifyPortrait;
    private BottomNavigationView btmnBottomMenu;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clarify_portrait);
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
            bitmapTemp = bitmapMain;
            imgMainImage.setImageBitmap(bitmapMain);
        }
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
            case R.id.action_cancel_2:
                cancelAction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void mapView() {
        findViewById(R.id.linearlayout_main).setOnTouchListener(this);
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        sekClarifyPortrait = (SeekBar) findViewById(R.id.seekbar_clarify_portrait_image);
        sekClarifyPortrait.setOnSeekBarChangeListener(this);
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
                gestureDetector.onTouchEvent(event);
                break;
            case R.id.linearlayout_main:
                gestureDetector.onTouchEvent(event);
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_insert_text:
                showDialogSave(bitmapTemp,InterfaceClass.InsertTextClass);
                break;
            case R.id.action_insert_frame:
                showDialogSave(bitmapTemp,InterfaceClass.InsertFrameClass);
                break;
            case R.id.action_cut_image:
                showDialogSave(bitmapTemp,InterfaceClass.CutImageClass);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        context = contextTmp;
        super.onBackPressed();
    }
    private Bitmap changeContrast(Bitmap src, int contrastVal) {
        int w = src.getWidth();
        int h = src.getHeight();
        double contrast = (double) contrastVal / 50.0;
        if (contrast <= 0.1)
            contrast = 0.1;
        if (contrast >= 2.0)
            contrast = 2.0;
        Mat mOrg = new Mat(h, w, CvType.CV_8SC4);
        Mat mOut = new Mat(h, w, CvType.CV_8SC4);
        Bitmap bmm = Bitmap.createBitmap(w,h,src.getConfig());
        Utils.bitmapToMat(src,mOrg);
        //mOrg.convertTo(mOut, -1, 1, contrastVal - 50);
        Mat canny = new Mat(h,w,CvType.CV_8SC1);
        Mat gray = new Mat(h,w,CvType.CV_8SC1);
        Imgproc.cvtColor(mOrg,gray,Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(gray,gray, new Size(3,3));
        Imgproc.Canny(gray,canny,contrastVal,contrastVal*3);
        NativeClass.convertToNegative(canny.getNativeObjAddr(),
                canny.getNativeObjAddr());
        Utils.matToBitmap(canny,bmm);
        return bmm;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        bitmapTemp = changeContrast(bitmapMain,progress);
        imgMainImage.setImageBitmap(bitmapTemp);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sekClarifyPortrait.setProgress(50);
    }

    private void saveImage() {
        bitmapMain = bitmapTemp;
        saveImageFile(fileUri,bitmapMain);
    }
    private void cancelAction() {
        bitmapTemp = bitmapMain;
        sekClarifyPortrait.setProgress(50);
        imgMainImage.setImageBitmap(bitmapMain);
    }
}
