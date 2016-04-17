package iso.io.iso;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.util.ArrayList;

/**
 * Created by JoshBeridon on 4/17/16.
 */
public class ImageAdapter extends BaseAdapter {
  private Context context;
  private int width;
  public ArrayList<Integer> imageArray = new ArrayList<Integer>();


  public ImageAdapter(Context c, int w){
    context = c;
    width = w;
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);
    imageArray.add(R.drawable.ic_cat);

  }
  @Override public int getCount() {
    return imageArray.size();
  }

  @Override public Object getItem(int position) {
    return null;
  }

  @Override public long getItemId(int position) {
    return 0;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ImageView imageView;

    if(convertView==null){
      imageView = new ImageView(context);
      imageView.setLayoutParams(new GridView.LayoutParams(width/2,width/2));
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      imageView.setPadding(0, 0, 0, 0);
    } else {
      imageView = (ImageView) convertView;
    }
    imageView.setImageResource(imageArray.get(position));
    return imageView;
  }
}
