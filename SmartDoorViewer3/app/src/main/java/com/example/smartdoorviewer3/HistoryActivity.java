package com.example.smartdoorviewer3;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {

    DatabaseReference dr;

    public static TextView textWhen;

    RecyclerView recyclerDay;
    ArrayList<PictureDay> pictureDays;
    AdapterDay adapterDay;
    GridLayoutManager gridLayoutManager1;

    RecyclerView recyclerPerson;
    public static ArrayList<Picture> pictures;
    public static AdapterPerson adapterPerson;
    GridLayoutManager gridLayoutManager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        init();

    }

    public void init() {

        textWhen=(TextView)findViewById(R.id.textWhen);
        recyclerDay=(RecyclerView)findViewById(R.id.recyclerDay);
        recyclerPerson=(RecyclerView)findViewById(R.id.recyclerPerson);

        pictureDays=new ArrayList<>();
        gridLayoutManager1=new GridLayoutManager(HistoryActivity.this, 1);
        recyclerDay.setLayoutManager(gridLayoutManager1);
        adapterDay=new AdapterDay(pictureDays, HistoryActivity.this);
        recyclerDay.setAdapter(adapterDay);

        pictures=new ArrayList<>();
        gridLayoutManager2=new GridLayoutManager(HistoryActivity.this, 2);
        recyclerPerson.setLayoutManager(gridLayoutManager2);
        adapterPerson=new AdapterPerson(pictures, HistoryActivity.this);
        recyclerPerson.setAdapter(adapterPerson);

        //데이터베이스 연결
        dr=FirebaseDatabase.getInstance().getReference();

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final int dayNum=(int)dataSnapshot.getChildrenCount(); //Storage 폴더의 개수
                int index=0; //마지막 날짜를 알기 위한 index

                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {

                    index++;
                    final String day=snapshot.getKey(); //day에는 폴더의 이름(날짜)이 저장됨
                    DatabaseReference dr2=dr.child(day);

                    final int finalIndex = index;
                    dr2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //폴더 이름을 저장
                            pictureDays.add(new PictureDay(day, (int)dataSnapshot.getChildrenCount()));

                            if(finalIndex==dayNum) {
                                //폴더들을 역순으로 저장(최근 날짜부터 확인)
                                Collections.reverse(pictureDays);
                                adapterDay.notifyDataSetChanged();

                                for(DataSnapshot snapshot2:dataSnapshot.getChildren()) {
                                            textWhen.setText(day.substring(0,4)+"/"+day.substring(4,6)+"/"+day.substring(6,8)+" 방문자");
                                            //사진들을 저장
                                            pictures.add(new Picture(day, snapshot2.getValue().toString()));
                                            adapterPerson.notifyDataSetChanged();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

}
