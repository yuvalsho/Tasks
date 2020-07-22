package com.example.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView myTextView;
    private MyAdapter.OnTaskClick onClick;

    ViewHolder(View itemView, MyAdapter.OnTaskClick onClick) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.taskText);
        this.onClick=onClick;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onClick.OnClick(getAdapterPosition());
    }
}