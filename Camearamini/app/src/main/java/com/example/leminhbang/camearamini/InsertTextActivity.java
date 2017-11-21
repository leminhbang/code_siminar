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
import android.text.TextPaint;
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

import java.util.ArrayList;

import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;

public class InsertTextActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener, View.OnLongClickListener, View.OnDragListener {
    private ImageView imgMainImage;
    private ImageView imgTempImage;
    private BottomNavigationView btmnBottomMenu;
    private EditText edtInsertText;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private ArrayList<EditText> arrEditTexts = new ArrayList<EditText>();

    private RelativeLayout.LayoutParams params;
    ViewGroup vg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextTmp = context;
        mapView();
        if (filePath != null) {
            imgMainImage.setImageURI(fileUri);
        }
        context = this;

        //them edittext hien tai vao mang edittext
        arrEditTexts.add(edtInsertText);
        //gan anh tam
        imgTempImage = imgMainImage;
        gestureDetector = new GestureDetector(this,new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

    }
    public void mapView() {
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        edtInsertText = (EditText) findViewById(R.id.edtInsertText);
        //edtInsertText.addTextChangedListener(this);
        edtInsertText.setOnLongClickListener(this);
        edtInsertText.setOnDragListener(this);
        int w = getWindowManager().getDefaultDisplay().getWidth()/3;
        int h = getWindowManager().getDefaultDisplay().getHeight()/10;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        w, h);
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
                saveImage();
                break;
            case R.id.action_new_text:
                addNewText();
                break;
            case R.id.action_cancle_2:
                imgMainImage = imgTempImage;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewText() {
        EditText edt = new EditText(this);
        int w = getWindowManager().getDefaultDisplay().getWidth()/3;
        int h = getWindowManager().getDefaultDisplay().getHeight()/10;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                w, h);
        layoutParams.leftMargin = getWindowManager().getDefaultDisplay().getWidth()/4;
        layoutParams.topMargin = getWindowManager().getDefaultDisplay().getHeight()/2;
        edt.setHint("Nhập chữ muốn chèn");
        edt.setHintTextColor(Color.BLUE);
        edt.setTextColor(Color.BLUE);
        edt.setLayoutParams(layoutParams);
//        edt.addTextChangedListener(this);
        edt.setOnLongClickListener(this);
        edt.setOnDragListener(this);
        arrEditTexts.add(edt);
        vg = (ViewGroup) findViewById(R.id.rellayout_main_layout);
        vg.addView(edt);
    }

    private void saveImage() {
        for (int i = 0; i < arrEditTexts.size(); i++) {
            imgMainImage.buildDrawingCache();
            Bitmap bitmap = imgMainImage.getDrawingCache();
            int x = (int) arrEditTexts.get(i).getX();
            int y = (int) arrEditTexts.get(i).getY();
            Bitmap b =  drawText(bitmap,arrEditTexts.get(i).getText().toString().trim(),
                    x,y);
            imgMainImage.setImageBitmap(b);
            arrEditTexts.get(i).setVisibility(View.INVISIBLE);
        }
        imgMainImage.buildDrawingCache();
        Bitmap bitmap = imgMainImage.getDrawingCache();
        saveImageFile(fileUri,bitmap);
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
        EditText edt = (EditText) v;
        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new
                View.DragShadowBuilder(edt);
        edt.startDrag(data, shadowBuilder, edt, 0);
        edt.setVisibility(View.INVISIBLE);
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
                params.leftMargin = x - getWindowManager().
                        getDefaultDisplay().getWidth()/4;
                params.topMargin =  y;
                view.setLayoutParams(params);
                view.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

}
