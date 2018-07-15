package com.example.leminhbang.camearamini;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

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
    private static Bitmap bitmapDraw;
    private int flag = 0;
    float x0, y0, x1, y1, x2, y2, x3, y3;
    private int CROP_IMAGE = 1;
    private boolean isFirst = true;

    private ImageButton imbPoint_1, imbPoint_2, imbPoint_3, imbPoint_4;
    private List<Point> touchPoints = new ArrayList<>();
    private float mX, mY;
    private boolean CHOOSETOUCH = false;
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (filePath != null) {
            if (bitmapTemp == null) {
                bitmapTemp = bitmapMain;
                showDialogChoose();
            }
            imgMainImage.setImageBitmap(bitmapTemp);
        }
    }
    private void showDialogChoose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn phương thức cắt ảnh");
        builder.setMessage("Bạn muốn cắt ảnh theo " +
                "điểm chọn hay khung chọn");
        builder.setCancelable(false);
        builder.setPositiveButton("Chọn khung", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CHOOSETOUCH = false;
                //reset();
                performCrop(fileUri);
            }
        });
        builder.setNegativeButton("Chọn điểm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CHOOSETOUCH = true;
                //reset();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_action, menu);
        invalidateOptionsMenu();
        MenuItem item = menu.findItem(R.id.action_finish);
        item.setVisible(true);
        return true;
    }
    private void mapView() {
        findViewById(R.id.linearlayout_main).setOnTouchListener(this);
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        btmnBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btmnBottomMenu.setOnNavigationItemSelectedListener(this);

        imbPoint_1 = (ImageButton) findViewById(R.id.imbPoint_1);
        imbPoint_2 = (ImageButton) findViewById(R.id.imbPoint_2);
        imbPoint_3 = (ImageButton) findViewById(R.id.imbPoint_3);
        imbPoint_4 = (ImageButton) findViewById(R.id.imbPoint_4);
        imbPoint_1.setOnTouchListener(this);
        imbPoint_2.setOnTouchListener(this);
        imbPoint_3.setOnTouchListener(this);
        imbPoint_4.setOnTouchListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save_2:
                bitmapMain = bitmapTemp;
                saveImageFile(fileUri, bitmapMain);
                flag = 0;
                break;
            case R.id.action_cancel_2:
                cancelAction();
                break;
            case R.id.action_finish:
                saveImage();;
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        context = contextTmp;
        showDialogSave(bitmapTemp, this, contextTmp.getClass());
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
                bitmapTemp = extras.getParcelable("data");
                imgMainImage.setImageBitmap(bitmapTemp);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (id) {
            case R.id.img_main_image:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (CHOOSETOUCH)
                        setCoordinate(event);
                }

                gestureDetector.onTouchEvent(event);
                break;
            case R.id.linearlayout_main:
                gestureDetector.onTouchEvent(event);
                break;
            case R.id.imbPoint_1:
                setTouchPoint(id, event, imbPoint_1);
                break;
            case R.id.imbPoint_2:
                setTouchPoint(id, event, imbPoint_2);
                break;
            case R.id.imbPoint_3:
                setTouchPoint(id, event, imbPoint_3);
                break;
            case R.id.imbPoint_4:
                setTouchPoint(id, event, imbPoint_4);
                break;
        }
        return true;
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
                //reset();
                showDialogChoose();
                break;
        }
        return true;
    }
    private void reset() {
        resetValue();
        bitmapTemp.recycle();
        imgMainImage.setImageBitmap(bitmapMain);
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
    public static float[] getPointFromPointChange(ImageView view,
                                                  float e1, float e2) {

        final float[] coords = new float[] { e1, e2 };
        Matrix m = new Matrix();
        view.getImageMatrix().invert(m);
        m.postTranslate(view.getScrollX(), view.getScrollY());
        m.mapPoints(coords);
        return coords;
    }
    private void setTouchPoint(int id, MotionEvent event,
                               ImageButton imb) {
        float x,y;
        List<Point>  pts = new ArrayList<>();
        float[] points;
        RelativeLayout.LayoutParams params = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getRawX() - 8;
                y = event.getRawY() + 108;
                imb.setX(x); imb.setY(y);
               points = getPointFromPointChange(imgMainImage, x, y);
                pts.add(new Point(points[0], points[1]));
                getTouchPointSelected(id, pts);
                drawOnChangeTouchPoint();
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getRawX() - 8;
                y = event.getRawY() + 109;
                imb.setX(x); imb.setY(y);

                points = getPointFromPointChange(imgMainImage, x, y);
                pts.add(new Point(points[0], points[1]));
                getTouchPointSelected(id, pts);
                drawOnChangeTouchPoint();
                break;
            case MotionEvent.ACTION_UP:
                /*points = getPointOfTouchedCordinate(imgMainImage
                        , event);
                pts = new ArrayList<>();
                pts.add(new Point(points[0], points[1]));
                getTouchPointSelected(id, pts);
                drawOnChangeTouchPoint();*/
                break;
        }
    }
    private void drawOnChangeTouchPoint() {
        Bitmap bmp = Bitmap.createBitmap(bitmapMain);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Bitmap outputBitmap = Bitmap.createBitmap(bmp.getWidth(),
                bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawBitmap(bmp, 0, 0, null);
        float[] p0 = {(float) touchPoints.get(0).x, (float) touchPoints.get(0).y};
        float[] p1 = {(float) touchPoints.get(1).x, (float) touchPoints.get(1).y};
        float[] p2 = {(float) touchPoints.get(2).x, (float) touchPoints.get(2).y};
        float[] p3 = {(float) touchPoints.get(3).x, (float) touchPoints.get(3).y};
        canvas.drawLines(new float[] {p0[0], p0[1], p1[0], p1[1]}, paint);
        canvas.drawLines(new float[] {p1[0], p1[1], p2[0], p2[1]}, paint);
        canvas.drawLines(new float[] {p2[0], p2[1], p3[0], p3[1]}, paint);
        canvas.drawLines(new float[] {p0[0], p0[1], p3[0], p3[1]}, paint);
        imgMainImage.setImageBitmap(outputBitmap);
    }
    private void getTouchPointSelected(int id,
                                              List<Point> pts) {
        double[] p = {pts.get(0).x, pts.get(0).y};
        if (id == R.id.imbPoint_1) {
            touchPoints.get(0).set(p);
        }
        else if (id == R.id.imbPoint_2) {
            touchPoints.get(1).set(p);
        }
        else if (id == R.id.imbPoint_3) {
            touchPoints.get(2).set(p);
        }
        else {
            touchPoints.get(3).set(p);
        }
    }
    public void setCoordinate(MotionEvent event) {
        float x = event.getX() - 10.0f, y = event.getY() + 60.0f;
        int[] viewCoords = new int[2];
        imgMainImage.getLocationOnScreen(viewCoords);
        float[] points = getPointOfTouchedCordinate(imgMainImage
                , event);
        if (flag == 4)
            return;
        switch (flag) {
            case 0:
                bitmapDraw = Bitmap.createBitmap(bitmapTemp);
                x0 = points[0];
                y0 = points[1];
                imbPoint_1.setX(x);
                imbPoint_1.setY(y);
                imbPoint_1.setVisibility(View.VISIBLE);
                touchPoints.add(new Point(x0, y0));
                bitmapDraw = drawPoint(bitmapDraw,x0,y0,x0,y0);
                break;
            case 1:
                x1 = points[0];
                y1 = points[1];
                imbPoint_2.setX(x);
                imbPoint_2.setY(y);
                imbPoint_2.setVisibility(View.VISIBLE);
                touchPoints.add(new Point(x1, y1));
                bitmapDraw = drawPoint(bitmapDraw,x0,y0,x1,y1);
                break;
            case 2:
                x2 = points[0];
                y2 = points[1];
                imbPoint_3.setX(x);
                imbPoint_3.setY(y);
                imbPoint_3.setVisibility(View.VISIBLE);
                touchPoints.add(new Point(x2, y2));
                bitmapDraw = drawPoint(bitmapDraw,x1,y1,x2,y2);
                break;
            case 3:
                x3 = points[0];
                y3 = points[1];
                imbPoint_4.setX(x);
                imbPoint_4.setY(y);
                imbPoint_4.setVisibility(View.VISIBLE);
                touchPoints.add(new Point(x3, y3));
                bitmapDraw = drawPoint(bitmapDraw,x2,y2,x3,y3);
                bitmapDraw = drawPoint(bitmapDraw,x3,y3,x0,y0);
                break;
        }
        imgMainImage.setImageBitmap(bitmapDraw);
        flag ++;

    }

    public static Bitmap drawPoint(Bitmap mBitmap,float a, float b, float c, float d) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Bitmap outputBitmap = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        //canvas.drawCircle(a, b, 5, paint);
        //canvas.drawCircle(c, d, 5, paint);
        canvas.drawLine(a,b,c,d,paint);
        mBitmap = outputBitmap;
        return outputBitmap;
    }

    public void saveImage() {
        if (flag != 4) {
            flag = 0;
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(bitmapMain.getWidth(),bitmapMain.getHeight(),
                bitmapMain.getConfig());
        //Canvas canvas = new Canvas(bitmap);
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
        //canvas.drawBitmap(bitmapMain, matrix, null);

        Mat inputMat = new Mat();
        Mat outputMat = new Mat();
        Utils.bitmapToMat(bitmapTemp, inputMat);
        Mat startM = Converters.vector_Point2f_to_Mat(touchPoints);

        List<Point> dst_pnt = new ArrayList<Point>();
        Point p4 = new Point(0.0, 0.0);
        dst_pnt.add(p4);
        Point p5 = new Point(0.0, bitmap.getHeight());
        Point p6 = new Point(bitmap.getWidth(), bitmap.getHeight());
        Point p7 = new Point(bitmap.getWidth(), 0);
        dst_pnt.add(p7);
        dst_pnt.add(p6);
        dst_pnt.add(p5);
        Mat endM = Converters.vector_Point2f_to_Mat(dst_pnt);
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);
        Size size = new Size(bitmap.getWidth(), bitmap.getHeight());
        Scalar scalar = new Scalar(50.0);
        Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, size,
                Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS,
               Core.BORDER_DEFAULT, scalar);
        bitmap = Bitmap.createBitmap(outputMat.cols(),
                outputMat.rows(),bitmapMain.getConfig());
        Utils.matToBitmap(outputMat, bitmap);

        //hien thi bitmap vua cat duoc
        imgMainImage.setImageBitmap(null);
        imgMainImage.setScaleX(1);
        imgMainImage.setScaleY(1);
        imgMainImage.setImageBitmap(bitmap);
        bitmapTemp = bitmap;
        resetValue();
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
        bitmapTemp = bitmapMain;
        bitmapDraw = bitmapMain;
        imgMainImage.setImageBitmap(bitmapMain);
        resetValue();
    }
    private void resetValue() {
        touchPoints = new ArrayList<>();
        imbPoint_1.setVisibility(View.GONE);
        imbPoint_2.setVisibility(View.GONE);
        imbPoint_3.setVisibility(View.GONE);
        imbPoint_4.setVisibility(View.GONE);
        flag = 0;
    }
}


