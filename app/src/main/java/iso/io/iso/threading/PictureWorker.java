package iso.io.iso.threading;

import android.graphics.Bitmap;

/**
 * Created by tbrown on 4/16/16.
 */
public class PictureWorker {

  private Thread thread;
  private MeshWorker meshWorker;
  public PictureMesher.PictureSide side;
  public Bitmap file;

  public PictureWorker(MeshWorker meshThread, PictureMesher.PictureSide side, final Bitmap file){
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

  public void work(Bitmap file){
    finished(null);
  }

  // Call this method when you are done so we can send it to mesh.
  public void finished(Object data){
    this.meshWorker.pictureWorkerFinished(this.side, data);
  }

}
