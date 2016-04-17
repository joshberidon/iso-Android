package iso.io.iso;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import iso.io.iso.threading.MeshWorkerCallback;
import iso.io.iso.threading.PictureMesher;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

  Camera camera;
  Button capture;
  String TAG = this.getClass().getSimpleName();
  CameraPreview cameraPreview;
  Context context;
  LinkedHashMap<PictureMesher.PictureSide, Bitmap> bitmapMap;
  PictureMesher.PictureSide currentSide;
  TextView diagram;
  PictureMesher pictureMesher;
  Boolean picturesFinished;
  float distance;
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

  private final Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
    @Override public void onPictureTaken(byte[] data, Camera camera) {
      Log.e(TAG, "JPEG onPictureTaken");
      Toast.makeText(context, "Yo logcat is fucked", Toast.LENGTH_SHORT).show();
      Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
      bitmapMap.put(currentSide, bitmap);
      doneTakingPicture();
    }
  };

  private void doneTakingPicture() {
    // check if weve gotten all the pictures
    if (currentSide.equals(PictureMesher.PictureSide.FRONT)) {
      pictureMesher.addPicture(bitmapMap.get(currentSide), currentSide);
      currentSide = PictureMesher.PictureSide.TOP;
    } else if (currentSide.equals(PictureMesher.PictureSide.TOP)) {
      pictureMesher.addPicture(bitmapMap.get(currentSide), currentSide);
      currentSide = PictureMesher.PictureSide.RIGHT;
    } else {
      picturesFinished = true;
      pictureMesher.doCalcs(new MeshWorkerCallback() {
        @Override public void meshWorkerCompleted(Object data) {
          //TODO DONE
          Toast.makeText(CameraActivity.this, "DO CALCS CALBACK BITCHES", Toast.LENGTH_SHORT)
              .show();
        }
      });
    }
    // if not
    this.runOnUiThread(new Runnable() {
      @Override public void run() {
        if (picturesFinished) {
          diagram.setVisibility(View.INVISIBLE);
          capture.setVisibility(View.INVISIBLE);
        }
        diagram.setText(currentSide.getAsString());
        camera.startPreview();
        // update ui and
      }
    });

    // else lets start fucikng making shit
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.context = this;
    bitmapMap = new LinkedHashMap<>();
    currentSide = PictureMesher.PictureSide.FRONT;
    setContentView(R.layout.activity_camera);
    capture = (Button) findViewById(R.id.button_capture);
    diagram = (TextView) findViewById(R.id.diagram);
    diagram.setText(currentSide.getAsString());
    pictureMesher = new PictureMesher(this);
    picturesFinished = false;
    FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

    View decorView = getWindow().getDecorView();
    // Hide the status bar.
    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
    decorView.setSystemUiVisibility(uiOptions);
    // Remember that you should never show the action bar if the
    // status bar is hidden, so hide that too if necessary.
    if(getActionBar() != null){
      ActionBar actionBar = getActionBar();
      actionBar.hide();
    }
    showInstructions();
    requestCamera();

    capture.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Log.e("Log", "taking picture");
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
      }
    });
  }

  private static File getOutputMediaFile() {
    File mediaStorageDir =
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp");
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        return null;
      }
    }
    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File mediaFile;
    mediaFile = new File(mediaStorageDir.getPath() + File.separator + "ISO_" + timeStamp + ".jpg");

    return mediaFile;
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
      e.printStackTrace();
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
    cameraPreview = new CameraPreview(this, camera);
    FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
    if (camera != null) {
      preview.addView(cameraPreview);
    } else {
      Toast.makeText(CameraActivity.this, "The camera is null", Toast.LENGTH_SHORT).show();
    }
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // Get the pointer ID
    Camera.Parameters params = camera.getParameters();
    int action = event.getAction();


    if (event.getPointerCount() > 1) {
      // handle multi-touch events
      if (action == MotionEvent.ACTION_POINTER_DOWN) {
        distance = getFingerDistance(event);
      } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
        camera.cancelAutoFocus();
        handleZoom(event, params);
      }
    } else {
      // handle single touch events
      if (action == MotionEvent.ACTION_UP) {
        handleFocus(event, params);
      }
    }
    return true;
  }
  private void handleZoom(MotionEvent event, Camera.Parameters params) {
    int maxZoom = params.getMaxZoom();
    int zoom = params.getZoom();
    float newDistance = getFingerDistance(event);
    if (newDistance > this.distance) {
      //zoom in
      if (zoom < maxZoom)
        zoom++;
    } else if (newDistance < distance) {
      //zoom out
      if (zoom > 0)
        zoom--;
    }
    distance = newDistance;
    params.setZoom(zoom);
    camera.setParameters(params);
  }

  public void handleFocus(MotionEvent event, Camera.Parameters params) {
    int pointerId = event.getPointerId(0);
    int pointerIndex = event.findPointerIndex(pointerId);
    // Get the pointer's current position
    float x = event.getX(pointerIndex);
    float y = event.getY(pointerIndex);

    List<String> supportedFocusModes = params.getSupportedFocusModes();
    if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
      camera.autoFocus(new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean b, Camera camera) {
          // currently set to auto-focus on single touch
        }
      });
    }
  }

  private float getFingerDistance(MotionEvent event) {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);
    return (float) Math.sqrt(x * x + y * y);
  }
  public void showInstructions(){
    new AlertDialog.Builder(context)
        .setMessage("Take pictures according to the diagram in the top right.")
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            // continue with delete
          }
        }).show();
  }
}






