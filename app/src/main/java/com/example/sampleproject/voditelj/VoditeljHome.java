package com.example.sampleproject.voditelj;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sampleproject.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import static android.app.Activity.RESULT_OK;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class VoditeljHome extends Fragment {
    private static final int REQUEST_CODE= 1;
    private View view;
    protected Button btnNoviFest;
    protected ImageView profilePicture;
    protected TextView imePrezime;
    protected TextView mojiFestivali;
    protected ListView festivali;
    private Button btnSettings;
    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String username;
    private Map userData;
    private List<Map> users;
    private String email;
    //private Map userData; //kod prvog stvaranja fragmenta se sve sprema ovdije


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.voditelj_home, container, false);

        btnNoviFest = view.findViewById(R.id.button);
        profilePicture = view.findViewById(R.id.profileImage);
        imePrezime = view.findViewById(R.id.imePrezime);
        mojiFestivali = view.findViewById(R.id.textView7);



        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        email = fuser.getEmail();
        Toast.makeText(getActivity(), email, Toast.LENGTH_SHORT).show();

        db = FirebaseFirestore.getInstance();
        dr = db.collection("emails").document(email);
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
                                userData.put("username", username);
                                userData.put("email", email);
                                imePrezime.setText(userData.get("name").toString()+ " " + userData.get("surname").toString());
                            }
                        }
                    });
                }
            }
        });

        storageRef.child("profile_pictures/" + email).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity().getApplicationContext())
                                .load(uri.toString())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(profilePicture);
                    }
                });

        db.collection("festivali").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){        Log.d(TAG, "usah u firestore festivale");
                    users = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        users.add(documentSnapshot.getData());
                    }
                    initRecycleView();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNoviFest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoviFestivalActivity.class);
                intent.putExtra("voditelj", fuser.getEmail());
                startActivity(intent);
            }
        });

        return view;
    }

    //privremeno
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            updatePicture();
        }
        //ako se u postavkama promijeni broj telefona, dohvacamo ga iz tog activityja umjesto iz baze podataka
        if(data!=null){
            userData.replace("phone number", data.getStringExtra("phone number"));
        }
    }

    //ako se u postavkama promijenila slika
    public void updatePicture(){
        storageRef.child("profile_pictures/" + email).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity().getApplicationContext())
                                .load(uri.toString())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(profilePicture);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRecycleView(){
        Log.d(TAG, "initRecycleView: init recycleview");
        RecyclerView recyclerView = view.findViewById(R.id.recycle_view); //isto
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));//isto
        FestivaliAdapter adapter  = new FestivaliAdapter(getActivity().getApplicationContext(), users, storageRef, fuser.getEmail()); //isto
        recyclerView.setAdapter(adapter);//isto
    }
}
