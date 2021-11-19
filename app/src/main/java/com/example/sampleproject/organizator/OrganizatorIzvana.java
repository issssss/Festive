package com.example.sampleproject.organizator;

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
import com.example.sampleproject.izvodac.SpecAdapter;
import com.example.sampleproject.izvodac.SpecItem;
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
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OrganizatorIzvana extends AppCompatActivity {
    private View view;
    private SpecAdapter adapter2;

    protected TextView imePrezime;
    protected ImageView profilePicture;
    protected RecyclerView popisFest;
    protected TextView mojiFest;
    protected TextView komentari;
    protected RecyclerView popisKom;
    private FirebaseFirestore db;
    private FirebaseFirestore db2;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm");


    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DocumentReference dr;
    private String username;
    private Map userData;
    private List<Map> dogadaji;
    private List<SpecItem> komentarcici;

    private EditText editKomentar;
    private Button dodajKomentar;
    private Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizator_izvana);
        profilePicture = findViewById(R.id.profileImage);
        mojiFest = findViewById(R.id.mojiFestivaliOrg);
        popisFest = findViewById(R.id.popisFestivalaOrg);
        komentari = findViewById(R.id.mojiKomentari);
        popisKom = findViewById(R.id.popisKomentara);
        imePrezime = findViewById(R.id.imePrezime);
        con=this.getApplicationContext();

        dodajKomentar = findViewById(R.id.dodajKomentar);
        editKomentar = findViewById(R.id.editKomentar);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();

        dr = db.collection("emails").document(getIntent().getStringExtra("mail"));
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
                                imePrezime.setText(userData.get("name").toString() + " " + userData.get("surname").toString());
                            }
                        }
                    });
                }
            }
        });

        final DocumentReference dr = db.collection("dogadaji").document(getIntent().getStringExtra("dogadaj"));
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        try {
                            if (Calendar.getInstance().getTime().compareTo(formatter.parse(doc.getData().get("date").toString() + " " + doc.getData().get("end").toString())) > 0) {
                                dodajKomentar.setVisibility(View.VISIBLE);
                                editKomentar.setVisibility(View.VISIBLE);
                            }
                        } catch (ParseException e) {
                            Log.d("THIS", "HAPPENED");
                        }


                    }
                }
            }
        });


        db2.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "usah u firestore festivale");
                    dogadaji = new ArrayList<>();
                    komentarcici = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.getData().get("organizator").toString().equals(getIntent().getStringExtra("mail"))){
                            dogadaji.add(documentSnapshot.getData());
                            try {
                                komentarcici.add(new SpecItem(documentSnapshot.getData().get("komentar").toString()));
                            } catch (Exception e){

                            }
                        }
                    }
                    initRecycleView();
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                }
            }
        });

        storageRef.child("profile_pictures/" + getIntent().getStringExtra("mail")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri.toString()).into(profilePicture);
            }
        });
        dodajKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem(editKomentar.getText().toString(), con);
            }
        });
    }


    private void initRecycleView() {
        Log.d(TAG, "initRecycleView: init recycleview");
        RecyclerView recyclerView = findViewById(R.id.popisFestivalaOrg); //isto
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//isto
        DogadajiAdapter adapter = new DogadajiAdapter(getApplicationContext(), dogadaji, storageRef, getIntent().getStringExtra("mail"), "da"); //isto
        recyclerView.setAdapter(adapter);//isto

        RecyclerView recyclerView2 = findViewById(R.id.popisKomentara); //isto
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//isto
        adapter2 = new SpecAdapter((ArrayList<SpecItem>) komentarcici); //isto
        recyclerView2.setAdapter(adapter2);

    }

    public void insertItem(final String spec, final Context con) {
        final DocumentReference dr = db.collection("dogadaji").document(getIntent().getStringExtra("dogadaj"));
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        try {
                            if (Calendar.getInstance().getTime().compareTo(formatter.parse(doc.getData().get("date").toString() + " " + doc.getData().get("end").toString())) > 0) {
                                dr.update("komentar", spec);
                                komentarcici.add(new SpecItem(spec));
                                adapter2.notifyItemInserted(komentarcici.size());
                            } else {
                                Toast.makeText(con, "Dogadaj nije zavrsio!", Toast.LENGTH_SHORT);
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


    }
}
