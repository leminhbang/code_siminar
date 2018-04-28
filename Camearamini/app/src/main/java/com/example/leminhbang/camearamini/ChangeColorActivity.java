package com.example.leminhbang.camearamini;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.leminhbang.camearamini.CutImageActivity.getPointOfTouchedCordinate;
import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.bitmapTemp;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;
import static com.example.leminhbang.camearamini.MainActivity.showDialogSave;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;

public class ChangeColorActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, TextWatcher {
    private ImageView imgMainImage;
    private ArrayList<ImageButton> imgbButtonColors = new ArrayList<ImageButton>();
    private BottomNavigationView btmnBottomMenu;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;

    public int currentObjectColor;
    private boolean isFirst = true;
    private float[] pointColor;
    private byte[] bytes;
    private boolean isOK = false;

    private EditText edtNColors, edtClusters;
    private Mat pixelLabels;
    private Mat mRGB, mOut;
    private List<Mat> kmeansResult;
    private Bitmap bmOut;

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
            pixelLabels = new Mat();
            kmeansResult =  new ArrayList<Mat>();
            int w = bitmapMain.getWidth(), h = bitmapMain.getHeight();
            bmOut = Bitmap.createBitmap(w, h,
                    bitmapMain.getConfig());
            mRGB = new Mat(h, w, CvType.CV_8SC4);
            mOut = new Mat(h, w, CvType.CV_8UC3);
            Utils.bitmapToMat(bitmapTemp, mRGB);
            Imgproc.cvtColor(mRGB, mRGB,
                    Imgproc.COLOR_RGBA2RGB);
            //segmentByKMeans(3);
        }
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

        edtClusters = (EditText) findViewById(R.id.edtCluster);
        edtClusters.addTextChangedListener(this);
        edtNColors = (EditText) findViewById(R.id.edtNColors);
        edtNColors.addTextChangedListener(this);
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
                /*scaleGestureDetector.onTouchEvent(event);
                if (!isFirst) {
                    float scale = MyScaleGesture.getScaleValue();
                    imgMainImage.setScaleX(scale);
                    imgMainImage.setScaleY(scale);
                } else {
                    isFirst = false;
                    MyScaleGesture.setScaleValue();
                }*/
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    pointColor = getPointOfTouchedCordinate(imgMainImage,event);
                    //openColorDialog(0);

                    int pixel = (int) (pointColor[0] +
                            pointColor[1]*bitmapMain.getWidth());
                    double label = pixelLabels.
                            get(pixel,0)[0];
                    drawSelectContour(mOut, label);
                    Toast.makeText(context, "Label  " + pointColor[0] +
                            " " + pointColor[1] + " " + label,
                            Toast.LENGTH_LONG).show();

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

    public Mat selectCluster(Mat mCluster, double label) {
        Mat mSelect = Mat.zeros(mCluster.size(), mCluster.type());
        int rows = mCluster.rows(), cols = mCluster.cols();
        for (int y = 0; y < rows; y ++) {
            for (int x = 0; x < cols; x ++) {
                if (pixelLabels.get(y*cols + x, 0)[0] ==
                        label) {
                    mSelect.put(y, x, 255.0, 255.0, 255.0);
                }
            }
        }
        return mSelect;
    }
    private void drawSelectContour(Mat mCluster, double label) {
        Mat mSelect = selectCluster(mCluster, label);
        Mat canny = new Mat(mCluster.rows(), mCluster.cols(),
                CvType.CV_8SC1);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.Canny(mSelect, canny, 50, 150);
        Utils.matToBitmap(mSelect,bmOut);
        Utils.matToBitmap(canny,bmOut);
        Imgproc.findContours(canny,contours, hierarchy,
                Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contours.size(); i++) {

            Imgproc.drawContours(mCluster,contours,i,
                    new Scalar(255,0,0),2,8,hierarchy,0,new Point());
        }
        Utils.matToBitmap(mCluster, bmOut);
        imgMainImage.setImageBitmap(bmOut);
    }
    public void segmentByKMeans(int k) {
        if (k == 0 || k == 1) return;
        Mat labels =new Mat();
        kmeansResult = cluster(mRGB, k);
        labels = kmeansResult.get(0);
        k = kmeansResult.get(1).rows();
        mOut = labels.reshape(1, mRGB.rows());
        mOut.convertTo(mOut, CvType.CV_32FC3, 255.0/(k - 1));
        /*NativeClass.segmentByColorKMeans(mOut.getNativeObjAddr(),
                k - 1);*/
        mOut.convertTo(mOut, CvType.CV_8UC3);
        Imgproc.cvtColor(mOut,mOut,Imgproc.COLOR_GRAY2BGR);
        Utils.matToBitmap(mOut, bmOut);
        imgMainImage.setImageBitmap(bmOut);
    }
    public List<Mat> cluster(Mat cutout, int k) {
        Mat samples32f = new Mat();
        List<Mat> lab = new ArrayList<Mat>();
        List<Mat> ab = new ArrayList<Mat>();
        Mat mLab = new Mat(cutout.rows(),cutout.cols(),cutout.type());
        Imgproc.cvtColor(cutout,mLab,Imgproc.COLOR_RGB2Lab);
        Core.split(mLab, lab);
        ab.add(lab.get(1));
        ab.add(lab.get(2));
        Core.merge(ab, samples32f);
        Mat samples = samples32f.reshape(1, cutout.cols() * cutout.rows());
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1,
                Core.KMEANS_PP_CENTERS, centers);
        labels.copyTo(pixelLabels);
        //return showClusters(cutout, labels, centers);
        List<Mat> list = new ArrayList<Mat>();
        list.add(labels);
        list.add(centers);
        return list;
    }

    private List<Mat> showClusters (Mat cutout, Mat labels, Mat centers) {
        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(3);
        List<Mat> clusters = new ArrayList<Mat>();
        for(int i = 0; i < centers.rows(); i++) {
            clusters.add(Mat.zeros(cutout.size(), cutout.type()));
        }
        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for(int i = 0; i < centers.rows(); i++)
            counts.put(i, 0);

        int rows = 0;
        for(int y = 0; y < cutout.rows(); y++) {
            for(int x = 0; x < cutout.cols(); x++) {
                int label = (int)labels.get(rows, 0)[0];
                int r = (int)centers.get(label, 2)[0];
                int g = (int)centers.get(label, 1)[0];
                int b = (int)centers.get(label, 0)[0];
                counts.put(label, counts.get(label) + 1);
                clusters.get(label).put(y, x, b, g, r);
                rows++;
            }
        }
        return clusters;
    }

    private Mat getCluster(Mat src, Mat labels, int k) {
        //labels.convertTo(labels, CvType.CV_8UC1, 255.0);
        labels.reshape(1, src.rows());
        Core.divide(labels, new Scalar(k), labels);
        //Core.multiply(labels, new Scalar(255), labels);
        Core.convertScaleAbs(labels, labels);//convert to unit8
        Bitmap b = Bitmap.createBitmap(bitmapMain.getWidth(),
                bitmapMain.getHeight(),bitmapMain.getConfig());
        Utils.matToBitmap(labels, b);
        imgMainImage.setImageBitmap(b);
        return labels;
    }

    public void connect() {
        /*try {
            InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
            Socket socket = new Socket(serverIpAddress, 3000);
            socket.setSendBufferSize(900000);
            socket.setReceiveBufferSize(600000);
            socket.setSoTimeout(1);
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new
                    OutputStreamWriter(socket.getOutputStream()));

            //client send bitmap data to server
            DataOutputStream dataOut =new DataOutputStream(socket.getOutputStream());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapMain.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte[] byteArray = stream.toByteArray();

            float[] position2 = new float[2];
            float[] position3 = new float[2];
            position2[0] = pointColor[0] + 40;
            position2[1] = pointColor[1];
            position3[0] = pointColor[0] + 20;
            position3[1] = pointColor[1] + 20;
            String s = pointColor[0] + " " + pointColor[1] + " " +
                    position2[0] + " " + position2[1] + " " +
                    position3[0] + " " + position3[1] + " " +
                    Color.red(currentObjectColor) + " " +
                    Color.green(currentObjectColor) + " " +
                    Color.blue(currentObjectColor) + " " +
                    byteArray.length;
            out.write(s);
            out.flush();
            dataOut.write(byteArray);

            //receive data
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            //int lengthOut = dataIn.read(results);
            byte[] byteTmp = new byte[100];
            String size = in.readLine();
            int size = dataIn.read(byteTmp);
            ByteBuffer wrapped = ByteBuffer.wrap(byteTmp,0,size);
            int length = Integer.parseInt(size);
            bytes = new byte[length];
            int messageSize = length,b = 0;
            while(b < messageSize)
            {
                b += dataIn.read(bytes,b,messageSize - b);
            }
            //bytes = Arrays.copyOf(results,lengthOut);
            bitmapTemp = BitmapFactory.decodeByteArray(bytes, 0,
                    bytes.length);

            //dataOut.flush();
            dataOut.close();
            out.close();
            in.close();
            dataIn.close();
            socket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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
        bitmapTemp = bitmapMain;
        imgMainImage.setImageBitmap(bitmapMain);
    }

    private void saveImage() {
        bitmapMain = bitmapTemp;
        saveImageFile(fileUri,bitmapMain);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str;
        int k = 0;
        if (edtNColors.getText().hashCode() == s.hashCode()) {
            str = edtNColors.getText().toString().trim();
            if (!str.equals("") && !str.isEmpty() && !str.equals("0")) {
                k = Integer.parseInt(str);
                segmentByKMeans(k);
            }
        }
    }
}