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

import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


public class VoditeljInfo extends Fragment {
    private View view;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFirestore db2 = FirebaseFirestore.getInstance();
    private List<String> festivali;
    private List<Map> dogadaji;
    private FirebaseUser fuser;
    SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yy HH:mm");
    private StorageReference storageRef;
    private FirebaseStorage storage;







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_voditelj_info, container, false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db.collection("festivali").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    festivali = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        try {
                            if (documentSnapshot.getData().get("voditelj").equals(fuser.getEmail())) festivali.add(documentSnapshot.getData().get("name").toString());
                        } catch (Exception e) {

                        }
                    }
                    db2.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                dogadaji = new ArrayList<>();
                                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    try {
                                        if (festivali.contains(documentSnapshot.getData().get("festival").toString()) && Calendar.getInstance().getTime().compareTo(formatter.parse(documentSnapshot.getData().get("date").toString()+" "+documentSnapshot.getData().get("end").toString()))>0) {
                                            dogadaji.add(documentSnapshot.getData());
                                        }
                                    } catch (ParseException e) {
                                        Log.d("THIS", "HAPPENED");
                                    }
                                }
                                initRecycleView();

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
        RecyclerView recyclerView = view.findViewById(R.id.popisDogadaja); //isto
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));//isto
        VodLicitAdapter adapter  = new VodLicitAdapter(getActivity().getApplicationContext(), dogadaji, storageRef, null, "ne"); //isto
        recyclerView.setAdapter(adapter);//
    }





}
