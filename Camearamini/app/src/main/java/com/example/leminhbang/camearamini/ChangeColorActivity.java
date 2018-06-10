package com.example.leminhbang.camearamini;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.leminhbang.camearamini.detector.ObjectDetect;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.leminhbang.camearamini.CutImageActivity.drawPoint;
import static com.example.leminhbang.camearamini.CutImageActivity.getPointOfTouchedCordinate;
import static com.example.leminhbang.camearamini.MainActivity.bitmapMain;
import static com.example.leminhbang.camearamini.MainActivity.bitmapTemp;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;
import static com.example.leminhbang.camearamini.MainActivity.showDialogSave;
import static com.example.leminhbang.camearamini.MyCameraHelper.saveImageFile;

public class ChangeColorActivity extends AppCompatActivity implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener, TextWatcher, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {
    private ImageView imgMainImage;
    private BottomNavigationView btmnBottomMenu;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Context contextTmp;
    private SeekBar sekChangeRadius;
    private EditText edtNColors;
    private Spinner spinnerChangeColor;
    private Button btnSign;

    public int currentObjectColor;
    private boolean isFirst = true;
    private float[] pointColor;
    private byte[] bytes;
    private boolean isSegment = false;
    private boolean isChoice = false;

    private Mat pixelLabels;
    private Mat mRGB, mHSV, mMask, mBW;
    private List<Mat> kmeansResult;
    private Bitmap bmOut;
    private Scalar mColorRadius = new Scalar(25, 50, 50, 0);
    private int clickCount = 0;
    private Rect rect;
    private Rect touchedRect;
    private int progH = 50, progS = 50, progV = 50;
    private Core.MinMaxLocResult minMaxH, minMaxS, minMaxV;
    private Scalar upper, lower;

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
            if (bitmapTemp != null)
                imgMainImage.setImageBitmap(bitmapTemp);
            else {
                bitmapTemp = bitmapMain;
                imgMainImage.setImageBitmap(bitmapMain);
            }
            pixelLabels = new Mat();
            kmeansResult =  new ArrayList<>();
            int w = bitmapMain.getWidth(), h = bitmapMain.getHeight();
            bmOut = Bitmap.createBitmap(w, h,
                    bitmapMain.getConfig());
            mRGB = new Mat(h, w, CvType.CV_8SC4);
            mHSV = new Mat(h, w, CvType.CV_8SC4);
            mMask = new Mat(h, w, CvType.CV_8UC1);
            Utils.bitmapToMat(bitmapTemp, mRGB);
            Imgproc.cvtColor(mRGB, mRGB,
                    Imgproc.COLOR_RGBA2RGB);
            Imgproc.cvtColor(mRGB, mHSV,
                    Imgproc.COLOR_RGB2HSV_FULL);
            if (mBW == null) {
                List<Mat> HSV = new ArrayList<Mat>();
                Core.split(mHSV, HSV);
                minMaxH = Core.minMaxLoc(HSV.get(0));
                minMaxS = Core.minMaxLoc(HSV.get(1));
                minMaxV = Core.minMaxLoc(HSV.get(2));
                lower = new Scalar(minMaxH.minVal, minMaxS.minVal,
                        minMaxV.minVal, 0);
                upper = new Scalar(minMaxH.maxVal, minMaxS.maxVal,
                        minMaxV.maxVal, 255);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_action, menu);
        invalidateOptionsMenu();
        MenuItem item = menu.findItem(R.id.action_finish);
        item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save_2:
                //saveImage();
                /*if (clickCount == 1)
                    segmentColor(touchedRect);
                if (clickCount == 3)
                    segmentColor(rect);
                clickCount = 0;*/

