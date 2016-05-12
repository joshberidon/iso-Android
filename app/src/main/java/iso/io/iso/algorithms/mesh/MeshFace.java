package iso.io.iso.algorithms.mesh;

import java.util.ArrayList;
import java.util.HashMap;

import iso.io.iso.algorithms.geometry.EdgePointRegion;
import iso.io.iso.algorithms.geometry.MeshLayer;
import iso.io.iso.algorithms.geometry.ReferencePlane;
import iso.io.iso.algorithms.AlgorithmConstants.DemoBoxConstants;
import iso.io.iso.algorithms.AlgorithmConstants.MeshConstants;

/**
 * Created by Jacob on 4/17/16.
 */
public class MeshFace {

    private ReferencePlane reference;
    private ArrayList<EdgePointRegion> points;
    public ProjectionOrientation orientation;
    private MeshLayer[] layers = new MeshLayer[MeshConstants.MESH_PROJECTION_LAYERS];

    private float nearPlane;
    private float farPlane;
    private float meshDeltaZ;

    public MeshFace(ReferencePlane reference, ProjectionOrientation orientation, ArrayList<EdgePointRegion> points) {
        this.reference = reference;
        this.points = points;
        this.orientation = orientation;

        this.farPlane = DemoBoxConstants.CAMERA_DISTANCE;
        this.nearPlane = DemoBoxConstants.CAMERA_DISTANCE - DemoBoxConstants.BOX_DIVISIONS_X;

        meshDeltaZ = (farPlane - nearPlane) / MeshConstants.MESH_PROJECTION_LAYERS;
    }

    public MeshFace(ReferencePlane reference, ProjectionOrientation orientation, ArrayList<EdgePointRegion> points, int nearPlane, int farPlane) {
        this.reference = reference;
        this.points = points;
        this.orientation = orientation;

        this.nearPlane = nearPlane;
        this.farPlane = farPlane;

        meshDeltaZ = (farPlane - nearPlane) / MeshConstants.MESH_PROJECTION_LAYERS;
    }


    private HashMap<EdgePointRegion, Deltas> deltaMap = new HashMap<>();
    private HashMap<EdgePointRegion, Coords> coordsMap = new HashMap<>();

    private void genDeltas() {

        for (EdgePointRegion region : points) {
            Deltas d = new Deltas();

            float z1 = farPlane;
            float z2 = farPlane - meshDeltaZ;

            float x1 = reference.getRealX(region.getCenterX());
            float y1 = reference.getRealY(region.getCenterY());

            float ratio = x1 / y1;
            double theta = Math.atan(y1 / z1);

            float y2 = (float) (z2 * Math.tan(theta));
            float x2 = y2 * ratio;

            d.deltaX = x2 - x1;
            d.deltaY = y2 - y1;

            deltaMap.put(region, d);

            Coords coords = new Coords();
            coords.x = x1;
            coords.y = y1;

            coordsMap.put(region, coords);
        }
    }


    // Project to real distances
    // Assume all points lie in cone-like projection
    // Decide near and far planes and divide remainder into equally spaced planes of projection
    public void generateMesh() {
        genDeltas();

        for (int i = 0; i < MeshConstants.MESH_PROJECTION_LAYERS; i++) {
            float depth = (farPlane - nearPlane) / 2 + (float) i * meshDeltaZ ;
            layers[i] = new MeshLayer(depth);

            for(EdgePointRegion region : points) {
                Deltas d = deltaMap.get(region);
                Coords c = coordsMap.get(region);
                layers[i].addPoint(c.x - i * d.deltaX, c.y - i * d.deltaY);
            }
        }
    }

    public MeshLayer[] getLayers() {
        return layers;
    }

    private class Deltas {
        float deltaX, deltaY;
    }

    private class Coords {
        float x, y;
    }
}
