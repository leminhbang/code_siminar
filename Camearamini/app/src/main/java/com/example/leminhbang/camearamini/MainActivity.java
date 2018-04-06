package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
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

    public static Bitmap bitmapMain;
    public static Bitmap bitmapTemp;
    public static Context context;
    public static ActionBar actionBar;

    private boolean isFirst = true;
    private boolean isChoose = false;

    public Mat matMain;

    static {
        System.loadLibrary("Mylib");
    }

    BaseLoaderCallback callback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    matMain = new Mat();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

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

        gestureDetector = new GestureDetector(this, new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (filePath == null || filePath.isEmpty() || filePath.equals("")) {
            Toast.makeText(context, "\t\t\t\t\t\t\tChưa có ảnh\nVui lòng chụp " +
                    "ảnh hoặc chọn một ảnh", Toast.LENGTH_LONG).show();
        } else {
            bitmapTemp = bitmapMain;
            imgMainImage.setImageBitmap(bitmapMain);
            if (isChoose)
                filePath = getPicturePath(fileUri);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            callback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            Log.d("Main", "Load opencv success");
        } else {
            Log.d("Main", "Load opencv failed");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,
                    this, callback);
        }
    }

    @Override
    public void onBackPressed() {
        if (bitmapMain != null) {
            //showDialogSave();
        }
    }

    @Override
    protected void onDestroy() {
        matMain.release();
        bitmapTemp.recycle();
        super.onDestroy();
    }

    public static void showDialogSave(final Bitmap bmSave,
                                      final int intendSelect) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.save_confirm);
        dialog.setMessage(R.string.save_message);
        dialog.setPositiveButton(R.string.No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                selectIntentToStart(intendSelect);
            }
        });
        dialog.setNegativeButton(R.string.Yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (bitmapMain != null) {
                    bitmapMain = bmSave;
                    saveImageFile(fileUri, bitmapMain);
                }
                selectIntentToStart(intendSelect);
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
    public static void selectIntentToStart(int intentSelected) {
        Intent intent = null;
        switch (intentSelected) {
            case InterfaceClass.MainClass:
                intent = new Intent(context,MainActivity.class);
                break;
            case InterfaceClass.ChangeColorClass:
                intent = new Intent(context,ChangeColorActivity.class);
                break;
            case InterfaceClass.CutImageClass:
                intent = new Intent(context,CutImageActivity.class);
                break;
            case InterfaceClass.ChangeShadeClass:
                intent = new Intent(context,ChangeShadeActivity.class);
                break;
            case InterfaceClass.InsertFrameClass:
                intent = new Intent(context,InsertFrameActivity.class);
                break;
            case InterfaceClass.InsertTextClass:
                intent = new Intent(context,InsertTextActivity.class);
                break;
        }
        context.startActivity(intent);
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
        if (bitmapMain == null) {
            invalidateOptionsMenu();
            MenuItem saveMenu = menu.findItem(R.id.action_save);
            saveMenu.setVisible(false);
            MenuItem deleteMenu = menu.findItem(R.id.action_delete);
            deleteMenu.setVisible(false);
            MenuItem cancelMenu = menu.findItem(R.id.action_cancel);
            cancelMenu.setVisible(false);
            MenuItem rotateleftMenu = menu.findItem(R.id.action_turn_left);
            rotateleftMenu.setVisible(false);
            MenuItem rotateRightMenu = menu.findItem(R.id.action_turn_right);
            rotateRightMenu.setVisible(false);
            MenuItem rotateMenu = menu.findItem(R.id.action_customize_rotate);
            rotateMenu.setVisible(false);
        }
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
                bitmapTemp = rotateImage(bitmapTemp, -90);
                imgMainImage.setImageBitmap(bitmapTemp);
                break;
            case R.id.action_turn_right:
                bitmapTemp = rotateImage(bitmapTemp, 90);
                imgMainImage.setImageBitmap(bitmapTemp);
                break;
            case R.id.action_customize_rotate:
                rotateImageCustomize();
                break;
            case R.id.action_load_image:
                isChoose = true;
                showFileChooser();
                break;
            case R.id.action_save:
                if (bitmapMain != null) {
                    bitmapMain = bitmapTemp;
                    saveImageFile(fileUri, bitmapMain);
                }
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
                viewImage(uri);
            }
        }
    }

    //bat su kien khi chon vao cac item tren bottom navigation bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (bitmapMain == null) {
            return false;
        }
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

    //bat su kien vuot vao man hinh hoac vuot vao anh main picture
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

    // hien anh vua chip len man hinh
    public void viewImage() {
        filePath = fileUri.getPath();
        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;
        bitmapMain = BitmapFactory.decodeFile(filePath, options);

        //convert bitmap to mat (opencv)
        Utils.bitmapToMat(bitmapMain, matMain);
        //display image in the iimage view
        imgMainImage.setImageBitmap(bitmapMain);
    }

    private void viewImage(Uri uri) {
        fileUri = uri;
        filePath = uri.getPath();

        String[] filePaths = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,
                filePaths, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.
                getColumnIndex(filePaths[0]));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmapMain = BitmapFactory.decodeFile(imagePath, options);
        cursor.close();
        //convert bitmap to mat (opencv)
        Utils.bitmapToMat(bitmapMain, matMain);
        //display image in the image view
        imgMainImage.setImageBitmap(bitmapMain);
    }

    //chuan bi cameara
    public void prepareCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = MyCameraHelper.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    //hien hop thoai de chon file khi nhan load anh
    private void showFileChooser() {
        isChoose = true;
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.
                Media.EXTERNAL_CONTENT_URI);
        //intent.setType("*/*");
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
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
        sekbCustomizeRotate.setProgress((int) imgMainImage.getRotation() + 180);
    }

    //huy cac hanh dong da chon dua anh ve anh goc ban dau
    public void cancelAction() {
        bitmapTemp = bitmapMain;
        imgMainImage.setScaleX(1);
        imgMainImage.setScaleY(1);
        imgMainImage.setImageBitmap(bitmapMain);
        imgMainImage.setRotation(View.SCROLL_AXIS_NONE);
        sekbCustomizeRotate.setProgress(180);
        MyCameraHelper.setLastDegree();
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
                filePath = null;
                fileUri = null;
                bitmapMain = null;
                imgMainImage.setImageBitmap(null);
            } else {
                Toast.makeText(MainActivity.this, "Xóa lỗi", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //bitmapMain = rotateImage(bitmapMain, progress - 180 - lastDegree);
        bitmapTemp = rotateImage(bitmapMain, progress - 180);
        imgMainImage.setImageBitmap(bitmapTemp);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public String getPicturePath(Uri uriImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uriImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }
}
