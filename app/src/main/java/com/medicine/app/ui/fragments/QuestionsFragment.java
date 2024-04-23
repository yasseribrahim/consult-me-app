package com.medicine.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.medicine.app.Constants;
import com.medicine.app.R;
import com.medicine.app.databinding.FragmentQuestionsBinding;
import com.medicine.app.models.Question;
import com.medicine.app.persenters.question.QuestionsCallback;
import com.medicine.app.persenters.question.QuestionsPresenter;
import com.medicine.app.ui.activities.QuestionActivity;
import com.medicine.app.ui.adptres.QuestionsAdapter;

import java.util.ArrayList;
import java.util.List;

public class QuestionsFragment extends Fragment implements QuestionsCallback, QuestionsAdapter.OnQuestionsClickListener {
    private FragmentQuestionsBinding binding;
    private QuestionsPresenter presenter;
    private QuestionsAdapter questionsAdapter;
    private List<Question> questions, searchedQuestions;

    public static QuestionsFragment newInstance() {
        Bundle args = new Bundle();
        QuestionsFragment fragment = new QuestionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuestionsBinding.inflate(inflater);

        presenter = new QuestionsPresenter(this);

        binding.refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        binding.textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(binding.textSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        questions = new ArrayList<>();
        searchedQuestions = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionsAdapter = new QuestionsAdapter(searchedQuestions, this, false);
        binding.recyclerView.setAdapter(questionsAdapter);

        return binding.getRoot();
    }

    private void load() {
        presenter.getQuestions();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onGetQuestionsComplete(List<Question> users) {
        this.questions.clear();
        this.questions.addAll(users);
        search(binding.textSearch.getText().toString());
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
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void search(String searchedText) {
        searchedQuestions.clear();
        if (!searchedText.isEmpty()) {
            for (Question user : questions) {
                if (isMatched(user, searchedText)) {
                    searchedQuestions.add(user);
                }
            }
        } else {
            searchedQuestions.addAll(questions);
        }

        refresh();
    }

    private boolean isMatched(Question user, String text) {
        String searchedText = text.toLowerCase();
        boolean result = user.getTitle().toLowerCase().contains(searchedText) ||
                (user.getDescription() != null && user.getDescription().toLowerCase().contains(searchedText));
        return result;
    }

    private void refresh() {
        binding.message.setVisibility(View.GONE);
        if (searchedQuestions.isEmpty()) {
            binding.message.setVisibility(View.VISIBLE);
        }

        questionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onQuestionViewListener(Question question) {
        openServiceActivity(question);
    }

    @Override
    public void onQuestionEditListener(Question question) {

    }

    @Override
    public void onQuestionDeleteListener(Question question) {

    }

    @Override
    public void onDeleteQuestionComplete(Question question) {
        int index = searchedQuestions.indexOf(question);
        if (index != -1) {
            searchedQuestions.remove(index);
            questionsAdapter.notifyItemRemoved(index);
        }
        Toast.makeText(getContext(), R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
    }

    private void openServiceActivity(Question question) {
        Intent intent = new Intent(getContext(), QuestionActivity.class);
        intent.putExtra(Constants.ARG_OBJECT, question);
        startActivity(intent);
    }
}