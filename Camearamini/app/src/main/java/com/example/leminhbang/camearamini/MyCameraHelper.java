package com.example.leminhbang.camearamini;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * Created by LE MINH BANG on 30/10/2017.
 */

public class MyCameraHelper {
    private Bitmap mBitmap;

    public static int[][][] convertBitmapToMatrix(Bitmap bitmap) {
        int[][][] pixelMat = new int[bitmap.getHeight()][bitmap.getWidth()][4];
        int pixel;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                pixel = bitmap.getPixel(i, j);
                pixelMat[j][i][0] = Color.alpha(pixel);
                pixelMat[j][i][1] = Color.red(pixel);
                pixelMat[j][i][2] = Color.green(pixel);
                pixelMat[j][i][3] = Color.blue(pixel);
            }
        }
        return pixelMat;
    }
    public static Bitmap convertMatrixToBitmap(int[][][] mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat[0].length,mat.length,
                Bitmap.Config.ARGB_8888);
        for (int i = 0; i < mat[0].length; i++) {
            for (int j = 0; j < mat.length; j++) {
                bitmap.setPixel(i, j, Color.argb(mat[j][i][0], mat[j][i][1],
                        mat[j][i][2], mat[j][i][3]));
            }
        }
        return bitmap;
    }
    public static void rotateImage(ImageView imgImage, int degree) {
        imgImage.setRotation(imgImage.getRotation() + degree);
    }

    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        /*File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Cameramini");*/
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                "Cameramini");

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
}
