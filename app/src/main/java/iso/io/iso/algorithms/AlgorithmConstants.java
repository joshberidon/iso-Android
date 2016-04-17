package iso.io.iso.algorithms;

/**
 * Created by Jacob on 4/16/16.
 */
public class AlgorithmConstants {

    // Define conversion constants for scaling colors
    public class ColorScale {
        public static final float GRAY_R = 0.3f;
        public static final float GRAY_G = 0.59f;
        public static final float GRAY_B = 0.11f;
    }

    // All approximate, used to scale bitmap down from large raw images
    public class Preprocess {
        public static final int IMAGE_SIZE_QVGA = 320;
        public static final int IMAGE_SIZE_WVGA = 800;
        public static final int IMAGE_SIZE_480P = 800;
        public static final int IMAGE_SIZE_720P = 1280;
        public static final int IMAGE_SIZE_1080P = 1920;
    }

    public class ColorThresholds {
        public static final float MIN_ALPHA = 0.35f;
        public static final float MAX_ALPHA = 0.70f;
    }

    public class DemoBoxConstants {
        // Constants in units of the box, in this case, 10 in. cube and 18 in. cam distance

        public static final int BOX_DIVISIONS_X = 10;
        public static final int BOX_DIVISIONS_Y = 10;
        public static final int CAMERA_DISTANCE = 18;
    }

    public class MeshConstants {
        public static final int MESH_PROJECTION_LAYERS = 40; // Estimate quarter inch divisions
        public static final float EDGE_THRESHOLD_SCALE = 0.063f;
    }
}
