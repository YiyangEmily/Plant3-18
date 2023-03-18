package com.example.plant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    EditText name_input;
    EditText var_input;
    EditText height_input;
    Button update_button, delete_button;

    String  id,name,var,height;
    ImageView imageView_update;
    private final int CAMERA_REQ_CODE = 200;
    static Uri uri2;
    ImageView imgCamera;
    // Define constant variable for folder name
    private static final String FOLDER_NAME = "PlantImages";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        name_input = findViewById(R.id.name_input2);
        var_input = findViewById(R.id.var_input2);
        height_input = findViewById(R.id.height_input2);
        imageView_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(iCamera, CAMERA_REQ_CODE);
            }
        });


        update_button = findViewById(R.id.update);
        delete_button = findViewById(R.id.delete_button);

        //first we call this
        getAndSetIntentData();

        //Set
        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setTitle(name);
        }

        update_button.setOnClickListener((view) -> {
            String filePath = getFilesDir().getAbsolutePath()+"/image.jpeg";
            Uri fileUri = Uri.parse(filePath);
            Intent intent = new Intent(UpdateActivity.this, MyDatabaseHelper.class);
            intent.putExtra("fileUri", fileUri);
            startActivity(intent);
            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
            name = name_input.getText().toString().trim();
            var = var_input.getText().toString().trim();
            height = String.valueOf(height_input.getText());
            try {
                myDB.updateData(id, name, var, Double.parseDouble(height));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }

    private void getAndSetIntentData() {
        if(getIntent().hasExtra("id") && getIntent().hasExtra("name") && getIntent().hasExtra("author") && getIntent().hasExtra("pages")) {

            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            var = getIntent().getStringExtra("var");
            height = getIntent().getStringExtra("height");
            //path

            name_input.setText(name);
            var_input.setText(var);
            height_input.setText(height);
            Log.d("stev", name + " " + var + " " + height );
        }else{
            Toast.makeText(this," No Data ",Toast.LENGTH_SHORT).show();
        }
    }
    void confirmDialog(){
        AlertDialog.Builder Builder = new AlertDialog.Builder(this);
        Builder.setTitle(" Delete "+ name +" ? ");
        Builder.setMessage(" Are you sure you want to delete "+ name +" ? ");
        Builder.setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper myBoo = new MyDatabaseHelper(UpdateActivity.this);
                myBoo.deleteOneRow(id);
                finish();
            }
        });
        Builder.setNegativeButton(" No ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Builder.create().show();
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
                            UpdateActivity.uri2 = uri;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}