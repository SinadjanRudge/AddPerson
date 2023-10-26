package com.example.addperson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class myAdapter extends FirebaseRecyclerAdapter<DataClass,myAdapter.myviewholder>
{


    private List<DataClass> dataList;
    public myAdapter(@NonNull FirebaseRecyclerOptions<DataClass> options) {
        super(options);
    }




    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull DataClass DataClass)
    {

        holder.name.setText(DataClass.getDataName());
        holder.course.setText(DataClass.getDataCourse());
        holder.email.setText(DataClass.getDataRoom());
        Glide.with(holder.img.getContext()).load(DataClass.getDataImage()).into(holder.img);
    }





    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        CircleImageView img;
        TextView name,course,email;
        public myviewholder(@NonNull View itemView)
        {
            super(itemView);
            img=(CircleImageView)itemView.findViewById(R.id.img1);
            name=(TextView)itemView.findViewById(R.id.nametext);
            course=(TextView)itemView.findViewById(R.id.coursetext);
            email=(TextView)itemView.findViewById(R.id.emailtext);
        }
    }
}
