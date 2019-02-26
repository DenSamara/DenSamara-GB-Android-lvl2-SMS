package ru.home.denis.konovalov.mysmsapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;
import ru.home.denis.konovalov.mysmsapplication.Global;
import ru.home.denis.konovalov.mysmsapplication.model.MySms;
import ru.home.denis.konovalov.mysmsapplication.R;

public class MySmsAdapter extends RecyclerView.Adapter<MySmsAdapter.SmsViewHolder> {
    private static final String TAG = MySmsAdapter.class.getSimpleName();

    private ArrayList<MySms> mItems;
    private static final byte TYPE_IN = 0, TYPE_OUT = 1;
    private SelectionTracker selectionTracker;

    public MySmsAdapter(ArrayList<MySms> items) {
        mItems = items;
    }

    public void setItems(ArrayList<MySms> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @Override
    public SmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_OUT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_my, parent, false);
            return new OutViewHolder(v);
        } else if (viewType == TYPE_IN) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_their, parent, false);
            return new InViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SmsViewHolder holder, int position) {
        MySms item = mItems.get(position);
        if (item != null)
            (holder).bind(item, selectionTracker != null ? selectionTracker.isSelected(item) : false);
    }

    @Override
    public int getItemViewType(int position) {
        MySms item = mItems.get(position);
        return item.getType() == MySms.InType.In ? TYPE_IN : TYPE_OUT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public abstract class SmsViewHolder extends RecyclerView.ViewHolder implements ViewHolderWithDetails {
        private TextView text;

        public SmsViewHolder(View view){
            super(view);
            //itemView наследуется из суперкласса
            text = itemView.findViewById(R.id.message);
        }

        public void bind(MySms item, boolean isActive){
            itemView.setActivated(isActive);
            if (text != null)
                text.setText(item.getMessage());
            else {
                Global.logE(TAG, "TextView \"message\" not found");
            }
        }

        @Override
        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new MySmsItemDetail(getAdapterPosition(), mItems.get(getAdapterPosition()));
        }
    }

    private class InViewHolder extends SmsViewHolder {
        private View avatar;
        private TextView username;
        private TextView timestamp;


        public InViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.username);
            timestamp = itemView.findViewById(R.id.timestamp);
        }

        public void bind(MySms item, boolean isActive) {
            super.bind(item, isActive);
            username.setText(item.getPhone());
            timestamp.setText(Global.timeLongToString(item.getTimeStamp()));
        }
    }

    private class OutViewHolder extends SmsViewHolder {
        public OutViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(MySms item, boolean isActive)
        {
            super.bind(item, isActive);
        }
    }
}
