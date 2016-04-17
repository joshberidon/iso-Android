package iso.io.iso.algorithms.mesh;

import java.util.HashMap;
import java.util.regex.Matcher;

import iso.io.iso.algorithms.AlgorithmConstants;
import iso.io.iso.algorithms.geometry.MeshLayer;

/**
 * Created by Jacob on 4/17/16.
 */
public class MeshMerge {

    private MeshFace[] faces;

    float xBound = 0;
    float yBound = 0;
    float zBound = 0;

    public MeshMerge(MeshFace... faces) {
        this.faces = faces;

    }

    public MeshCloud runMesh() {
        for(MeshFace face: faces) {
            switch (face.orientation) {
                case FRONT:
                    for(MeshPoint point : face.getLayers()[0].points) {
                        if(Math.abs(point.x) > xBound) {
                            xBound = point.x;
                        }
                    }
                    break;

                case TOP:
                    for(MeshPoint point : face.getLayers()[0].points) {
                        if(Math.abs(point.x) > yBound) {
                            yBound = point.x;
                        }
                    }
                    break;

                case RIGHT:
                    for(MeshPoint point : face.getLayers()[0].points) {
                        if(Math.abs(point.x) > xBound) {
                            zBound = point.x;
                        }
                    }
                    break;
            }
        }

        normalizeCoordinates();

        MeshCloud cloud = new MeshCloud(xBound, yBound, zBound);
        for(MeshFace face : faces) {
            for(MeshLayer layer : face.getLayers()) {
                cloud.points.addAll(layer.points);
            }
        }
        cloud.stupidFilter();

        return cloud;
    }

    private void normalizeCoordinates() {
        for (int i = 0; i < faces.length; i++) {

            for (int j = 0; j < faces[i].getLayers()[i].points.size(); j++) {
                MeshPoint old = faces[i].getLayers()[i].points.get(j);
                faces[i].getLayers()[i].points.set(j, normalizePoint(old, faces[i].orientation));
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
