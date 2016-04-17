package iso.io.iso.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;

import iso.io.iso.algorithms.AlgorithmConstants.ColorScale;

/**
 * Created by Jacob on 4/16/16.
 */
public final class ImageUtils {

    private ImageUtils() {
        // No instances.
    }

    public static Matrix getRotationMatrix(File imagePath) {
        int orientation = -1;
        Matrix matrix = new Matrix();

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath.getAbsolutePath());
        } catch (IOException e) {
            return new Matrix();
        }

        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                orientation = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                orientation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                orientation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                orientation = -90;
                break;
        }

        matrix.postRotate(orientation);

        return matrix;
    }

    public static Bitmap getScaledBitmapWithBounds(String filePath, int maxDimension) {


        BitmapFactory.Options inOptions = new BitmapFactory.Options();
        inOptions.inJustDecodeBounds = true; // Just check size first

        Bitmap raw = BitmapFactory.decodeFile(filePath, inOptions);

        int compressSize = 1;
        if (raw == null) { // raw always null, check to stop inspection
            compressSize = Math.max(inOptions.outWidth, inOptions.outHeight) / maxDimension;
        }

        inOptions.inJustDecodeBounds = false; // Actually decode image now
        inOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        inOptions.inDither = true;
        inOptions.inScaled = false;
        inOptions.inSampleSize = compressSize;

        return BitmapFactory.decodeFile(filePath, inOptions);
    }

    /**
     * Takes a bitmap and scales it such that its aspect ratio is maintained, but its largest
     * dimension is exactly described. Bitmap must have had bounds decoded already.
     *
     * @param maxDimension the largest width or height the bitmap can have.
     * @return the scaled bitmap
     */
    public static Bitmap scaleBitmapToDimension(Bitmap in, int maxDimension) {
        float startH = in.getHeight();
        float startW = in.getWidth();

        float compressedH = maxDimension;
        float compressedW = maxDimension;

        if (startH > startW) {
            compressedW = maxDimension * (startW / startH);
        } else if (startW > startH) {
            compressedH = maxDimension * (startH / startW);
        }

        return Bitmap.createScaledBitmap(
                in,
                (int) compressedW,
                (int) compressedH,
                true
        );
    }

    public static Bitmap convertToGrayScale(Bitmap original) {
        Bitmap copy = Bitmap.createBitmap(original);

        int height = original.getHeight();
        int width = original.getWidth();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int pixel = original.getPixel(x, y);

                int gray = toGray(pixel);
                copy.setPixel(x, y, Color.rgb(gray, gray, gray));
            }
        }

        return copy;
    }

    public static int scalePixelColor(int pixel, float rFactor, float gFactor, float bFactor) {
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);

        return (int) (red * rFactor + green * gFactor + blue * bFactor);
    }

    public static int toGray(int pixel) {
        return scalePixelColor(pixel, ColorScale.GRAY_R, ColorScale.GRAY_G, ColorScale.GRAY_B);
    }
}