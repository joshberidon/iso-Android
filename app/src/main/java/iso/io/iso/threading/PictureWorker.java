package iso.io.iso.threading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


/**
 * Created by tbrown on 4/16/16.
 */
public class PictureWorker {

  private Thread thread;
  private MeshWorker meshWorker;
  public PictureMesher.PictureSide side;
  public Bitmap file;
  private IplConvKernel morphKernel;
  private Context context;

  public final static int GREEN_COLOR_RED = 255;
  public final static int GREEN_COLOR_GREEN = 255;
  public final static int GREEN_COLOR_BLUE = 255;
  public final static int THRESHOLD = 15;

  public PictureWorker(Context context, MeshWorker meshThread, PictureMesher.PictureSide side, final Bitmap file){
    this.context = context;
    this.meshWorker = meshThread;
    this.side = side;
    this.file = file;
    thread = new Thread(new Runnable() {
      @Override public void run() {
        work(file);
      }
    });
  }

  public void beginWorking(){
    this.thread.run();
  }

  public void work(Bitmap file) {

    IplImage iplImage = IplImage.create(cvSize(file.getWidth(), file.getHeight()), 8, 4);
    file.copyPixelsToBuffer(iplImage.getByteBuffer());

    IplImage redChannel = IplImage.create(cvGetSize(iplImage), 8, 4);
    IplImage greenChannel = IplImage.create(cvGetSize(iplImage), 8, 4);
    IplImage blueChannel = IplImage.create(cvGetSize(iplImage), 8, 4);

    IplImage redPostThresholdMin = IplImage.create(cvGetSize(iplImage), 8, 4);
    IplImage redPostThresholdMax = IplImage.create(cvGetSize(iplImage), 8, 4);
    IplImage greenPostThresholdMin = IplImage.create(cvGetSize(iplImage), 8, 4);
    IplImage greenPostThresholdMax = IplImage.create(cvGetSize(iplImage), 8, 4);
    IplImage bluePostThresholdMin = IplImage.create(cvGetSize(iplImage), 8, 4);
    IplImage bluePostThresholdMax = IplImage.create(cvGetSize(iplImage), 8, 4);

    IplImage combinedImage = IplImage.create(cvGetSize(iplImage), 8, 4);

    cvSplit(iplImage, blueChannel, greenChannel, redChannel, null);

    cvThreshold(redChannel, redPostThresholdMin, GREEN_COLOR_RED - THRESHOLD, 255, THRESH_BINARY);
    cvThreshold(redChannel, redPostThresholdMax, GREEN_COLOR_RED + THRESHOLD, 255, THRESH_BINARY_INV);
    cvThreshold(greenChannel, greenPostThresholdMin, GREEN_COLOR_GREEN - THRESHOLD, 255, THRESH_BINARY);
    cvThreshold(greenChannel, greenPostThresholdMax, GREEN_COLOR_GREEN + THRESHOLD, 255, THRESH_BINARY_INV);
    cvThreshold(blueChannel, bluePostThresholdMin, GREEN_COLOR_BLUE - THRESHOLD, 255, THRESH_BINARY);
    cvThreshold(blueChannel, bluePostThresholdMax, GREEN_COLOR_BLUE + THRESHOLD, 255, THRESH_BINARY_INV);

    cvAdd(redPostThresholdMin, redPostThresholdMax, combinedImage, null);
    cvAdd(combinedImage, greenPostThresholdMin, combinedImage, null);
    cvAdd(combinedImage, greenPostThresholdMax, combinedImage, null);
    cvAdd(combinedImage, bluePostThresholdMin, combinedImage, null);
    cvAdd(combinedImage, bluePostThresholdMax, combinedImage, null);

    Log.e("@@@", "man i hope this shit works");

    cvSave(String.format("%s/ggnore%s.bmp", context.getFilesDir().getAbsolutePath(),
        side.getAsString()), combinedImage);

    //cvMorphologyEx(combinedImage, combinedImage, null, );


    finished(null);
  }

  // Call this method when you are done so we can send it to mesh.
  public void finished(Object data){
    this.meshWorker.pictureWorkerFinished(this.side, data);
  }

}
