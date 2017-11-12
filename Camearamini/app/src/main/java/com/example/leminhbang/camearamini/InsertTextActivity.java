package com.example.leminhbang.camearamini;

import android.content.ClipData;
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
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static com.example.leminhbang.camearamini.MainActivity.context;

public class InsertTextActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener, TextWatcher, View.OnLongClickListener, View.OnDragListener {
    private ImageView imgMainImage;
    private ImageView imgTempImage;
    private BottomNavigationView btmnBottomMenu;
    private EditText edtInsertText;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;

    private RelativeLayout.LayoutParams params;

    int mode = 0, drag = 1;
    ViewGroup vg;


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
       //edtInsertText.setOnTouchListener(this);
        edtInsertText.setOnLongClickListener(this);
        edtInsertText.setOnDragListener(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        380, 100);
        layoutParams.leftMargin = getWindowManager().getDefaultDisplay().getWidth()/4;
        layoutParams.topMargin = getWindowManager().getDefaultDisplay().getHeight()/2;
        edtInsertText.setLayoutParams(layoutParams);
        btmnBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btmnBottomMenu.setOnNavigationItemSelectedListener(this);

        findViewById(R.id.rellayout_main_layout).setOnDragListener(this);
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
        }
        return true;
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
        int x = (int) edtInsertText.getX();
        int y = (int) edtInsertText.getY();
        Bitmap b = drawText(bitmap, s.toString(),x,y);
        imgMainImage.setImageBitmap(b);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public Bitmap drawText(Bitmap bitmap, String text, int x,int y) {
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(outputBitmap);
        c.drawBitmap(bitmap,0,0,null);
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setTextSize(60);
        c.drawText(text, x, y,paint);
        return outputBitmap;
    }

    @Override
    public boolean onLongClick(View v) {
        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new
                View.DragShadowBuilder(edtInsertText);
        edtInsertText.startDrag(data, shadowBuilder, edtInsertText, 0);
        edtInsertText.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        View view = (View) event.getLocalState();
        vg = (ViewGroup) v.getParent();
        RelativeLayout rl = (RelativeLayout)
                vg.findViewById(R.id.rellayout_main_layout);

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                int x = (int) event.getX();
                int y = (int) event.getY();
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                x = (int) event.getX();
                y = (int) event.getY();
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                break;

            case DragEvent.ACTION_DROP:
                int childCountDropped = rl.getChildCount();
                x = (int) event.getX();
                y = (int) event.getY();
                params.leftMargin = x;
                params.topMargin =  y;

                view.setLayoutParams(params);
                edtInsertText.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                break;
            default:
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
                            (10 * params.width);
                    params.bottomMargin = params.topMargin +
                            (10 * params.height);
                    edt.setLayoutParams(params);
                }
                break;
        }
    }
}
