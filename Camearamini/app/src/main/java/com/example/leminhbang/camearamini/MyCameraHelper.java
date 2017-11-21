package com.example.leminhbang.camearamini;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.example.leminhbang.camearamini.MainActivity.context;

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

    public static void saveImageFile(Uri uri, Bitmap bitmap) {

       /* if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23
            );

        }*/

        String photopath = uri.getPath();
        Ringtone r = RingtoneManager.getRingtone(context, uri);
        String fileName = r.getTitle(context);
        if (!fileName.contains(".jpg")) {
            fileName = fileName + ".jpg";
        }
        File file = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Cameramini");

        String path = file.getPath() + file.separator + fileName;
        FileOutputStream fOut;
        try {
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
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Cameramini");
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
}
