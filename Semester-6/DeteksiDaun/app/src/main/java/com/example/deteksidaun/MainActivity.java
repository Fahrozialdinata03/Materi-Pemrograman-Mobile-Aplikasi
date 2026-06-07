package com.example.deteksidaun;
import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DeteksiDaunApp";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
                    new String[]{Manifest.permission.CAMERA} :
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private PreviewView previewView;
    private OverlayView overlayView;
    private Button captureButton, recordButton, stopRecordButton;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private ImageAnalysis imageAnalysis;
    private ObjectDetector objectDetector;
    private ExecutorService cameraExecutor;

    private List<String> labels; // Untuk menyimpan label dari labels.txt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        overlayView = findViewById(R.id.overlayView);
        captureButton = findViewById(R.id.captureButton);
        recordButton = findViewById(R.id.recordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Inisialisasi labels dari labels.txt
        loadLabels();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        captureButton.setOnClickListener(v -> takePhoto());
        recordButton.setOnClickListener(v -> startRecording());
        stopRecordButton.setOnClickListener(v -> stopRecording());
    }

    private void loadLabels() {
        labels = new ArrayList<>();
        try {
            // Sesuaikan nama file label jika berbeda
            String[] labelArray = getAssets().open("labels.txt").bufferedReader().readLine().split(","); // Atau baca baris per baris
            for (String label : labelArray) {
                labels.add(label.trim());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading labels.txt", e);
            Toast.makeText(this, "Error loading labels: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3) // Sesuaikan dengan aspek rasio yang diinginkan
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();

                // For video capture (requires androidx.camera:camera-video)
                videoCapture = new VideoCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .setTargetResolution(new Size(640, 480)) // Sesuaikan resolusi video
                        .build();

                imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 640)) // Ukuran input model
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_LATEST)
                        .build();

                // Inisialisasi ObjectDetector
                try {
                    ObjectDetector.ObjectDetectorOptions options =
                            ObjectDetector.ObjectDetectorOptions.builder()
                                    .setBaseOptions(org.tensorflow.lite.task.core.BaseOptions.builder().useGpu().build()) // Gunakan GPU jika tersedia
                                    .setMaxResults(5) // Maksimal hasil deteksi
                                    .setScoreThreshold(0.5f) // Threshold kepercayaan
                                    .build();
                    objectDetector = ObjectDetector.createFromFileAndOptions(this, "model.tflite", options);
                } catch (IOException e) {
                    Log.e(TAG, "Error loading TFLite model", e);
                    Toast.makeText(this, "Error loading model: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    detectObject(imageProxy);
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture,
                        videoCapture, // Bind videoCapture
                        imageAnalysis
                );

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void detectObject(ImageProxy imageProxy) {
        // Konversi ImageProxy ke Bitmap
        Bitmap bitmap = imageProxyToBitmap(imageProxy);
        if (bitmap == null) {
            imageProxy.close();
            return;
        }

        // Resize bitmap ke ukuran input model (640x640)
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(640, 640, ResizeOp.ResizeMethod.BILINEAR))
                .build();

        TensorImage tensorImage = new TensorImage(org.tensorflow.lite.DataType.UINT8); // Sesuaikan dengan tipe data input model Anda
        tensorImage.load(bitmap);
        tensorImage = imageProcessor.process(tensorImage);

        // Jalankan inferensi
        List<Detection> results = objectDetector.detect(tensorImage);

        // Normalisasi hasil deteksi ke koordinat PreviewView
        float scaleX = (float) previewView.getWidth() / 640f; // Asumsi model input 640x640
        float scaleY = (float) previewView.getHeight() / 640f;

        // Gambar bounding boxes
        List<OverlayView.DetectionResult> detectionResults = new ArrayList<>();
        for (Detection detection : results) {
            RectF boundingBox = detection.getBoundingBox();
            float score = detection.getScore();
            String category = detection.getCategories().get(0).getDisplayName();

            // Normalisasi bounding box ke ukuran previewView
            RectF normalizedBox = new RectF(
                    boundingBox.left * scaleX,
                    boundingBox.top * scaleY,
                    boundingBox.right * scaleX,
                    boundingBox.bottom * scaleY
            );

            detectionResults.add(new OverlayView.DetectionResult(normalizedBox, category, score));
        }

        // Kirim hasil ke OverlayView untuk digambar
        runOnUiThread(() -> overlayView.setResults(detectionResults, previewView.getWidth(), previewView.getHeight()));

        imageProxy.close();
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy plane = image.getPlanes()[0];
        ByteBuffer buffer = plane.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        // Create a new YuvImage from the data
        android.graphics.YuvImage yuvImage = new android.graphics.YuvImage(
                bytes,
                android.graphics.ImageFormat.NV21,
                image.getWidth(),
                image.getHeight(),
                null
        );

        // Convert YuvImage to JPEG
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        yuvImage.compressToJpeg(new android.graphics.Rect(0, 0, image.getWidth(), image.getHeight()), 90, out);
        byte[] imageBytes = out.toByteArray();

        // Decode JPEG to Bitmap
        Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        // Rotate the bitmap if needed (CameraX provides rotation information)
        int rotationDegrees = image.getImageInfo().getRotationDegrees();
        if (rotationDegrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }


    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        String name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DeteksiDaun");
        }

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
                .build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = outputFileResults.getSavedUri();
                        String msg = "Photo capture succeeded: " + savedUri;
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e(TAG, "Photo capture failed: " + exc.getMessage(), exc);
                        Toast.makeText(getBaseContext(), "Photo capture failed: " + exc.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private VideoCapture.OnVideoSavedCallback videoSavedCallback; // Untuk melacak callback rekaman

    private void startRecording() {
        if (videoCapture == null) {
            return;
        }

        recordButton.setVisibility(View.GONE);
        stopRecordButton.setVisibility(View.VISIBLE);

        String name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/DeteksiDaun");
        }

        VideoCapture.OutputFileOptions outputOptions = new VideoCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Izin perekaman audio diperlukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        videoSavedCallback = new VideoCapture.OnVideoSavedCallback() {
            @Override
            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                Uri savedUri = outputFileResults.getSavedUri();
                String msg = "Video capture succeeded: " + savedUri;
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                Log.d(TAG, msg);
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @org.jetbrains.annotations.Nullable Throwable cause) {
                Log.e(TAG, "Video capture failed: " + message, cause);
                Toast.makeText(getBaseContext(), "Video capture failed: " + message, Toast.LENGTH_SHORT).show();
            }
        };

        videoCapture.startRecording(outputOptions, ContextCompat.getMainExecutor(this), videoSavedCallback);
    }

    private void stopRecording() {
        if (videoCapture != null) {
            videoCapture.stopRecording();
            recordButton.setVisibility(View.VISIBLE);
            stopRecordButton.setVisibility(View.GONE);
        }
    }


    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (objectDetector != null) {
            objectDetector.close();
        }
    }
}