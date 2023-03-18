package com.example.plant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddActivity extends AppCompatActivity {

    EditText name_input, var_input, height_input;
    TextView text_add_pic;
    ImageView imageView_add;
    Button btn_add;

    private final int CAMERA_REQ_CODE = 100;
    static Uri uri1;
    ImageView imgCamera;
    // Define constant variable for folder name
    private static final String FOLDER_NAME = "PlantImages";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        name_input = findViewById(R.id.name_input);
        var_input = findViewById(R.id.var_input);
        height_input = findViewById(R.id.height_input);
        btn_add = findViewById(R.id.btn_add);
        imgCamera = findViewById(R.id.imageView_add);
        imgCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(iCamera, CAMERA_REQ_CODE);
            }
        });
        
        btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
                try {
                    myDB.addPlant(name_input.getText().toString().trim(),var_input.getText().toString().trim(), Integer.parseInt(height_input.getText().toString().trim()));
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQ_CODE) {
                //camera
                if (data != null) {
                    Bitmap img = (Bitmap) data.getExtras().get("data");
                    imgCamera.setImageBitmap(img);

                    // Save the image to the device's MediaStore
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    String fileName = timeStamp + ".jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + FOLDER_NAME);
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    try {
                        OutputStream outputStream = getContentResolver().openOutputStream(uri);
                        if (outputStream != null) {
                            // Compress and save the image to the OutputStream
                            img.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                            outputStream.close();
                            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
                            AddActivity.uri1 = uri;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}