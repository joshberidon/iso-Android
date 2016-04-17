package iso.io.iso.algorithms.geometry;

import java.util.ArrayList;

import iso.io.iso.algorithms.mesh.MeshPoint;

/**
 * Created by Jacob on 4/17/16.
 */
public class MeshLayer {
    public final float depth;
    public final ArrayList<MeshPoint> points = new ArrayList<>();

    public MeshLayer(float depth) {
        this.depth = depth;

    }

    public void addPoint(float x, float y) {
        points.add(new MeshPoint(x, y, depth));
    }

    public void addPoint(float x, float y, float z) {
        points.add(new MeshPoint(x, y, z));
    }
}