                break;
            case R.id.action_cancel_2:
                cancelAction();
                break;
            case R.id.action_finish:
                if (mBW != null)
                    openColorDialog(mMask, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void mapView() {
        imgMainImage = (ImageView) findViewById(R.id.img_main_image);
        imgMainImage.setOnTouchListener(this);
        btmnBottomMenu = (BottomNavigationView) findViewById(R.id.btmnBottom_menu_view);
        btmnBottomMenu.setOnNavigationItemSelectedListener(this);

        edtNColors = (EditText) findViewById(R.id.edtNColors);
        edtNColors.addTextChangedListener(this);

        sekChangeRadius = (SeekBar) findViewById(R.id.sekChangeRadius);
        sekChangeRadius.setOnSeekBarChangeListener(this);

        String[] spinnerItems = {"V", "H", "S"};
        spinnerChangeColor = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                spinnerItems);
        //spinnerChangeColor.setLayoutMode(Spinner.MODE_DIALOG);
        /*adapter.setDropDownViewResource(android.R.layout.
                simple_list_item_single_choice);*/
        adapter.setDropDownViewResource(android.R.layout.
                simple_list_item_single_choice);
        spinnerChangeColor.setAdapter(adapter);
        spinnerChangeColor.setOnItemSelectedListener(this);
        spinnerChangeColor.setSelection(0, true);
        btnSign = (Button) findViewById(R.id.btnSign);
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sign = (String) btnSign.getText();
                if (sign.equals("-")) btnSign.setText("+");
                if (sign.equals("+")) btnSign.setText("-");
                lower = new Scalar(minMaxH.minVal, minMaxS.minVal,
                        minMaxV.minVal, 0);
                upper = new Scalar(minMaxH.maxVal, minMaxS.maxVal,
                        minMaxV.maxVal, 255);
            }
        });
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
                    mMask = getSelectedObject(mMask,(int) pointColor[1],
                            (int) pointColor[0]);
                    mBW.release();
                    Core.bitwise_and(mRGB, mRGB, mBW, mMask);
                    bitmapTemp = convertMatToBitmap(mBW);
                    imgMainImage.setImageBitmap(bitmapTemp);

                    /*int pixel = (int) (pointColor[0] +
                            pointColor[1]*bitmapMain.getWidth());*/

                    //processOnTouch(event);
                    /*if (isSegment) {
                        *//*double label = pixelLabels.
                                get(pixel,0)[0];
                        drawSelectContour(mOut, label);
                        Toast.makeText(context, "Label  " + pointColor[0] +
                                        " " + pointColor[1] + " " + label,
                                Toast.LENGTH_LONG).show();
                        isSegment = false;*//*
                    }*/

                }
                gestureDetector.onTouchEvent(event);
                break;
            case R.id.linearlayout_main:
                gestureDetector.onTouchEvent(event);
                break;
        }
        return true;
    }

    private void processOnTouch(MotionEvent event) {
        int cols = mHSV.cols();
        int rows = mHSV.rows();

        pointColor = getPointOfTouchedCordinate(imgMainImage,event);
        int x = (int)pointColor[0];
        int y = (int)pointColor[1];
        if ((x < 0) || (y < 0) || (x > cols) || (y > rows))
            return;

        switch (clickCount) {
            case 0:
                rect = new Rect();
                rect.x = x;
                rect.y = y;
                bitmapTemp = drawPoint(bitmapTemp,x,y,x,y);
                break;
            case 1:
                rect.width = Math.abs(x - rect.x);
                bitmapTemp = drawPoint(bitmapTemp,rect.x, rect.y,
                        rect.x + rect.width, rect.y);
                break;
            case 2:
                rect.height = Math.abs(y - rect.y);
                bitmapTemp = drawPoint(bitmapTemp,rect.x + rect.width,
                        rect.y, rect.x + rect.width,
                        rect.y + rect.height);
                bitmapTemp = drawPoint(bitmapTemp,rect.x, rect.y,
                        rect.x + rect.width,
                        rect.y + rect.height);
                break;
        }
        imgMainImage.setImageBitmap(bitmapTemp);
        clickCount ++;

        touchedRect = new Rect();
        touchedRect.x = (x > 4) ? x - 4 : 0;
        touchedRect.y = (y > 4) ? y - 4 : 0;

        touchedRect.width = (x + 4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y + 4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;


    }
    public void segmentColor(Rect _rect) {
        ObjectDetect mDetector = new ObjectDetect();
        Mat touchedRegionRgba = mHSV.submat(_rect);

        Mat touchedRegionHsv = mHSV.submat(_rect);
        //Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        Scalar mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = _rect.width * _rect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        //mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
        mDetector.setmColorRadius(mColorRadius);
        mDetector.setHsvColor(mBlobColorHsv);
        Mat mDilate = mDetector.process(mHSV);

        //Imgproc.pyrUp(mDilate,mDilate);
        Bitmap b = Bitmap.createBitmap(mDilate.cols(),
                mDilate.rows(),bitmapMain.getConfig());
        Utils.matToBitmap(mDilate, b);
        imgMainImage.setImageBitmap(b);

        //show color dialog
        openColorDialog(mDilate, 0);

        //Imgproc.resize(mDetector.getSpectrum(), mSpectrum,
        // SPECTRUM_SIZE, 0, 0,Imgproc.INTER_LINEAR_EXACT);

        touchedRegionRgba.release();
        touchedRegionHsv.release();
    }
    public void changeColor(Mat mDilate) {
        if (mDilate != null) {
            int cols = mHSV.cols();
            int rows = mHSV.rows();
            List<Mat> listHSV = new ArrayList<>();
            List<Mat> listHS = new ArrayList<>();
            Core.split(mHSV, listHSV);
            listHS.add(listHSV.get(0));
            listHS.add(listHSV.get(1));
            Mat des = new Mat(rows, cols, mHSV.type());
            Mat tmp = new Mat(rows, cols, CvType.CV_8UC2);
            Core.merge(listHS, tmp);
            Scalar newColor = new Scalar(Color.red(currentObjectColor),
                    Color.green(currentObjectColor),
                    Color.blue(currentObjectColor));
            Mat mNewColor = new Mat(1, 1, CvType.CV_8UC3, newColor);
            Imgproc.cvtColor(mNewColor, mNewColor,
                    Imgproc.COLOR_RGB2HSV_FULL);
            Scalar newHsv = new Scalar(mNewColor.get(0, 0)[0],
                    mNewColor.get(0, 0)[1], mNewColor.get(0, 0)[2]);
            tmp.setTo(newHsv, mDilate);
            Core.split(tmp, listHS);
            List<Mat> newListHSV = new ArrayList<Mat>();
            newListHSV.add(listHS.get(0));
            newListHSV.add(listHS.get(1));
            //newListHSV.add(listHSV.get(1));
            newListHSV.add(listHSV.get(2));
            Core.merge(newListHSV, des);

            Imgproc.cvtColor(des, des,
                    Imgproc.COLOR_HSV2RGB_FULL);
            Bitmap b = Bitmap.createBitmap(mDilate.cols(),
                    mDilate.rows(),bitmapMain.getConfig());
            Utils.matToBitmap(des, b);
            imgMainImage.setImageBitmap(b);
        }
        isChoice = false;
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

    public void openColorDialog(final Mat mDilate, final int index) {

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentObjectColor, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentObjectColor = color;
                isChoice = true;
                changeColor(mDilate);
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

    private Mat selectCluster(Mat mCluster, double label) {
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
        /*if (k == 0 || k == 1) return;
        kmeansResult = kMeansCluster(mHSV, k);
        pixelLabels = kmeansResult.get(0);
        k = kmeansResult.get(1).rows();
        mOut = pixelLabels.reshape(1, mHSV.rows());
        mOut.convertTo(mOut, CvType.CV_32FC3, 255.0/(k - 1));
        *//*NativeClass.segmentByColorKMeans(mOut.getNativeObjAddr(),
                k - 1);*//*
        mOut.convertTo(mOut, CvType.CV_8UC3);
        Imgproc.cvtColor(mOut,mOut,Imgproc.COLOR_GRAY2BGR);
        Utils.matToBitmap(mOut, bmOut);
        imgMainImage.setImageBitmap(bmOut);
        isSegment = true;*/
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
        clickCount = 0;
        progH = 50; progS = 50; progV = 50;
        sekChangeRadius.setProgress(progH);
        sekChangeRadius.setProgress(progS);
        sekChangeRadius.setProgress(progV);
        lower = new Scalar(minMaxH.minVal, minMaxS.minVal,
                minMaxV.minVal, 0);
        upper = new Scalar(minMaxH.maxVal, minMaxS.maxVal,
                minMaxV.maxVal, 255);
        spinnerChangeColor.setSelection(0);
        if (mBW != null) {
            mBW.release();
            mMask.release();
        }
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
        /*String str;
        int k = 0;
        if (edtNColors.getText().hashCode() == s.hashCode()) {
            str = edtNColors.getText().toString().trim();
            if (!str.equals("") && !str.isEmpty() && !str.equals("0")) {
                k = Integer.parseInt(str);
                segmentByKMeans(k);
            }
        }*/
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        double[] d = {25, 50, 25 + progress, 0};
        mColorRadius.set(d);
        double percent = progress*1.0/100.0;
        String sSign = (String) btnSign.getText();
        int sign = sSign.equals("+") ? 1 : 0;
        List<Mat> results = detectObject(mHSV, percent, sign);
        mBW = results.get(0);
        mMask = results.get(1);
        bitmapTemp = convertMatToBitmap(mBW);
        imgMainImage.setImageBitmap(bitmapTemp);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (spinnerChangeColor.getSelectedItemPosition()) {
            case 0:
                progV = sekChangeRadius.getProgress();
                break;
            case 1:
                progH = sekChangeRadius.getProgress();
                break;
            case 2:
                progS = sekChangeRadius.getProgress();
                break;
        }
    }

    private List<Mat> detectObject(Mat src, double percent, int sign) {
        List<Mat> results = new ArrayList<>();
        Mat mask = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
        Mat dst = new Mat(src.rows(), src.cols(), CvType.CV_8UC3);
        double selectValue;
        switch (spinnerChangeColor.getSelectedItemPosition()) {
            case 0:
                selectValue = percent*minMaxV.maxVal +
                        (1 - percent)*minMaxV.minVal;
                if (sign == 0) {
                    upper.val[2] = selectValue;
                } else {
                    lower.val[2] = selectValue;
                }
                break;
            case 1:
                selectValue = percent*minMaxH.maxVal +
                        (1 - percent)*minMaxH.minVal;
                if (sign == 0) {
                    upper.val[0] = selectValue;
                } else {
                    lower.val[0] = selectValue;
                }
                break;
            case 2:
                selectValue = percent*minMaxS.maxVal +
                        (1 - percent)*minMaxS.minVal;
                if (sign == 0) {
                    upper.val[1] = selectValue;
                } else {
                    lower.val[1] = selectValue;
                }
                break;
        }
        Core.inRange(src, lower, upper, mask);//dst is mask mat
        Imgproc.dilate(mask, mask, new Mat());
        Core.bitwise_and(mRGB, mRGB, dst, mask);
        results.add(dst);
        results.add(mask);
        return results;
    }
    private Mat getSelectedObject(Mat mBinary, int x, int y) {
        Mat mSelected = new Mat();
        Mat mLabels = new Mat();
        Imgproc.connectedComponents(mBinary, mLabels);
        int label = (int) (mLabels.get(x, y)[0]);
        Core.compare(mLabels, new Scalar(label), mSelected,
                Core.CMP_EQ);
        return mSelected;
    }
    public Bitmap convertMatToBitmap(Mat mat) {
        Bitmap b = Bitmap.createBitmap(mat.cols(),
                mat.rows(),bitmapMain.getConfig());
        Utils.matToBitmap(mat, b);
        return b;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                sekChangeRadius.setProgress(progV);
                break;
            case 1:
                sekChangeRadius.setProgress(progH);
                break;
            case 2:
                sekChangeRadius.setProgress(progS);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBW != null)
            mBW.release();
        mHSV.release();
    }
}