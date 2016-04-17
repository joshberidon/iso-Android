package iso.io.iso.threading;

import android.graphics.Bitmap;

import iso.io.iso.algorithms.mesh.MeshFace;
import iso.io.iso.algorithms.mesh.MeshProcess;
import iso.io.iso.algorithms.mesh.ProjectionOrientation;

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
    ProjectionOrientation orientation;

    switch (this.side) {
      case TOP:
        orientation = ProjectionOrientation.TOP;
        break;

      case RIGHT:
        orientation = ProjectionOrientation.RIGHT;
        break;

      case FRONT:
        orientation = ProjectionOrientation.FRONT;
        break;

      default:
        orientation = ProjectionOrientation.FRONT;
        break;
    }

    MeshProcess process = new MeshProcess(file, orientation);
    MeshFace face = process.meshPipeline();
    face.generateMesh();

    finished(face);
  }

  // Call this method when you are done so we can send it to mesh.
  public void finished(MeshFace data){
    this.meshWorker.pictureWorkerFinished(this.side, data);
  }

}
