package com.example.sampleproject.voditelj;
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
import com.example.sampleproject.organizator.OrganizatorIzvana;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterOrganizatora extends RecyclerView.Adapter<AdapterOrganizatora.MyViewHolder>{

    private List<String> prijave = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference cr;
    private DocumentReference dr;
    private StorageReference storageRef;
    private Context mContext;
    private String dogadaj;
    private View view;


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
    public AdapterOrganizatora(List<String> prijave, List<Map> dogadaji, Map<String, Object> doga, Context mContext, StorageReference storageRef) {
        this.dogadaj=doga.get("name").toString();
        this.mContext=mContext;
        this.storageRef = storageRef;
        if (!prijave.isEmpty()){
            for (String s : prijave){
                if (!s.equals("")){
                    int zastavica=0;
                        for (Map dog : dogadaji) {
                            try {
                                if (dog.get("organizator").equals(s)) {
                                    if (dog.get("date").equals(doga.get("date"))) {

                                        if (doga.get("end").toString().compareTo(dog.get("start").toString()) < 0 || doga.get("start").toString().compareTo(dog.get("end").toString()) > 0) {


                                            continue;
                                        } else {
                                            zastavica = 1;
                                            break;
                                        }
                                    }
                                }
                                ;
                            } catch (Exception e) {


                                continue;
                            }
                        }
                    if (zastavica==0){
                        this.prijave.add(s);
                        Log.d("prijave", s);
                    }

                }

            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_organizatora, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterOrganizatora.MyViewHolder holder, final int position) {

        String prijava = prijave.get(position);
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
        final String imePrezime = prijava;
        holder.nameSurname.setText(imePrezime);

        holder.pregled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OrganizatorIzvana.class);
                intent.putExtra("mail", imePrezime);
                Log.d("mail", imePrezime);
                Log.d("dogadaj", dogadaj);
                intent.putExtra("dogadaj", dogadaj);
                view.getContext().startActivity(intent);
            }
        });
        holder.odabir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cr = firestore.collection("licitacije_org");
                dr = cr.document(dogadaj);
                dr.update("aktivnost", "false");
                cr = firestore.collection("dogadaji");
                dr = cr.document(dogadaj);
                dr.update("organizator", imePrezime);
                Intent intent = new Intent(view.getContext(), PrikazOrganizatora.class);
                intent.putExtra("sanja", "da");
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return prijave.size();
    }



}