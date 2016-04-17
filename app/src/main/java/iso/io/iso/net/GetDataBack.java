package iso.io.iso.net;

import java.util.List;

/**
 * Created by tbrown on 4/17/16.
 */
public class GetDataBack {

  public List<DataBack> files;

  public class DataBack{
    public String fileName;
    public String data;
    public String bitmap;
  }
}
