package com.example.sampleproject.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PropAdapter extends RecyclerView.Adapter<PropAdapter.ViewHolder> {
    private static final String TAG = "PropAdapter";
    private ArrayList<FestItem> festivali = new ArrayList<>();
    private Context pContext;
    private PropAdapter.OnItemClickListener pListener;
    private StorageReference storageRef;
    private static int redniBroj=1;
    Map<String,Integer> redni = new HashMap<>();

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onAddClick(int position);
    }

    public void setOnItemClickListener(PropAdapter.OnItemClickListener listener){
        pListener = listener;
    }




    public PropAdapter(Context context, ArrayList<FestItem> festivals,  StorageReference storageReference){
        pContext=context;
        festivali.addAll(festivals);
        storageRef=storageReference;

   }


    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView textView;
        ConstraintLayout parentLayout;
        ImageView logo;
        TextView posao;

        public ViewHolder(@NonNull View itemView, final PropAdapter.OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.imeFest);
            logo=itemView.findViewById(R.id.festLogo);
           parentLayout = itemView.findViewById(R.id.parent_fest);
            posao=itemView.findViewById(R.id.posao);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fest_item,parent,false);
        return new ViewHolder(view, pListener);
    }


    @Override
    public int getItemCount() {
        return festivali.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final PropAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBind: called");
        //FestItem currentItem = festivali.get(position);
        //holder.textView.setText(currentItem.getText());
        final FestItem fest = festivali.get(position);
        Log.d(TAG, fest.getTextFest());

       // redni.put(fest.getTextPro(),1);

        holder.textView.setText(fest.getTextFest());
        if(!fest.getTextPos().equals("")){
            holder.posao.setText(fest.getTextPos());
        }
        storageRef.child("festival_logos/" + fest.getTextFest()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(pContext.getApplicationContext())
                                .load(uri.toString())
                                .into(holder.logo);
                    }
                });
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Propusnica.class);
                intent.putExtra("festi", fest.getTextFest());
                intent.putExtra("dogadaj i posao", fest.getTextPos());
                intent.putExtra("prostorija",fest.getTextPro());
                intent.putExtra("brOso", fest.getTextBrOso());
                intent.putExtra("brPos",fest.getTextBrPos());
                if (redni.containsKey(fest.getTextPro()))redni.put(fest.getTextPro(),redni.get(fest.getTextPro())+1);
                else  redni.put(fest.getTextPro(),1);
                intent.putExtra("redniBroj", redni.get(fest.getTextPro()));
                view.getContext().startActivity(intent);
            }
        });
    }
}

