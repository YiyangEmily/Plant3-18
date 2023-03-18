package com.example.plant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private Context context;
        Activity activity;
        private ArrayList plant_id,plant_name,plant_var,plant_height,plant_target;

        Animation translate_anim;

        CustomAdapter(Activity activity,Context context,ArrayList plant_id, ArrayList plant_name, ArrayList plant_var, ArrayList plant_height,ArrayList plant_target){
                    this.activity = activity;
                    this.context = context;
                    this.plant_id = plant_id;
                    this.plant_name = plant_name;
                    this.plant_var = plant_var;
                    this.plant_height = plant_height;
                    this.plant_target = plant_target;
        }
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View view = inflater.inflate(R.layout.my_row,parent,false);
                    return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
                    holder.id_text.setText(String.valueOf(plant_id.get(position)));
                    holder.name_text.setText(String.valueOf(plant_name.get(position)));
                    holder.var_text.setText(String.valueOf(plant_var.get(position)));
                    holder.height_text.setText(String.valueOf(plant_height.get(position)));
                    holder.target_text.setText(String.valueOf(plant_target.get(position)));
                    holder.mainLayout.setOnClickListener(new View.OnClickListener() {
        @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,UpdateActivity.class);
                    intent.putExtra("id",String.valueOf(plant_id.get(position)));
                    intent.putExtra("name",String.valueOf(plant_name.get(position)));
                    intent.putExtra("var",String.valueOf(plant_var.get(position)));
                    intent.putExtra("height",String.valueOf(plant_height.get(position)));
                    intent.putExtra("target",String.valueOf(plant_target.get(position)));
                    activity.startActivityForResult(intent,1);
                }
        });
        }
        @Override
        public int getItemCount() {
                return plant_id .size();
        }
        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView id_text,name_text,var_text,height_text,target_text;
            LinearLayout mainLayout;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                id_text = itemView.findViewById(R.id.id_text);
                name_text = itemView.findViewById(R.id.name_text);
                mainLayout = itemView.findViewById(R.id.mainLayout);
                //Animate
                translate_anim = AnimationUtils.loadAnimation(context,R.anim.tranlate_anim);
                mainLayout.setAnimation(translate_anim);
             }
        }
}

