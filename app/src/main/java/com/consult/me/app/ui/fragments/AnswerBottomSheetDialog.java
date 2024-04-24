package com.consult.me.app.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.consult.me.app.Constants;
import com.consult.me.app.R;
import com.consult.me.app.databinding.FragmentBottomSheetDialogAnswerBinding;
import com.consult.me.app.models.Answer;
import com.consult.me.app.models.Question;
import com.consult.me.app.persenters.question.QuestionsCallback;
import com.consult.me.app.persenters.question.QuestionsPresenter;
import com.consult.me.app.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class AnswerBottomSheetDialog extends BottomSheetDialogFragment implements QuestionsCallback {
    private FragmentBottomSheetDialogAnswerBinding binding;
    private QuestionsPresenter presenter;
    private Question question;
    private Answer answer;
    private OnPriceChangedCallback callback;

    public interface OnPriceChangedCallback {
        void onPriceChangedCallback(Question question);
    }

    public AnswerBottomSheetDialog() {
    }

    public static AnswerBottomSheetDialog newInstance(Question question, Answer answer) {
        AnswerBottomSheetDialog fragment = new AnswerBottomSheetDialog();
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_OBJECT_1, question);
        args.putParcelable(Constants.ARG_OBJECT_2, answer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPriceChangedCallback) {
            callback = (OnPriceChangedCallback) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new QuestionsPresenter(this);
        question = getArguments().getParcelable(Constants.ARG_OBJECT_1);
        answer = getArguments().getParcelable(Constants.ARG_OBJECT_2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBottomSheetDialogAnswerBinding.inflate(inflater);
        bind();
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        var dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setDimAmount(0.4f); /** Set dim amount here (the dimming factor of the parent fragment) */
        return dialog;
    }

    private void bind() {
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getAnswers() == null) {
                    question.setAnswers(new ArrayList<>());
                }
                if (binding.answer.getText().toString().isEmpty()) {
                    binding.answer.setError("Invalid Answer");
                    binding.answer.requestFocus();
                    return;
                }

                int index = question.getAnswers().indexOf(answer);
                if (index != -1) {
                    answer = question.getAnswers().get(index);
                } else {
                    question.getAnswers().add(answer);
                }
                answer.setAnswer(binding.answer.getText().toString());
                answer.setDate(Calendar.getInstance().getTime());

                presenter.save(question);
            }
        });

        binding.answer.setText(answer.getAnswer() + "");
        binding.username.setText(answer.getConsultantId());
    }

    @Override
    public void onSaveQuestionComplete() {
        ToastUtils.longToast(R.string.str_message_updated_successfully);
        if (callback != null) {
            callback.onPriceChangedCallback(question);
        }
        dismiss();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        ToastUtils.longToast(message);
    }

    @Override
    public void onShowLoading() {
        ProgressDialogFragment.show(getParentFragmentManager());
    }

    @Override
    public void onHideLoading() {
        ProgressDialogFragment.hide(getParentFragmentManager());
    }
}