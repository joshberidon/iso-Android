package iso.io.iso.algorithms.geometry;

import android.content.Loader;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Jacob on 4/17/16.
 */
public abstract class GeoRegion<T extends GeoRegion> {
    public final int x1;
    public final int x2;
    public final int y1;
    public final int y2;

    public int maxSize = -1;

    public T[] subregions;

    public GeoRegion(int x1, int x2, int y1, int y2) {
        this.x1 = x1;
        this.x2 = x2;

        this.y1 = y1;
        this.y2 = y2;
    }

    public GeoRegion(int x1, int x2, int y1, int y2, int maxSize) {
        this.x1 = x1;
        this.x2 = x2;

        this.y1 = y1;
        this.y2 = y2;

        this.maxSize = maxSize;
    }

    public abstract T[] getSubregions();
    public abstract boolean hasFeature(Bitmap reference);

    public int getCenterX() {
        return (x1 + x2) / 2;
    }

    public int getCenterY() {
        return (y1 + y2) / 2;
    }

    public int[][] getPixels(Bitmap bitmap) {
        int width = x2 - x1;
        int height = y2 - y1 + 1;

        Log.e("@@@", width + "    fjksjdfkjs    " + height);

        int[][] colors = new int[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if(y1 + i < bitmap.getHeight() && x1 + j < bitmap.getWidth()) {
                    colors[i][j] = bitmap.getPixel(x1 + j, y1 + i);
                }
            }
        }

        return colors;
    }
}
