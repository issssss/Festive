package com.example.sampleproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    Button changePhoneNumber;
    Button changeProfilePicture;
    TextView namePlaceHolder;
    TextView usernamePlaceHolder;
    TextView emailPlaceHolder;
    TextView phoneNumberPlaceHolder;
    EditText phoneNumberInput;
    private CircleImageView profilePicture;
    private FirebaseFirestore db;
    private CollectionReference cr;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri selectedImage;
    private String email;
    private String username;
    private String newNumber;
    private Map userData;
    private boolean change;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }

        changePhoneNumber = findViewById(R.id.button12);
        changeProfilePicture = findViewById(R.id.button16);
        namePlaceHolder = findViewById(R.id.textView13);
        usernamePlaceHolder = findViewById(R.id.textView17);
        emailPlaceHolder = findViewById(R.id.textView18);
        phoneNumberPlaceHolder = findViewById(R.id.textView20);
        profilePicture = findViewById(R.id.imageView5);

        change = false;
        userData = (Map)getIntent().getSerializableExtra("userData");
        email = userData.get("email").toString();
        username = userData.get("username").toString();

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        emailPlaceHolder.setText(userData.get("email").toString());
        usernamePlaceHolder.setText(userData.get("username").toString());
        phoneNumberPlaceHolder.setText(userData.get("phone number").toString());
        namePlaceHolder.setText(userData.get("name").toString()+ " " + userData.get("surname").toString());
        storageRef.child("profile_pictures/" + email).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri.toString())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(profilePicture);
                    }
                });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Pohrani", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newNumber = phoneNumberInput.getText().toString();
                phoneNumberPlaceHolder.setText(newNumber);
                change = true;
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Odbaci", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        changePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberInput = new EditText(getApplicationContext());
                phoneNumberInput.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(phoneNumberInput);
                builder.create().show();
            }
        });

        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
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
                    profilePicture.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    change = true;
                }
        }
    }

    @Override
    public void onBackPressed() {
        if(change){
            final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle("");
            builder2.setMessage("Pohrani promjene");
            builder2.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(selectedImage!=null){
                        StorageReference ref = storageRef.child("profile_pictures/" + email);
                        ref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if(newNumber!=null){
                                    cr = db.collection("users");
                                    cr.document(username).update("phone number", newNumber);
                                    Intent intent = new Intent();
                                    intent.putExtra("phone number", newNumber);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    setResult(RESULT_OK, null);
                                    finish();
                                }
                            }
                        });
                    } else if (newNumber!=null){
                        cr = db.collection("users");
                        cr.document(username).update("phone number", newNumber);
                        Intent intent = new Intent();
                        intent.putExtra("phone number", newNumber);
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    } else {
                        finish();
                    }

                }
            });
            builder2.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder2.create().show();
        } else {
            finish();
        }
    }

}
