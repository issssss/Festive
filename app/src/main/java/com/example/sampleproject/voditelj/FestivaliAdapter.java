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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sampleproject.R;
import com.example.sampleproject.authentication.AdminAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FestivaliAdapter extends RecyclerView.Adapter<FestivaliAdapter.ViewHolder>{

    private List<Map> userList= new ArrayList<>();
    private static final String TAG = "DostupniOrganizatoriAdapter";
    private StorageReference storageRef;
    private Context mContext;
    private View view;


    //Constructor
    public FestivaliAdapter(Context mContext, List<Map> userMap, StorageReference storageRef, String voditelj) {
        Log.d(TAG, "FestivaliAdapter: usa u costruktor" + voditelj);
        for(Map user : userMap){
            Log.d(TAG, "FestivaliAdapter: " + user.get("voditelj"));
            if((user.get("voditelj")).equals(voditelj)) this.userList.add(user);
        }

        this.storageRef = storageRef;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profilna;
        TextView nameSurname;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilna = itemView.findViewById(R.id.profilna);
            nameSurname = itemView.findViewById(R.id.nameSurname);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_listitem_org_za_dogadaj,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBind: called");

        Map user = userList.get(position);
        storageRef.child("festival_logos/" + user.get("name")).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(mContext.getApplicationContext())
                                .load(uri.toString())
                                .into(holder.profilna);
                    }
                });
        final String imePrezime = user.get("name").toString();
        holder.nameSurname.setText(user.get("name").toString());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on" + imePrezime);
                Toast.makeText(mContext,imePrezime,Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(), FestivalActivity.class);
                intent.putExtra("festival", imePrezime);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
