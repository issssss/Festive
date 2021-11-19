package com.example.sampleproject.izvodac;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sampleproject.R;

import java.util.ArrayList;

public class SpecAdapter extends RecyclerView.Adapter<SpecAdapter.SpecViewHolder> {
    private ArrayList<SpecItem> mSpecList;
    private SpecAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(SpecAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    public static class SpecViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ImageView mDeleteImage;

        public SpecViewHolder(@NonNull View itemView, final SpecAdapter.OnItemClickListener listener) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.textView);
            mDeleteImage = itemView.findViewById(R.id.ukloniSpec);

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

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public SpecAdapter(ArrayList<SpecItem> specList){
        mSpecList = specList;
    }

    @NonNull
    @Override
    public SpecAdapter.SpecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spec_item,parent,false);
        return new SpecAdapter.SpecViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecAdapter.SpecViewHolder holder, int position) {
        SpecItem currentItem = mSpecList.get(position);
        holder.mTextView.setText(currentItem.getText());
    }

    @Override
    public int getItemCount() {
        return mSpecList.size();
    }
}
