package com.example.shortenrest;

import android.text.Editable;
import android.text.TextWatcher;
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

public class SnippetAdapter extends RecyclerView.Adapter<SnippetAdapter.AdapterViewHolder> {

    private ArrayList<SnippetCard> mArrayList;

    public ArrayList<SnippetCard> getmArrayList() {
        return mArrayList;
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {

        EditText snippetContent;
        ImageView removeSnippet;
        TextView snippetID;

        public AdapterViewHolder(View itemView) {
            super(itemView);

            snippetContent = itemView.findViewById(R.id.snippetContent);
            removeSnippet = itemView.findViewById(R.id.removeSnippet);
            snippetID = itemView.findViewById(R.id.snippetID);

        }
    } // end AdapterViewHolder

    public SnippetAdapter(ArrayList<SnippetCard> arrayList) {
        mArrayList = arrayList;

    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_card, parent, false);
        AdapterViewHolder vh = new AdapterViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterViewHolder holder, final int position) {
        //passing values
        final SnippetCard currentItem = mArrayList.get(position);

        holder.snippetContent.setText(currentItem.getSnippetContent());
        holder.snippetID.setText(currentItem.getSnippetID());
        holder.removeSnippet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAt(holder.getAdapterPosition()); //or positiion???
                Toast.makeText(v.getContext(), "Snippet row has been deleted successfully!", Toast.LENGTH_SHORT).show();

            }
        });

        holder.snippetContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentItem.setSnippetContent(s.toString());
                mArrayList.set(position, currentItem);
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
