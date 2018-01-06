package com.example.leminhbang.camearamini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MyCameraHelper.convertToRGB;

public class ChangeColorActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ImageView imgMainImage;
    private ArrayList<ImageButton> imgbButtonColors = new ArrayList<ImageButton>();
    private BottomNavigationView btmnBottomMenu;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private Bitmap bitmapTemp;
    //private static String serverIpAddress = "172.29.132.43";
    private String serverIpAddress = "192.168.1.11";
    private InetAddress serverAddr;
    private Socket socket;
    private DataOutputStream dataOut;

    public int currentObjectColor;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_color);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextTmp = context;
        context = this;
        mapView();

        gestureDetector = new GestureDetector(this, new MyGesture());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGesture());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (filePath != null) {
            bitmapTemp = bitmapMain;
            imgMainImage.setImageBitmap(bitmapMain);
            convertToRGB(bitmapMain);
        }
        Thread cThread = new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        });
        cThread.start();
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
                saveImage();
                ;
                break;
            case R.id.action_cancel_2:
                cancelAction();
                break;
        }
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
    public void onClick(View v) {
        int id = v.getId();
        int index;
        switch (id) {
            case R.id.imgb_color_image_1:
                index = imgbButtonColors.indexOf(findViewById(R.id.imgb_color_image_1));
                currentObjectColor = ContextCompat.getColor(this, R.color.colorAccent);
                openColorDialog(index);
                break;
            case R.id.imgb_color_image_2:
                index = imgbButtonColors.indexOf(findViewById(R.id.imgb_color_image_2));
                currentObjectColor = ContextCompat.getColor(this, R.color.colorAccent);
                openColorDialog(index);
                break;
            case R.id.imgb_color_image_3:
                index = imgbButtonColors.indexOf(findViewById(R.id.imgb_color_image_3));
                currentObjectColor = ContextCompat.getColor(this, R.color.colorAccent);
                openColorDialog(index);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_insert_text:
                Intent intent = new Intent(context, InsertTextActivity.class);
                startActivity(intent);
                break;
            case R.id.action_insert_frame:
                intent = new Intent(context, InsertFrameActivity.class);
                startActivity(intent);
                break;
            case R.id.action_cut_image:
                intent = new Intent(context, CutImageActivity.class);
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
    public void connect() {
        try {
            serverAddr = InetAddress.getByName(serverIpAddress);
            socket = new Socket(serverIpAddress, 3000);
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new
                    OutputStreamWriter(socket.getOutputStream()));

            //client send bitmap data to server
            dataOut =new DataOutputStream(socket.getOutputStream());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapMain.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte[] byteArray = stream.toByteArray();
            dataOut.write(byteArray,0,byteArray.length);
            //dataOut.flush();
            //dataOut.close();
            int r1 = Color.red(bitmapMain.getPixel(0,0));
            int g1 = Color.green(bitmapMain.getPixel(0,0));
            int b1 = Color.blue(bitmapMain.getPixel(0,0));
            int r2 = Color.red(bitmapMain.getPixel(370,1110));
            int g2 = Color.green(bitmapMain.getPixel(370,1110));
            int b2 = Color.blue(bitmapMain.getPixel(370,1110));

            out.close();
            in.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public static void connectServer() {
//        while (true) {
//            try {
//                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
//
//                Socket socket = new Socket(serverIpAddress, 3000);
//                BufferedReader in = new BufferedReader(new
//                        InputStreamReader(socket.getInputStream()));
//                BufferedWriter out = new BufferedWriter(new
//                        OutputStreamWriter(socket.getOutputStream()));
//                out.write("exit", 0, 4);
//                out.flush();
//                String inMsg = "";
//                boolean b = false;
//                while (!b) {
//                    inMsg = in.readLine();
//                    if (inMsg.equals("Hello")) {
//                        b = true;
//                    }
//                    out.write("exit");
//                    out.flush();
//                }
//                socket.close();
//
//            } catch (Exception e) {
//                Log.e("ClientActivity", "S: Error", e);
//            }
//        }
//    }


    private void cancelAction() {

    }

    private void saveImage() {
        /*int[][][] rgb = new int[1][1][2];
        rgb[0][0][0] = 2;
        rgb[0][0][1] = 3;
        int width = rgb[0].length;
        int height = rgb.length;
        String w = String.valueOf(width);
        out.write(w);
        out.flush();
        String h = String.valueOf(height);
        out.write(h);
        out.flush();
        for (int i = 0; i < rgb[0].length; i++) {
            for (int[][] j : rgb) {
                String value =  String.valueOf(j[i][1]);
                out.write(value);
                out.flush();
            }
        }*/
    }

}