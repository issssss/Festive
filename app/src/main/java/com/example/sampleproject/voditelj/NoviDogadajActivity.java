package com.example.sampleproject.voditelj; //NoviDogadajActivity.java

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.R;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NoviDogadajActivity extends AppCompatActivity {

    private static final String TAG = "NoviDogadajActivity";
    private FirebaseUser fuser;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private List<Map> users;
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yy");


    //private ArrayList<String> mNames = new ArrayList<>();
    //private ArrayList<String> mImageUrls = new ArrayList<>();

    protected EditText nazivText;
    protected EditText opisText;
    protected EditText prostorText;
    protected EditText datumText;
    protected EditText pocetakText;
    protected EditText krajText;

    private Map<Object, Object> dogadajData;
    private Map<Object, Object> licData;
    private FirebaseFirestore db;
    protected Button spremiBtn;
    protected Button bckBtn;


    String pripadniFestival;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novi_dogadaj);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        nazivText = findViewById(R.id.nazivText);
        opisText = findViewById(R.id.opisText);
        prostorText = findViewById(R.id.prostorText);
        datumText = findViewById(R.id.datumText);
        pocetakText = findViewById(R.id.pocetakText);
        krajText = findViewById(R.id.krajText);
        spremiBtn = findViewById(R.id.spremiBtn);
        bckBtn = findViewById(R.id.backBtn);

        dogadajData = new HashMap<>();
        licData=new HashMap<>();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();

        fuser = FirebaseAuth.getInstance().getCurrentUser(); //todo sve ahh ... povuci podatke za festival act kako bi priko intenta to stavia u pripadnifestival
        db = FirebaseFirestore.getInstance();

   //     firestore.collection("org_vod").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      //      @Override
        //    public void onComplete(@NonNull Task<QuerySnapshot> task) {
   //             if(task.isSuccessful()){        Log.d(TAG, "usah u firestore org_vod");
     //               users = new ArrayList<>();
       //             for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
         //               users.add(documentSnapshot.getData());
           //         }
                    // initRecycleView();
           //     } else {
             //       Toast.makeText(getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
               // }
        //    }
       // });

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

        nazivText.addTextChangedListener(tw);
        opisText.addTextChangedListener(tw);
        datumText.addTextChangedListener(tw);
        pocetakText.addTextChangedListener(tw);
        krajText.addTextChangedListener(tw);
        prostorText.addTextChangedListener(tw);

        Intent intent = getIntent(); //get the intent that started this activity
        pripadniFestival = intent.getStringExtra("festival");

        spremiBtn.setEnabled(false);
        spremiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference dr = db.collection("dogadaji").document(nazivText.getText().toString());
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                Toast.makeText(NoviDogadajActivity.this, "Ime dogadaja je zauzeto.", Toast.LENGTH_SHORT).show();  // for testing purposes
                            }else if (pocetakText.getText().toString().compareTo(krajText.getText().toString())>0){
                                Toast.makeText(NoviDogadajActivity.this, "Kraj ne može biti prije početka.", Toast.LENGTH_SHORT).show();  // for testing purposes
                            }
                            else {
                                DocumentReference dr2 = db.collection("festivali").document(pripadniFestival);
                                dr2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc2 = task.getResult();
                                            if (doc2.exists()) {
                                                try {
                                                    Date pocetniDatum = formatter.parse(doc2.getData().get("start").toString());
                                                    Date zavrsniDatum = formatter.parse(doc2.getData().get("end").toString());
                                                    if (formatter.parse(datumText.getText().toString()).compareTo(pocetniDatum)<0 || formatter.parse(datumText.getText().toString()).compareTo(zavrsniDatum)>0){
                                                        Toast.makeText(NoviDogadajActivity.this, "Datum nije u granicama festivala", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        dogadajData.put("festival", pripadniFestival);
                                                        dogadajData.put("date", datumText.getText().toString());
                                                        dogadajData.put("place", prostorText.getText().toString());
                                                        dogadajData.put("start", pocetakText.getText().toString());
                                                        dogadajData.put("end", krajText.getText().toString());
                                                        dogadajData.put("name", nazivText.getText().toString());
                                                        dogadajData.put("description", opisText.getText().toString());
                                                        dogadajData.put("organizator", "");


                                                        CollectionReference cr = db.collection("dogadaji");
                                                        cr.document(nazivText.getText().toString()).set(dogadajData);

                                                        licData.put("festival", pripadniFestival);
                                                        licData.put("dogadaj", nazivText.getText().toString());
                                                        licData.put("aktivnost", "true");
                                                        licData.put("start", pocetakText.getText().toString());
                                                        licData.put("end", krajText.getText().toString());
                                                        licData.put("date", datumText.getText().toString());
                                                        licData.put("prijave", "");


                                                        cr = db.collection("licitacije_org");
                                                        cr.document(nazivText.getText().toString()).set(licData);

                                                        Intent intent = new Intent(NoviDogadajActivity.this, FestivalActivity.class);
                                                        intent.putExtra("festival", pripadniFestival);
                                                        startActivity(intent);
                                                    }
                                                } catch (ParseException e) {
                                                    Log.d("Što parser pravi?", "Probleme!");
                                                }
                                            }
                                        }
                                    }
                                });



                            }
                        }
                    }
                });
            }
        });

        stvoriDateAndTimePicker();
        bckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(NoviDogadajActivity.this);
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
        });
    }

    private void stvoriDateAndTimePicker() {
        // na pokusaj upisa datuma pop uppa kalendar za odabrat dan

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
                datumText.setText(date);
            }
        };
        datumText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    new DatePickerDialog(NoviDogadajActivity.this, date_end, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //na pokusaj upisa vremena pocetka i kraja pojavi se sat na koji se moze odabrat tocan sat i minuta

        final TimePickerDialog.OnTimeSetListener time_start = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                myCalendar.set(Calendar.MINUTE, minute);


                String myFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                String date = sdf.format(myCalendar.getTime());
                pocetakText.setText(date);
            }
        };
        pocetakText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    new TimePickerDialog(NoviDogadajActivity.this, time_start, myCalendar
                            .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener time_end = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                myCalendar.set(Calendar.MINUTE, minute);


                String myFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                String date = sdf.format(myCalendar.getTime());
                krajText.setText(date);
            }
        };
        krajText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    new TimePickerDialog(NoviDogadajActivity.this, time_end, myCalendar
                            .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });


        Log.d(TAG, "onCreate: started.");
    }

    private void checkRequired(){
        if(nazivText.getText().toString().isEmpty() || opisText.getText().toString().isEmpty() || pocetakText.getText().toString().isEmpty()
                || krajText.getText().toString().isEmpty()){
            spremiBtn.setEnabled(false);
        } else {
            spremiBtn.setEnabled(true);
        }
    }

    // private void initImageBitmaps(){
    //    Log.d(TAG, "initImageBitmaps: preparing bitmaps");
//
  //      mImageUrls.add("https://www.incimages.com/uploaded_files/image/970x450/laugh1_24055.jpg");
   //     mNames.add("Pre Smišno");
     //   mImageUrls.add("https://www.scienceabc.com/wp-content/uploads/2016/01/shutterstock_338992685.jpg");
   //     mNames.add("Znači NeMogu");
   //     mImageUrls.add("https://cms.qz.com/wp-content/uploads/2016/07/rtx2c9ws.jpg?quality=75&strip=all&w=1900&h=1068");
    //    mNames.add("Ha Hagaha");
   //     mImageUrls.add("https://gabrielhalpern.com/wp-content/uploads/2016/04/laughing.png");
 //       mNames.add("Ja Umiren");


   //     initRecycleView();
 //   }

 //   private void initRecycleView(){
   //     Log.d(TAG, "initRecycleView: init recycleview");
     //   RecyclerView recyclerView = findViewById(R.id.recycle_view); //isto
  //      recyclerView.setLayoutManager(new LinearLayoutManager(this));//isto
   //     DostupniOrganizatoriAdapter adapter  = new DostupniOrganizatoriAdapter(this,users,storageRef); //isto
     //   recyclerView.setAdapter(adapter);//isto
 //   }
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
