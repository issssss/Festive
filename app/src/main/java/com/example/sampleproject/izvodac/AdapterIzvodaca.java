package com.example.sampleproject.izvodac;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterIzvodaca extends RecyclerView.Adapter<AdapterIzvodaca.MyViewHolder>{

    private List<String> prijave = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference cr;
    private DocumentReference dr;
    private StorageReference storageRef;
    private Context mContext;
    private Map<String,Object> dogadaj = new HashMap<>();
    private View view;
    private String[] raspad;


    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profilna;
        TextView nameSurname;
        TextView pregled;
        TextView odabir;
        RelativeLayout parentLayout;

        public MyViewHolder(View view) {
            super(view);
            nameSurname = view.findViewById(R.id.nameSurname);
            pregled = view.findViewById(R.id.pregled);
            odabir = view.findViewById(R.id.odabir);
            profilna = view.findViewById(R.id.profilna);
            parentLayout=view.findViewById(R.id.parent_layout);
        }
    }

    //Constructor
    public AdapterIzvodaca(List<String> prijave, Map<String, Object> dogadaj, Context mContext, StorageReference storageRef) {
        this.mContext=mContext;
        this.storageRef = storageRef;
        this.dogadaj.putAll(dogadaj);
        for (String prijava: prijave){
            if (!prijava.isEmpty())
            this.prijave.add(prijava);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_organizatora, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterIzvodaca.MyViewHolder holder, final int position) {

        final String prijava = prijave.get(position);
        if (Arrays.asList(dogadaj.get("prihvacene").toString().split(",")).contains(prijava)) holder.odabir.setVisibility(View.INVISIBLE);
        Log.d("Sanja", prijava);
        storageRef.child("profile_pictures/" + prijava).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(mContext.getApplicationContext())
                                .load(uri.toString())
                                .into(holder.profilna);
                    }
                });

        holder.nameSurname.setText(prijava);
        holder.pregled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), IzvodacIzvana.class);
                intent.putExtra("ime", prijava);
                intent.putExtra("imeLicitacije", dogadaj.get("name").toString());
                intent.putExtra("imeDogadaja", dogadaj.get("dogadaj").toString());

                view.getContext().startActivity(intent);
            }
        });
        holder.odabir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cr = firestore.collection("licitacije_izv");
                dr = cr.document(dogadaj.get("name").toString());
                dr.update("prihvacene", dogadaj.get("prihvacene").equals("")?prijava:dogadaj.get("prihvacene")+","+prijava);
                firestore.collection("ponude").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                raspad = documentSnapshot.getId().split(" ");
                                Log.d("raspad", raspad[raspad.length-1]);
                                if (prijava.equals(raspad[raspad.length-1]) && documentSnapshot.getId().equals(dogadaj.get("name").toString()+" "+raspad[raspad.length-1])){
                                    Log.d("Dodem do","vamo");
                                    if (dogadaj.get("cijena").toString().isEmpty() || dogadaj.get("cijena").toString().compareTo(documentSnapshot.getData().get("cijena").toString())>0){
                                        Log.d("Cak i do", "vamo");
                                        dr.update("cijena", documentSnapshot.getData().get("cijena").toString());
                                        dr.update("izvodac", raspad[raspad.length-1]);
                                        dr.update("brOsoba", documentSnapshot.getData().get("brOsoba").toString());
                                        dr.update("brIskaznica", documentSnapshot.getData().get("brOsoba").toString());
                                        Log.d("Ovo je dr",dr.getId());
                                    }
                                }

                            }
                        }
                    }
                });


                holder.odabir.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return prijave.size();
    }



}