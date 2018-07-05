package com.example.leminhbang.camearamini;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.agilie.RotatableAutofitEditText;

import java.util.ArrayList;

import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.bitmapTemp;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;
import static com.example.leminhbang.camearamini.MainActivity.showDialogSave;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;

public class InsertTextActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener/*, View.OnLongClickListener, View.OnDragListener*/ {
    private ImageView imgMainImage;
    private BottomNavigationView btmnBottomMenu;
    //private EditText edtInsertText;
    private RotatableAutofitEditText edtInsertText;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private ArrayList<RotatableAutofitEditText> arrEditTexts = new ArrayList<>();

    private RelativeLayout.LayoutParams params;
    ViewGroup vg;
    private boolean isFirst = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextTmp = context;
        mapView();

        context = this;

        //them edittext hien tai vao mang edittext
        arrEditTexts.add(edtInsertText);

        gestureDetector = new GestureDetector(this,new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bitmapTemp == null)
            bitmapTemp = bitmapMain;
        imgMainImage.setImageBitmap(bitmapTemp);
    }

    public void mapView() {
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        edtInsertText = (RotatableAutofitEditText) findViewById(R.id.edtInsertText);
        //edtInsertText.addTextChangedListener(this);
       /* edtInsertText.setOnLongClickListener(this);
        edtInsertText.setOnDragListener(this);*/
        //edtInsertText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        int w = getWindowManager().getDefaultDisplay().getWidth()/2;
        int h = getWindowManager().getDefaultDisplay().getHeight()/10;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        w, h);
        //layoutParams.leftMargin = getWindowManager().getDefaultDisplay().getWidth()/4;
        //layoutParams.topMargin = getWindowManager().getDefaultDisplay().getHeight()/2;
        //edtInsertText.setLayoutParams(layoutParams);
        btmnBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btmnBottomMenu.setOnNavigationItemSelectedListener(this);

        //findViewById(R.id.rellayout_main_layout).setOnDragListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_action, menu);
        invalidateOptionsMenu();
        MenuItem newText = menu.findItem(R.id.action_new_text);
        newText.setVisible(true);
        MenuItem finish = menu.findItem(R.id.action_finish);
        finish.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save_2:
                //goi ham ve canvas
                bitmapMain = bitmapTemp;
                saveImageFile(fileUri,bitmapMain);
                imgMainImage.setImageBitmap(bitmapMain);
                break;
            case R.id.action_new_text:
                addNewText();
                break;
            case R.id.action_finish:
                saveImage();
                break;
            case R.id.action_cancel_2:
                cancelAction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewText() {
        RotatableAutofitEditText edt = new RotatableAutofitEditText(this);
        int w = getWindowManager().getDefaultDisplay().getWidth()/2;
        int h = getWindowManager().getDefaultDisplay().getHeight()/10;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                w, h);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.leftMargin = 0;//getWindowManager().getDefaultDisplay().getWidth()/4;
        layoutParams.topMargin = 100;//getWindowManager().getDefaultDisplay().getHeight()/2;
        edt.setHint("Nhập chữ muốn chèn");
        edt.setHintTextColor(Color.argb(255, 51, 181, 229));
        edt.setTextColor(Color.argb(255, 0, 153, 204));
        edt.setGravity(Gravity.CENTER);
        edt.setPadding(16, 16, 16,16);

        edt.setBackgroundResource(R.drawable.rounded_corners_white_transparent_50);
        //edt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        edt.setMaxTextSize(getResources().getDimension(
                R.dimen.autoresize_max_text_size));
        edt.setMinTextSize(getResources().getDimension(
                R.dimen.autoresize_min_text_size));
        edt.setMinWidth(50);

        edt.shouldResize(true);
        edt.shouldRotate(true);
        edt.setShouldTranslate(true);
        edt.setLayoutParams(layoutParams);
        arrEditTexts.add(edt);
        vg = (ViewGroup) findViewById(R.id.rellayout_main_layout);
        vg.addView(edt);
    }

    private void saveImage() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().
                getMetrics(metrics);
        float sw =imgMainImage.getMeasuredWidth();
        float sh = imgMainImage.getMeasuredHeight();
        int bw = bitmapMain.getWidth();
        int bh = bitmapMain.getHeight();
        float ratio = bw > bh ? bh*1.0f/bw : bw*1.0f/bh;
        float mx = (sw - ratio*bw)/2.0f >= 0 ?
                (sw - ratio*bw)/2.0f : 0;
        float my = (sh - ratio*bh)/2.0f >= 0 ?
                (sh - ratio*bh)/2.0f + 120 : 0;
        for (int i = 0; i < arrEditTexts.size(); i++) {
            int[] pos = new int[2];
            //arrEditTexts.get(i).getLocationInWindow(pos);
            pos[0] = (int) arrEditTexts.get(i).getX();
            pos[1] = (int) arrEditTexts.get(i).getY();
            /*float x = 1.0f*pos[0]*bw/sw;
            float y = (pos[1] -60)*1.0f*bh/sh ;*/
            float[] cod = getTextPosition(imgMainImage,
                    arrEditTexts.get(i));
            float x = cod[0], y = cod[1];
            bitmapTemp = drawText(bitmapTemp ,
                    arrEditTexts.get(i), x, y - 60);
            imgMainImage.setImageBitmap(bitmapTemp);
            arrEditTexts.get(i).setVisibility(View.GONE);
        }
        arrEditTexts.clear();
    }
    public float[] getTextPosition(ImageView view,
                                   RotatableAutofitEditText e) {
        final float[] coords = new float[] { e.getX(), e.getY() };
        Matrix m = new Matrix();
        view.getImageMatrix().invert(m);
        m.postTranslate(view.getScrollX(), view.getScrollY());
        m.mapPoints(coords);
        return coords;

    }
    public Bitmap drawText(Bitmap bitmap, RotatableAutofitEditText edt, float x, float y) {

        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(outputBitmap);
        c.drawBitmap(bitmap, 0, 0, null);

        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(edt.getTextColors().getDefaultColor());
        float size = edt.getTextSize();
        paint.setTextSize(size);
        //draw text on bitmap
        String text = edt.getText().toString().trim();
        c.save();
        float direction = edt.getRotation();
        c.rotate(direction, x, y);
        float rx = bitmap.getWidth()*1.0f/imgMainImage.getMeasuredWidth();
        float ry = bitmap.getHeight()*1.0f/imgMainImage.getMeasuredHeight();
        /*x = edt.getLeft()*rx;
        y = edt.getTop()*ry;*/
        c.drawText(text, x, y, paint);

        return outputBitmap;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (id) {
            case R.id.img_main_image:
                scaleGestureDetector.onTouchEvent(event);
                if (!isFirst) {
                    float scale = MyScaleGesture.getScaleValue();
                    /*imgMainImage.setScaleX(scale);
                    imgMainImage.setScaleY(scale);*/
                } else {
                    isFirst = false;
                    MyScaleGesture.setScaleValue(1.0f);
                }
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

    /*@Override
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
    }*/
    private void cancelAction() {
        bitmapMain = bitmapTemp;
        imgMainImage.setImageBitmap(bitmapMain);
        for (int i = 0; i < arrEditTexts.size(); i++) {
            arrEditTexts.get(i).setVisibility(View.GONE);
        }
        arrEditTexts.clear();
    }
}
