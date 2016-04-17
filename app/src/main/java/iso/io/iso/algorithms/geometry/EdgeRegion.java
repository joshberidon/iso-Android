package iso.io.iso.algorithms.geometry;

import android.graphics.Bitmap;
import android.graphics.Color;

import iso.io.iso.algorithms.AlgorithmConstants.ColorThresholds;

/**
 * Created by Jacob on 4/17/16.
 */
public class EdgeRegion extends GeoRegion<EdgeRegion> {

    public EdgeRegion(int x1, int x2, int y1, int y2) {
        super(x1, x2, y1, y2);
    }

    @Override
    public EdgeRegion[] getSubregions() {
        int cx = getCenterX();
        int cy = getCenterY();

        EdgeRegion leftTop = new EdgeRegion(x1, cx, y1, cy);
        EdgeRegion rightTop = new EdgeRegion(cx, x2, y1, cy);
        EdgeRegion leftBottom = new EdgeRegion(x1, cx, cy, y2);
        EdgeRegion rightBottom = new EdgeRegion(cx, x2, cy, y2);

        return new EdgeRegion[]{
                leftTop,
                leftBottom,
                rightTop,
                rightBottom
        };
    }

    @Override
    public boolean hasFeature(Bitmap reference) {
        int alpha = sumAlpha(reference);

        float alphaRatio = (float) alpha / (255 * (y2-y1) * (x2-x1));

        return alphaRatio > ColorThresholds.MIN_ALPHA
                && alphaRatio < ColorThresholds.MAX_ALPHA;
    }

    private int sumAlpha(Bitmap bitmap) {
        int[][] colors = getPixels(bitmap);

        int sum = 0;

        for(int[] column : colors) {
            for (int color : column) {
                sum += Color.alpha(color);
            }
        }

        return sum;
    }
}
