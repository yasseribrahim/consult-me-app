package com.consult.me.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.consult.me.app.Constants;
import com.consult.me.app.R;
import com.consult.me.app.databinding.ActivityQuestionsRepliedBinding;
import com.consult.me.app.models.Question;
import com.consult.me.app.persenters.question.QuestionsCallback;
import com.consult.me.app.persenters.question.QuestionsPresenter;
import com.consult.me.app.ui.adptres.QuestionsAdapter;
import com.consult.me.app.utilities.helpers.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public class QuestionsRepliedActivity extends BaseActivity implements QuestionsCallback, QuestionsAdapter.OnQuestionsClickListener {
    private ActivityQuestionsRepliedBinding binding;
    private QuestionsPresenter presenter;
    private QuestionsAdapter adapter;
    private List<Question> questions, searchedQuestions;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsRepliedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        clientId = getIntent().getParcelableExtra(Constants.ARG_OBJECT);
        binding.username.setText(clientId);

        presenter = new QuestionsPresenter(this);
        clientId = getIntent().getExtras().getString(Constants.ARG_OBJECT);
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
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionsAdapter(searchedQuestions, this, false);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        presenter.getQuestionsReplied(clientId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(Constants.ARG_OBJECT, question);
        startActivity(intent);
    }

    @Override
    public void onQuestionEditListener(Question question) {

    }

    @Override
    public void onQuestionDeleteListener(Question question) {

    }

    @Override
    public void onDeleteQuestionComplete(Question question) {

    }
}