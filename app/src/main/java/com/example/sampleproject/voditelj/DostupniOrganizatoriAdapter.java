package com.example.sampleproject.voditelj; //DostupniOrganizatoriAdapter.java

import android.content.Context;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DostupniOrganizatoriAdapter extends RecyclerView.Adapter<DostupniOrganizatoriAdapter.ViewHolder>{
    private static final String TAG = "DostupniOrganizatoriAdapter";
    private List<Map> userList = new ArrayList<>();
    private StorageReference storageRef;
    private FirebaseFirestore firestore;
    private CollectionReference cr;
    private DocumentReference dr;

   // private ArrayList<String> mImageNames;
  //  private ArrayList<String> mImages ;
    private Context mContext;

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

    public DostupniOrganizatoriAdapter(Context mContext, List<Map> userMap, StorageReference storageRef) {
        for(Map user : userMap){
            if(user.get("role").equals("organizator")) this.userList.add(user);
        }
        this.storageRef = storageRef;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.layout_listitem_org_za_dogadaj,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBind: called");

        Map user = userList.get(position);
        storageRef.child("test_za_org_vod/" + user.get("name") +".jpg").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(mContext.getApplicationContext())
                                .load(uri.toString())
                                .into(holder.profilna);
                    }
                });
        final String imePrezime = user.get("name").toString().concat(" ").concat(user.get("surname").toString());
        holder.nameSurname.setText(user.get("name").toString().concat(" ").concat(user.get("surname").toString()));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on" + imePrezime);
                Toast.makeText(mContext,imePrezime,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


}

