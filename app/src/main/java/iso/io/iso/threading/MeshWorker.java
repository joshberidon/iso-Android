package iso.io.iso.threading;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import iso.io.iso.algorithms.mesh.MeshFace;

/**
 * Created by tbrown on 4/16/16.
 */
public class MeshWorker {

  private Thread thread;
  private AtomicInteger threadsCompleted;
  private int numThreads;
  private LinkedHashMap<PictureMesher.PictureSide,MeshFace> completedDatas;
  private MeshWorkerCallback callback;

  public MeshWorker(int numThreads, MeshWorkerCallback callback){

    this.numThreads = numThreads;
    this.callback = callback;
    this.threadsCompleted = new AtomicInteger();
    this.completedDatas = new LinkedHashMap<>(numThreads);

    thread = new Thread(new Runnable() {
      @Override public void run() {
        work();
      }
    });
  }

  public void work(){

    finished(null);
  }

  private void finished(Object data){
    callback.meshWorkerCompleted(data);
  }

  public void pictureWorkerFinished(PictureMesher.PictureSide side, MeshFace data){
    int completed = threadsCompleted.incrementAndGet();
    completedDatas.put(side,data);
    if(completed == numThreads){
      this.thread.run();
    }
  }
}

