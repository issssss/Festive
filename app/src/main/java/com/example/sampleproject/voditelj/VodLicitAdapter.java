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
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class VodLicitAdapter extends RecyclerView.Adapter<VodLicitAdapter.ViewHolder>{

    private List<Map> dostupneLic= new ArrayList<>();
    private static final String TAG = "LicitAdapter";
    private StorageReference storageRef;
    private Context mContext;
    private View view;
    private List<String> festivaliOrg;
    private String tkoSam;
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm");


    //Constructor
    public VodLicitAdapter(Context mContext, List<Map> licit, StorageReference storageRef, List<String> festivaliOrg, String tkoSam) {

        this.storageRef = storageRef;
        this.tkoSam=tkoSam;
        this.mContext = mContext;
        this.festivaliOrg=festivaliOrg;

        for(Map lic : licit){
            if (tkoSam.equals("da")) {
                try {
                    if (festivaliOrg.contains(lic.get("festival").toString()) && lic.get("aktivnost").equals("true") && Calendar.getInstance().getTime().compareTo(formatter.parse(lic.get("date").toString() + " " + lic.get("start").toString())) < 0) {
                        dostupneLic.add(lic);
                    }
                } catch (ParseException e) {
                    Log.d("Što kaže parser?", "Neće moći ove noći!");
                }
            }
            else dostupneLic.add(lic);


        }



    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profilna;
        TextView nameSurname;
        TextView dogadaj;
        TextView prijava;
        TextView vrijeme;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilna = itemView.findViewById(R.id.profilna);
            nameSurname = itemView.findViewById(R.id.nameSurname);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            dogadaj=itemView.findViewById(R.id.dogadaj);
            vrijeme=itemView.findViewById(R.id.vrijeme);
            prijava=itemView.findViewById(R.id.prijavica);

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBind: called");

        final Map user = dostupneLic.get(position);
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
        if (tkoSam.equals("da")) holder.dogadaj.setText(user.get("dogadaj").toString());
        else holder.dogadaj.setText(user.get("name").toString());
        if (tkoSam.equals("da")) holder.prijava.setText("PREGLEDAJ PRIJAVE");
        else holder.prijava.setText("ODI NA PROFIL ORGANIZATORA");
        holder.vrijeme.setText(user.get("date").toString()+" "+user.get("start").toString()+" "+user.get("end").toString());
        final String prijave = tkoSam.equals("da")? user.get("prijave").toString():"";
        final String dogadaj = tkoSam.equals("da")?user.get("dogadaj").toString():"";

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tkoSam.equals("da")) {
                    Intent intent = new Intent(view.getContext(), PrikazOrganizatora.class);
                    intent.putExtra("lic", prijave);
                    intent.putExtra("licit", dogadaj);
                    view.getContext().startActivity(intent);
                }else {
                    try {
                        if (!user.get("organizator").toString().isEmpty()){
                            Intent intent = new Intent(view.getContext(), OrganizatorIzvana.class);
                            intent.putExtra("dogadaj", user.get("name").toString());
                            Log.d("dpgadaj", user.get("name").toString());
                            Log.d("mail", user.get("organizator").toString());
                            intent.putExtra("mail", user.get("organizator").toString());
                            view.getContext().startActivity(intent);
                        }

                    } catch (Exception e){
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dostupneLic.size();
    }

}
