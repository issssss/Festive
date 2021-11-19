package com.example.sampleproject.organizator;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OrganizatorHome extends Fragment {

    private View view;

    protected TextView imePrezime;
    protected ImageView profilePicture;
    protected RecyclerView popisFest;
    protected TextView mojiFest;
    protected TextView komentari;
    protected RecyclerView popisKom;
    private FirebaseFirestore db;
    private FirebaseFirestore db2;
    private FirebaseUser fuser;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DocumentReference dr;
    private String username;
    private Map userData;
    private List<Map> dogadaji;
    private List<SpecItem> komentarcici;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.organizator_home, container, false);

        profilePicture = view.findViewById(R.id.profileImage);
        mojiFest = view.findViewById(R.id.mojiFestivaliOrg);
        popisFest = view.findViewById(R.id.popisFestivalaOrg);
        komentari = view.findViewById(R.id.mojiKomentari);
        popisKom = view.findViewById(R.id.popisKomentara);
        imePrezime = view.findViewById(R.id.imePrezime);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(getActivity(), fuser.getEmail(), Toast.LENGTH_SHORT).show();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        db = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();

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
                                imePrezime.setText(userData.get("name").toString()+ " " + userData.get("surname").toString());
                            }
                        }
                    });
                }
            }
        });
        db2.collection("dogadaji").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){        Log.d(TAG, "usah u firestore festivale");
                    dogadaji = new ArrayList<>();
                    komentarcici = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        if (documentSnapshot.getData().get("organizator").toString().equals(fuser.getEmail())) {
                            dogadaji.add(documentSnapshot.getData());
                            try {
                                komentarcici.add(new SpecItem(documentSnapshot.getData().get("komentar").toString()));
                            } catch (Exception e) {

                            }
                        }
                    }
                    initRecycleView();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Couldn't fetch documents", Toast.LENGTH_SHORT).show();
                }
            }
        });

        storageRef.child("profile_pictures/" + fuser.getEmail()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity().getApplicationContext()).load(uri.toString()).into(profilePicture);
            }
        });

        return view;

    }
    private void initRecycleView(){
        Log.d(TAG, "initRecycleView: init recycleview");
        RecyclerView recyclerView = view.findViewById(R.id.popisFestivalaOrg); //isto
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));//isto
        DogadajiAdapter adapter  = new DogadajiAdapter(getActivity().getApplicationContext(), dogadaji, storageRef, fuser.getEmail(), "ne"); //isto
        recyclerView.setAdapter(adapter);//isto

        RecyclerView recyclerView2 = view.findViewById(R.id.popisKomentara); //isto
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));//isto
        SpecAdapter adapter2 = new SpecAdapter((ArrayList<SpecItem>) komentarcici); //isto
        recyclerView2.setAdapter(adapter2);
    }
}

