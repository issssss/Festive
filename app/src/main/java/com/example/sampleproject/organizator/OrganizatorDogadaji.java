package com.example.sampleproject.organizator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sampleproject.izvodac.LicIzvAdapter;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class OrganizatorDogadaji extends Fragment {

    private View view;
    protected TextView lic;
    private FirebaseFirestore db, db3;
    private FirebaseFirestore db2;
    private FirebaseUser fuser;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private List<Map> licit, licit2;
    private List<String> dogadaji;
    String mail;
    SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yy HH:mm");




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.organizator_dogadaji, container, false);
        lic = view.findViewById(R.id.licitacijeOrg);
        lic.setText("Aktivne licitacije");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        db2=FirebaseFirestore.getInstance();
        db3=FirebaseFirestore.getInstance();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mail = fuser.getEmail();
        db.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    dogadaji = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        try {
                            if (documentSnapshot.getData().get("organizator").equals(fuser.getEmail())) dogadaji.add(documentSnapshot.getData().get("name").toString());
                        } catch (Exception e) {

                        }
                    }
                    db2.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                licit = new ArrayList<>();
                                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    try {
                                        if (dogadaji.contains(documentSnapshot.getData().get("dogadaj")) && Calendar.getInstance().getTime().compareTo(formatter.parse(documentSnapshot.getData().get("kraj").toString()))<0) {
                                            licit.add(documentSnapshot.getData());
                                        }
                                    } catch (ParseException e) {
                                        Log.d("THIS", "HAPPENED");
                                    }
                                }
                                initRecycleView();

                            }
                        }
                    });
                    db3.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                licit2 = new ArrayList<>();
                                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    try {
                                        if (dogadaji.contains(documentSnapshot.getData().get("dogadaj")) && Calendar.getInstance().getTime().compareTo(formatter.parse(documentSnapshot.getData().get("kraj").toString()))>0) {
                                            licit2.add(documentSnapshot.getData());
                                        }
                                    } catch (ParseException e) {
                                        Log.d("THIS", "HAPPENED");
                                    }
                                }
                                initRecycleView2();

                            }
                        }
                    });
                }
            }
        });

        return view;

    }
    private void initRecycleView(){
        Log.d(TAG, "initRecycleView: init recycleview");
        RecyclerView recyclerView = view.findViewById(R.id.popisLicitacija); //isto
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));//isto
        LicIzvAdapter adapter  = new LicIzvAdapter(getActivity().getApplicationContext(), licit, "aktivne", mail);
        recyclerView.setAdapter(adapter);//
    }
    private void initRecycleView2(){
        Log.d(TAG, "initRecycleView: init recycleview");
        RecyclerView recyclerView = view.findViewById(R.id.popisLicitacijaGotovih); //isto
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));//isto
        LicIzvAdapter adapter  = new LicIzvAdapter(getActivity().getApplicationContext(), licit2, "zavrsene", mail);
        recyclerView.setAdapter(adapter);//
    }



}
