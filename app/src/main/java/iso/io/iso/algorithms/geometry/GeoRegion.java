package iso.io.iso.algorithms.geometry;

import android.graphics.Bitmap;

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
        int height = y2 - y1;

        int[][] colors = new int[height][width];

        for(int i = 0; i < height; i++) {
            bitmap.getPixels(colors[i], 0, width, x1, y1 + i, width, 1);
        }

        return colors;
    }
}
