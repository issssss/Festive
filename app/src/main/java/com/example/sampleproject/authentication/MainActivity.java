package com.example.sampleproject.authentication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sampleproject.izvodac.IzvodacActivity;
import com.example.sampleproject.organizator.OrganizatorActivity;
import com.example.sampleproject.R;
import com.example.sampleproject.voditelj.VoditeljActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class MainActivity extends AppCompatActivity {
    private EditText textEmail;
    private EditText textPassword;
    private Button btnSignUp;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView signUpView;
    private String role;
    private DocumentReference dr;
    private FirebaseAuth.AuthStateListener al;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //za provjeru settingsActivity
        //startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        signUpView = findViewById(R.id.textViewSignUp);
        textEmail = findViewById(R.id.text_email);
        textPassword = findViewById(R.id.text_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        auth.signOut();

        al = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();

                if(user!=null){
                    db = FirebaseFirestore.getInstance();
                    dr = db.collection("emails").document(user.getEmail());

                    //dodajemo ovo da (osim dohvacanja dokumenta) detektiramo promjene u potvrdi racuna u stvarnom vremenu
                    dr.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(documentSnapshot.exists()){
                                if(documentSnapshot.get("confirmed").equals("true")){
                                    role = documentSnapshot.get("role").toString();

                                    if(role.equals("voditelj")){
                                        Intent intent = new Intent(MainActivity.this, VoditeljActivity.class);
                                        startActivity(intent);
                                    } else if(role.equals("organizator")){
                                        Intent intent = new Intent(MainActivity.this, OrganizatorActivity.class);
                                        startActivity(intent);
                                    } else if(role.equals("izvodac")){
                                        Intent intent = new Intent(MainActivity.this, IzvodacActivity.class);
                                        startActivity(intent);
                                    } else if(role.equals("admin")){
                                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                } else {
                                    setContentView(R.layout.activity_waiting_confirmation);
                                }
                            }
                        }
                    });
                }
            }
        };

        auth.addAuthStateListener(al);

        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OdabirActivity.class);
                startActivity(intent);
            }
        });
    }

    public void submit(){
        final String email = textEmail.getText().toString();
        final String password = textPassword.getText().toString();

        if (email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty() || password.length()<8){
            Toast.makeText(getApplicationContext(), "Choose a valid password", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Credentials incorrect",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });


        }
    }


    @Override
    protected void onDestroy() {
        auth.removeAuthStateListener(al);
        super.onDestroy();
    }
    @Override
    public void onBackPressed(){

    }
}
