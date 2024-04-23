package com.medicine.app.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medicine.app.models.Review;
import com.medicine.app.utilities.ToastUtils;
import com.medicine.app.utilities.helpers.StorageHelper;
import com.medicine.app.R;
import com.medicine.app.databinding.BottomSheetReviewBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ReviewBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetReviewBinding binding;
    private OnReviewCallback callback;
    private Review review;

    public static ReviewBottomSheet newInstance() {
        Bundle bundle = new Bundle();
        ReviewBottomSheet sheet = new ReviewBottomSheet();
        sheet.setArguments(bundle);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetReviewBinding.inflate(inflater);
        review = new Review(StorageHelper.getCurrentUser().getUsername());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                review.setNote(s.toString().trim());
            }
        });

        binding.review.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                review.setValue(rating);
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review.getValue() == 0) {
                    ToastUtils.longToast(R.string.str_review_hint);
                    return;
                }

                if (callback != null) {
                    callback.onReviewCallback(review);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReviewCallback) {
            callback = (OnReviewCallback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnQuestionArticleSolveCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface OnReviewCallback {
        void onReviewCallback(Review review);
    }
}