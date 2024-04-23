package com.medicine.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.medicine.app.models.Notification;
import com.medicine.app.utilities.helpers.StorageHelper;
import com.medicine.app.R;
import com.medicine.app.databinding.ItemNotificationBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private final List<Notification> notifications;
    private final OnItemClickListener listener;

    // data is passed into the constructor
    public NotificationsAdapter(List<Notification> notifications, OnItemClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.binding.notification.setText(notification.getMessage());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(notification.getTimestamp());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        holder.binding.date.setText(format.format(calendar.getTime()));
    }

    private int getSize(String id) {
        return notifications.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public interface OnItemClickListener {
        void onItemViewListener(Notification notification);

        void onDeleteItemViewListener(Notification notification);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemNotificationBinding.bind(view);
            binding.btnDelete.setVisibility(StorageHelper.getCurrentUser().isAdmin() ? View.VISIBLE : View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemViewListener(notifications.get(getAdapterPosition()));
                }
            });

            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onDeleteItemViewListener(notifications.get(getAdapterPosition()));
                }
            });
        }
    }
}