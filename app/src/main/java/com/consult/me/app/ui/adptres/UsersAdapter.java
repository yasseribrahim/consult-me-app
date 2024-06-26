package com.consult.me.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.consult.me.app.R;
import com.consult.me.app.databinding.ItemUserBinding;
import com.consult.me.app.models.User;
import com.consult.me.app.utilities.helpers.StorageHelper;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<User> users;
    private List<String> linksIds;
    private OnItemClickListener listener;

    public UsersAdapter(List<User> users, OnItemClickListener listener, List<String> linksIds) {
        this.users = users;
        this.listener = listener;
        this.linksIds = linksIds;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.name.setText(user.getFullName());
        holder.binding.username.setText(user.getUsername());
        holder.binding.phone.setText(user.getPhone());
        holder.binding.containerRemove.setVisibility(StorageHelper.getCurrentUser() != null ? StorageHelper.getCurrentUser().getId().equalsIgnoreCase(user.getId()) ? View.GONE : View.VISIBLE : View.GONE);
        holder.binding.containerAdd.setVisibility(StorageHelper.getCurrentUser().isAdmin() || linksIds.contains(user.getUsername()) ? View.GONE : View.VISIBLE);
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
        ItemUserBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemUserBinding.bind(view);
            binding.containerActions.setVisibility(StorageHelper.getCurrentUser().isAdmin() ? View.VISIBLE : View.GONE);
            binding.containerAdd.setVisibility(!StorageHelper.getCurrentUser().isAdmin() ? View.VISIBLE : View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemViewListener(getAdapterPosition());
                }
            });
            binding.containerRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onDeleteItemViewListener(getAdapterPosition());
                }
            });
            binding.containerEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemEditListener(getAdapterPosition());
                }
            });
            binding.containerAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onAddClickListener(users.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemViewListener(int position);

        void onItemEditListener(int position);

        void onDeleteItemViewListener(int position);

        void onAddClickListener(User user);
    }
}