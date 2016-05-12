package iso.io.iso;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import iso.io.iso.algorithms.AlgorithmConstants;
import iso.io.iso.algorithms.mesh.MeshCloud;
import iso.io.iso.algorithms.mesh.MeshPoint;
import iso.io.iso.net.WebAPI;
import iso.io.iso.net.WebData;
import iso.io.iso.threading.MeshWorkerCallback;
import iso.io.iso.threading.PictureMesher;
import iso.io.iso.utils.ImageUtils;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CameraActivity extends AppCompatActivity {

    Camera camera;
    Button capture;
    CameraPreview cameraPreview;
    Context context;
    LinkedHashMap<PictureMesher.PictureSide, Bitmap> bitmapMap;
    PictureMesher.PictureSide currentSide;
    TextView diagram;
    TextView pictureText;
    PictureMesher pictureMesher;
    Boolean picturesFinished;
    Bitmap ogBitmap;
    float distance;
    private WebAPI webAPI;
    private String modalName;
    private MeshCloud cloud;
    Camera.Parameters p;
    public final String TAG = this.getClass().getSimpleName();

    private final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.e(TAG, "onShutter");
            Toast.makeText(context, "Waiting on picture", Toast.LENGTH_SHORT).show();
        }
    };

    private final Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "Raw Callback");
        }
    };

    private final Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.e(TAG, "JPEG onPictureTaken");
            Toast.makeText(context, currentSide.getAsString() + " picture logged.", Toast.LENGTH_SHORT).show();
            Bitmap bitmap = ImageUtils.scaleBitmapToDimension(BitmapFactory.decodeByteArray(data, 0, data.length), AlgorithmConstants.Preprocess.IMAGE_SIZE_WVGA);
            bitmapMap.put(currentSide, bitmap);
            doneTakingPicture();
        }
    };

    private void doneTakingPicture() {
        Bitmap bitmap = bitmapMap.get(currentSide);
        File file = new File(context.getFilesDir(), String.format("bitmap%s.png", currentSide.getAsString()));
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri destination = Uri.fromFile(file);
        Crop.of(destination, destination).start(this);
    }

    public Bitmap stripThisShit(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        final int threshold = 150;

        int thatGreenColor = this.getResources().getColor(R.color.fuck_your_green);
        int greenRed = Color.red(thatGreenColor);
        int greenGreen = Color.green(thatGreenColor);
        int greenBlue = Color.blue(thatGreenColor);

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int pixelAsInt = bitmap.getPixel(w, h);

                int red = Color.red(pixelAsInt);
                int green = Color.green(pixelAsInt);
                int blue = Color.blue(pixelAsInt);

                int redDiff = Math.abs(greenRed - red);
                int greenDiff = Math.abs(greenGreen - green);
                int blueDiff = Math.abs(greenBlue - blue);

                if (redDiff < threshold && greenDiff < threshold && blueDiff < threshold) {
                    bitmap.setPixel(w, h, context.getResources().getColor(R.color.clearIThink));
                }
            }
        }

        return bitmap;
    }

    public void slimShady(Bitmap bitmap) {
        // check if weve gotten all the pictures

        // stripping this shit
        Bitmap smallerBitmap = CameraActivity.scaleBitmapToDimension(bitmap, 480);
        bitmapMap.put(currentSide, stripThisShit(smallerBitmap.copy(bitmap.getConfig(), true)));

        if (currentSide.equals(PictureMesher.PictureSide.FRONT)) {
            pictureMesher.addPicture(bitmapMap.get(currentSide), currentSide);
            ogBitmap = bitmap;// TODO
            //ogBitmap = bitmapMap.get(currentSide);
            currentSide = PictureMesher.PictureSide.TOP;
        } else if (currentSide.equals(PictureMesher.PictureSide.TOP)) {
            pictureMesher.addPicture(bitmapMap.get(currentSide), currentSide);
            currentSide = PictureMesher.PictureSide.RIGHT;
        } else {
            picturesFinished = true;
            pictureMesher.doCalcs(new MeshWorkerCallback() {
                @Override
                public void meshWorkerCompleted(MeshCloud data) {
                    //TODO DONE
                    Toast.makeText(CameraActivity.this, "Doing calculations for model.", Toast.LENGTH_LONG)
                            .show();
                    cloud = data;
                    modalThingShow();
                }
            });
        }
        // if not
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (picturesFinished) {
                    diagram.setVisibility(View.INVISIBLE);
                    capture.setVisibility(View.INVISIBLE);
                    pictureText.setVisibility(View.INVISIBLE);
                }
                diagram.setText(currentSide.getAsString());
                camera.startPreview();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Log.e(TAG, "wtf shit actually worked");
            Bitmap bitmap = BitmapFactory.decodeFile((new File(context.getFilesDir(), String.format("bitmap%s.png", currentSide.getAsString()))).getAbsolutePath());
            slimShady(bitmap);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://isoapp.herokuapp.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(interceptor)
                .build();

        webAPI = restAdapter.create(WebAPI.class);

        bitmapMap = new LinkedHashMap<>();
        currentSide = PictureMesher.PictureSide.FRONT;
        setContentView(R.layout.activity_camera);


        capture = (Button) findViewById(R.id.button_capture);
        diagram = (TextView) findViewById(R.id.diagram);
        pictureText = (TextView) findViewById(R.id.picture_text);
        diagram.setText(currentSide.getAsString());
        pictureMesher = new PictureMesher();
        picturesFinished = false;


        requestCamera();

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Log", "taking picture");
                //p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
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

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
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
        if (camera != null) {
            p = camera.getParameters();
            cameraPreview = new CameraPreview(this, camera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(cameraPreview);
        } else {
            Toast.makeText(CameraActivity.this, "The camera is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getActionBar() != null) {
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
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
            if (zoom < maxZoom) {
                zoom++;
            }
        } else if (newDistance < distance) {
            //zoom out
            if (zoom > 0) {
                zoom--;
            }
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

    public void showInstructions() {
        new AlertDialog.Builder(context)
                .setMessage("Take pictures according to the diagram in the top right.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                }).show();
    }

    public void readyToSendData() {

        //int size = ogBitmap.getByteCount();
        //ByteBuffer buffer = ByteBuffer.allocate(size);
        //ogBitmap.copyPixelsToBuffer(buffer);
        //String bitmapAsStr = Arrays.toString(buffer.array());
        //String encoded = Base64.encodeToString(ogBitmap.by, Base64.DEFAULT);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ogBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String bitmapAsStr = Base64.encodeToString(byteArray, Base64.DEFAULT);

        StringBuilder builder = new StringBuilder();
        builder.append("x, y, z\n");

        for (MeshPoint point : cloud.points) {
            builder.append(String.format(Locale.US, "%.3f, %.3f, %.3f%n", point.x, point.y, point.z));
        }

        Log.e("@@@@", builder.toString());

        WebData data = new WebData(modalName, builder.toString(), bitmapAsStr);
        webAPI.sendFile(data, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.e(TAG, "gg web works");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "retrofit failed yo: " + error.getMessage());
            }
        });
    }

    public void modalThingShow() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Your model needs a name!");
        alert.setMessage("Please set a name for your model.");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Do something with value!
                Log.e(TAG, "your thing is called: " + input.getText().toString());
                modalName = input.getText().toString();
                readyToSendData();
            }
        });

        alert.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        //camera.setParameters(p);
        //camera.stopPreview();
        //camera.release();
    }

    public static Bitmap scaleBitmapToDimension(Bitmap in, int maxDimension) {
        float startH = in.getHeight();
        float startW = in.getWidth();

        float compressedH = maxDimension;
        float compressedW = maxDimension;

        if (startH > startW) {
            compressedW = maxDimension * (startW / startH);
        } else if (startW > startH) {
            compressedH = maxDimension * (startH / startW);
        }

        return Bitmap.createScaledBitmap(
                in,
                (int) compressedW,
                (int) compressedH,
                true
        );
    }
}






