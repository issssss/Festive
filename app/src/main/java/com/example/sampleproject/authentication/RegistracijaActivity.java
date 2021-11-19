package com.example.sampleproject.authentication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sampleproject.izvodac.IzvodacActivity;
import com.example.sampleproject.organizator.OrganizatorActivity;
import com.example.sampleproject.R;
import com.example.sampleproject.voditelj.VoditeljActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegistracijaActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;

    protected EditText userName;
    protected EditText password;
    protected EditText name;
    protected EditText surname;
    protected EditText email;
    protected EditText phoneNumber;
    protected Button BtnAddImage;
    protected ImageView image;
    protected Button BtnRegister;
    private String role;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri selectedImage;
    private DocumentReference dr;
    private FirebaseAuth.AuthStateListener al;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }

        userName = findViewById(R.id.korisnickoIme);
        password = findViewById(R.id.lozinka);
        name = findViewById(R.id.ime);
        surname = findViewById(R.id.prezime);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.brojMobitela);
        BtnAddImage = findViewById(R.id.dodajSliku);
        image = findViewById(R.id.slika);
        BtnRegister = findViewById(R.id.registracija);
        BtnRegister.setEnabled(false);

        Intent intent = getIntent(); //get the intent that started this activity
        role = intent.getStringExtra("accountType");
        Toast.makeText(this, role, Toast.LENGTH_SHORT).show(); // for testing purposes

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        al = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                uploadImg();
                if(user!=null){
                    db = FirebaseFirestore.getInstance();
                    dr = db.collection("emails").document(user.getEmail());

                    /*detektiramo promjene parametra "confirmed" kako bi u slucaju potvrde od
                    strane administratora mogli nastavit dalje
                    */
                    dr.addSnapshotListener(RegistracijaActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(documentSnapshot.exists()){
                                if(documentSnapshot.get("confirmed").equals("true")){
                                    role = documentSnapshot.get("role").toString();

                                    if(role.equals("voditelj")){
                                        Intent intent = new Intent(RegistracijaActivity.this, VoditeljActivity.class);
                                        startActivity(intent);
                                    } else if(role.equals("organizator")){
                                        Intent intent = new Intent(RegistracijaActivity.this, OrganizatorActivity.class);
                                        startActivity(intent);
                                    } else if(role.equals("izvodac")){
                                        Intent intent = new Intent(RegistracijaActivity.this, IzvodacActivity.class);
                                        startActivity(intent);
                                    } else if(role.equals("admin")){
                                        Intent intent = new Intent(RegistracijaActivity.this, AdminActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                } else {
                                    /*Za sad random (nimalo aesthetic) layout dok korisnik ceka potvrdu
                                     * */
                                    setContentView(R.layout.activity_waiting_confirmation);
                                }
                            }
                        }
                    });
                }
            }
        };

        auth.addAuthStateListener(al);

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkRequired();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        userName.addTextChangedListener(tw);
        password.addTextChangedListener(tw);
        email.addTextChangedListener(tw);
        name.addTextChangedListener(tw);
        surname.addTextChangedListener(tw);
        phoneNumber.addTextChangedListener(tw);

        BtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        BtnRegister.setOnClickListener(new View.OnClickListener() { // starting new activity based on role
            @Override
            public void onClick(View v) {
                final String username = userName.getText().toString();

                /*Gledamo postoji li dokument s unesenim korisnickim imenom (ako da, korisnik se nece registrirati).
                * */
                DocumentReference dr = db.collection("users").document(username);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()) {
                                Toast.makeText(RegistracijaActivity.this, "Sorry, that username is taken.", Toast.LENGTH_SHORT).show();  // for testing purposes
                            }
                            else if (password.getText().toString().length()<8){
                                Toast.makeText(RegistracijaActivity.this, "Your password is too short (min 8 characters required)", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) { //an account is created
                                        String nameParam = name.getText().toString();
                                        String surnameParam = surname.getText().toString();
                                        String emailParam = email.getText().toString();
                                        String phoneNumberParam = phoneNumber.getText().toString();

                                        /*Spremamo određene podatke u document koji odgovara novom korisniku.
                                         */
                                        CollectionReference cr = db.collection("users");
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("role", role);
                                        data.put("name", nameParam);
                                        data.put("surname", surnameParam);
                                        data.put("phone number", phoneNumberParam);
                                        cr.document(username).set(data);

                                        /*Spremamo sve podatke o (nepotvrđenom!) korisniku u adminov collection
                                        * */
                                        cr = db.collection("admin");
                                        data.put("email", emailParam);
                                        data.put("role", role);
                                        data.put("username", username);
                                        cr.document(emailParam).set(data);

                                        /*Povezujemo mail sa korisnickim imenom radi zahtjeva projekta
                                        * */
                                        cr = db.collection("emails");
                                        data = new HashMap<>();
                                        data.put("username", username);
                                        data.put("role", role);
                                        data.put("confirmed", "false");
                                        cr.document(email.getText().toString()).set(data);


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) { //an account already exists
                                        Toast.makeText(RegistracijaActivity.this, "You already have an account.", Toast.LENGTH_SHORT).show();
                                        auth.signOut();
                                    }
                                });
                            }
                        }
                        else {
                            Toast.makeText(RegistracijaActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();  // for testing purposes
                        }
                    }
                });


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Pristup odobren.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Pristup nije odbren.", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                }
        }
    }

    private void checkRequired(){
        if(userName.getText().toString().isEmpty() || email.getText().toString().isEmpty() || password.getText().toString().isEmpty()
        || name.getText().toString().isEmpty() || surname.getText().toString().isEmpty() || phoneNumber.getText().toString().isEmpty()){
            BtnRegister.setEnabled(false);
        } else {
            BtnRegister.setEnabled(true);
        }
    }

    private void uploadImg(){
        if(selectedImage == null)
            return;
        StorageReference ref = storageRef.child("profile_pictures/" + email.getText().toString());
        ref.putFile(selectedImage);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Odustanak od registracije");
        builder.setMessage("Želite li odustati od registracije? Promjene neće biti pohranjene.");
        builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(RegistracijaActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        auth.removeAuthStateListener(al);
        super.onDestroy();
    }
}
