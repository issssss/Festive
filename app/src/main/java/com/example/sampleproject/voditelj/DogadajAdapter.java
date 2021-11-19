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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DogadajAdapter extends RecyclerView.Adapter<DogadajAdapter.ViewHolder> {

    private List<Map> userList = new ArrayList<>();
    private static final String TAG = "DogadajAdapter";
    private StorageReference storageRef;
    private Context mContext;
    private View view;

    //Constructor
    public DogadajAdapter( List<Map> userMap, String festival) {
        Log.d(TAG, "FestivaliAdapter: usa u costruktor" + festival);

        for(Map user : userMap){
            Log.d(TAG, "FestivaliAdapter: " + user.get("festival"));
            if((user.get("festival")).equals(festival)) this.userList.add(user);
        }
       // this.userList= userMap;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameSurname,datumText,prostorija;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSurname = itemView.findViewById(R.id.nazivText);
            datumText = itemView.findViewById(R.id.datumText);
            prostorija = itemView.findViewById(R.id.prostorija);
            parentLayout = itemView.findViewById(R.id.parent_layout);
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
                Log.d(TAG, "onClick: clicked on" + imePrezime);
                Toast.makeText(view.getContext(),imePrezime,Toast.LENGTH_SHORT).show();

              //  Intent intent = new Intent(view.getContext(), FestivalActivity.class);
              //  intent.putExtra("festival", imePrezime);
              //  view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
