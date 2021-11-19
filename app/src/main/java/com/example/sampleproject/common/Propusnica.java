package com.example.sampleproject.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Propusnica extends AppCompatActivity {
    private static final String TAG = "propusnica";
    ImageView qrimg; ImageView logo; ImageView profilna;
    TextView festival; TextView uloga; TextView ime; TextView prezime; TextView dogPos; TextView brPos; TextView prostorija;
    TextView posao;
    String inputvalue;
    Button saveBtn;
    OutputStream outputStream;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    RelativeLayout layout;
    String role;

    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private DocumentReference dr, dr2;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String username;
    private Map userData;
    private TextView brOsoba;
    private static Map <String,Integer> redniBroj;
    private int redniBr=1;
    private String redniBrPosla;
    private String novi;
    private String dogadaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_propusnica);
        //redniBroj= new HashMap<>();




        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
       // final String imefest= getIntent().getStringExtra(festi);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(getActivity(), fuser.getEmail(), Toast.LENGTH_SHORT).show();

        db = FirebaseFirestore.getInstance();
        dr = db.collection("emails").document(fuser.getEmail());
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    Map map = doc.getData();
                    username = map.get("username").toString();
                    role=map.get("role").toString();

                    switch(role) {
                        case "voditelj":
                            setContentView(R.layout.activity_propusnica_vod);
                            Log.d(TAG, "onComplete: vod");
                            break;
                        case "organizator": setContentView(R.layout.activity_propusnica_org);
                            Log.d(TAG, "onComplete: org"); break;
                        case "izvodac": setContentView(R.layout.activity_propusnica_izv);
                            Log.d(TAG, "onComplete: izv"); break;
                    }


                    layout = findViewById(R.id.iskaznica);

                    brOsoba= findViewById(R.id.brOsobe);
                    qrimg = findViewById(R.id.qrcode);
                    logo = findViewById(R.id.logo);
                    profilna = findViewById(R.id.profilna);
                    festival = findViewById(R.id.festival);
                    //uloga = findViewById(R.id.uloga);
                    ime  = findViewById(R.id.ime);
                    prezime  = findViewById(R.id.prezime);
                    saveBtn = findViewById(R.id.save);
                    prostorija=findViewById(R.id.prostorija);
                    saveBtn.setEnabled(false);
                    if(!getIntent().getStringExtra("dogadaj i posao").equals("")) {
                        // dogPos.setText(getIntent().getStringExtra("dogadaj i posao"));
                        dogadaj = getIntent().getStringExtra("dogadaj i posao");


                        //dogPos=findViewById(R.id.posaoDog);
                        //brPos=findViewById(R.id.brPos);

                        //potrebno vrijeme dogadanja posla tj dogadaja? nah
                        //posao=findViewById(R.id.Posao);

                        fuser = FirebaseAuth.getInstance().getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        dr = db.collection("licitacije_izv").document(dogadaj);
                        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    Map map = doc.getData();
                                    try {
                                        novi = map.get("brIskaznica").toString();
                                    }catch (Exception e ){
                                        novi = map.get("brOsoba").toString();
                                    }
                                    Log.d(TAG, "onComplete: novi = " + novi);
                                    Log.d(TAG, "onComplete: brosoba = " + getIntent().getStringExtra("brOso"));

                                    saveBtn.setText("SAVE (" + novi + ")");
                                    int i = Integer.parseInt(getIntent().getStringExtra("brOso"))-Integer.parseInt(novi)+1;
                                    if(i>Integer.parseInt(getIntent().getStringExtra("brOso")))
                                        i=Integer.parseInt(getIntent().getStringExtra("brOso"));
                                    Log.d(TAG, "onComplete: i = " + i);
                                    redniBr=i;

                                    brOsoba.setText(i + "/" + getIntent().getStringExtra("brOso"));
                                    Log.d(TAG, "onComplete: i = " + i);

                                    if (novi.equals("0")) {
                                        saveBtn.setEnabled(false);
                                        Toast.makeText(getApplicationContext(), "Dosegnut moguci broj ispisa propusnica", Toast.LENGTH_SHORT).show();

                                    }
                                    dr2 = db.collection("users").document(username);
                                    dr2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                userData = doc.getData();

                                                ime.setText(userData.get("name").toString());
                                                prezime.setText(userData.get("surname").toString());
                                                //uloga.setText(role);
                                                festival.setText(getIntent().getStringExtra("festi")); //link sa pravin poljem festivala
                                                storageRef.child("profile_pictures/" + fuser.getEmail()).getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                Glide.with(getApplicationContext())
                                                                        .load(uri.toString())
                                                                        .into(profilna);
                                                                storageRef.child("festival_logos/" + getIntent().getStringExtra("festi")).getDownloadUrl()
                                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                            @Override
                                                                            public void onSuccess(Uri uri) {
                                                                                Glide.with(getApplicationContext())
                                                                                        .load(uri.toString())
                                                                                        .into(logo);
                                                                                if(!getIntent().getStringExtra("brPos").equals("")){
                                                                                    // posao.setText("Posao");
                                                                                    //brPos.setText(getIntent().getStringExtra("brPos"));
                                                                                    redniBrPosla = getIntent().getStringExtra("brPos");
                                                                                }
                                                                                if(!getIntent().getStringExtra("prostorija").equals("")){
                                                                                    prostorija.setText(getIntent().getStringExtra("prostorija"));
                                                                                    //    if(!redniBroj.containsKey(prostorija.getText().toString()))redniBroj.put(prostorija.getText().toString(),1);
                                                                                }
                                                                                Log.d(TAG, "onComplete: redniBroj=" + getIntent().getIntExtra("redniBroj",0));
                                                                                //redniBr=getIntent().getIntExtra("redniBroj",0);

                                                                                // novi = getIntent().getStringExtra("brOso");
                                                                                // saveBtn.setText("SAVE ("+ novi + ")");

                                                                                if(!getIntent().getStringExtra("brOso").equals(""))
                                                                                    //              if (redniBr <= Double.parseDouble(getIntent().getStringExtra("brOso"))){
                                                                                    brOsoba.setText(redniBr + "/" + getIntent().getStringExtra("brOso"));
                                                                                //          } else {
                                                                                //            saveBtn.setEnabled(false);
                                                                                //          Toast.makeText(getApplicationContext(), "Dosegnut moguci broj ispisa propusnica", Toast.LENGTH_SHORT).show();

                                                                                //     }

                                                                                if (!novi.equals("0")) saveBtn.setEnabled(true);
                                                                                qrGenerator();
                                                                            }
                                                                        });
                                                            }
                                                        });

                                                //posao.setText("");



                                            }
                                        }
                                    });

                                }
                            }
                        });

                    }

                    else {
                        dr2 = db.collection("users").document(username);
                        dr2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    userData = doc.getData();

                                    ime.setText(userData.get("name").toString());
                                    prezime.setText(userData.get("surname").toString());
                                    //uloga.setText(role);
                                    festival.setText(getIntent().getStringExtra("festi")); //link sa pravin poljem festivala
                                    storageRef.child("profile_pictures/" + fuser.getEmail()).getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Glide.with(getApplicationContext())
                                                            .load(uri.toString())
                                                            .into(profilna);
                                                    storageRef.child("festival_logos/" + getIntent().getStringExtra("festi")).getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    Glide.with(getApplicationContext())
                                                                            .load(uri.toString())
                                                                            .into(logo);
                                                                }
                                                            });
                                                    qrGenerator();
                                                    saveBtn.setEnabled(true);
                                                }
                                            });
                                }
                            }
                        });




                    }

                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            qrGenerator();
                            LayoutToBitmapToPDF(layout);


                            if(!getIntent().getStringExtra("dogadaj i posao").equals("")) {
                                int broj= Integer.parseInt(novi) -1;
                                novi= "" + broj ;

                            //redniBroj.replace(prostorija.getText().toString(),redniBroj.get(prostorija.getText().toString())+1); //di cu spremat redni brojjjjjj

                                CollectionReference cr = db.collection("licitacije_izv");
                                cr.document(dogadaj).update("brIskaznica", novi);
                                saveBtn.setText("SAVE ("+ novi + ")");
                                int i = Integer.parseInt(getIntent().getStringExtra("brOso"))-broj+1;
                                if(i>Integer.parseInt(getIntent().getStringExtra("brOso")))
                                    i=Integer.parseInt(getIntent().getStringExtra("brOso"));
                                Log.d(TAG, "onClick: i= " + i);
                                redniBr=i;
                                if(!getIntent().getStringExtra("brOso").equals(""))
                                    brOsoba.setText(i + "/" + getIntent().getStringExtra("brOso"));

                                if(novi.equals("0")){
                                    saveBtn.setEnabled(false);
                                    Toast.makeText(getApplicationContext(), "Dosegnut moguci broj ispisa propusnica", Toast.LENGTH_SHORT).show();

                                }


                        }}
                    });


                }
            }
        });





    }

    public static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void qrGenerator() {
        if(!role.equals("izvodac"))
        inputvalue = getMd5( username
                                + festival.getText().toString().trim()); //korisnicko ime i festival
        else  inputvalue = getMd5( username
                + festival.getText().toString().trim()
                +redniBr
                +redniBrPosla); //korisnicko ime i festival + redni br osobe i redni br posla


        if(inputvalue.length()>0){
            WindowManager manager =(WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerdimension = width<height ? width : height;
            smallerdimension = smallerdimension*3/4;
            qrgEncoder = new QRGEncoder(inputvalue, null, QRGContents.Type.TEXT,smallerdimension);
            try{
                bitmap=qrgEncoder.encodeAsBitmap();
                qrimg.setImageBitmap(bitmap);

            }catch (com.google.zxing.WriterException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    private void LayoutToBitmapToPDF(RelativeLayout screen) {
        //layout to bitmap
        screen.setDrawingCacheEnabled(true);
        screen.buildDrawingCache();
        Bitmap bm = screen.getDrawingCache();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //stvori file datoteku
        File slika = new File(getExternalFilesDir(null),"slike");
        slika.mkdir();
        //stvori file u kojem ce bit slika
        File img001 = new File(slika, System.currentTimeMillis()+".pdf");

        //img to pdf
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(img001));
        } catch (DocumentException e) { e.printStackTrace();
        } catch (FileNotFoundException e) { e.printStackTrace();
        }
        document.open();
        Image myImg = null;
        try {
            myImg = Image.getInstance(bytes.toByteArray());
        } catch (BadElementException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        //prilagodavanje velicine slike za pdf dokument
        float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0)
                / myImg.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
        myImg.scalePercent(scaler);
        myImg.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
        try {
            document.add(myImg);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();
    }
}
