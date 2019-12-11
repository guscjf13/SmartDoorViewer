package com.example.smartdoorviewer3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.smartdoorviewer3.HistoryActivity.pictures;
import static com.example.smartdoorviewer3.HistoryActivity.textWhen;
import static com.example.smartdoorviewer3.HistoryActivity.adapterPerson;

public class AdapterDay extends RecyclerView.Adapter<AdapterDay.ViewHolder> {

    List<PictureDay> m_data;
    Context context;


    public AdapterDay(List<PictureDay> data, Context context) {
        m_data = data;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.textDay.setText(m_data.get(position).day.substring(0,4)+"/"+m_data.get(position).day.substring(4,6)+"/"+m_data.get(position).day.substring(6,8));
        holder.textNum.setText(Integer.toString(m_data.get(position).num)+"명 방문");

    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearwhole;
        TextView textDay;
        TextView textNum;

        public ViewHolder(View itemView) {

            super(itemView);
            linearwhole = (LinearLayout)itemView.findViewById(R.id.linearwhole);
            textDay = (TextView)itemView.findViewById(R.id.textDay);
            textNum = (TextView)itemView.findViewById(R.id.textNum);

            linearwhole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position=getAdapterPosition();
                    final String day=m_data.get(position).day;
                    textWhen.setText(day.substring(0,4)+"/"+day.substring(4,6)+"/"+day.substring(6,8)+" 방문자");

                    pictures.clear();

                    DatabaseReference dr= FirebaseDatabase.getInstance().getReference().child(day);
                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                    pictures.add(new Picture(day, snapshot.getValue().toString()));
                                    adapterPerson.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });

                }
            });

        }

    }
}