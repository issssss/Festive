package com.example.sampleproject.izvodac;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class IzvodacLicitacije extends Fragment {

    private View view;
    private RecyclerView mRecyclerView;
    private LicIzvAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    protected TextView lic;
    private FirebaseFirestore db;
    private FirebaseFirestore db2;
    private FirebaseUser fuser;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private List<Map> licit;
    private List<String> poslovi;
    String role = "izvodac";
    String mail;
    SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yy HH:mm");



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_lic_org, container, false);
        lic = view.findViewById(R.id.licitacijeOrg);
        lic.setText("Dostupne licitacije");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        db2=FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mail = fuser.getEmail();
        db.collection("poslovi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    poslovi = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        try {
                            if (Arrays.asList(documentSnapshot.getData().get("izvodaci").toString().split(",")).contains(fuser.getEmail())) poslovi.add(documentSnapshot.getData().get("name").toString());
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
                                        if (poslovi.contains(documentSnapshot.getData().get("posao")) && Calendar.getInstance().getTime().compareTo(formatter.parse(documentSnapshot.getData().get("kraj").toString()))<0) {
                                            licit.add(documentSnapshot.getData());
                                        }
                                    } catch (ParseException e) {
                                        Log.d("THIS", "HAPPENED");
                                    }
                                }
                                buildRecyclerView();

                            }
                        }
                    });
                }
            }
        });





        return view;
    }



    public void buildRecyclerView() {
        mRecyclerView = view.findViewById(R.id.popisLicitacija);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new LicIzvAdapter(getActivity().getApplicationContext(), licit, role, mail);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
/**       mAdapter.setOnItemClickListener(new LicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), LicitacijaPrijavaActivity.class);
                intent.putExtra("Example Item", mExampleList.get(position));

                startActivity(intent);
            }
        });
 */
    }
    /**
     //insert item kada se objavi nova licitacija na pozicij 0
     public void insertItem(int position){
     mExampleList.add(new ExampleItem(R.drawable.ic_android, "New festival", "Opis festivala"));
     mAdapter.notifyItemInserted(position);
     }

     //remove item kada istekne 24h
     public void removeItem(int position){
     mExampleList.remove(position);
     mAdapter.notifyItemRemoved(position);
     }

     public void changeItem(int position, String text){
     mExampleList.get(position).changeText1(text);
     mAdapter.notifyItemChanged(position);
     }*/

    //TODO: sto kada se prijavi na licitaciju, drop down prijava na natječaj
}
