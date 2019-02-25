package ru.home.denis.konovalov.mysmsapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MySMSAdapter extends RecyclerView.Adapter {
    private static final String TAG = MySMSAdapter.class.getSimpleName();

    private ArrayList<MySMS> mItems;
    private static final byte TYPE_IN = 0, TYPE_OUT = 1;

    public MySMSAdapter(ArrayList<MySMS> items) {
        mItems = items;
    }

    public void setItems(ArrayList<MySMS> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MySMS item = mItems.get(position);
        if (item != null)
            if (holder instanceof InViewHolder)
                ((InViewHolder) holder).bind(item);
            else if (holder instanceof OutViewHolder)
                ((OutViewHolder) holder).bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        MySMS item = mItems.get(position);
        return item.getType() == MySMS.InType.In ? TYPE_IN : TYPE_OUT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class InViewHolder extends RecyclerView.ViewHolder {
        private View avatar;
        private TextView username;
        private TextView text;

        public InViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.message);
        }

        public void bind(MySMS item) {
            username.setText(item.getPhone());
            text.setText(item.getMessage());
        }
    }

    private class OutViewHolder extends RecyclerView.ViewHolder {

        private TextView mMessage;

        public OutViewHolder(View itemView) {
            super(itemView);
            mMessage = itemView.findViewById(R.id.tv_message);
        }

        public void bind(MySMS item) {
            if (mMessage != null)
                mMessage.setText(item.getMessage());
            else {
                Global.logE(TAG, "tv_message not found");
            }
        }
    }
}
