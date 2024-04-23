package com.consult.me.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.consult.me.app.models.Review;
import com.consult.me.app.utilities.helpers.StorageHelper;
import com.consult.me.app.R;
import com.consult.me.app.databinding.ItemReviewBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final List<Review> reviews;
    private final OnItemClickListener listener;

    // data is passed into the constructor
    public ReviewsAdapter(List<Review> reviews, OnItemClickListener listener) {
        this.reviews = reviews;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.binding.username.setText(review.getUsername());
        holder.binding.note.setText(review.getNote());
        holder.binding.review.setRating(review.getValue());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(review.getDate().getTime());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        holder.binding.date.setText(format.format(calendar.getTime()));
    }

    private int getSize(String id) {
        return reviews.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public interface OnItemClickListener {
        void onItemViewListener(Review review);

        void onDeleteItemViewListener(Review review);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReviewBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemReviewBinding.bind(view);

            binding.btnDelete.setVisibility(StorageHelper.getCurrentUser().isAdmin() ? View.VISIBLE : View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemViewListener(reviews.get(getAdapterPosition()));
                }
            });

            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onDeleteItemViewListener(reviews.get(getAdapterPosition()));
                }
            });
        }
    }
}