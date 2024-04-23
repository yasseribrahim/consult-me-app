package com.medicine.app.ui.adptres;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.medicine.app.models.Category;
import com.medicine.app.R;
import com.medicine.app.databinding.ItemCategorySelectorBinding;

import java.util.List;

public class CategoriesSelectorAdapter extends RecyclerView.Adapter<CategoriesSelectorAdapter.ViewHolder> {
    private List<Category> categories;
    private OnCategoriesClickListener listener;
    private Category selectedCategory;

    public CategoriesSelectorAdapter(List<Category> categories, OnCategoriesClickListener listener, Category selectedCategory) {
        this.categories = categories;
        this.listener = listener;
        this.selectedCategory = selectedCategory;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_selector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.binding.name.setText(category.getName());
        if (selectedCategory != null && selectedCategory.equals(category)) {
            holder.binding.name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            holder.binding.checked.setImageDrawable(ResourcesCompat.getDrawable(holder.binding.checked.getResources(), R.drawable.ic_check, null));
        } else {
            holder.binding.name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.binding.checked.setImageDrawable(ResourcesCompat.getDrawable(holder.binding.checked.getResources(), R.drawable.ic_check_empty, null));
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return categories.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCategorySelectorBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemCategorySelectorBinding.bind(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onCategorySelectedListener(categories.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnCategoriesClickListener {

        void onCategorySelectedListener(Category category);
    }
}