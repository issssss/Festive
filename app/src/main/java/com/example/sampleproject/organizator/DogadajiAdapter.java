package com.example.sampleproject.organizator;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DogadajiAdapter extends RecyclerView.Adapter<DogadajiAdapter.ViewHolder>{

    private List<Map> userList= new ArrayList<>();
    private static final String TAG = "DostupniOrganizatoriAdapter";
    private StorageReference storageRef;
    private Context mContext;
    private View view;
    private String izvana;

    public DogadajiAdapter(Context mContext, List<Map> userMap, StorageReference storageRef, String organizator, String izvana) {
        Log.d(TAG, "DogadajiAdapter: usa u costruktor" + organizator);
        for(Map user : userMap){
            Log.d(TAG, "DogadajiAdapter: " + user.get("organizator"));
            try{
                if(user.get("organizator").equals(organizator)) this.userList.add(user);
            }catch (Exception e ){

            }
        }

        this.storageRef = storageRef;
        this.mContext = mContext;
        this.izvana=izvana;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profilna;
        TextView nameSurname, datumText, prostorija;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilna = itemView.findViewById(R.id.profilna);
            nameSurname = itemView.findViewById(R.id.nazivText);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            datumText = itemView.findViewById(R.id.datumText);
            prostorija = itemView.findViewById(R.id.prostorija);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dogadaj_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBind: called");

        Map user = userList.get(position);

        final String imePrezime = user.get("name").toString();
        holder.nameSurname.setText(user.get("name").toString());
        holder.datumText.setText(user.get("date").toString());
        holder.prostorija.setText(user.get("place").toString());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (izvana.equals("ne")) {

                    Log.d(TAG, "onClick: clicked on" + imePrezime);
                    Toast.makeText(mContext, imePrezime, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(view.getContext(), OrganizatorPoslovi.class);
                    intent.putExtra("dogadaj", imePrezime);
                    view.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
