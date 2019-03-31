package com.example.user.studenthack;

import android.Manifest;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;


import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int GET_FROM_GALLERY = 3;
    private ImageView imageView;
    private String mCurrentPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button uploadButton = findViewById(R.id.buttonUpload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("tag", "Upload button pressed");
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

            }
        });



        final Button imageButton = findViewById(R.id.buttonImage);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("tag", "Image button pressed");
                openCameraIntent();
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    File photoFile = null;
//                    try {
//                        photoFile = createImageFile();
//                    }
//                    catch (IOException e) {
//                        Log.i("main", "IOException");
//                    }
//                    if (photoFile != null) {
//                        Log.d("main", photoFile.toString());
//                        Uri photoURI = getPhotoUri(photoFile);
//
//                        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                    }
//
//                }
            }


        });
    }

    private void openCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                Log.i("main", "IOException");
            }
            if (photoFile != null) {
                Log.d("main", photoFile.toString());
                //Uri photoURI = FileProvider.getUriForFile(this,"${applicationId}.fileprovider",photoFile);
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.user.studenthack.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

//    private Uri getPhotoUri(File photoFile) {
//        return FileProvider.getUriForFile(this,"com.example.android.fileprovider",photoFile);
//
//    }
    private File createImageFile() throws IOException {
        // Create an image file name
        Log.d("main", "in create image file");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Log.d("main", "time stamp made");
        String imageFileName = "JPEG_" + timeStamp + "_";
        Log.d("main", imageFileName);
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String permission = Environment.getExternalStorageState();

        Log.d("tag", ""+storageDir.exists());
        Log.d("main", ""+storageDir.canWrite());
        if (!storageDir.canWrite())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        Log.d("main", ""+storageDir.canWrite());
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        Log.d("main", "file created");
        Log.d("main", image.toString());

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = imageFileName+".jpg";
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri image;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bitmap mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                imageView.setImageBitmap(mImageBitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
//            Bundle extras = data.getExtras();
//            Log.d("tag", extras.get("data").toString());
//            Bitmap imageBitmap = null;
//            try {
//                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            imageView.setImageBitmap(imageBitmap);

            Intent intent = new Intent(this, RecordActivity.class);
            intent.putExtra("photoPath", mCurrentPhotoPath);
            startActivity(intent);
        }
        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            image = data.getData();
            Log.d("main", image.toString());
            String[] proj = { MediaStore.Images.Media.DATA };
            String result = null;

            CursorLoader cursorLoader = new CursorLoader(this,
                    image, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();

            if(cursor != null){
                int column_index =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
            }
            Log.d("main", "RESULT: " +result);


          //  Bitmap bitmap = null;
//            try {
//               // bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
//            } catch (FileNotFoundException e) {
//                //
//                e.printStackTrace();
//            } catch (IOException e) {
//                //
//                e.printStackTrace();
//            }
            String filepath = result;
//            Log.d("main", filepath);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 8; // down sizing image as it throws OutOfMemory Exception for larger images
//            filepath = filepath.replace("file://", ""); // remove to avoid BitmapFactory.decodeFile return null
//            Log.d("main", filepath);
//            File imgFile = new File(result);
//            String absPath = imgFile.getAbsolutePath();
//            Log.d("main", "ABSOLUTE PATH: " + absPath);
//            if (imgFile.exists()) {
//                imageView = (ImageView) findViewById(R.id.imageView2);
//                if (imageView == null) {
//                    Log.d("main", "ImageView null");
//                }
//                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
//                if (bitmap == null) {
//                    Log.d("main", "Bitmap null");
//                }
//                imageView.setImageBitmap(bitmap);
//
//            }
//            else {
//                Log.d("main", "file doesn't exist");
//            }
            Intent intent = new Intent(this, RecordActivity.class);
            intent.putExtra("photoPath", filepath);
            //intent.setData(image);
            startActivity(intent);
        }


    }
}
