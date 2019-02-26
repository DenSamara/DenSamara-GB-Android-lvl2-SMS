package ru.home.denis.konovalov.mysmsapplication.adapter;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class MySmsLookup extends ItemDetailsLookup {
    private final RecyclerView recyclerView;

    public MySmsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null){
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof MySmsAdapter.SmsViewHolder) {
                return ((MySmsAdapter.SmsViewHolder)viewHolder).getItemDetails();
            }
        }
        return null;
    }
}
