package iso.io.iso.threading;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by tbrown on 4/16/16.
 */
public class PictureMesher {

  public enum PictureSide{
    FRONT(0),
    LEFT(1),
    RIGHT(2);

    private int index;

    private PictureSide(int index){
      this.index = index;
    }

    public int getIndex(){
      return this.index;
    }

    public static PictureSide getFromInteger(int i){
      PictureSide side = FRONT;
      switch(i){
        case 0:
          side = FRONT;
          break;
        case 1:
          side = LEFT;
          break;
        case 2:
          side = RIGHT;
          break;
      }
      return side;
    }
  }

  private LinkedHashMap<Integer, File> fileMap;
  private MeshWorker meshWorker;
  private ArrayList<PictureWorker> pictureWorkers;

  public PictureMesher(){
    fileMap = new LinkedHashMap<>();
  }

  public void addPicture(File pictureFile, PictureSide side){
    fileMap.put(side.getIndex(), pictureFile);
  }

  public void doCalcs(MeshWorkerCallback callback){
    pictureWorkers = new ArrayList<>(fileMap.keySet().size());

    // This guy will start on its own
    this.meshWorker = new MeshWorker(fileMap.keySet().size(), callback);

    for(Integer i : fileMap.keySet()){
      PictureWorker worker = new PictureWorker(meshWorker, PictureSide.getFromInteger(i), fileMap.get(i));
      worker.beginWorking();
      pictureWorkers.add(worker);

    }
  }

}
