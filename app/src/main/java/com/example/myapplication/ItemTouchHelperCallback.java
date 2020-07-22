package com.example.myapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private MyAdapter adapter;
    private Drawable icon;
    private ColorDrawable background;
    private TaskViewModel viewModel;
    private RecyclerView recyclerView;

    /**
     * creating an instance of the callback
     * @param adapter is the adapter of the list that the callback is attached to
     * @param viewModel is the viewModel that connects the activity of the list to the database
     * @param recyclerView is the view of the list that the callback is attached to
     */
    ItemTouchHelperCallback(MyAdapter adapter,TaskViewModel viewModel,RecyclerView recyclerView){
        this.adapter=adapter;
        this.recyclerView=recyclerView;
        this.viewModel=viewModel;
        icon= ContextCompat.getDrawable(adapter.getContext(),R.mipmap.delete);
        background=new ColorDrawable(Color.RED);
    }

    /**
     * creating the background of the list item when it is swiped
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX the x axis value of the swipe
     * @param dY the y axis value of the dargging
     * @param actionState
     * @param isCurrentlyActive
     */
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
            icon.setBounds(0,0,0,0);
        }

        background.draw(c);
        icon.draw(c);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    /**
     * adding delete and restore to swipe action
     * @param viewHolder connects the list to the database
     * @param i
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final int position = viewHolder.getAdapterPosition();
            final Task removed=adapter.removeItem(position);
            viewModel.delete(removed);
            Snackbar snackbar = Snackbar
                    .make(recyclerView, R.string.removed, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.restore, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(removed, position);
                    recyclerView.scrollToPosition(position);
                    viewModel.insert(removed);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
    }
}
