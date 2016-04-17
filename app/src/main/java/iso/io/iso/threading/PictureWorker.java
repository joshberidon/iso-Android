package iso.io.iso.threading;

import java.io.File;

/**
 * Created by tbrown on 4/16/16.
 */
public class PictureWorker {

  private Thread thread;
  private MeshWorker meshWorker;
  public PictureMesher.PictureSide side;
  public File file;

  public PictureWorker(MeshWorker meshThread, PictureMesher.PictureSide side, final File file){
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

  public void work(File file){
    finished(null);
  }

  // Call this method when you are done so we can send it to mesh.
  public void finished(Object data){
    this.meshWorker.pictureWorkerFinished(this.side, data);
  }

}
