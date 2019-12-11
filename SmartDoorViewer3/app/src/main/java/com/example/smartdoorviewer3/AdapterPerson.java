package com.example.smartdoorviewer3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterPerson extends RecyclerView.Adapter<AdapterPerson.ViewHolder> {

    List<Picture> m_data;
    Context context;


    public AdapterPerson(List<Picture> data, Context context) {
        m_data = data;
        this.context=context;
    }

    @Override
    public AdapterPerson.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
        AdapterPerson.ViewHolder vh = new AdapterPerson.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(AdapterPerson.ViewHolder holder, int position) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://smartdoorviewer-85ca9.appspot.com");
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(m_data.get(position).day+"/"+m_data.get(position).time+".jpg");

        Glide.with(context)
                .load(pathReference)
                .into(holder.imagePerson);

        holder.textTime.setText(m_data.get(position).time.substring(0,2)+":"+m_data.get(position).time.substring(2,4)+":"+m_data.get(position).time.substring(4,6));

    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imagePerson;
        TextView textTime;

        public ViewHolder(View itemView) {

            super(itemView);
            imagePerson = (ImageView)itemView.findViewById(R.id.imagePerson);
            textTime = (TextView)itemView.findViewById(R.id.textTime);

        }

    }
}