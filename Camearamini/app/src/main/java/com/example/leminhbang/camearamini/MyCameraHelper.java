package com.example.leminhbang.camearamini;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.example.leminhbang.camearamini.ChangeShadeActivity.convertToBlue;
import static com.example.leminhbang.camearamini.ChangeShadeActivity.convertToBlur;
import static com.example.leminhbang.camearamini.ChangeShadeActivity.convertToClassic;
import static com.example.leminhbang.camearamini.ChangeShadeActivity.convertToEmboss;
import static com.example.leminhbang.camearamini.ChangeShadeActivity.convertToGray;
import static com.example.leminhbang.camearamini.ChangeShadeActivity.convertToNegative;
import static com.example.leminhbang.camearamini.MainActivity.context;
import static com.example.leminhbang.camearamini.MainActivity.filePath;
import static com.example.leminhbang.camearamini.MainActivity.fileUri;

/**
 * Created by LE MINH BANG on 30/10/2017.
 */

public class MyCameraHelper {
    protected static int lastDegree = 0;

    public static Bitmap rotateImage(Bitmap image, int degree) {
        //imgImage.setRotation(imgImage.getRotation() + degree);
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bitmap = Bitmap.createBitmap(image,0,0,image.getWidth(),
                image.getHeight(), matrix,false);
        lastDegree += degree;
        return bitmap;
    }

    public static void setLastDegree() {
        lastDegree = 0;
    }

    public static void saveImageFile(Uri uri, Bitmap bitmap) {
       /* if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23
            );
        }*/

        File file = new File(Environment
                .getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                "Cameramini");

        //String path = file.getPath() + file.separator + fileName;
        if (filePath.equals(null) || filePath.equals(""))
            return;
        String path = filePath;
        File f = new File(path);
        FileOutputStream fOut;
        try {
            if (file.exists()) {
                f.deleteOnExit();
            }
            fOut = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type, false));
    }

    private static File getOutputMediaFile(int type, boolean isThumbnail) {
        // External sdcard location
        File mediaStorageDir;
        if (isThumbnail) {
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES),
                    "Cameramini/thumbnail");
        } else {
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES),
                    "Cameramini");
        }
        /*File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                "Cameramini");*/

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
               /* Log.d(TAG, "Oops! Failed create "
                        + "Android File Upload" + " directory");*/
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }
    public static String getFileName(Uri uri) {
         String photopath = uri.getPath();
        Ringtone r = RingtoneManager.getRingtone(context, uri);
        String fileName = r.getTitle(context);
        if (!fileName.contains(".jpg")) {
            fileName = fileName + ".jpg";
        }
        return fileName;
    }
    public static void saveThumbnail(String path, Bitmap mbitmap) {
        if (mbitmap == null) return;

        File f = new File(path);
        FileOutputStream fOut;
        try {
            if (f.exists()) {
                f.deleteOnExit();
            }
            fOut = new FileOutputStream(path);
            mbitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static List<String> prepareThumbnails(final Bitmap mBitmap) {
        final List<String> listPath = new ArrayList<String>();
        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //Uri thumbnailUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE, true));
                String filename = getFileName(fileUri);
                File file = new File(Environment
                        .getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES),
                        "Cameramini/thumbnail");

                String thumbnailPath = file.getPath() + file.separator + filename;
                //final String thumbnailPath = thumbnailUri.getPath();

                Bitmap thumbImg = Bitmap.createScaledBitmap(mBitmap,
                        128, 128, false); //normal thumbnail
                listPath.add(thumbnailPath);
                saveThumbnail(thumbnailPath, thumbImg);
                String[] filterName = filename.split("\\.");

                //save filter thumbnail
                Bitmap thumbFilter = Bitmap.createBitmap(128, 128,
                        thumbImg.getConfig());
                Mat tmp = new Mat(mBitmap.getHeight(),
                        mBitmap.getWidth(), CvType.CV_8UC3);
                Utils.bitmapToMat(mBitmap, tmp);
                Bitmap mTmp = Bitmap.createBitmap(mBitmap.getWidth(),
                        mBitmap.getHeight(),
                        mBitmap.getConfig());
                Mat mSrc = new Mat(128, 128, CvType.CV_8UC4);
                Utils.bitmapToMat(thumbImg, mSrc);
                Mat mFilter = new Mat(128, 128, CvType.CV_8UC1);
                //Mat mFade = new Mat(128, 128, CvType.CV_8UC4);
                //Mat mSepria = new Mat(128, 128, CvType.CV_8UC4);
                int count = FilterInterfaeClass.filterCount;
                for (int i = 1; i < count; i ++) {
                    if (i == FilterInterfaeClass.GRAY) {
                        mFilter = convertToGray(mSrc);
                        Utils.matToBitmap(mFilter, thumbFilter);
                    } else if (i == FilterInterfaeClass.NEGATIVE) {
                        mFilter = convertToNegative(mSrc);
                        Utils.matToBitmap(mFilter, thumbFilter);
                    } else if (i == FilterInterfaeClass.EMBOSS) {
                        mFilter = convertToEmboss(mSrc);
                        Utils.matToBitmap(mFilter, thumbFilter);
                    } else if (i == FilterInterfaeClass.FADE) {
                        Mat mFade = convertToBlur(mSrc);
                        Utils.matToBitmap(mFade, thumbFilter);
                    } else if (i == FilterInterfaeClass.CLASSIC) {
                        Mat mSepria = convertToClassic(tmp);
                        Utils.matToBitmap(mSepria, mTmp);
                        thumbFilter = Bitmap.createScaledBitmap(mTmp,
                                128, 128, false);
                    } else if (i == FilterInterfaeClass.BLUE) {
                        Mat mBlue = convertToBlue(tmp);
                        Utils.matToBitmap(mBlue, mTmp);
                        thumbFilter = Bitmap.createScaledBitmap(mTmp,
                                128, 128, false);
                    }
                    filename = filterName[0] + "_" + i
                            + "." + filterName[1];
                    thumbnailPath = file.getPath() + file.separator
                            + filename;
                    saveThumbnail(thumbnailPath, thumbFilter);
                    listPath.add(thumbnailPath);
                }
                Log.d("error", "run: run on thread");
                mSrc.release();
                mFilter.release();
                thumbImg.recycle();
                thumbFilter.recycle();
            }
        };
        Thread thread = new Thread(r);
        thread.start();
        return listPath;
    }

}
