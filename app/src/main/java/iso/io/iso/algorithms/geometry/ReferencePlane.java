package iso.io.iso.algorithms.geometry;

/**
 * Created by Jacob on 4/17/16.
 */
public class ReferencePlane {
    private int height;
    private int width;

    private int centerX;
    private int centerY;

    private float xRatio;
    private float yRatio;

    private int xDiv = 1;
    private int yDiv = 1;

    private int cameraDistance;

    private String divUnit = "cm";

    /*public ReferencePlane(Bitmap bitmap, String divUnit) {
        // Programmatically find reference points
    }*/

    /**
     * Hardcoded size divisions for our setup.
     *
     * @param xDiv number of divisions in the left to right direction
     * @param yDiv number of divisions in the up down position
     */
    public ReferencePlane(int width, int height, int xDiv, int yDiv, int cameraDistance) {
        this.xDiv = xDiv;
        this.yDiv = yDiv;
        this.cameraDistance = cameraDistance;
        this.height = height;
        this.width = width;

        this.divUnit = "Inches";

        this.xRatio = (float) xDiv / width;
        this.yRatio = (float) yDiv / height;
    }

    /**
     * @param xCoord bitmap pixel xCoordinate
     */
    public float getRealX(int xCoord) {
        return (xCoord - centerX) * xRatio;
    }

    public float getRealY(int yCoord) {
        return (yCoord - centerY) * xRatio;
    }
}
