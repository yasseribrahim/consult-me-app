package com.medicine.app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.medicine.app.models.Review;
import com.medicine.app.models.User;
import com.medicine.app.persenters.user.UsersCallback;
import com.medicine.app.persenters.user.UsersPresenter;
import com.medicine.app.utilities.NumbersUtils;
import com.medicine.app.Constants;
import com.medicine.app.R;
import com.medicine.app.databinding.ActivityReviewsBinding;
import com.medicine.app.ui.adptres.ReviewsAdapter;
import com.medicine.app.ui.fragments.ReviewBottomSheet;

import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends BaseActivity implements UsersCallback, ReviewBottomSheet.OnReviewCallback, ReviewsAdapter.OnItemClickListener {
    private ActivityReviewsBinding binding;
    private UsersPresenter presenter;
    private ReviewsAdapter adapter;
    private List<Review> reviews;
    private User user;
    private boolean isDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new UsersPresenter(this);
        user = getIntent().getParcelableExtra(Constants.ARG_OBJECT);

        binding.btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                var dialog = ReviewBottomSheet.newInstance();
                dialog.show(getSupportFragmentManager(), "");
            }
        });

        binding.refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        reviews = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReviewsAdapter(reviews, this);
        binding.recyclerView.setAdapter(adapter);
        bind();
    }

    private void load() {
        presenter.getUserById(user.getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onGetUserComplete(User user) {
        this.user = user;
        bind();
    }

    @Override
    public void onReviewCallback(Review review) {
        int index = user.getReviews().indexOf(review);
        if (index != -1) {
            user.getReviews().set(index, review);
        } else {
            user.getReviews().add(review);
        }
        presenter.save(user);
    }

    private void bind() {
        binding.review.setRating(user.getRating());
        binding.reviewValue.setText(NumbersUtils.round(user.getRating(), 1) + "");
        this.reviews.clear();
        this.reviews.addAll(user.getReviews());
        adapter.notifyDataSetChanged();

        binding.message.setVisibility(reviews.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onShowLoading() {
        binding.refreshLayout.setRefreshing(true);
    }

    @Override
    public void onHideLoading() {
        binding.refreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemViewListener(Review review) {

    }

    @Override
    public void onDeleteItemViewListener(Review review) {
        int position = reviews.indexOf(review);
        reviews.remove(review);
        adapter.notifyItemRemoved(position);
        user.setReviews(reviews);
        isDelete = true;
        presenter.save(user);
    }

    @Override
    public void onSaveUserComplete() {
        if (isDelete) {
            Toast.makeText(this, R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.str_message_updated_successfully, Toast.LENGTH_LONG).show();
        }
        isDelete = false;
        load();
    }
}