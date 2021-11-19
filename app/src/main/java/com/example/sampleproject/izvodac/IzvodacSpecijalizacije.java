package com.example.sampleproject.izvodac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.*;
import com.example.sampleproject.common.PosaoAdapter;
import com.example.sampleproject.common.PosaoItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class IzvodacSpecijalizacije extends Fragment {
    private View view;

    private ArrayList<SpecItem> mSpecList;
    private ArrayList<PosaoItem> mSpecList2;

    private RecyclerView mRecyclerView, pRecyclerView;
    private SpecAdapter mAdapter;
    private PosaoAdapter pAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonInsert;
    private Button buttonRemove;
    private EditText editTextInsert;
    private EditText editTextRemove;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFirestore db2 = FirebaseFirestore.getInstance();
    private FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.izvodac_specijalizacije, container, false);

        db.collection("poslovi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mSpecList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (Arrays.asList(documentSnapshot.getData().get("izvodaci").toString().split(",")).contains(fuser.getEmail()))
                            mSpecList.add(new SpecItem(documentSnapshot.getData().get("name").toString()));

                    }

                    buildRecyclerView();
                    setButtons();

                }
            }
        });
        db2.collection("poslovi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mSpecList2 = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (!Arrays.asList(documentSnapshot.getData().get("izvodaci").toString().split(",")).contains(fuser.getEmail()))
                            mSpecList2.add(new PosaoItem(documentSnapshot.getData().get("name").toString()));

                    }

                    buildRecyclerView2();
                    setButtons2();

                }
            }
        });



        return view;
    }



    public void buildRecyclerView() {
        mRecyclerView = view.findViewById(R.id.recyclerViewSpec);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new SpecAdapter(mSpecList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }
    public void buildRecyclerView2() {
        pRecyclerView = view.findViewById(R.id.recyclerViewSpec2);
        mLayoutManager = new LinearLayoutManager(getContext());
        pAdapter = new PosaoAdapter(mSpecList2, "da");

        pRecyclerView.setLayoutManager(mLayoutManager);
        pRecyclerView.setAdapter(pAdapter);

    }

    public void insertItem(final String spec){
        DocumentReference dr = db.collection("poslovi").document(spec);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Toast.makeText(view.getContext(), "Takav posao već postoji", Toast.LENGTH_SHORT);



                    } else {

                        Map<String, String> myData = new HashMap<>();
                        myData.put("name", spec);
                        myData.put("izvodaci",fuser.getEmail());


                        CollectionReference cr = db.collection("poslovi");
                        cr.document(spec).set(myData);

                        mSpecList.add(new SpecItem(spec));
                        mAdapter.notifyItemInserted(mSpecList.size());

                    }
                }
            }
        });

    }
    public void insertItem2(final int position){
        DocumentReference dr = db.collection("poslovi").document(mSpecList2.get(position).getText());
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        CollectionReference cr = db.collection("poslovi");
                        cr.document(mSpecList2.get(position).getText()).update("izvodaci", doc.getData().get("izvodaci").toString().equals("")?fuser.getEmail():doc.getData().get("izvodaci").toString()+","+fuser.getEmail());
                        mSpecList.add(new SpecItem(mSpecList2.get(position).getText()));
                        mSpecList2.remove(position);
                        mAdapter.notifyItemInserted(mSpecList.size());
                        pAdapter.notifyItemRemoved(position);
                        pAdapter.notifyItemChanged(position, mSpecList2.size());



                    }
                }
            }
        });

    }

    public void removeItem(final int position){
        db.collection("poslovi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String izvodaci = "";
                    List<String> listizv = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.getData().get("name").toString().equals(mSpecList.get(position).getText()))
                            izvodaci=documentSnapshot.getData().get("izvodaci").toString();
                    }
                    listizv=Arrays.asList(izvodaci.split(","));
                    izvodaci="";
                    for (String s : listizv){
                        if (!s.equals(fuser.getEmail())&&!s.isEmpty()) izvodaci+=s+",";
                    }
                    if (!izvodaci.isEmpty()) izvodaci=izvodaci.substring(0, izvodaci.length()-1);
                    DocumentReference dr = db.collection("poslovi").document(mSpecList.get(position).getText());
                    dr.update("izvodaci", izvodaci);
                    mSpecList2.add(new PosaoItem (mSpecList.get(position).getText()));
                    mSpecList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemChanged(position, mSpecList.size());
                    pAdapter.notifyItemInserted(mSpecList2.size());
                }
            }
        });
    }


    public void setButtons(){
        buttonInsert = view.findViewById(R.id.dodajSpec);
        editTextInsert = view.findViewById(R.id.editSpec_dodaj);

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem(editTextInsert.getText().toString());
            }
        });

        mAdapter.setOnItemClickListener(new SpecAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }
    public void setButtons2(){


        pAdapter.setOnItemClickListener(new PosaoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onAddClick(int position) {
                insertItem2(position);
            }
        });
    }
}
