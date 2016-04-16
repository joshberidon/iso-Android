package iso.io.iso.threading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tbrown on 4/16/16.
 */
public class MeshWorker {

  private Thread thread;
  private AtomicInteger threadsCompleted;
  private int numThreads;
  private ArrayList<Object> completedDatas;
  private MeshWorkerCallback callback;

  public MeshWorker(int numThreads, MeshWorkerCallback callback){

    this.numThreads = numThreads;
    this.callback = callback;
    this.threadsCompleted = new AtomicInteger();
    this.completedDatas = (ArrayList<Object>) Collections.synchronizedList(new ArrayList<>(numThreads));

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

  public void pictureWorkerFinished(PictureMesher.PictureSide side, Object data){
    int completed = threadsCompleted.incrementAndGet();
    completedDatas.set(side.getIndex(), data);
    if(completed == numThreads){
      this.thread.run();
    }
  }
}

interface MeshWorkerCallback{
  void meshWorkerCompleted(Object data);
}
