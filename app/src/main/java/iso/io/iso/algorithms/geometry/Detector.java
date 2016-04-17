package iso.io.iso.algorithms.geometry;

import android.graphics.Bitmap;

import java.util.ArrayList;

import iso.io.iso.algorithms.AlgorithmConstants;

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

        int patchX = (int) (AlgorithmConstants.MeshConstants.EDGE_THRESHOLD_SCALE * xSz);
        int patchY = (int) (AlgorithmConstants.MeshConstants.EDGE_THRESHOLD_SCALE * ySz);

        for(int i = 0; i < xSz - patchX - 1; i += patchX) {
            for (int j = 0; j <ySz - patchX - 1; j += patchY) {
                if(region.hasFeature(reference)) {
                    listener.onFeatureDetected(i, j, i + xSz, j + ySz);
                }
            }
        }
    }

    public interface Listener<T extends GeoRegion> {
        void onRegionStarted(T region);
        void onRegionFinished(T region);
        void onFeatureDetected(int x1, int x2, int y1, int y2);
    }
}