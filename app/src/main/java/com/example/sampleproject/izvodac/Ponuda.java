package com.example.sampleproject.izvodac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class Ponuda extends AppCompatActivity {


    protected EditText brOsoba;
    protected EditText komentar;
    protected EditText trajanje;
    protected EditText cijena;
    protected Button objavi;
    private Map<Object, Object> ponuda;
    private FirebaseFirestore db;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private String licitacija, ime, d, old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ponuda);

        licitacija=getIntent().getStringExtra("licitacija");
        ime=getIntent().getStringExtra("ime");
        d=getIntent().getStringExtra("d");
        old=getIntent().getStringExtra("old");


        brOsoba = findViewById(R.id.brOsoba);
        komentar = findViewById(R.id.komentar);
        trajanje = findViewById(R.id.trajanje);
        cijena = findViewById(R.id.cijena);
        objavi = findViewById(R.id.button3);
        ponuda = new HashMap<>();

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkRequired();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        brOsoba.addTextChangedListener(tw);
        komentar.addTextChangedListener(tw);
        trajanje.addTextChangedListener(tw);
        cijena.addTextChangedListener(tw);


        objavi.setEnabled(false);
        objavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double.parseDouble(brOsoba.getText().toString());
                    Double.parseDouble(cijena.getText().toString());
                    Double.parseDouble(trajanje.getText().toString());


                    dr = db.collection("ponude").document(licitacija + " " + ime);
                    dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    Toast.makeText(Ponuda.this, "Ponuda već postoji!", Toast.LENGTH_SHORT).show();  // for testing purposes
                                } else {

                                    ponuda.put("brOsoba", brOsoba.getText().toString());
                                    ponuda.put("trajanje", trajanje.getText().toString());
                                    ponuda.put("komentar", komentar.getText().toString());
                                    ponuda.put("cijena", cijena.getText().toString());
                                    ponuda.put("licitacija", licitacija);



                                    CollectionReference cr = db.collection("ponude");
                                    cr.document(licitacija + " " + ime).set(ponuda);
                                    //todo dodati ime festivala u podatke voditelja ? potrebna colekcija voditelji?


                                    db = FirebaseFirestore.getInstance();
                                    cr = db.collection("licitacije_izv");
                                    dr = cr.document(d);
                                    dr.update("prijave", old.equals("")?ime:old+","+ime);




                                    Toast.makeText(getApplicationContext(), "Prijava uspješno obavljena!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Ponuda.this, IzvodacActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Broj osoba, trajanje i cijena moraju biti u brojčanom obliku!", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }




    private void checkRequired(){
        if(cijena.getText().toString().isEmpty() || trajanje.getText().toString().isEmpty() || brOsoba.getText().toString().isEmpty()
                || komentar.getText().toString().isEmpty()){
            objavi.setEnabled(false);
        } else {
            objavi.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit editor");
        builder.setMessage("Abort changes and go back?"); //npr
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.show();
    }

}