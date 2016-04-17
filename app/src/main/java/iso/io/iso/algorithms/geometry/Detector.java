package iso.io.iso.algorithms.geometry;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

import iso.io.iso.algorithms.AlgorithmConstants;

/**
 * Created by Jacob on 4/17/16.
 */
public class Detector {

    private Bitmap reference;
    private Listener<EdgeRegion> listener;
    private int threshold;

    public Detector(Bitmap reference, Listener<EdgeRegion> listener, int threshold) {
        this.reference = reference;
        this.listener = listener;
        this.threshold = threshold;
    }

    public void inspect(EdgeRegion region) {
        listener.onRegionStarted(region);

        if(region.maxSize >= 0 && region.maxSize < threshold) {
            throw new IllegalStateException("Cannot detect feature for region smaller than the minimum region size.");
        }

        int xSz = region.x2 - region.x1;
        int ySz = region.y2 - region.y1;

        int patchX = (int) (AlgorithmConstants.MeshConstants.EDGE_THRESHOLD_SCALE * xSz);
        int patchY = (int) (AlgorithmConstants.MeshConstants.EDGE_THRESHOLD_SCALE * ySz);

        for(int i = 0; i < xSz - patchX - 1; i += patchX) {
            for (int j = 0; j <ySz - patchX - 1; j += patchY) {

                region = new EdgeRegion(i, i + xSz, j, j + ySz);

                if(region.hasFeature(reference)) {
                    listener.onFeatureDetected(region);
                } else {
                    Log.e("@@@@", "No features = RIP dreams");
                }
            }
        }

        listener.onRegionFinished(region);
    }

    public interface Listener<T extends GeoRegion> {
        void onRegionStarted(T region);
        void onRegionFinished(T region);
        void onFeatureDetected(T region);
    }
}