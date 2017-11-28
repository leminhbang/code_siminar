package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;

public class ChangeColorActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage;
    private ArrayList<ImageButton> imgbButtonColors = new ArrayList<ImageButton>();
    private BottomNavigationView btmnBottomMenu;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;

    public int currentObjectColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_color);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    public void mapView() {
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        imgbButtonColors.add((ImageButton) findViewById(R.id.imgb_color_image_1));
        imgbButtonColors.add((ImageButton) findViewById(R.id.imgb_color_image_2));
        imgbButtonColors.add((ImageButton) findViewById(R.id.imgb_color_image_3));
        setClickForChangeImageColor();
        btmnBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btmnBottomMenu.setOnNavigationItemSelectedListener(this);
    }
    public void setClickForChangeImageColor() {
        for (int i = 0; i < imgbButtonColors.size(); i++) {
            imgbButtonColors.get(i).setOnClickListener(this);
        }
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
            case R.id.linearlayout_main:
                gestureDetector.onTouchEvent(event);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int index;
        switch (id) {
            case R.id.imgb_color_image_1:
                index = imgbButtonColors.indexOf(findViewById(R.id.imgb_color_image_1));
                currentObjectColor = ContextCompat.getColor(this,R.color.colorAccent);
                openColorDialog(index);
                break;
            case R.id.imgb_color_image_2:
                index = imgbButtonColors.indexOf(findViewById(R.id.imgb_color_image_2));
                currentObjectColor = ContextCompat.getColor(this,R.color.colorAccent);
                openColorDialog(index);
                break;
            case R.id.imgb_color_image_3:
                index = imgbButtonColors.indexOf(findViewById(R.id.imgb_color_image_3));
                currentObjectColor = ContextCompat.getColor(this,R.color.colorAccent);
                openColorDialog(index);
                break;
        }
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
                intent = new Intent(context, InsertFrameActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void openColorDialog(final int index) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentObjectColor, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentObjectColor = color;
                imgbButtonColors.get(index).setBackgroundColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                //Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        context = contextTmp;
        super.onBackPressed();
    }
}
