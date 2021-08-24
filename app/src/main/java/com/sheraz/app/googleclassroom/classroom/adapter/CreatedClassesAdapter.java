package com.sheraz.app.googleclassroom.classroom.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.model.ClassModel;
import com.sheraz.app.googleclassroom.classroom.activities.CreatedClassInfoActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatedClassesAdapter extends RecyclerView.Adapter<CreatedClassesAdapter.ClassesViewHolder> {
    List<ClassModel> classes;
    Context context;

    public CreatedClassesAdapter(List<ClassModel> classes, Context context) {
        this.classes = classes;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.classes_layout, null);
        return new CreatedClassesAdapter.ClassesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassesViewHolder holder, int position) {
        ClassModel classObj = classes.get(position);
        holder.className.setText(classObj.getClass_name());
        holder.teacherName.setText(classObj.getSubject_teacher());
        Glide.with(context).load(classObj.getImg()).placeholder(R.drawable.ic_launcher_background).into(holder.classImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoClassInfo = new Intent(context, CreatedClassInfoActivity.class);
                gotoClassInfo.putExtra("classcode",classes.get(position).getClassCode());
                context.startActivity(gotoClassInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public class ClassesViewHolder extends RecyclerView.ViewHolder {
        CircleImageView classImage;
        TextView teacherName;
        TextView className;


        public ClassesViewHolder(@NonNull View itemView) {
            super(itemView);
            classImage = itemView.findViewById(R.id.classLayout_img);
            teacherName = itemView.findViewById(R.id.classLayout_teacherName);
            className = itemView.findViewById(R.id.classLayout_className);
        }
    }
}
