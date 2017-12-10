package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;

public class CutImageActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage;
    private BottomNavigationView btmnBottomMenu;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private int flag = 0;
    float x0, y0, x1, y1, x2, y2, x3, y3;

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
            imgMainImage.setImageURI(fileUri);
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
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        context = contextTmp;
        super.onBackPressed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (id) {
            case R.id.img_main_image:
                scaleGestureDetector.onTouchEvent(event);
                float scale = MyScaleGesture.getScaleValue();

                //scale imageview de hien thi
                imgMainImage.setScaleX(scale);
                imgMainImage.setScaleY(scale);

                if (event.getAction() == MotionEvent.ACTION_DOWN && flag < 4) {
                    Toast.makeText(context,"Cham lan " + (flag + 1),
                            Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(context,InsertTextActivity.class);
                startActivity(intent);
                break;
            case R.id.action_insert_frame:
                intent = new Intent(CutImageActivity.this, InsertFrameActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void setCoordinate(MotionEvent event) {
        switch (flag) {
            case 0:
                x0 = event.getX();
                y0 = event.getY();
                break;
            case 1:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case 2:
                x2 = event.getX();
                y2 = event.getY();
                break;
            case 3:
                x3 = event.getX();
                y3 = event.getY();
                break;
        }
        flag ++;

    }
    public void saveImage() {
        imgMainImage.buildDrawingCache();
        Bitmap b = imgMainImage.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(b.getWidth(),b.getHeight(),
                b.getConfig());
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
        canvas.drawBitmap(b,matrix,null);

        //hien thi bitmap vua cat duoc
        imgMainImage.setImageBitmap(null);
        imgMainImage.setScaleX(1);
        imgMainImage.setScaleY(1);
        imgMainImage.setImageBitmap(bitmap);
        flag = 0;
    }
}

