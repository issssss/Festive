package com.example.sampleproject.common;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sampleproject.R;
import com.example.sampleproject.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostavkeFragment extends Fragment {

    View view;
    TextView postavkeText;
    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String username;
    private Map userData;
    private List<Map> users;
    private String email;
    private static final int REQUEST_CODE= 1;



    public PostavkeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_postavke_fragmet, container, false);
        postavkeText=view.findViewById(R.id.postavkeText);
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
                                //imePrezime.setText(userData.get("name").toString()+ " " + userData.get("surname").toString());
                            }
                        }
                    });
                }
            }
        });

        postavkeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra("userData", (Serializable) userData);
                getActivity().startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return view;
    }


}
