package com.medicine.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.medicine.app.models.Category;
import com.medicine.app.R;
import com.medicine.app.databinding.ItemCategoryBinding;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<Category> categories;
    private OnCategoriesClickListener listener;

    public CategoriesAdapter(List<Category> categories, OnCategoriesClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.binding.name.setText(category.getName());
    }

    private int getSize(String id) {
        return categories.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return categories.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemCategoryBinding.bind(view);
            binding.containerRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onCategoryDeleteListener(categories.get(getAdapterPosition()));
                }
            });
            binding.containerEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onCategoryEditListener(categories.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnCategoriesClickListener {

        void onCategoryEditListener(Category category);

        void onCategoryDeleteListener(Category category);
    }
}