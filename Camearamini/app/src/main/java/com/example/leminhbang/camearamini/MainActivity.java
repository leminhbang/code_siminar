package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.example.leminhbang.camearamini.MyCameraHelper.lastDegree;
import static com.example.leminhbang.camearamini.MyCameraHelper.rotateImage;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    public static Uri fileUri;
    public static String filePath = null;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int FILE_SELECT_CODE = 10;
    private ImageView imgMainImage, imgTempImage;
    private BottomNavigationView btnvBottomMenu;
    private SeekBar sekbCustomizeRotate;
    GestureDetector gestureDetector;
    ScaleGestureDetector scaleGestureDetector;

    private int currentObjectColor;

    private Bitmap mainBitmap;
    public static Context context;
    public static ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapView();
        context = this;
        myGetActionBar();
        imgTempImage = imgMainImage;
        mainBitmap = convertToBitmap(imgMainImage);

        gestureDetector = new GestureDetector(this,new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (filePath == null || filePath.isEmpty() || filePath.equals("")) {
            Toast.makeText(context,"\t\t\t\t\t\t\tChưa có ảnh\nVui lòng chụp " +
                    "ảnh hoặc chọn một ảnh",Toast.LENGTH_LONG).show();
        } else {
            imgMainImage.setImageURI(fileUri);
        }
        imgMainImage.buildDrawingCache();
        mainBitmap = imgMainImage.getDrawingCache();
    }

    public void mapView() {
        findViewById(R.id.linearlayout_main).setOnTouchListener(this);
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        btnvBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btnvBottomMenu.setOnNavigationItemSelectedListener(this);
        sekbCustomizeRotate = (SeekBar) findViewById(R.id.seekbar_customize_rotate);
        sekbCustomizeRotate.setOnSeekBarChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // bat su kien khi chon vao cac menu tren action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_camera:
                prepareCamera();
                break;
            case R.id.action_turn_left:
                mainBitmap = rotateImage(imgMainImage, -90);
                imgMainImage.setImageBitmap(mainBitmap);
                break;
            case R.id.action_turn_right:
                mainBitmap = rotateImage(imgMainImage, 90);
                imgMainImage.setImageBitmap(mainBitmap);
                break;
            case R.id.action_customize_rotate:
                rotateImageCustomize();
                break;
            case R.id.action_load_image:
                showFileChooser();
                break;
            case R.id.action_save:
                saveImageFile(fileUri,mainBitmap);
                break;
            case R.id.action_cancel:
                cancelAction();
                break;
            case R.id.action_delete:
                deleteAction();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                viewImage();
            }
        }
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                fileUri = uri;
                filePath = uri.getPath();
                imgMainImage.setImageURI(uri);
                imgTempImage = imgMainImage;
            }
        }

    }

    //bat su kien khi chon vao cac item tren bottom navigation bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_insert_text:
                Intent intent = new Intent(context,InsertTextActivity.class);;
                startActivity(intent);
                break;
            case R.id.action_insert_frame:
                intent = new Intent(context, InsertFrameActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    //bat su kien vuot vao man hinh hoac vuot vao anh main picture
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (id) {
            case R.id.img_main_image:
                scaleGestureDetector.onTouchEvent(event);
                float scale = MyScaleGesture.getScaleValue();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
    public void myGetActionBar() {
        ActionBar acb = getSupportActionBar();
        actionBar = acb;
    }

    public Bitmap convertToBitmap(ImageView img) {
        img.buildDrawingCache();
        return img.getDrawingCache();
    }
    // hien anh vua chip len man hinh
    public void viewImage() {
        filePath = fileUri.getPath();
        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        imgMainImage.setImageBitmap(bitmap);
        imgTempImage = imgMainImage;



    }

    //chuan bi cameara
    public void prepareCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = MyCameraHelper.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    //hien hop thoai de chon file khi nhan load anh
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void rotateImageCustomize() {
        sekbCustomizeRotate.setVisibility(View.VISIBLE);
        sekbCustomizeRotate.setProgress((int)imgMainImage.getRotation() + 180);
    }

    //huy cac hanh dong da chon dua anh ve anh goc ban dau
    public void cancelAction() {
        imgMainImage = imgTempImage;
        imgMainImage.setRotation(View.SCROLL_AXIS_NONE);
        sekbCustomizeRotate.setProgress(180);
        sekbCustomizeRotate.setVisibility(View.INVISIBLE);
    }

    //xoa anh hien tai
    public void deleteAction() {
        if (filePath == null || filePath.equals("") || filePath.isEmpty()) {
            return;
        }
        File file = new File(filePath);
        if (file.exists()) {
            boolean delete = file.delete();
            if (delete == true) {
                Toast.makeText(MainActivity.this, "Xoa thành công", Toast.LENGTH_LONG).show();
                imgMainImage.setImageResource(android.R.color.transparent);
            } else {
                Toast.makeText(MainActivity.this, "Xóa lỗi", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mainBitmap = rotateImage(imgMainImage, progress - 180 - lastDegree);
        imgMainImage.setImageBitmap(mainBitmap);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    public ImageView getMainImage() {
        return imgMainImage;
    }
}
