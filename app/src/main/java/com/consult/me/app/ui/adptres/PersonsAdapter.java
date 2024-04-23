package com.consult.me.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.consult.me.app.R;
import com.consult.me.app.databinding.ItemPersonBinding;
import com.consult.me.app.models.User;
import com.consult.me.app.utilities.helpers.StorageHelper;

import java.util.List;

public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.ViewHolder> {
    private List<User> users;
    private OnItemClickListener listener;

    public PersonsAdapter(List<User> users, OnItemClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.name.setText(user.getFullName());
        holder.binding.username.setText(user.getUsername());
        holder.binding.phone.setText(user.getPhone());
        holder.binding.containerRemove.setVisibility(StorageHelper.getCurrentUser() != null ? StorageHelper.getCurrentUser().getId().equalsIgnoreCase(user.getId()) ? View.GONE : View.VISIBLE : View.GONE);
        Glide.with(holder.itemView.getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(holder.binding.image);
    }

    private int getSize(String id) {
        return users.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return users.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPersonBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemPersonBinding.bind(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onViewListener(users.get(getAdapterPosition()));
                }
            });
            binding.containerRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onDeleteListener(users.get(getAdapterPosition()));
                }
            });
            binding.containerCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onCallListener(users.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onViewListener(User user);

        void onDeleteListener(User user);

        void onCallListener(User user);
    }
}