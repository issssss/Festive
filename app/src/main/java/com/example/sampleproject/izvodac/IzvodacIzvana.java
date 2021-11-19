package com.example.sampleproject.izvodac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class IzvodacIzvana extends AppCompatActivity {
    private Context con;
    protected ImageView profilePicture;
    protected TextView ime;
    private ArrayList<SpecItem> mSpecList1;
    private ArrayList<SpecItem> mSpecList2;
    private ArrayList<SpecItem> mSpecList3;
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm");


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
    private String fuser;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String username;
    private Map userData;
    private EditText editKomentar;
    private Button dodajKomentar;
    private String imeLicitacije, imeDogadaja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izvodac_izvana);


        profilePicture = findViewById(R.id.profileImage);
        ime = findViewById(R.id.imePrezime);
        con=this.getApplicationContext();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imeLicitacije=getIntent().getStringExtra("imeLicitacije");
        imeDogadaja=getIntent().getStringExtra("imeDogadaja");

        fuser = getIntent().getStringExtra("ime");
        db = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();
        dr = db.collection("emails").document(fuser);
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
        storageRef.child("profile_pictures/" + fuser).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri.toString()).into(profilePicture);
            }
        });


        final DocumentReference dr = db.collection("licitacije_izv").document(imeLicitacije);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        DocumentReference dr2 = db.collection("dogadaji").document(imeDogadaja);
                        dr2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        try {
                                            if (Calendar.getInstance().getTime().compareTo(formatter.parse(doc.getData().get("date").toString()+" "+doc.getData().get("end").toString())) > 0){
                                                dodajKomentar.setVisibility(View.VISIBLE);
                                                editKomentar.setVisibility(View.VISIBLE);
                                            }
                                            else{
                                                Toast.makeText(con,"Dogadaj nije zavrsio!",Toast.LENGTH_SHORT);
                                            }
                                        } catch (ParseException e) {
                                            Log.d("THIS", "HAPPENED");
                                        }

                                    } else {

                                        Log.d("Nesta smo", "sjebali");

                                    }
                                }
                            }
                        });




                    } else {

                        Log.d("Nesta smo", "sjebali");

                    }
                }
            }
        });


        dodajKomentar = findViewById(R.id.dodajKomentar);
        editKomentar = findViewById(R.id.editKomentar);

        dodajKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem(editKomentar.getText().toString(), con);
            }
        });

        db.collection("poslovi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mSpecList1 = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (Arrays.asList(documentSnapshot.getData().get("izvodaci").toString().split(",")).contains(fuser))
                            mSpecList1.add(new SpecItem(documentSnapshot.getData().get("name").toString()));

                    }
                    db2.collection("licitacije_izv").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                mSpecList2 = new ArrayList<>();
                                mSpecList3 = new ArrayList<>();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    if (documentSnapshot.getData().get("izvodac").toString().equals(fuser)){
                                        mSpecList2.add(new SpecItem(documentSnapshot.getData().get("name").toString()));
                                        try{
                                            mSpecList3.add(new SpecItem(documentSnapshot.getData().get("komentar").toString()));
                                        }catch (Exception e) {
                                            Log.d("No can do", "babydoll");

                                        }

                                    }

                                }
                                buildRecyclerView();

                            }
                        }
                    });

                }
            }
        });

    }
    public void buildRecyclerView() {
        mRecyclerView1 = findViewById(R.id.popisSpecijalizacija);
        mRecyclerView2 = findViewById(R.id.popisObavljenihPoslova);
        mRecyclerView3 = findViewById(R.id.popisKomentara);

        mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        mAdapter1 = new SpecAdapter(mSpecList1);
        mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        mAdapter2 = new SpecAdapter(mSpecList2);
        mLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        mAdapter3 = new SpecAdapter(mSpecList3);

        mRecyclerView1.setLayoutManager(mLayoutManager1);
        mRecyclerView1.setAdapter(mAdapter1);

        mRecyclerView2.setLayoutManager(mLayoutManager2);
        mRecyclerView2.setAdapter(mAdapter2);

        mRecyclerView3.setLayoutManager(mLayoutManager3);
        mRecyclerView3.setAdapter(mAdapter3);

    }

    public void insertItem(final String spec, final Context con){
        final DocumentReference dr = db.collection("licitacije_izv").document(imeLicitacije);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        DocumentReference dr2 = db.collection("dogadaji").document(imeDogadaja);
                        dr2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        try {
                                            if (Calendar.getInstance().getTime().compareTo(formatter.parse(doc.getData().get("date").toString()+" "+doc.getData().get("end").toString())) > 0){
                                                dr.update("komentar", spec);
                                                mSpecList3.add(new SpecItem(spec));
                                                mAdapter3.notifyItemInserted(mSpecList3.size());
                                            }
                                            else{
                                                Toast.makeText(con,"Dogadaj nije zavrsio!",Toast.LENGTH_SHORT);
                                            }
                                        } catch (ParseException e) {
                                            Log.d("THIS", "HAPPENED");
                                        }

                                    } else {

                                        Log.d("Nesta smo", "sjebali");

                                    }
                                }
                            }
                        });




                    } else {

                        Log.d("Nesta smo", "sjebali");

                    }
                }
            }
        });

    }
}
