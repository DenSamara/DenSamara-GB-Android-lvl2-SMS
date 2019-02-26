package ru.home.denis.konovalov.mysmsapplication.adapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import ru.home.denis.konovalov.mysmsapplication.model.MySms;

public class MySmsKeyProvider extends ItemKeyProvider {
    private final ArrayList<MySms> items;
    /**
     * Creates a new provider with the given scope.
     *
     * @param scope Scope can't be changed at runtime.
     */
    public MySmsKeyProvider(int scope, ArrayList<MySms> items) {
        super(scope);
        this.items = items;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return items.get(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        return items.indexOf(key);
    }
}
