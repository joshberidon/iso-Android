package iso.io.iso;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {

  Camera camera;
  Button capture;
  String TAG = "Camera Activity";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_camera);
    capture = (Button) findViewById(R.id.button_capture);
    requestCamera();
    loadCamera();

    capture.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Log.d("Log", "taking picture");
        camera.takePicture(shutterCallback, null, callback);
      }
    });
  }

  final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
    @Override
    public void onShutter() {
      Log.d(TAG, "onShutter");
    }
  };

  final Camera.PictureCallback callback = new Camera.PictureCallback() {

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
      Log.d(TAG, "onPictureTaken - jpeg");
      try {
        //Store the photo
      } catch (Exception e) {
        //some exceptionhandling
        Log.e(TAG, e.getMessage());
      }
    }
  };

  public static Camera getCameraInstance() {
    Camera c = null;
    try {
      c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
    } catch (Exception e) {
      Log.e("Error", "Camera is not available" + e.getMessage());
      // Camera is not available - in use or does not exist
    }
    return c;
  }

  public void requestCamera() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {

      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.READ_CONTACTS)) {

      } else {

        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA },
            123);//TODO fix the request int
      }
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    switch (requestCode) {
      case 123: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          loadCamera();
          //Allowed camera

        } else {
          //Did not allow camera
        }
        return;
      }
    }
  }

  @Override protected void onPause() {
    super.onPause();
    releaseCamera();
  }

  public void releaseCamera() {
    if (camera != null) {
      camera.release();
      camera = null;
    }
  }

  public void loadCamera() {
    camera = getCameraInstance();
    CameraPreview cameraPreview = new CameraPreview(this, camera);
    FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
    if (camera != null) {
      preview.addView(cameraPreview);
    } else {
      Toast.makeText(CameraActivity.this, "The camera is null", Toast.LENGTH_SHORT).show();
    }
  }


}

