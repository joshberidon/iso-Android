package iso.io.iso;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import iso.io.iso.net.STLFile;
import iso.io.iso.net.WebAPI;
import retrofit.Callback;
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

    RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint("https://isodfw.herokuapp.com")
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .build();

    webAPI = restAdapter.create(WebAPI.class);

    STLFile file = new STLFile();
    file.fileName = "rawrggg";
    file.data = "hellow I am the body of this thing";
    webAPI.sendFile(file.fileName, file.data, new Callback<Response>() {
      @Override public void success(Response response, Response response2) {
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
