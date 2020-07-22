package com.example.myapplication;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> implements ItemTouchHelperAdapter {

    private OnTaskClick onClick;
    private Context context;
    protected List<Task> mData;
    private LayoutInflater mInflater;
    ViewHolder viewHolder;

    public interface OnTaskClick{
        void OnClick(int position);
    }

    // data is passed into the constructor
    MyAdapter(Context context, OnTaskClick onClick) {
        this.mInflater = LayoutInflater.from(context);
        this.context=context;
        this.onClick=onClick;
        mData=new ArrayList<>();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.task, parent, false);
        this.viewHolder=new ViewHolder(view,onClick);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Task task = mData.get(position);
        holder.myTextView.setText(task.toString());
    }
    void setTasks(List<Task> tasks){
        if (mData.isEmpty()){
            mData = tasks;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    //item in the position is removed from the list
    Task removeItem(int position) {
        Task removed=mData.get(position);
        mData.remove(position);
        notifyItemRemoved(position);
        return removed;
    }
    public Context getContext(){
        return context;
    }

    //item is inserted to the list in a specific position
    void restoreItem(Task item, int position) {
        mData.add(position, item);
        notifyItemInserted(position);
    }
}
