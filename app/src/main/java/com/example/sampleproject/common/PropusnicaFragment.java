package com.example.sampleproject.common;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class PropusnicaFragment extends Fragment {
    View view;
    Button propusnica;

    private ArrayList<FestItem> festivali= new ArrayList<>();
    private ArrayList<FestItem> dogadaji= new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser fuser;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView pozadina;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yy HH:mm");

    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // pozadina.findViewById(R.id.pozadina);
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        storage=FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        dr = db.collection("emails").document(fuser.getEmail());
        Log.d("dobiven dr", dr.toString());
        //makeRecView();
        dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot == null) return;
                       String role=new String();
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.get("confirmed").equals("true")) {
                                role = documentSnapshot.get("role").toString();
                                Log.d("Uloga je", role);

                                if (role.equals("voditelj")) {
                                    db.collection("festivali").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            Log.d("Festivali dohvaceni","za voditelja");
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                    try {
                                                        if(Calendar.getInstance().getTime().compareTo((formatter.parse((documentSnapshot.getData().get("end").toString().concat(" ").concat("23:59")))))<0)
                                                        if (fuser.getEmail().equals(documentSnapshot.getData().get("voditelj").toString()))
                                                            festivali.add(new FestItem(documentSnapshot.getData().get("name").toString()));
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                                for (FestItem item : festivali) {
                                                    Log.d("", item.getTextFest());
                                                }
                                                makeRecView();
                                            }
                                        }
                                    });
                                }

                            }
                            if (role.equals("organizator")){
                               // makeRecView();
                                Log.d("Uloga", "Organizator");
                                db.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        Log.d("Organizator dogadaji", "Dogadaji su dohvaceni");
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                if (documentSnapshot.getData().containsKey("organizator")) {
                                                    Log.d("Organizator", documentSnapshot.getData().get("organizator").toString());
                                                    try {
                                                        if (Calendar.getInstance().getTime().compareTo(formatter.parse((documentSnapshot.getData().get("date").toString().concat(" ").concat(documentSnapshot.getData().get("end").toString())))) < 0) {
                                                            if (documentSnapshot.getData().get("organizator").toString().equals(fuser.getEmail())) {
                                                                Log.d("Organizator", documentSnapshot.getData().get("festival").toString());
                                                                festivali.add(new FestItem(documentSnapshot.getData().get("festival").toString()));

                                                            }
                                                        }
                                                    } catch(ParseException ex){
                                                                ex.printStackTrace();
                                                            }

                                                        }
                                                    }
                                            makeRecView();
                                        }
                                    }
                                });
                            }
                            if (role.equals("izvodac")) {
                                Log.d("Uloga","Izvodac");
                                db.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                try {
                                                    if ((Calendar.getInstance().getTime().compareTo((formatter.parse((documentSnapshot.getData().get("kraj").toString())))) > 0) && documentSnapshot.getData().containsKey("izvodac") && fuser.getEmail().equals(documentSnapshot.getData().get("izvodac").toString())) {
                                                        try {
                                                            //dodavamo ime dogadaja kojem posao pripada, ime posla (dogadaj + posao), broj posla i broj osoba
                                                            dogadaji.add(new FestItem(documentSnapshot.getData().get("dogadaj").toString(), documentSnapshot.getData().get("name").toString(), documentSnapshot.getData().get("rbr").toString(), documentSnapshot.getData().get("brOsoba").toString()));
                                                            Log.d("Izvodaceve licitacije", documentSnapshot.getData().get("name").toString());
                                                        }catch(Exception e){
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                } catch (ParseException ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                            db.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                            try {
                                                                if(Calendar.getInstance().getTime().compareTo((formatter.parse((documentSnapshot.getData().get("date").toString().concat(" ").concat(documentSnapshot.getData().get("end").toString())))))<0) {
                                                                    Log.d("Vrijeme","nije isteklo");
                                                                    for (FestItem posao : dogadaji) {
                                                                        Log.d(posao.getTextFest(),documentSnapshot.getData().get("name").toString());
                                                                        if (posao.getTextFest().equals(documentSnapshot.getData().get("name").toString())) {
                                                                            Log.d("Izvodac poslovi", posao.getTextFest());
                                                                            festivali.add(new FestItem(documentSnapshot.getData().get("festival").toString(),posao.getTextPos(), documentSnapshot.getData().get("place").toString(),posao.getTextBrPos(),posao.getTextBrOso()));
                                                                        }
                                                                    }
                                                                }
                                                            } catch (ParseException ex) {
                                                                ex.printStackTrace();
                                                            }

                                                        }
                                                        makeRecView();

                                                    }
                                                }
                                            });
                                        }
                                    }
                                    });

                                }

                            }
                        }
                });
        view= inflater.inflate(R.layout.fragment_propusnica, container, false);
        return view;
    }

    private void makeRecView(){
       Log.d("", "Starting RecycleView");
        RecyclerView recyclerView = view.findViewById(R.id.festivali);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        PropAdapter adapter=new PropAdapter(getActivity().getApplicationContext(),festivali,storageRef);
        recyclerView.setAdapter(adapter);
    }

}
