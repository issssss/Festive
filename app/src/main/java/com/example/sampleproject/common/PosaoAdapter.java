package com.example.sampleproject.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sampleproject.R;

import java.util.ArrayList;

public class PosaoAdapter extends RecyclerView.Adapter<PosaoAdapter.PosaoViewHolder> {
    private ArrayList<PosaoItem> posloviList;
    private PosaoAdapter.OnItemClickListener pListener;
    private String slika;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onAddClick(int position);
    }

    public void setOnItemClickListener(PosaoAdapter.OnItemClickListener listener){
        pListener = listener;
    }

    public class PosaoViewHolder extends RecyclerView.ViewHolder{
        public TextView pTextView;
        public ImageView pAddImage;

        public PosaoViewHolder(@NonNull View itemView, final PosaoAdapter.OnItemClickListener listener) {
            super(itemView);
            pTextView = itemView.findViewById(R.id.textView);
            pAddImage = itemView.findViewById(R.id.uvediPosao);
            if (slika.equals("da")) pAddImage.setVisibility(View.VISIBLE);

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

            pAddImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onAddClick(position);
                        }
                    }
                }
            });
        }
    }

    public PosaoAdapter(ArrayList<PosaoItem> pList, String slika){
        posloviList = pList; this.slika=slika;
    }

    @NonNull
    @Override
    public PosaoAdapter.PosaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.posao_item,parent,false);
        return new PosaoAdapter.PosaoViewHolder(v, pListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PosaoAdapter.PosaoViewHolder holder, int position) {
        PosaoItem currentItem = posloviList.get(position);
        holder.pTextView.setText(currentItem.getText());
    }

    @Override
    public int getItemCount() {
        return posloviList.size();
    }
}
