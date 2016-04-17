package iso.io.iso.threading;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by tbrown on 4/16/16.
 */
public class PictureMesher {

  public enum PictureSide{
    FRONT(0),
    TOP(1),
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
          side = TOP;
          break;
        case 2:
          side = RIGHT;
          break;
      }
      return side;
    }
    public String getAsString(){
      String str = null;
      switch (this.index){
        case 0:
          str = "Front";
          break;
        case 1:
          str = "Top";
          break;
        case 2:
          str = "Right";
          break;
      }

      return str;
    }
  }

  private LinkedHashMap<Integer, Bitmap> bitmapMap;
  private MeshWorker meshWorker;
  private ArrayList<PictureWorker> pictureWorkers;

  public PictureMesher(){
    bitmapMap = new LinkedHashMap<>();
  }

  public void addPicture(Bitmap pictureFile, PictureSide side){
    bitmapMap.put(side.getIndex(), pictureFile);
  }

  public void doCalcs(MeshWorkerCallback callback){
    pictureWorkers = new ArrayList<>(bitmapMap.keySet().size());

    // This guy will start on its own
    this.meshWorker = new MeshWorker(bitmapMap.keySet().size(), callback);

    for(Integer i : bitmapMap.keySet()){
      PictureWorker worker = new PictureWorker(meshWorker, PictureSide.getFromInteger(i), bitmapMap.get(i));
      worker.beginWorking();
      pictureWorkers.add(worker);

    }
  }

}
