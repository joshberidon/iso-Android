package iso.io.iso;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import iso.io.iso.net.GetDataBack;
import iso.io.iso.net.WebAPI;
import iso.io.iso.threading.ModelActivity;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GridActivity extends AppCompatActivity {
  GridView gridView;
  Context context;
  WebAPI webAPI;

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

    RequestInterceptor interceptor = new RequestInterceptor() {
      @Override public void intercept(RequestFacade request) {
        request.addHeader("Content-Type", "application/json");
      }
    };

    RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint("https://isoapp.herokuapp.com")
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setRequestInterceptor(interceptor)
        .build();

    webAPI = restAdapter.create(WebAPI.class);
    webAPI.getShit(new Callback<GetDataBack>() {
      @Override public void success(GetDataBack getDataBack, Response response) {
        //Log.e("@@@@", "stuff: " + response.getBody().toString());
        for(GetDataBack.DataBack data : getDataBack.files){
          int x = 3;
          //Log.e("@@@", data.fileName);
        }
      }

      @Override public void failure(RetrofitError error) {
        Log.e("@@@@", "error " + error.getMessage());
      }
    });

    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Toast.makeText(context, "This grid was clicked, " + position, Toast.LENGTH_SHORT).show();
        // DO something
        Intent intent = new Intent(context, ModelActivity.class);
        startActivity(intent);
      }
    });
  }
}
