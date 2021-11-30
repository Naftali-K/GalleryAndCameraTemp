package com.nk.galleryandcameratemp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int PHOTO_MADE_CODE = 101;
    private static final int VIDEO_MADE_CODE = 102;
    private static final int SELECT_FROM_GALLERY_CODE = 103;

    ImageView image_from_camera, play_image_view, pause_image_view;
    VideoView video_from_camera;
    Button photo_camera_btn, video_camera_btn, camera_btn, gallery_btn;

    MediaController mediaController;

    private Uri videoURI;

    private static final String TAG = "CAMERA_CHECK";

    ActivityResultLauncher activityResultLauncher; // new option for startActivityForResult!!!!!!!!!!!!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setReferences();

        getCameraPermission();

        // new option for startActivityForResult!!!!!!!!!!!!!!
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){

                    Toast.makeText(getBaseContext(), "Made photo", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onActivityResult: Made photo: " + result.getData().getExtras().get("data"));

                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    image_from_camera.setImageBitmap(bitmap);

                }
            }
        });

        photo_camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoByCamera();
            }
        });

        video_camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeVideoByCamera();
            }
        });

        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        play_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video_from_camera.start();
            }
        });

        pause_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video_from_camera.pause();
            }
        });
    }

    private void setReferences(){
        image_from_camera = findViewById(R.id.image_from_camera);
        play_image_view = findViewById(R.id.play_image_view);
        pause_image_view = findViewById(R.id.pause_image_view);
        video_from_camera = findViewById(R.id.video_from_camera);
            setVideoControllerToVideoView();
        photo_camera_btn = findViewById(R.id.photo_camera_btn);
        video_camera_btn = findViewById(R.id.video_camera_btn);
        camera_btn = findViewById(R.id.camera_btn);
        gallery_btn = findViewById(R.id.gallery_btn);
    }

    private void setVideoControllerToVideoView(){
        mediaController = new MediaController(this);
        mediaController.setAnchorView(video_from_camera);
        video_from_camera.setMediaController(mediaController);
    }

    private void takePhotoByCamera(){
        if (isCameraPresentInPhone()){
            Log.d(TAG, "takePhotoByCamera: Have camera in phone");
            makePhoto();
        }else{
            Log.d(TAG, "takePhotoByCamera: Not have camera in phone");
        }
    }

    private void takeVideoByCamera(){
        if (isCameraPresentInPhone()){
            makeVideo();
        }
    }

    private boolean isCameraPresentInPhone(){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            return true;
        }else{
            return false;
        }
    }

    private void getCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void makePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, PHOTO_MADE_CODE);

        // new option for startActivityForResult!!!!!!!!!!!!!!
        activityResultLauncher.launch(intent);
    }

    private void makeVideo(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_MADE_CODE);
    }


    private void openGallery(){
        Intent intent = new Intent();
//        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); // can see all options (pictures and videos. don't need setType
//        intent.setType("image/*");
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), SELECT_FROM_GALLERY_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_MADE_CODE && resultCode == RESULT_OK){

            Toast.makeText(getBaseContext(), "Made photo", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onActivityResult: Made photo: " + data.getExtras().get("data"));

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            image_from_camera.setImageBitmap(bitmap);

        }else if(requestCode == VIDEO_MADE_CODE && resultCode == RESULT_OK){

            Toast.makeText(getBaseContext(), "Made video", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onActivityResult: Made video: " + data.getData());

            videoURI = data.getData();
            video_from_camera.setVideoURI(videoURI);
            video_from_camera.start();

        }else if(requestCode == SELECT_FROM_GALLERY_CODE && resultCode == RESULT_OK){

            //pictures option
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());
//                image_from_camera.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            //video option
            videoURI = data.getData();
            video_from_camera.setVideoURI(videoURI);
            video_from_camera.start();

            String videoPath = videoURI.getPath(); // OI FILE Manager
            String selectedVideoPath = getPath(videoURI); // MEDIA GALLERY

            Log.d(TAG, "onActivityResult:\t Uri video: " + videoURI +  "\t OI file manager: " + videoPath + "\t Media gallery: " + selectedVideoPath);

        }else{
            Toast.makeText(getBaseContext(), "NOT did nothing.", Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}