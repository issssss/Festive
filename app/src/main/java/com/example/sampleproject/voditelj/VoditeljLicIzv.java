package com.example.sampleproject.voditelj;

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
import android.widget.Toast;

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


public class VoditeljLicIzv extends Fragment {

    private View view;
    protected TextView lic;
    private FirebaseFirestore db;
    private FirebaseFirestore db2, db3;
    private FirebaseUser fuser;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private List<Map> licit, licit2;
    private List<String> festivali;
    private List<String> dogadaji;
    SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yy HH:mm");



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_voditelj_lic_izv, container, false);
        lic = view.findViewById(R.id.licitacijeOrg);
        lic.setText("Aktivne licitacije");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        db2=FirebaseFirestore.getInstance();
        db3=FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("festivali").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "usah u firestore licitacija");
                    festivali = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        if (documentSnapshot.getData().get("voditelj").equals(fuser.getEmail()))
                        festivali.add(documentSnapshot.getData().get("name").toString());
                    }
                    db2.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                dogadaji = new ArrayList<>();
                                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    if (festivali.contains(documentSnapshot.getData().get("festival").toString()))
                                        dogadaji.add(documentSnapshot.getData().get("name").toString());
                                }
                                db3.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            licit = new ArrayList<>();
                                            licit2 = new ArrayList<>();
                                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                                try {
                                                    if (dogadaji.contains(documentSnapshot.getData().get("dogadaj").toString())&& Calendar.getInstance().getTime().compareTo(formatter.parse(documentSnapshot.getData().get("kraj").toString()))<0){
                                                        licit.add(documentSnapshot.getData());
                                                    }
                                                    else if (dogadaji.contains(documentSnapshot.getData().get("dogadaj").toString()) && Calendar.getInstance().getTime().compareTo(formatter.parse(documentSnapshot.getData().get("kraj").toString()))>0){
                                                        licit2.add(documentSnapshot.getData());

                                                    }
                                                } catch (ParseException e) {
                                                    Log.d("THIS", "HAPPENED");
                                                }
                                            }
                                            initRecycleView();
                                            initRecycleView2();
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                }
            }
        });




        return view;
    }

    private void initRecycleView(){
        Log.d(TAG, "initRecycleView: init recycleview");
        RecyclerView recyclerView = view.findViewById(R.id.popisLicitacija); //isto
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));//isto
        LicIzvAdapter adapter  = new LicIzvAdapter(getActivity().getApplicationContext(), licit, "aktivne", fuser.getEmail());
        recyclerView.setAdapter(adapter);//
    }
    private void initRecycleView2(){
        Log.d(TAG, "initRecycleView: init recycleview");
        RecyclerView recyclerView2 = view.findViewById(R.id.popisLicitacijaGotovih); //isto
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));//isto
        LicIzvAdapter adapter2  = new LicIzvAdapter(getActivity().getApplicationContext(), licit2, "zavrsene", fuser.getEmail());
        recyclerView2.setAdapter(adapter2);//
    }


}
