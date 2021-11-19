package com.example.sampleproject.izvodac;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Map;

import javax.annotation.Nullable;

public class IzvodacHome extends Fragment {

    private View view;
    protected ImageView profilePicture;
    protected TextView ime;
    private ArrayList<SpecItem> mSpecList1;
    private ArrayList<SpecItem> mSpecList2;
    private ArrayList<SpecItem> mSpecList3;
    SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yy HH:mm");


    private RecyclerView mRecyclerView1;
    private RecyclerView mRecyclerView2;
    private RecyclerView mRecyclerView3;

    private SpecAdapter mAdapter1;
    private SpecAdapter mAdapter2;
    private SpecAdapter mAdapter3;

    private RecyclerView.LayoutManager mLayoutManager1;
    private RecyclerView.LayoutManager mLayoutManager2;
    private RecyclerView.LayoutManager mLayoutManager3;

    private FirebaseFirestore db;
    private FirebaseFirestore db2;
    private FirebaseUser fuser;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String username;
    private Map userData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.izvodac_home, container, false);

        profilePicture = view.findViewById(R.id.profileImage);
        ime = view.findViewById(R.id.imePrezime);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(getActivity(), fuser.getEmail(), Toast.LENGTH_SHORT).show();

        db = FirebaseFirestore.getInstance();
        db2=FirebaseFirestore.getInstance();
        dr = db.collection("emails").document(fuser.getEmail());
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    Map map = doc.getData();
                    username = map.get("username").toString();

                    dr = db.collection("users").document(username);
                    dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                userData = doc.getData();
                                ime.setText(userData.get("name").toString()+ " " + userData.get("surname").toString());
                            }
                        }
                    });
                }
            }
        });

        storageRef.child("profile_pictures/" + fuser.getEmail()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity().getApplicationContext()).load(uri.toString()).into(profilePicture);
            }
        });

        db.collection("poslovi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mSpecList1 = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (Arrays.asList(documentSnapshot.getData().get("izvodaci").toString().split(",")).contains(fuser.getEmail()))
                            mSpecList1.add(new SpecItem(documentSnapshot.getData().get("name").toString()));

                    }
                    db2.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                mSpecList2 = new ArrayList<>();
                                mSpecList3 = new ArrayList<>();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    try {
                                        if (documentSnapshot.getData().get("izvodac").toString().equals(fuser.getEmail()) && Calendar.getInstance().getTime().compareTo(formatter.parse(documentSnapshot.getData().get("kraj").toString()))>0)
                                            mSpecList2.add(new SpecItem(documentSnapshot.getData().get("name").toString()));
                                        try{
                                            mSpecList3.add(new SpecItem(documentSnapshot.getData().get("komentar").toString()));
                                        }catch (Exception e) {
                                            Log.d("No can do", "babydoll");

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
        mRecyclerView1 = view.findViewById(R.id.popisSpecijalizacija);
        mRecyclerView2 = view.findViewById(R.id.popisObavljenihPoslova);
        mRecyclerView3 = view.findViewById(R.id.popisKomentara);

        mLayoutManager1 = new LinearLayoutManager(getContext());
        mAdapter1 = new SpecAdapter(mSpecList1);
        mLayoutManager2 = new LinearLayoutManager(getContext());
        mAdapter2 = new SpecAdapter(mSpecList2);
        mLayoutManager3 = new LinearLayoutManager(getContext());
        mAdapter3 = new SpecAdapter(mSpecList3);

        mRecyclerView1.setLayoutManager(mLayoutManager1);
        mRecyclerView1.setAdapter(mAdapter1);

        mRecyclerView2.setLayoutManager(mLayoutManager2);
        mRecyclerView2.setAdapter(mAdapter2);

        mRecyclerView3.setLayoutManager(mLayoutManager3);
        mRecyclerView3.setAdapter(mAdapter3);

    }

}
