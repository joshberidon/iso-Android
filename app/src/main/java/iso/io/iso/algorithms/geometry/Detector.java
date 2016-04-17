package iso.io.iso.algorithms.geometry;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Jacob on 4/17/16.
 */
public class Detector<T extends GeoRegion<T>> {

    private Bitmap reference;
    private Listener<T> listener;
    private int threshold;

    public Detector(Bitmap reference, Listener<T> listener, int threshold) {
        this.reference = reference;
        this.listener = listener;
        this.threshold = threshold;
    }

    public void inspect(T region) {
        if(region.maxSize >= 0 && region.maxSize < threshold) {
            throw new IllegalStateException("Cannot detect feature for region smaller than the minimum region size.");
        }

        int xSz = region.x2 - region.x1;
        int ySz = region.y2 - region.y1;

        if(((xSz < region.maxSize && ySz < region.maxSize) || region.maxSize < 0) && region.hasFeature(reference)) {
            listener.onFeatureDetected(region);
        }

        if ((ySz > threshold)
                && xSz > threshold) {
            for (T sub : region.subregions) {
                inspect(sub);
            }
        }
    }

    public interface Listener<T extends GeoRegion> {
        void onRegionStarted(T region);
        void onRegionFinished(T region);
        void onFeatureDetected(T region);
    }
}