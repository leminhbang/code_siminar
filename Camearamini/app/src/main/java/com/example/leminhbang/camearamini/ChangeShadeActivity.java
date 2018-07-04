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
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static com.example.leminhbang.camearamini.ImageViewUtils.ImageViewUtil.usingSimpleImage;
import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.bitmapTemp;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;
import static com.example.leminhbang.camearamini.MainActivity.showDialogSave;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;

public class ChangeShadeActivity extends AppCompatActivity implements View.OnTouchListener, AdapterView.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage;
    private Gallery galleryChangeShade;
    private BottomNavigationView btmnBottomMenu;
    private int REQUEST = 2;
    GestureDetector gestureDetector;
    ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private boolean isFirst = true;

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

        //zoom and movee image, pinch to zoom
        usingSimpleImage(imgMainImage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (filePath != null) {
            if (bitmapTemp == null)
                bitmapTemp = bitmapMain;
            imgMainImage.setImageBitmap(bitmapTemp);
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
                saveImage();;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bitmapTemp = bitmapMain;
        int w = bitmapMain.getWidth();
        int h = bitmapMain.getHeight();
        Mat org = new Mat(h,w, CvType.CV_8SC4);
        Mat gray = new Mat(h,w,CvType.CV_8SC1);
        Utils.bitmapToMat(bitmapMain,org);
        Bitmap bmm = Bitmap.createBitmap(w,h,bitmapMain.getConfig());
        switch (position) {
            case 0:
                Toast.makeText(context,"Không có",
                        Toast.LENGTH_SHORT).show();
                bitmapTemp = bitmapMain;
                break;
            case 1:
                Toast.makeText(context,"Ảnh đen trắng",
                        Toast.LENGTH_SHORT).show();
                /*NativeClass.convertToGray(org.getNativeObjAddr(),
                        gray.getNativeObjAddr());*/
                gray = convertToGray(org);
                Utils.matToBitmap(gray,bmm);
                bitmapTemp = bmm;
                break;
            case 2:
                Toast.makeText(context,"Ảnh âm bản",
                        Toast.LENGTH_SHORT).show();
                gray = convertToNegative(org);
                Utils.matToBitmap(gray,bmm);
                bitmapTemp = bmm;
                break;
            case 3:
                Toast.makeText(context,"Ảnh chạm nổi",
                        Toast.LENGTH_SHORT).show();
                Mat mEmboss = new Mat(h, w,CvType.CV_8SC3);
                mEmboss = convertToEmboss(org);
                Utils.matToBitmap(mEmboss, bmm);
                bitmapTemp = bmm;
                break;
            case 4:
                Toast.makeText(context,"Ảnh mờ",
                        Toast.LENGTH_SHORT).show();
                Mat mBlur = new Mat(h, w,CvType.CV_8SC4);
                mBlur = convertToBlur(org);
                Utils.matToBitmap(mBlur, bmm);
                bitmapTemp = bmm;
                break;
            case 5:
                Toast.makeText(context,"Ảnh cổ điển",
                        Toast.LENGTH_SHORT).show();
                Mat mSepria = new Mat(h, w,CvType.CV_8SC4);
                mSepria = convertToClassic(org);
                Utils.matToBitmap(mSepria, bmm);
                bitmapTemp = bmm;
                break;
            case 6:
                Toast.makeText(context,"Ảnh cổ điển",
                        Toast.LENGTH_SHORT).show();
                Mat mBlue = new Mat(h, w,CvType.CV_8SC4);
                mBlue = convertToBlue(org);
                Utils.matToBitmap(mBlue, bmm);
                bitmapTemp = bmm;
                break;
        }
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
                showDialogSave(bitmapTemp, this, InterfaceClass.InsertFrameClass);
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

    public static Mat convertToGray(Mat src) {
        Mat mGray = new Mat(src.rows(),src.cols(),CvType.CV_8UC1);
        Imgproc.cvtColor(src, mGray,Imgproc.COLOR_RGBA2GRAY);
        return mGray;
    }
    public static Mat convertToNegative(Mat src) {
        Mat mNegative = new Mat(src.rows(),src.cols(),CvType.CV_8UC1);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2RGB);
        /*NativeClass.convertToNegative(src.getNativeObjAddr(),
                mNegative.getNativeObjAddr());*/
        Imgproc.cvtColor(src, mNegative, Imgproc.COLOR_RGB2GRAY);
        Core.subtract(new MatOfDouble(255), src, mNegative);
        return mNegative;
    }
    public static Mat convertToBlackWhite(Mat src) {
        /*Mat mBW = new Mat(src.rows(),src.cols(),CvType.CV_8UC1);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(src, mBW, 0, 255, Imgproc.THRESH_BINARY);
        return mBW;*/
        Mat mNegative = new Mat(src.rows(),src.cols(),CvType.CV_8UC1);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(src, mNegative, Imgproc.COLOR_RGB2GRAY);
        Core.subtract(new MatOfDouble(255), mNegative, mNegative);
        return mNegative;
    }
    public static Mat convertToBlur(Mat src) {
        Mat mBlur = new Mat(src.rows(), src.cols(),CvType.CV_8SC4);
        Imgproc.blur(src, mBlur, new Size(20,1));
        return mBlur;
    }
    public Mat convertToSketchPencil(Mat src) {
        Mat mSketch = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
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
    public static Mat convertToEmboss(Mat src) {
        Mat mOut = new Mat(src.rows(), src.cols(), CvType.CV_8UC3);
        Mat kernel2 = new Mat(5, 5,CvType.CV_32F, new Scalar(0));
        kernel2.put(0, 0, 1.0); kernel2.put(1, 1, 1.0);
        kernel2.put(3, 3, -1.0); kernel2.put(4, 4, -1.0);
        Imgproc.filter2D(src, mOut, CvType.CV_16S, kernel2);
        mOut.convertTo(mOut, CvType.CV_8UC3, 1, 128);
        return mOut;
    }
    public static Mat convertToClassic(Mat src) {
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        Mat kernel = new Mat(4, 4,CvType.CV_32F);
        kernel.put(0, 0, 0.393);kernel.put(0, 1, 0.769);
        kernel.put(0, 2, 0.189);kernel.put(0, 3, 0.0f);
        kernel.put(1, 0, 0.349);kernel.put(1, 1, 0.686);
        kernel.put(1, 2, 0.168);kernel.put(1, 3, 0.0);
        kernel.put(2, 0, 0.272);kernel.put(2, 1, 0.534);
        kernel.put(2, 2, 0.131);kernel.put(2, 3, 0.0);
        kernel.put(3, 0, 0.0);kernel.put(3, 1, 0.0);
        kernel.put(3, 2, 0.0);kernel.put(3, 3, 1.0);
        Core.transform(src, dst, kernel);
        return dst;
    }
    public static Mat convertToBlue(Mat src) {
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        Mat kernel = new Mat(4, 4,CvType.CV_32F);
        kernel.put(0, 0, 0.272f);kernel.put(0, 1, 0.534f);
        kernel.put(0, 2, 0.131f);kernel.put(0, 3, 0.0f);
        kernel.put(1, 0, 0.349f);kernel.put(1, 1, 0.686f);
        kernel.put(1, 2, 0.168f);kernel.put(1, 3, 0.0);
        kernel.put(2, 0, 0.393f);kernel.put(2, 1, 0.769f);
        kernel.put(2, 2, 0.189f);kernel.put(2, 3, 0.0);
        kernel.put(3, 0, 0.0);kernel.put(3, 1, 0.0);
        kernel.put(3, 2, 0.0);kernel.put(3, 3, 1.0);
        Core.transform(src, dst, kernel);
        return dst;
    }

    private void saveImage() {
        bitmapMain = bitmapTemp;
        saveImageFile(fileUri,bitmapMain);
    }
    private void cancelAction() {
        imgMainImage.setImageBitmap(bitmapMain);
    }
}
