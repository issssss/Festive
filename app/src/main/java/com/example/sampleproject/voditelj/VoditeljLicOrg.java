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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.sampleproject.R.layout.fragment_voditelj_lic_org;


public class VoditeljLicOrg extends Fragment {

    private View view;
    protected TextView lic;
    private FirebaseFirestore db;
    private FirebaseFirestore db2;
    private FirebaseUser fuser;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private List<Map> licit;
    private List<Map> festivali;
    private List<String> festivaliOrg = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(fragment_voditelj_lic_org, container, false);
        lic = view.findViewById(R.id.licitacijeOrg);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        db2=FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("licitacije_org").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "usah u firestore licitacija");
                    licit = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        licit.add(documentSnapshot.getData());
                    }
                    db2.collection("festivali").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                festivali = new ArrayList<>();
                                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    festivali.add(documentSnapshot.getData());
                                }
                                for (Map fest : festivali) {
                                    try {
                                        if (fest.get("voditelj").equals(fuser.getEmail())) festivaliOrg.add(fest.get("name").toString());
                                    } catch (Exception e) {
                                        continue;
                                    }
                                }
                                initRecycleView();
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
        VodLicitAdapter adapter  = new VodLicitAdapter(getActivity().getApplicationContext(), licit, storageRef, festivaliOrg, "da"); //isto
        recyclerView.setAdapter(adapter);//
    }


}
