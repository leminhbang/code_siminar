package com.example.leminhbang.camearamini;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
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
    public int getGalleryCount() {
        return galleryChangeShade.getCount();
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
                imgMainImage.setImageBitmap(bitmapMain);
                break;
            case 1:
                Toast.makeText(context,"Ảnh xám",
                        Toast.LENGTH_SHORT).show();
                /*NativeClass.convertToGray(org.getNativeObjAddr(),
                        gray.getNativeObjAddr());*/
                gray = convertToGray(org);
                Utils.matToBitmap(gray,bmm);
                bitmapTemp = bmm;
                imgMainImage.setImageBitmap(bitmapTemp);
                break;
            case 2:
                Toast.makeText(context,"Ảnh âm bản",
                        Toast.LENGTH_SHORT).show();
                gray = convertToNegative(org);
                Utils.matToBitmap(gray,bmm);
                bitmapTemp = bmm;
                imgMainImage.setImageBitmap(bitmapTemp);
                break;
            case 3:
                Toast.makeText(context,"Ảnh truyện tranh",
                        Toast.LENGTH_SHORT).show();
                gray = convertToBlackWhite(org);
                Utils.matToBitmap(gray,bmm);
                bitmapTemp = bmm;
                imgMainImage.setImageBitmap(bitmapTemp);
                break;
            case 4:
                Toast.makeText(context,"Ảnh mờ",
                        Toast.LENGTH_SHORT).show();
                Mat mBlur = new Mat(h, w,CvType.CV_8SC4);
                mBlur = convertToBlur(org);
                Utils.matToBitmap(mBlur, bmm);
                bitmapTemp = bmm;
                imgMainImage.setImageBitmap(bitmapTemp);
                break;
            case 5:
                Toast.makeText(context,"Ảnh cổ điển",
                        Toast.LENGTH_SHORT).show();
                bitmapTemp = convertToClassic(bitmapMain);
                imgMainImage.setImageBitmap(bitmapTemp);
                break;
        }
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

    public static Mat convertToGray(Mat src) {
        Mat mGray = new Mat(src.rows(),src.cols(),CvType.CV_8UC1);
        Imgproc.cvtColor(src, mGray,Imgproc.COLOR_RGBA2GRAY);
        return mGray;
    }
    public static Mat convertToNegative(Mat src) {
        Mat mNegative = new Mat(src.rows(),src.cols(),CvType.CV_8UC1);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2RGB);
        NativeClass.convertToNegative(src.getNativeObjAddr(),
                mNegative.getNativeObjAddr());
        return mNegative;
    }
    public static Mat convertToBlackWhite(Mat src) {
        Mat mBW = new Mat(src.rows(),src.cols(),CvType.CV_8UC1);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(src, mBW, 110, 240, Imgproc.THRESH_BINARY);
        return mBW;
    }
    public static Mat convertToBlur(Mat src) {
        Mat mBlur = new Mat(src.rows(), src.cols(),CvType.CV_8SC4);
        Imgproc.blur(src, mBlur, new Size(20,1));
        return mBlur;
    }
    public static Bitmap convertToClassic(Bitmap mBitmap) {
        Drawable d = new BitmapDrawable(context.getResources(),
                mBitmap);
        d.setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
        return ((BitmapDrawable) d).getBitmap();
    }
    public static Bitmap _convertToBlur(Context context, Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        Bitmap inputBitmap = Bitmap.createBitmap(image);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs,
                Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(5);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    private void saveImage() {
        bitmapMain = bitmapTemp;
        saveImageFile(fileUri,bitmapMain);
    }
    private void cancelAction() {
        imgMainImage.setImageBitmap(bitmapMain);
    }
}
