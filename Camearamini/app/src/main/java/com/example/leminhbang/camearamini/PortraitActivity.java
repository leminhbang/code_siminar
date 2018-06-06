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

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.bitmapTemp;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;
import static com.example.leminhbang.camearamini.MainActivity.showDialogSave;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;

public class PortraitActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage;
    private BottomNavigationView btnvBottomMenu;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextTmp = context;
        mapView();

        context = this;
        gestureDetector = new GestureDetector(this,new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

    }
    private void mapView() {
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        btnvBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btnvBottomMenu.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (filePath != null) {
            bitmapTemp = bitmapMain;
            imgMainImage.setImageBitmap(bitmapMain);
            getPortrait();
        }
    }
    public void getPortrait() {
        int w = bitmapMain.getWidth(), h = bitmapMain.getHeight();
        Mat mSrc = new Mat(h, w, CvType.CV_8UC4);
        Mat mPortrait = new Mat(h, w, CvType.CV_8UC3);
        Bitmap b = Bitmap.createBitmap(w, h, bitmapMain.getConfig());
        Utils.bitmapToMat(bitmapMain, mSrc);
        //mPortrait = convertToSketchPencil(mSrc);
        mPortrait = convertToEmboss(mSrc);
        Utils.matToBitmap(mPortrait, b);
        bitmapTemp = b;
        imgMainImage.setImageBitmap(bitmapTemp);
    }

    public Mat convertToSketchPencil(Mat src) {
        Mat mSketch = new Mat(src.rows(), src.cols(), CvType.CV_8UC3);
        Mat mGray = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
        Mat mNeg = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
        //convert original image to gray
        Imgproc.cvtColor(src, mGray, Imgproc.COLOR_RGBA2GRAY);
        //convert gray image to negative
        Core.subtract(new MatOfDouble(255), mGray, mNeg);
        //apply gaussian blur
        Mat mBlur = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
        Imgproc.GaussianBlur(mNeg, mBlur,new Size(21, 21), 0);
        //apply sketch pencil
        Core.subtract(new MatOfDouble(255), mBlur, mBlur);
        Core.divide(mGray, mBlur, mSketch, 255);
        return mSketch;
    }
    public Mat convertToEmboss(Mat src) {
        Mat mOut = new Mat(src.rows(), src.cols(), CvType.CV_8UC3);
        Mat kernel = new Mat(3, 3,CvType.CV_32F);
        //convert original image to gray
        kernel.put(0, 0, -1.0);kernel.put(0, 1, 0.0);
        kernel.put(0, 2, 0.0); //kernel.put(0, 3, 0.0f);
        kernel.put(1, 0, 0.0);kernel.put(1, 1, 0.0);
        kernel.put(1, 2, 0.0); //kernel.put(1, 3, 0.0);
        kernel.put(2, 0, 0.0);kernel.put(2, 1, 0.0);
        kernel.put(2, 2, 1.0); //kernel.put(2, 3, 0.0);
        /*kernel.put(3, 0, 0.0);kernel.put(3, 1, 0.0);
        kernel.put(3, 2, 0.0); //kernel.put(3, 3, 1.0);*/
        //Core.transform(src, mOut, kernel);

        //Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY);
        Mat kernel2 = new Mat(5, 5,CvType.CV_32F, new Scalar(0));
        kernel2.put(0, 0, 1.0); kernel2.put(1, 1, 1.0);
        kernel2.put(3, 3, -1.0); kernel2.put(4, 4, -1.0);
        Imgproc.filter2D(src, mOut, CvType.CV_16S, kernel2);
        mOut.convertTo(mOut, CvType.CV_8UC3, 1, 128);
        return mOut;
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
                saveImage();;
                break;
            case R.id.action_cancel_2:
                cancelAction();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void saveImage() {
        bitmapMain = bitmapTemp;
        saveImageFile(fileUri,bitmapMain);
    }
    private void cancelAction() {
        imgMainImage.setImageBitmap(bitmapMain);
    }

}
