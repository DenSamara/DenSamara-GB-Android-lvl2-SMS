package ru.home.denis.konovalov.mysmsapplication.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import ru.home.denis.konovalov.mysmsapplication.model.MySms;

public class MySmsItemDetail extends ItemDetailsLookup.ItemDetails {
    private final int adapterPosition;
    private final MySms selectionKey;

    public MySmsItemDetail(int adapterPosition, MySms selectionKey){
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public Object getSelectionKey() {
        return selectionKey;
    }
}
