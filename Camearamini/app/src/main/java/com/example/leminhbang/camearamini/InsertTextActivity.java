package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static com.example.leminhbang.camearamini.MainActivity.context;

public class InsertTextActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener, TextWatcher {
    private ImageView imgMainImage;
    private ImageView imgTempImage;
    private BottomNavigationView btmnBottomMenu;
    private EditText edtInsertText;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;

    int mode = 0, drag = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextTmp = context;
        context = this;
        mapView();

        imgTempImage = imgMainImage;
        gestureDetector = new GestureDetector(this,new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

    }
    public void mapView() {
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        edtInsertText = (EditText) findViewById(R.id.edtInsertText);
        edtInsertText.addTextChangedListener(this);
        edtInsertText.setOnTouchListener(this);
        edtInsertText.requestFocus();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        380, 100);
        params.leftMargin = getWindowManager().getDefaultDisplay().getWidth()/4;
        params.topMargin = getWindowManager().getDefaultDisplay().getHeight()/2;
        edtInsertText.setLayoutParams(params);
        btmnBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btmnBottomMenu.setOnNavigationItemSelectedListener(this);
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
                //goi ham ve canvas
                break;
            case R.id.action_cancle_2:
                imgMainImage = imgTempImage;
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
                float scale = MyScaleGesture.getScaleValue();
                //imgMainImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imgMainImage.setScaleX(scale);
                imgMainImage.setScaleY(scale);
                gestureDetector.onTouchEvent(event);
                break;
            case R.id.rellayout_main_layout:
                gestureDetector.onTouchEvent(event);
                break;
            case R.id.edtInsertText:
                moveEditText(v,event);
                break;
        }
        return true;
    }
    public void moveEditText(View v, MotionEvent event) {
        EditText edt = (EditText) v;
        RelativeLayout.LayoutParams params;
       float dx = 0f, dy = 0f, x = 0f, y = 0f;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                params = (RelativeLayout.LayoutParams) edt.getLayoutParams();
                dx = event.getRawX() - params.leftMargin;
                dy = event.getRawY() - params.topMargin;
                /*dx = event.getRawX();
                dy = event.getRawY();*/

                mode = drag;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == drag) {
                    params = (RelativeLayout.LayoutParams) edt.getLayoutParams();
                    x = event.getRawX();
                    y = event.getRawY();
                    params.leftMargin = (int) (x - dx);
                    params.topMargin = (int) (y - dy);
                    params.rightMargin = 0;
                    params.bottomMargin = 0;
                    params.rightMargin = params.leftMargin +
                            (5 * params.width);
                    params.bottomMargin = params.topMargin +
                            (10 * params.height);
                    edt.setLayoutParams(params);
                }
                break;
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_insert_text:
                break;
            case R.id.action_insert_frame:
                Intent intent = new Intent(context,InsertFrameActivity.class);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        imgMainImage = imgTempImage;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        imgMainImage.buildDrawingCache();
        Bitmap bitmap = imgMainImage.getDrawingCache();
        Bitmap b = drawText(bitmap, s.toString());
        imgMainImage.setImageBitmap(b);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    public Bitmap drawText(Bitmap bitmap, String text) {
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(outputBitmap);
        c.drawBitmap(bitmap,0,0,null);
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setTextSize(60);
        c.drawText(text, outputBitmap.getWidth()/4,
                outputBitmap.getHeight() - 100,paint);
        return outputBitmap;
    }
}
