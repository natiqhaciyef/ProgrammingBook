package com.natiqhaciyef.programmingbook;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.natiqhaciyef.programmingbook.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramHolder> {

    ArrayList<ProgramFeature> programsArrayList ;

    public ProgramAdapter (ArrayList<ProgramFeature> programsArrayList){
        this.programsArrayList = programsArrayList ;
    }

    @NonNull
    @Override
    public ProgramHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent ,false);
        return new ProgramHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramHolder holder, int position) {
        holder.binding.recyclerView.setText(programsArrayList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext() , ProgrammingLanguages.class);
                intent.putExtra("info", "existed");
                intent.putExtra("programId" , programsArrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return programsArrayList.size();
    }

    public class ProgramHolder extends RecyclerView.ViewHolder{
    private RecyclerRowBinding binding ;
        public ProgramHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding ;
        }
    }
}
