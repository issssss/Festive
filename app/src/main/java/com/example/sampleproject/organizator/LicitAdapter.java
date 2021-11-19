package com.example.sampleproject.organizator;

import android.content.Context;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LicitAdapter extends RecyclerView.Adapter<LicitAdapter.ViewHolder>{

    private List<Map> dostupneLic= new ArrayList<>();
    private static final String TAG = "LicitAdapter";
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private FirebaseUser fuser;
    private DocumentReference dr;
    private FirebaseStorage storage;
    private Context mContext;
    private View view;
    private List<Map> licit;
    private List<Map> dogadajiOrg;
    private CollectionReference cr;
    private String mail;
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm");



    //Constructor
    public LicitAdapter(Context mContext, List<Map> licit, StorageReference storageRef, String mail, List<Map> dogadajiOrg) {

        this.storageRef = storageRef;
        this.mContext = mContext;
        this.dogadajiOrg=dogadajiOrg;
        this.licit=licit;
        this.mail=mail;


        for(Map lic : licit){
            if((lic.get("aktivnost")).equals("true")&& !Arrays.asList(lic.get("prijave").toString().split(",")).contains(mail)) {

                int zastavica = 0;
                try {
                    for (Map dog : dogadajiOrg) {

                        if (lic.get("date").equals(dog.get("date"))) {

                            if (lic.get("end").toString().compareTo(dog.get("start").toString()) < 0 || lic.get("start").toString().compareTo(dog.get("end").toString()) > 0) {


                                continue;
                            } else {
                                zastavica = 1;
                                break;
                            }
                        }
                        ;


                    }
                } catch (Exception e) {
                    try {
                        if (Calendar.getInstance().getTime().compareTo(formatter.parse(lic.get("date").toString() + " " + lic.get("end").toString())) < 0)
                            this.dostupneLic.add(lic);

                        continue;
                    } catch (ParseException p) {
                        Log.d("Parseri", "ne valjaju");
                    }
                }
                if (zastavica == 0) {
                    try {

                        if (Calendar.getInstance().getTime().compareTo(formatter.parse(lic.get("date").toString() + " " + lic.get("end").toString())) < 0)
                            this.dostupneLic.add(lic);
                    } catch (ParseException p) {
                        Log.d("Parseri", "ne valjaju");
                    }
                }
            }

        }



    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profilna;
        TextView nameSurname;
        TextView dogadaj;
        TextView vrijeme;
        TextView prijavica;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilna = itemView.findViewById(R.id.profilna);
            nameSurname = itemView.findViewById(R.id.nameSurname);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            dogadaj=itemView.findViewById(R.id.dogadaj);
            vrijeme=itemView.findViewById(R.id.vrijeme);
            prijavica=itemView.findViewById(R.id.prijavica);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_licit_adapter,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBind: called");

        Map user = dostupneLic.get(position);
        storageRef.child("festival_logos/" + user.get("festival")).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(mContext.getApplicationContext())
                                .load(uri.toString())
                                .into(holder.profilna);
                    }
                });
        final String imePrezime = user.get("festival").toString();
        holder.nameSurname.setText(user.get("festival").toString());
        holder.dogadaj.setText(user.get("dogadaj").toString());
        holder.vrijeme.setText(user.get("date").toString()+" "+user.get("start").toString()+" "+user.get("end").toString());

        holder.prijavica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseFirestore.getInstance();
                cr = db.collection("licitacije_org");
                final String d = dostupneLic.get(position).get("dogadaj").toString();
                final String old = dostupneLic.get(position).get("prijave").toString();
                dr = cr.document(d);
                dr.update("prijave", old.equals("")?mail:old+","+mail);
                dostupneLic.remove(position);
                notifyItemRemoved(position);
                notifyItemChanged(position, dostupneLic.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dostupneLic.size();
    }

}
