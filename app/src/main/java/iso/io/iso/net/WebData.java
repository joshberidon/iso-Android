package iso.io.iso.net;

/**
 * Created by tbrown on 4/16/16.
 */
public class WebData {
  String bitmap;
  String data;
  String filename;

  public WebData(String fileNAme, String data, String bitmap){
    this.filename = fileNAme;
    this.data = data;
    this.bitmap = bitmap;
  }
}