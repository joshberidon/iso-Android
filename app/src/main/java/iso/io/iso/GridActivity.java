package iso.io.iso;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class GridActivity extends AppCompatActivity {
  GridView gridView;
  Context context;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_grid);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    gridView = (GridView) findViewById(R.id.view_grid);
    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    context = this;
    display.getSize(size);
    gridView.setAdapter(new ImageAdapter(this, size.x));

    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View v,
          int position, long id) {
        Toast.makeText(context  ,"This grid was clicked, " + position,Toast.LENGTH_SHORT).show();
        // DO something

      }
    });
  }
}
