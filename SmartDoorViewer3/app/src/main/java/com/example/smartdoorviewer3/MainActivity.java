package com.example.smartdoorviewer3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    ImageView imagePerson;
    Button btnCamera, btnTodayImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken=instanceIdResult.getToken();
                Log.e("newToken", newToken);
            }
        });

        init();
        addListener();

    }

    public void init() {

        imagePerson=(ImageView)findViewById(R.id.imagePerson);
        btnCamera=(Button)findViewById(R.id.btnCamera);
        btnTodayImg=(Button)findViewById(R.id.btnTodayImg);

        setImage();

    }

    public void setImage() {

        final DatabaseReference dr=FirebaseDatabase.getInstance().getReference();
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final int dayNum=(int)dataSnapshot.getChildrenCount();
                int index=0;

                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {

                    index++;
                    if(index==dayNum) {
                        final String day=snapshot.getKey();
                        DatabaseReference dr2=dr.child(day);

                        dr2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                final int pictureNum=(int)dataSnapshot2.getChildrenCount();
                                int index2=0;

                                for(DataSnapshot snapshot2:dataSnapshot2.getChildren()) {

                                    index2++;
                                    if(index2==pictureNum) {
                                        String path=day+"/"+snapshot2.getValue().toString()+".jpg";

                                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://smartdoorviewer-85ca9.appspot.com");
                                        StorageReference storageRef = storage.getReference();
                                        StorageReference pathReference = storageRef.child(path);

                                        Glide.with(MainActivity.this)
                                                .load(pathReference)
                                                .into(imagePerson);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    public void addListener() {

        btnTodayImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

    }

}
