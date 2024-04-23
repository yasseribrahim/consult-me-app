package com.consult.me.app.ui.fragments;

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

import com.consult.me.app.Constants;
import com.consult.me.app.R;
import com.consult.me.app.databinding.FragmentQuestionsCreatedBinding;
import com.consult.me.app.models.Question;
import com.consult.me.app.persenters.question.QuestionsCallback;
import com.consult.me.app.persenters.question.QuestionsPresenter;
import com.consult.me.app.ui.activities.QuestionActivity;
import com.consult.me.app.ui.adptres.QuestionsAdapter;
import com.consult.me.app.utilities.helpers.StorageHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class QuestionsCreatedFragment extends Fragment implements QuestionsCallback, QuestionsAdapter.OnQuestionsClickListener {
    private FragmentQuestionsCreatedBinding binding;
    private QuestionsPresenter presenter;
    private QuestionsAdapter adapter;
    private List<Question> questions, searchedQuestions;

    public static QuestionsCreatedFragment newInstance() {
        Bundle args = new Bundle();
        QuestionsCreatedFragment fragment = new QuestionsCreatedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuestionsCreatedBinding.inflate(inflater);

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
        adapter = new QuestionsAdapter(searchedQuestions, this, true);
        binding.recyclerView.setAdapter(adapter);

        binding.btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                var service = new Question();
                service.setCreatedBy(StorageHelper.getCurrentUser().getUsername());
                service.setDate(Calendar.getInstance().getTime());
                openServiceActivity(service);
            }
        });

        return binding.getRoot();
    }

    private void load() {
        presenter.getQuestionsCreated();
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
    public void onGetQuestionsComplete(List<Question> questions) {
        this.questions.clear();
        this.questions.addAll(questions);
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

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onQuestionViewListener(Question question) {
        openServiceActivity(question);
    }

    @Override
    public void onQuestionEditListener(Question question) {
        openServiceActivity(question);
    }

    @Override
    public void onQuestionDeleteListener(Question question) {

    }

    @Override
    public void onDeleteQuestionComplete(Question question) {
        int index = searchedQuestions.indexOf(question);
        if (index != -1) {
            searchedQuestions.remove(index);
            adapter.notifyItemRemoved(index);
        }
        Toast.makeText(getContext(), R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
    }

    private void openServiceActivity(Question question) {
        Intent intent = new Intent(getContext(), QuestionActivity.class);
        intent.putExtra(Constants.ARG_OBJECT, question);
        startActivity(intent);
    }
}