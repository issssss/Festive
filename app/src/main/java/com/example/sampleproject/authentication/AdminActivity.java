package com.example.sampleproject.authentication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private List<Map> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();



        firestore.collection("admin").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    users = new ArrayList<>();

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        users.add(documentSnapshot.getData());
                    }

                    RecyclerView recyclerView = findViewById(R.id.admin_recycler);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapter = new AdminAdapter(users);
                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        auth.signOut();
        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
