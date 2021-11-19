package com.example.sampleproject.voditelj;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.sampleproject.R;
import com.example.sampleproject.authentication.AdminAdapter;
import com.example.sampleproject.authentication.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class PrikazOrganizatora extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Map> dogadaji;
    Map<String, Object> doga;
    List<String> prijave2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_organizatora);
        mContext=getApplicationContext();
        try {
            final String prijave = getIntent().getStringExtra("lic");
            final String dogadaj = getIntent().getStringExtra("licit");
            prijave2 = Arrays.asList(prijave.split(","));


            db.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        dogadaji = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            dogadaji.add(documentSnapshot.getData());
                            if (documentSnapshot.getData().get("name").toString().equals(dogadaj))
                                doga = documentSnapshot.getData();
                        }
                        initRecycleView();
                    }
                }
            });
        }catch(Exception e) {


            String dohvati = getIntent().getStringExtra("sanja");
            if (dohvati.equals("da")) {
                setContentView(R.layout.activity_kraj);
            }


        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PrikazOrganizatora.this, VoditeljActivity.class);
        startActivity(intent);
    }
    private void initRecycleView(){

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new AdapterOrganizatora(prijave2, dogadaji, doga, mContext, FirebaseStorage.getInstance().getReference());
        recyclerView.setAdapter(adapter);
    }


}

