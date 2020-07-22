package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    List<String> data;
    public Context context;
    OnTaskClick onTaskClick;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    public interface OnTaskClick{
        void OnClick(int position);
    }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textView;
        private SimpleAdapter.OnTaskClick onTaskClick;


        public ViewHolder(View itemView,OnTaskClick onTaskClick) {
            super(itemView);
            textView=itemView.findViewById(R.id.taskText);
            this.onTaskClick=onTaskClick;
            itemView.setOnClickListener(this);
        }
        public void onClick(View v) {
                onTaskClick.OnClick(getAdapterPosition());
            }
    }
    public SimpleAdapter (Context context,List<String> list,OnTaskClick onTaskClick){
        this.data=list;
        this.context=context;
        this.onTaskClick=onTaskClick;
    }
    @NonNull
    @Override
    public SimpleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.subtask, parent, false);
        return new ViewHolder(v,onTaskClick);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleAdapter.ViewHolder viewHolder, int position) {
        viewHolder.textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public Context getContext(){
        return context;
    }

    String removeItem(int position) {
        String removed=data.get(position);
        data.remove(position);
        notifyItemRemoved(position);
        return removed;
    }

    void restoreItem(String item, int position) {
        data.add(position,item);
        notifyItemInserted(position);
    }
}