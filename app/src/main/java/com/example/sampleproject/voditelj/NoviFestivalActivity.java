package com.example.sampleproject.voditelj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NoviFestivalActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    protected EditText naziv;
    protected EditText opis;
    protected EditText pocetak;
    protected EditText kraj;
    protected Button dodajLogo;
    protected Button objavi;
    protected ImageView logo;
    private Map<Object, Object> festivalData;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Uri selectedImage;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener al;
    private FirebaseStorage storage;
    private String voditelj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novi_festival);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }

        naziv = findViewById(R.id.editText);
        logo=findViewById(R.id.imageView3);
        opis = findViewById(R.id.editText2);
        pocetak = findViewById(R.id.editText3);
        kraj = findViewById(R.id.editText4);
        dodajLogo = findViewById(R.id.button2);
        objavi = findViewById(R.id.button3);
        festivalData = new HashMap<>();


        db = FirebaseFirestore.getInstance();

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date_end = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                String date = sdf.format(myCalendar.getTime());
                kraj.setText(date);
            }
        };
        final DatePickerDialog.OnDateSetListener date_start = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                String date = sdf.format(myCalendar.getTime());
                pocetak.setText(date);
            }
        };
        pocetak.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                new DatePickerDialog(NoviFestivalActivity.this, date_start, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        kraj.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                new DatePickerDialog(NoviFestivalActivity.this, date_end, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
     //   auth = FirebaseAuth.getInstance();
       // al = new FirebaseAuth.AuthStateListener() {
          //  @Override
          //  public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
         //       uploadImg();
         //   }
      //  };
        //auth.addAuthStateListener(al);

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
        naziv.addTextChangedListener(tw);
        opis.addTextChangedListener(tw);
        pocetak.addTextChangedListener(tw);
        kraj.addTextChangedListener(tw);

        Intent intent = getIntent(); //get the intent that started this activity
        voditelj = intent.getStringExtra("voditelj");


        objavi.setEnabled(false);
        objavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference dr = db.collection("festivali").document(naziv.getText().toString());
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                Toast.makeText(NoviFestivalActivity.this, "Ime festivala je zauzeto.", Toast.LENGTH_SHORT).show();  // for testing purposes
                            } else {
                                festivalData.put("voditelj", voditelj);

                                festivalData.put("start", pocetak.getText().toString());
                                festivalData.put("end", kraj.getText().toString());
                                festivalData.put("name", naziv.getText().toString());
                                festivalData.put("description", opis.getText().toString());
                                uploadImg();


                                CollectionReference cr = db.collection("festivali");
                                cr.document(naziv.getText().toString()).set(festivalData);
                                //todo dodati ime festivala u podatke voditelja ? potrebna colekcija voditelji?


                                Intent intent = new Intent(NoviFestivalActivity.this, VoditeljActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        });

        dodajLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

    }

    private void uploadImg(){
        if(selectedImage == null)
            return;
        StorageReference ref = storageRef.child("festival_logos/" + naziv.getText().toString());
        ref.putFile(selectedImage);
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
                    logo.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                }
        }
    }
    private void checkRequired(){
        if(naziv.getText().toString().isEmpty() || opis.getText().toString().isEmpty() || pocetak.getText().toString().isEmpty()
                || kraj.getText().toString().isEmpty()){
            objavi.setEnabled(false);
        } else {
            objavi.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit editor");
        builder.setMessage("Abort changes and go back?"); //npr
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.show();
    }

}
