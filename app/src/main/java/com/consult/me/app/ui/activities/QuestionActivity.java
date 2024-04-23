package com.consult.me.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.consult.me.app.Constants;
import com.consult.me.app.models.ChatId;
import com.consult.me.app.models.Notification;
import com.consult.me.app.models.Question;
import com.consult.me.app.models.User;
import com.consult.me.app.persenters.notification.NotificationsCallback;
import com.consult.me.app.persenters.notification.NotificationsPresenter;
import com.consult.me.app.persenters.question.QuestionsCallback;
import com.consult.me.app.persenters.question.QuestionsPresenter;
import com.consult.me.app.persenters.user.UsersCallback;
import com.consult.me.app.persenters.user.UsersPresenter;
import com.consult.me.app.utilities.DatesUtils;
import com.consult.me.app.utilities.helpers.LocaleHelper;
import com.consult.me.app.utilities.helpers.StorageHelper;
import com.consult.me.app.R;
import com.consult.me.app.databinding.ActivityQuestionBinding;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends BaseActivity implements QuestionsCallback, UsersCallback, NotificationsCallback {
    private ActivityQuestionBinding binding;
    private QuestionsPresenter questionsPresenter;
    private NotificationsPresenter notificationsPresenter;
    private UsersPresenter usersPresenter;
    private Question question;
    private List<User> users;
    private User currentUser;
    private boolean canEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        questionsPresenter = new QuestionsPresenter(this);
        notificationsPresenter = new NotificationsPresenter(this);
        usersPresenter = new UsersPresenter(this);

        question = getIntent().getParcelableExtra(Constants.ARG_OBJECT);
        currentUser = StorageHelper.getCurrentUser();
        canEdit = currentUser.getUsername().equalsIgnoreCase(question.getCreatedBy());

        if (question.getAnswers() == null) {
            question.setAnswers(new ArrayList<>());
        }

        users = new ArrayList<>();

        binding.btnAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionActivity.this, QuestionAnswersActivity.class);
                intent.putExtra(Constants.ARG_OBJECT, question);
                startActivity(intent);
            }
        });

        if (canEdit) {
            binding.btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = binding.title.getText().toString().trim();
                    String description = binding.description.getText().toString().trim();

                    if (title.isEmpty()) {
                        binding.title.setError(getString(R.string.str_title_hint));
                        binding.title.requestFocus();
                        return;
                    }
                    if (description.isEmpty()) {
                        binding.description.setError(getString(R.string.str_description_hint));
                        binding.description.requestFocus();
                        return;
                    }

                    question.setTitle(title);
                    question.setDescription(binding.description.getText().toString());
                    if (question.getId() == null) {
                        question.setCreatedBy(StorageHelper.getCurrentUser().getUsername());
                    }

                    questionsPresenter.save(question);
                }
            });
        } else {
            binding.btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user1 = null;
                    User user2 = currentUser;
                    for (User user : users) {
                        if (user.getUsername().equalsIgnoreCase(question.getCreatedBy())) {
                            user1 = user;
                            break;
                        }
                    }

                    if (user1 != null) {
                        ChatId id = new ChatId(user1, user2);
                        Intent intent = new Intent(QuestionActivity.this, MessagingActivity.class);
                        intent.putExtra(Constants.ARG_OBJECT, id);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (question.getId() != null) {
            questionsPresenter.getQuestion(question.getId());
        }
    }

    @Override
    public void onGetQuestionComplete(Question question) {
        this.question = question;
        bind();
        usersPresenter.getUsers(0);
    }

    @Override
    public void onGetUsersComplete(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
    }

    @Override
    public void onSaveQuestionComplete() {
        Toast.makeText(this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        Notification notification = new Notification();
        notification.setMessage(getString(R.string.str_notification_message_question, question.getTitle(), DatesUtils.formatDate(question.getDate())));
        notification.setPrescriptionId(question.getId());
        if (StorageHelper.getCurrentUser().isAdmin()) {
            notificationsPresenter.save(notification, users);
        } else {
            finish();
        }
    }

    @Override
    public void onSaveNotificationComplete() {
        finish();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, getString(R.string.str_save_fail, message), Toast.LENGTH_LONG).show();
    }

    private void bind() {
        binding.title.setEnabled(canEdit);
        binding.description.setEnabled(canEdit);
        binding.btnSave.setVisibility(canEdit ? View.VISIBLE : View.GONE);

        binding.btnChat.setVisibility(!question.isAccepted() && currentUser.isDoctor() ? View.VISIBLE : View.GONE);
        binding.btnAnswers.setVisibility(question.getId() != null ? View.VISIBLE : View.GONE);

        binding.username.setText(question.getCreatedBy());
        binding.title.setText(question.getTitle());
        binding.description.setText(question.getDescription());
        binding.date.setText(DatesUtils.formatDate(question.getDate()));
    }
}