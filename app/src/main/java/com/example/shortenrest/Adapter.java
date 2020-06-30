package com.example.shortenrest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {

    private ArrayList<ItemCard>  mArrayList;

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {

        EditText paramEdit;
        EditText valueEdit;
        TextView equal;

        public AdapterViewHolder(View itemView) {
            super(itemView);

            paramEdit = itemView.findViewById(R.id.param);
            valueEdit = itemView.findViewById(R.id.value);
            equal = itemView.findViewById(R.id.equal);

        }
    }

    public Adapter(ArrayList<ItemCard> arrayList) {
        mArrayList = arrayList;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        AdapterViewHolder vh = new AdapterViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        //passing values
        ItemCard currentItem = mArrayList.get(position);

        holder.paramEdit.setText(currentItem.getParamEdit());
        holder.valueEdit.setText(currentItem.getValueEdit());
        holder.equal.setText(currentItem.getEqual());
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }
}


