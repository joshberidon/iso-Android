package iso.io.iso.algorithms.mesh;

import java.util.ArrayList;

/**
 * Created by Jacob on 4/17/16.
 */
public class MeshCloud {

    public ArrayList<MeshPoint> points = new ArrayList<>();

    private float maxX;
    private float maxY;
    private float maxZ;

    public MeshCloud(float maxX, float maxY, float maxZ) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void stupidFilter() {
        for (int i = 0; i < points.size(); i++) {
            if (Math.abs(points.get(i).x) > maxX
                    || Math.abs(points.get(i).y) > maxY
                    || Math.abs(points.get(i).z) > maxZ) {
                points.remove(i);
            }
        }
    }
}
