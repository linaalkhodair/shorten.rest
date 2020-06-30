package com.example.shortenrest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {

    private ArrayList<ItemCard>  mArrayList;

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {

        EditText paramEdit;
        EditText valueEdit;
        TextView equal;
        ImageView removeUtm;

        public AdapterViewHolder(View itemView) {
            super(itemView);

            paramEdit = itemView.findViewById(R.id.param);
            valueEdit = itemView.findViewById(R.id.value);
            equal = itemView.findViewById(R.id.equal);
            removeUtm = itemView.findViewById(R.id.removeUtm);



        }
    } // end AdapterViewHolder

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
    public void onBindViewHolder(@NonNull final AdapterViewHolder holder, final int position) {
        //passing values
        ItemCard currentItem = mArrayList.get(position);

        holder.paramEdit.setText(currentItem.getParamEdit());
        holder.valueEdit.setText(currentItem.getValueEdit());
        holder.equal.setText(currentItem.getEqual());
        holder.removeUtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAt(holder.getAdapterPosition()); //or positiion???
                Toast.makeText(v.getContext(), "UTM row has been deleted successfully!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void removeAt(int position) {
        mArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mArrayList.size());
    }
}

