package iso.io.iso.algorithms.mesh;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import iso.io.iso.algorithms.AlgorithmConstants;
import iso.io.iso.algorithms.geometry.Detector;
import iso.io.iso.algorithms.geometry.EdgePointRegion;
import iso.io.iso.algorithms.geometry.EdgeRegion;
import iso.io.iso.algorithms.geometry.ReferencePlane;
import iso.io.iso.algorithms.AlgorithmConstants.DemoBoxConstants;
import iso.io.iso.algorithms.AlgorithmConstants.MeshConstants;

/**
 * Created by Jacob on 4/17/16.
 */
public class MeshProcess {

    private final Bitmap raw;
    private ProjectionOrientation orientation;
    private final int rawHeight;
    private final int rawWidth;

    public MeshProcess(Bitmap raw, ProjectionOrientation orientation) {
        this.raw = raw;
        this.rawHeight = raw.getHeight();
        this.rawWidth = raw.getWidth();
        this.orientation = orientation;
    }

    public MeshFace meshPipeline() {

        // Create reference plane
        ReferencePlane realGeometry = new ReferencePlane(rawWidth, rawHeight,
                DemoBoxConstants.BOX_DIVISIONS_X,
                DemoBoxConstants.BOX_DIVISIONS_Y,
                DemoBoxConstants.CAMERA_DISTANCE);


        // Detect edge points
        float threshold = MeshConstants.EDGE_THRESHOLD_SCALE;
        int edgeResolution = (int) Math.min(threshold * rawHeight, threshold * rawWidth);

        Detector<EdgeRegion> edgeRegionDetector = new Detector<>(raw, edgeRegionListener, edgeResolution);
        edgeRegionDetector.inspect(new EdgeRegion(0, rawWidth, 0, rawHeight)); // inspect the whole thing, broken down recursively


        // Create f(n, r) projected layers to merge into the final model
        // n = number of reference divisions, r = desired mesh resolution
        return new MeshFace(realGeometry, orientation, edgePoints);
    }

    private final ArrayList<EdgePointRegion> edgePoints = new ArrayList<>();

    // Synchronously updated while edgeRegionDetector inspects the specified region
    private final Detector.Listener<EdgeRegion> edgeRegionListener = new Detector.Listener<EdgeRegion>() {
        @Override
        public void onRegionStarted(EdgeRegion region) {
            Log.d(this.getClass().getSimpleName(), "Parsing region: " + region.getCenterX() + " " + region.getCenterY());
        }

        @Override
        public void onRegionFinished(EdgeRegion region) {
            Log.d(this.getClass().getSimpleName(), "Finished region: " + region.getCenterX() + " " + region.getCenterY());
        }

        @Override
        public void onFeatureDetected(EdgeRegion region) {
            edgePoints.add(new EdgePointRegion(region.getCenterX(), region.getCenterY()));
        }
    };
}
