package com.example.sampleproject.common;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.R;
import com.example.sampleproject.izvodac.AdapterIzvodaca;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrikazIzvodaca extends AppCompatActivity {


    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Map> dogadaji;
    Map<String, Object> doga = new HashMap<>();
    List<String> prijave2;
    private TextView rbr;
    private EditText noviRbr;
    private Button submit;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_izvodaca);
        rbr=findViewById(R.id.rbr);
        mContext = getApplicationContext();
        noviRbr = findViewById(R.id.editSpec_dodaj);
        submit = findViewById(R.id.dodajSpec);
        final String prijave = getIntent().getStringExtra("prijave");
        final String dogadaj = getIntent().getStringExtra("dogadaj");
        db.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.getData().get("name").toString().equals(dogadaj)){

                            doga=documentSnapshot.getData();

                        }

                    }
                    try {
                        rbr.setText("Redni broj posla: "+doga.get("rbr").toString());

                    } catch (Exception e){

                    }
                    prijave2 = Arrays.asList(prijave.split(","));
                    initRecycleView();

                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference cr = db.collection("licitacije_izv");
                cr.document(dogadaj).update("rbr", noviRbr.getText().toString());
                rbr.setText("Redni broj posla: "+noviRbr.getText().toString());
            }
        });

    }
    private void initRecycleView(){

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        AdapterIzvodaca adapter = new AdapterIzvodaca(prijave2, doga, mContext, FirebaseStorage.getInstance().getReference());
        recyclerView.setAdapter(adapter);
    }
}
