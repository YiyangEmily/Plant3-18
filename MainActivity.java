package com.example.plant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;

    MyDatabaseHelper myBoo;
    ArrayList<String> plant_id, plant_name,plant_var,plant_height,plant_target;
    CustomAdapter customAdapter;
    ImageView empty_image;
    TextView no_empty;

    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //权限申请
        askPermission();

        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        empty_image = findViewById(R.id.empty_image);
        no_empty = findViewById(R.id.no_empty);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
        myBoo = new MyDatabaseHelper(MainActivity.this);
        plant_id = new ArrayList<>();
        plant_name = new ArrayList<>();
        plant_var = new ArrayList<>();
        plant_height = new ArrayList<>();
        plant_target = new ArrayList<>();

        storeDataInArrays();


        customAdapter = new CustomAdapter(MainActivity.this, this, plant_id, plant_name, plant_var, plant_height,plant_target);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void storeDataInArrays() {
        Cursor cursor = myBoo.readAllData();
        if (cursor.getCount() == 0) {
            empty_image.setVisibility(View.VISIBLE);
            no_empty.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                plant_id.add(cursor.getString(0));
                plant_name.add(cursor.getString(1));
                plant_var.add(cursor.getString(2));
                plant_height.add(cursor.getString(3));
                plant_target.add(cursor.getString(4));
            }
            empty_image.setVisibility(View.GONE);
            no_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }
    void confirmDialog(){
        AlertDialog.Builder Builder = new AlertDialog.Builder(this);
        Builder.setMessage(" Are you sure you want to delete All ? ");
        Builder.setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper myBoo = new MyDatabaseHelper(MainActivity.this);
                myBoo.deleteAllData();
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
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
    private void askPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        },0);
    }
}
