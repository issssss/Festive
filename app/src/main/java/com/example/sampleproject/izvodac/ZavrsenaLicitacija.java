package com.example.sampleproject.izvodac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sampleproject.R;
import com.example.sampleproject.izvodac.IzvodacIzvana;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZavrsenaLicitacija extends AppCompatActivity {
    TextView izvodac, trajanje, cijena, komentar, brOsoba, info;
    Button odiNaProfil;
    FirebaseFirestore db;
    Map<String, Object> mapa;
    List<String> prijave;
    String dogadaj, stvarnoDogadaj;
    String raspad[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prijave= Arrays.asList(getIntent().getStringExtra("prijave").split(","));
        Log.d("prijave", getIntent().getStringExtra("prijave"));
        dogadaj=getIntent().getStringExtra("dogadaj");
        stvarnoDogadaj = getIntent().getStringExtra("stvarnoDogadaj");
        Log.d("lic", dogadaj);
        db = FirebaseFirestore.getInstance();
        db.collection("ponude").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    mapa = new HashMap<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        raspad = documentSnapshot.getId().split(" ");
                        Log.d("raspad",raspad[raspad.length-1]);
                        if (prijave.contains(raspad[raspad.length-1]) && documentSnapshot.getId().equals(dogadaj+" "+raspad[raspad.length-1])){
                            if (mapa.isEmpty()){
                                mapa=documentSnapshot.getData();
                                mapa.put("izvodac", raspad[raspad.length-1]);
                            } else{
                                Double cijena1= Double.parseDouble(mapa.get("cijena").toString());
                                Double cijena2= Double.parseDouble(documentSnapshot.getData().get("cijena").toString());
                              if (cijena1.compareTo(cijena2) >0){
                                  mapa=documentSnapshot.getData();
                                  mapa.put("izvodac", raspad[raspad.length-1]);
                              }
                            }
                        }
                    }
                    if (!mapa.isEmpty()){
                        setContentView(R.layout.activity_zavrsena_licitacija);
                        izvodac=findViewById(R.id.info);
                        odiNaProfil=findViewById(R.id.odiNaProfil);
                        odiNaProfil.setOnClickListener(new View.OnClickListener() { // starting new activity based on role
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), IzvodacIzvana.class);
                                intent.putExtra("ime", mapa.get("izvodac").toString());
                                intent.putExtra("imeLicitacije", dogadaj);
                                intent.putExtra("imeDogadaja", stvarnoDogadaj);
                                startActivity(intent);
                            }
                        });
                        izvodac=findViewById(R.id.izvodac);
                        trajanje=findViewById(R.id.trajanje);
                        cijena=findViewById(R.id.cijena);
                        brOsoba=findViewById(R.id.brOsoba);
                        komentar=findViewById(R.id.komentar);
                        izvodac.setText(mapa.get("izvodac").toString());
                        komentar.setText("Komentar: "+mapa.get("komentar").toString());
                        brOsoba.setText("Br osoba: "+mapa.get("brOsoba").toString());
                        trajanje.setText("Trajanje: "+mapa.get("trajanje").toString()+"h");
                        cijena.setText("Cijena: "+mapa.get("cijena").toString()+"kn");

                    }
                    else {
                        setContentView(R.layout.fragment_postavke_fragmet);
                        info = findViewById(R.id.postavkeText);
                        info.setText("Nema prihvaćenih prijava, produži licitaciju!");
                    }


                }
            }
        });


    }
}
