package iso.io.iso;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

  Camera camera;
  Button capture;
  Button finished;
  String TAG = this.getClass().getSimpleName();

  private final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
    @Override public void onShutter() {
      Log.e(TAG, "onShutter");
    }
  };

  private final Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
    @Override public void onPictureTaken(byte[] data, Camera camera) {
      Log.d(TAG, "Raw Callback");
    }
  };

  private final Camera.PictureCallback callback = new Camera.PictureCallback() {
    @Override public void onPictureTaken(byte[] data, Camera camera) {
      Log.e(TAG, "JPEG onPictureTaken");
      enableFinished();
      File pictureFile = getOutputMediaFile();
      if (pictureFile == null) {
        return;
      }
      try {
        FileOutputStream fos = new FileOutputStream(pictureFile);
        fos.write(data);
        fos.close();
      } catch (FileNotFoundException e) {

      } catch (IOException e) {
      }
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_camera);
    capture = (Button) findViewById(R.id.button_capture);
    finished = (Button) findViewById(R.id.button_finished);

    capture.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Log.e("Log", "taking picture");
        camera.takePicture(shutterCallback, rawCallback, callback);
        camera.setErrorCallback(new Camera.ErrorCallback() {
          @Override public void onError(int error, Camera camera) {
            Log.e("@@@@@@", "shits fucked yo: " + error);
          }
        });

        disableFinished();
      }
    });

    finished.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        camera.startPreview();
      }
    });
  }



  private static File getOutputMediaFile() {
    File mediaStorageDir =
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp");
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.d("MyCameraApp", "failed to create directory");
        return null;
      }
    }
    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File mediaFile;
    mediaFile = new File(mediaStorageDir.getPath() + File.separator + "ISO_" + timeStamp + ".jpg");

    return mediaFile;
  }

  public void enableFinished() {
    finished.setEnabled(true);
  }

  public void disableFinished() {
    finished.setEnabled(false);
  }

  public void requestCamera() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {

      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
        loadCamera();
      } else {

        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA },
            123);//TODO fix the request int
      }
    }
    loadCamera();
  }

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

  @Override protected void onResume() {
    super.onResume();
    requestCamera();
    disableFinished();
  }
}





