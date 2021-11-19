package com.example.sampleproject.izvodac;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.common.PrikazIzvodaca;
import com.example.sampleproject.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.example.sampleproject.organizator.OrganizatorActivity;

public class LicIzvAdapter extends RecyclerView.Adapter<LicIzvAdapter.ViewHolder>{

    private List<Map> dostupneLic= new ArrayList<>();
    private static final String TAG = "LicitAdapter";
    private Context mContext;
    private View view;
    private String role;
    private String mail;
    private FirebaseFirestore db;
    private CollectionReference cr;
    private DocumentReference dr;
    SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yy HH:mm");




    //Constructor
    public LicIzvAdapter(Context mContext, List<Map> licit, String role, String mail) {
        this.role=role;
        this.mContext = mContext;
        this.mail=mail;

        for(Map lic : licit){
            if (!Arrays.asList(lic.get("prijave").toString().split(",")).contains(mail))
                dostupneLic.add(lic);

        }



    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView uvediPosao;
        TextView textView;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            uvediPosao = itemView.findViewById(R.id.uvediPosao);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posao_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBind: called");

        Map user = dostupneLic.get(position);

        if (role.equals("produzi")) holder.uvediPosao.setVisibility(View.VISIBLE);
        final String imePrezime = user.get("name").toString();
        holder.textView.setText(imePrezime);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (role.equals("produzi")){
                    holder.uvediPosao.setVisibility(View.VISIBLE);
                    db = FirebaseFirestore.getInstance();
                    cr = db.collection("licitacije_izv");
                    final String d = dostupneLic.get(position).get("name").toString();
                    dr = cr.document(d);
                    Calendar c = Calendar.getInstance();
                    c.setTime(c.getTime());
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    dr.update("kraj", formatter.format(c.getTime()));
                    dostupneLic.remove(position);
                    notifyItemRemoved(position);
                    notifyItemChanged(position, dostupneLic.size());
                    Toast.makeText(view.getContext(), "Licitacija uspješno produžena!", Toast.LENGTH_SHORT).show();  // for testing purposes
                    Intent intent = new Intent(view.getContext(), OrganizatorActivity.class);
                    intent.putExtra("licitacija", d);
                    intent.putExtra("ime", mail);
                    view.getContext().startActivity(intent);
                }
                else if (role.equals("aktivne")){
                    Intent intent = new Intent(view.getContext(), PrikazIzvodaca.class);
                    intent.putExtra("prijave",dostupneLic.get(position).get("prijave").toString());
                    intent.putExtra("dogadaj",dostupneLic.get(position).get("name").toString());
                    view.getContext().startActivity(intent);
                }
                else if (role.equals("zavrsene")){
                    Intent intent = new Intent(view.getContext(), ZavrsenaLicitacija.class);
                    intent.putExtra("prijave",dostupneLic.get(position).get("prihvacene").toString());
                    intent.putExtra("dogadaj",dostupneLic.get(position).get("name").toString());
                    intent.putExtra("stvarnoDogadaj", dostupneLic.get(position).get("dogadaj").toString());
                    view.getContext().startActivity(intent);
                }
                else {
                    holder.uvediPosao.setVisibility(View.VISIBLE);
                    final String d = dostupneLic.get(position).get("name").toString();
                    final String old = dostupneLic.get(position).get("prijave").toString();
                    Intent intent = new Intent(view.getContext(), Ponuda.class);
                    intent.putExtra("licitacija", d);
                    intent.putExtra("ime", mail);
                    intent.putExtra("old", old);
                    intent.putExtra("d", d);
                    view.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dostupneLic.size();
    }

}