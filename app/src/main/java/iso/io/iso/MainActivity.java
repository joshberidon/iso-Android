package iso.io.iso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import iso.io.iso.net.BitmapShit;
import iso.io.iso.net.WebAPI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

  public WebAPI webAPI;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show();
      }
    });

    RequestInterceptor interceptor = new RequestInterceptor() {
      @Override public void intercept(RequestFacade request) {
        request.addHeader("Content-Type", "application/json");
      }
    };

    RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint("https://isodfw.herokuapp.com")
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setRequestInterceptor(interceptor)
        .build();

    webAPI = restAdapter.create(WebAPI.class);

    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);
    int size = bitmap.getByteCount();
    ByteBuffer buffer = ByteBuffer.allocate(size);
    bitmap.copyPixelsToBuffer(buffer);
    String x = Arrays.toString(buffer.array());

    BitmapShit asdf = new BitmapShit("qqFilenameqq", "this is an stl file hopefully at some point", x);

    Toast.makeText(this, x, Toast.LENGTH_LONG).show();


    webAPI.sendFile(asdf, new Callback<Response>() {
      @Override public void success(Response rsponse, Response response2) {
        Log.e("@@@@@", "shit works yo");
      }

      @Override public void failure(RetrofitError error) {
        Log.e("@@@@", "shit failed yo: " + error.getMessage());
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so f2 clong
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
