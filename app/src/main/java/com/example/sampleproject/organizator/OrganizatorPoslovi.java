package com.example.sampleproject.organizator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.common.PosaoAdapter;
import com.example.sampleproject.common.PosaoItem;
import com.example.sampleproject.common.PrikazIzvodaca;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public class OrganizatorPoslovi extends AppCompatActivity {

    private Map<String, Object> dogadajData = new HashMap<>();
    private Map<Object, Object> licData = new HashMap<>();
    private FirebaseFirestore db2 = FirebaseFirestore.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFirestore db3 = FirebaseFirestore.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm");


    private ArrayList<PosaoItem> posloviList, posloviList2;
    private List<String> dogadaji;
    private Set<String> kriticnooo;

    private RecyclerView pRecyclerView, pRecyclerView2;
    private PosaoAdapter pAdapter, pAdapter2;
    private RecyclerView.LayoutManager pLayoutManager, pLayoutManager2;

    private Button dodaj;
    private Button obrisi;
    private TextView textView;
    private EditText tekstObrisi;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizator_poslovi);
        textView = findViewById(R.id.textView);
        db.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    dogadaji = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        try {
                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.DAY_OF_MONTH, 1);
                            if (documentSnapshot.getData().get("name").toString().equals(getIntent().getStringExtra("dogadaj")) && c.getTime().compareTo(formatter.parse(documentSnapshot.getData().get("date").toString() + " " + documentSnapshot.getData().get("start").toString())) < 0)
                                dogadaji.add(documentSnapshot.getData().get("name").toString());
                        } catch (ParseException e) {
                            Log.d("THIS", "HAPPENED");
                        }

                    }
                    if (!dogadaji.isEmpty()) {
                        textView.setVisibility(View.VISIBLE);
                        db2.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    kriticnooo = new HashSet<>();
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        if (dogadaji.contains(documentSnapshot.getData().get("dogadaj").toString()))
                                        kriticnooo.add(documentSnapshot.getData().get("posao").toString());

                                    }
                                    db2.collection("poslovi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                posloviList = new ArrayList<>();
                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                    if (!kriticnooo.contains(documentSnapshot.getData().get("name").toString()))
                                                    posloviList.add(new PosaoItem(documentSnapshot.getData().get("name").toString()));

                                                }
                                                buildRecyclerView();
                                                setButtons();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                }
            }
        });

        db3.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    posloviList2 = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.getData().get("dogadaj").toString().equals(getIntent().getStringExtra("dogadaj")))
                            posloviList2.add(new PosaoItem(documentSnapshot.getData().get("posao").toString()));

                    }
                    buildRecyclerView2();
                    setButtons2();
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void buildRecyclerView() {
        pRecyclerView = findViewById(R.id.recyclerViewPoslovi);
        pLayoutManager = new LinearLayoutManager(getApplicationContext());
        pAdapter = new PosaoAdapter(posloviList, "da");

        pRecyclerView.setLayoutManager(pLayoutManager);
        pRecyclerView.setAdapter(pAdapter);

    }

    public void buildRecyclerView2() {
        pRecyclerView2 = findViewById(R.id.recyclerViewPoslovi2);
        pLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        pAdapter2 = new PosaoAdapter(posloviList2, "ne");

        pRecyclerView2.setLayoutManager(pLayoutManager2);
        pRecyclerView2.setAdapter(pAdapter2);

    }

    public void insertItem(String pos) {
        posloviList.add(new PosaoItem(pos));
        pAdapter.notifyItemInserted(posloviList.size());
    }


    public void setButtons() {


        pAdapter.setOnItemClickListener(new PosaoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onAddClick(int position) {
                final String posao = posloviList.get(position).getText();
                DocumentReference dr = db2.collection("licitacije_izv").document(getIntent().getStringExtra("dogadaj") + " " + posao);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                Toast.makeText(OrganizatorPoslovi.this, "Takav posao na ovom događaju već postoji.", Toast.LENGTH_SHORT).show();  // for testing purposes
                            } else {

                                licData.put("dogadaj", getIntent().getStringExtra("dogadaj"));
                                licData.put("izvodac", "");
                                licData.put("prijave", "");
                                licData.put("prihvacene", "");
                                licData.put("cijena", "");
                                licData.put("brOsoba", "");
                                licData.put("brIskaznica",""); //novo
                                licData.put("name", getIntent().getStringExtra("dogadaj") + " " + posao);
                                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm");
                                Calendar c = Calendar.getInstance();
                                c.setTime(c.getTime());
                                licData.put("nastanak", formatter.format(c.getTime()));
                                licData.put("posao", posao);
                                c.add(Calendar.DAY_OF_MONTH, 1);
                                licData.put("kraj", formatter.format(c.getTime()));
                                licData.put("rbr", "1");


                                CollectionReference cr = db.collection("licitacije_izv");
                                cr.document(getIntent().getStringExtra("dogadaj") + " " + posao).set(licData);


                                Intent intent = new Intent(OrganizatorPoslovi.this, OrganizatorPoslovi.class);
                                intent.putExtra("dogadaj", getIntent().getStringExtra("dogadaj"));
                                startActivity(intent);
                            }
                        }
                    }
                });


            }
        });
    }

    public void setButtons2() {
        pAdapter2.setOnItemClickListener(new PosaoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final String posao = posloviList2.get(position).getText();
                db.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                try {
                                    if (documentSnapshot.getData().get("name").equals(getIntent().getStringExtra("dogadaj") + " " + posao) && Calendar.getInstance().getTime().compareTo(formatter.parse(documentSnapshot.getData().get("kraj").toString())) < 0) {
                                        dogadajData = documentSnapshot.getData();
                                        break;
                                    } else if (documentSnapshot.getData().get("name").equals(getIntent().getStringExtra("dogadaj") + " " + posao) && Calendar.getInstance().getTime().compareTo(formatter.parse(documentSnapshot.getData().get("kraj").toString())) > 0) {
                                        Toast.makeText(getApplicationContext(), "Izvodac: " + documentSnapshot.getData().get("izvodac"), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                } catch (ParseException e) {
                                    Log.d("THIS", "HAPPENED");
                                }
                            }
                            if (!dogadajData.isEmpty()) {
                                Intent intent = new Intent(OrganizatorPoslovi.this, PrikazIzvodaca.class);
                                intent.putExtra("prijave", dogadajData.get("prijave").toString());
                                intent.putExtra("dogadaj", dogadajData.get("name").toString());
                                startActivity(intent);
                            }
                        }
                    }
                });
            }

            public void onAddClick(int position) {

            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(OrganizatorPoslovi.this, OrganizatorActivity.class);
        startActivity(intent);
    }
}
