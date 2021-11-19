package com.example.sampleproject.voditelj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FestivalActivity extends AppCompatActivity {

    protected Button noviDogadaj;
    protected ImageView voditelj;
    protected TextView festival;
    protected TextView dogadaji;
    protected ListView listaDogadaja;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference dr;
    private FirebaseUser fuser;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String username;
    private Map userData;
    private ImageView logo;
    private List<Map> users;
    private String nameFestival="";
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival);
        noviDogadaj = findViewById(R.id.button4);
        festival = findViewById(R.id.imePrezime);
        dogadaji = findViewById(R.id.textView7);
        logo= findViewById(R.id.profileImage);




        Intent intent = getIntent(); //get the intent that started this activity
         final String imeFestivala = intent.getStringExtra("festival");
         nameFestival = imeFestivala;

        db.collection("festivali").document(imeFestivala).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        if (Calendar.getInstance().getTime().compareTo(formatter.parse(task.getResult().getData().get("end").toString())) > 0){
                            noviDogadaj.setEnabled(false);
                        }

                    } catch (ParseException e) {
                        Log.d("Problemi", "parsera");
                    }
                }
            }
        });
        noviDogadaj.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FestivalActivity.this, NoviDogadajActivity.class);
                        intent.putExtra("festival", festival.getText().toString());
                        startActivity(intent);

                    }
                }
        );


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
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
                                festival.setText(imeFestivala); //ime festivala
                            }
                        }
                    });
                }
            }
        });

        storageRef.child("festival_logos/" + imeFestivala).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri.toString())
                                .into(logo);
                    }
                });

        db.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){        Log.d(TAG, "usah u firestore dogadaje");
                    users = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        users.add(documentSnapshot.getData());
                    }
                    initRecycleView();
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(FestivalActivity.this, VoditeljActivity.class);
        startActivity(intent);

    }

    private void initRecycleView(){
        Log.d(TAG, "initRecycleView: init recycleview");
        RecyclerView recyclerView = findViewById(R.id.recycle_view); //isto
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//isto
        DogadajAdapter adapter  = new DogadajAdapter(users, nameFestival); //isto
        recyclerView.setAdapter(adapter);//isto
    }
}
