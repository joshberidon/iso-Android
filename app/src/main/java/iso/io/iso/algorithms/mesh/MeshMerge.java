package iso.io.iso.algorithms.mesh;

import iso.io.iso.algorithms.geometry.EdgePointRegion;
import iso.io.iso.algorithms.geometry.MeshLayer;

/**
 * Created by Jacob on 4/17/16.
 */
public class MeshMerge {

    private MeshFace[] faces;

    private float frontBound;
    private float backBound;
    private float leftBound;
    private float rightBound;
    private float topBound;
    private float bottomBound;

    public MeshMerge(MeshFace... faces) {
        this.faces = faces;

        normalizeCoordinates();
    }

    public void runMesh() {

    }

    public void cull() {
        for (MeshFace face: faces) {
            for(MeshLayer layer : face.getLayers()) {
                for(MeshPoint point : layer.points) {
                    if(point.x < leftBound) {
                        leftBound = point.x;
                    }

                    if(point.x > rightBound) {
                        rightBound = point.x;
                    }

                    if(point.y > topBound) {
                        topBound = point.y;
                    }

                    if (point.y < bottomBound) {
                        bottomBound = point.y;
                    }

                    if (point.z > backBound) {
                        backBound = point.z;
                    }

                    if(point.z < frontBound) {
                        frontBound = point.z;
                    }
                }
            }
        }

    }

    private void normalizeCoordinates() {
        for (int i = 0; i < faces.length; i++) {

            MeshLayer[] layers = faces[i].getLayers();

            for (int j = 0; j < layers[i].points.size(); j++) {
                MeshPoint old = layers[i].points.get(j);
                layers[i].points.set(j, normalizePoint(old, faces[i].orientation));
            }
        }
    }

    private MeshPoint normalizePoint(MeshPoint point, ProjectionOrientation orientation) {
        float newX = point.x;
        float newY = point.y;
        float newZ = point.z;

        switch (orientation) {
            case FRONT:
                // All is well
                break;

            case TOP:
                newX = -point.y;
                newY = point.z;
                newZ = point.x;
                break;

            case RIGHT:
                newX = point.z;
                newY = point.y;
                newZ = point.x;
        }

        return new MeshPoint(newX, newY, newZ);
    }
}
