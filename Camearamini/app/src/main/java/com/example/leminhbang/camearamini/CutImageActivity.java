package com.example.leminhbang.camearamini;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
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
import android.widget.Toast;

import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.bitmapTemp;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;
import static com.example.leminhbang.camearamini.MainActivity.showDialogSave;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;

public class CutImageActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage;
    private BottomNavigationView btmnBottomMenu;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private Bitmap bitmapDraw;
    private int flag = 0;
    float x0, y0, x1, y1, x2, y2, x3, y3;
    private int CROP_IMAGE = 1;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextTmp = context;
        context = this;
        mapView();
        gestureDetector = new GestureDetector(this,new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

        //performCrop(fileUri);
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
    private void mapView() {
        findViewById(R.id.linearlayout_main).setOnTouchListener(this);
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        btmnBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btmnBottomMenu.setOnNavigationItemSelectedListener(this);
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
    public void onBackPressed() {
        context = contextTmp;
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP_IMAGE) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                bitmapMain = extras.getParcelable("data");

                imgMainImage.setImageBitmap(bitmapMain);
            }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (id) {
            case R.id.img_main_image:
                /*scaleGestureDetector.onTouchEvent(event);
                float scale = MyScaleGesture.getScaleValue();*/
                /*if (!isFirst) {
                    imgMainImage.setScaleX(scale);
                    imgMainImage.setScaleY(scale);
                } else {
                    isFirst = false;
                    MyScaleGesture.setScaleValue();
                }*/
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setCoordinate(event);
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
                break;
        }
        return true;
    }
    public static float[] getPointOfTouchedCordinate(ImageView view, MotionEvent e) {

        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix m = new Matrix();
        view.getImageMatrix().invert(m);
        m.postTranslate(view.getScrollX(), view.getScrollY());
        m.mapPoints(coords);
        return coords;

    }
    public void setCoordinate(MotionEvent event) {
        int[] viewCoords = new int[2];
        imgMainImage.getLocationOnScreen(viewCoords);
        float[] points = getPointOfTouchedCordinate(imgMainImage
                , event);
        if (flag == 4)
            flag = 0;
        switch (flag) {
            case 0:
                bitmapDraw = Bitmap.createBitmap(bitmapMain);
                x0 = points[0];
                y0 = points[1];
                drawPoint(x0,y0,x0,y0);
                break;
            case 1:
                x1 = points[0];
                y1 = points[1];
                drawPoint(x0,y0,x1,y1);
                break;
            case 2:
                x2 = points[0];
                y2 = points[1];
                drawPoint(x1,y1,x2,y2);
                break;
            case 3:
                x3 = points[0];
                y3 = points[1];
                drawPoint(x2,y2,x3,y3);
                drawPoint(x3,y3,x0,y0);
                break;
        }
        flag ++;

    }

    private void drawPoint(float a, float b, float c, float d) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Bitmap outputBitmap = Bitmap.createBitmap(bitmapDraw.getWidth(),
                bitmapDraw.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawBitmap(bitmapDraw,0,0,null);
        canvas.drawCircle(a, b, 10, paint);
        canvas.drawCircle(c, d, 10, paint);
        canvas.drawLine(a,b,c,d,paint);
        bitmapDraw = outputBitmap;
        imgMainImage.setImageBitmap(bitmapDraw);
    }

    public void saveImage() {
        if (flag != 4) {
            flag = 0;
            return;
        }
        bitmapTemp = bitmapMain;
        Bitmap bitmap = Bitmap.createBitmap(bitmapMain.getWidth(),bitmapMain.getHeight(),
                bitmapMain.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.setPolyToPoly(
                new float[] {
                        x0, y0,
                        x1, y1,
                        x3, y3,
                        x2, y2},
                0,
                new float[] {
                        0, 0,
                        bitmap.getWidth(), 0,
                        0, bitmap.getHeight(),
                        bitmap.getWidth(), bitmap.getHeight()
                }, 0, 4);
        //tao bitmap moi
        canvas.drawBitmap(bitmapMain,matrix,null);

        //hien thi bitmap vua cat duoc
        imgMainImage.setImageBitmap(null);
        imgMainImage.setScaleX(1);
        imgMainImage.setScaleY(1);
        imgMainImage.setImageBitmap(bitmap);
        bitmapMain = bitmap;
        flag = 0;

        saveImageFile(fileUri,bitmapMain);
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_IMAGE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void cancelAction() {
        bitmapMain = bitmapTemp;
        bitmapDraw = bitmapMain;
        imgMainImage.setImageBitmap(bitmapMain);
        flag = 0;
    }
}


