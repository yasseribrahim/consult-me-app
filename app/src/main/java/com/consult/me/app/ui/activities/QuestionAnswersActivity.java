package com.consult.me.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.consult.me.app.Constants;
import com.consult.me.app.models.Answer;
import com.consult.me.app.models.ChatId;
import com.consult.me.app.models.Link;
import com.consult.me.app.models.Notification;
import com.consult.me.app.models.Question;
import com.consult.me.app.models.User;
import com.consult.me.app.persenters.links.LinksCallback;
import com.consult.me.app.persenters.notification.NotificationsCallback;
import com.consult.me.app.persenters.notification.NotificationsPresenter;
import com.consult.me.app.persenters.question.QuestionsCallback;
import com.consult.me.app.persenters.question.QuestionsPresenter;
import com.consult.me.app.persenters.user.UsersCallback;
import com.consult.me.app.persenters.user.UsersPresenter;
import com.consult.me.app.ui.adptres.AnswersAdapter;
import com.consult.me.app.ui.fragments.AnswerBottomSheetDialog;
import com.consult.me.app.utilities.DatesUtils;
import com.consult.me.app.utilities.helpers.LocaleHelper;
import com.consult.me.app.utilities.helpers.StorageHelper;
import com.consult.me.app.R;
import com.consult.me.app.databinding.ActivityQuestionAnswersBinding;
import com.consult.me.app.persenters.links.LinksPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class QuestionAnswersActivity extends BaseActivity implements QuestionsCallback, LinksCallback, NotificationsCallback, AnswerBottomSheetDialog.OnPriceChangedCallback, AnswersAdapter.OnAnswersClickListener, UsersCallback {
    private ActivityQuestionAnswersBinding binding;
    private QuestionsPresenter questionsPresenter;
    private NotificationsPresenter notificationsPresenter;
    private LinksPresenter linksPresenter;
    private UsersPresenter usersPresenter;
    private AnswersAdapter adapter;
    private Question question;
    private List<User> users;
    private User currentUser;
    private boolean canEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionAnswersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        questionsPresenter = new QuestionsPresenter(this);
        notificationsPresenter = new NotificationsPresenter(this);
        usersPresenter = new UsersPresenter(this);
        linksPresenter = new LinksPresenter(this);

        question = getIntent().getParcelableExtra(Constants.ARG_OBJECT);
        currentUser = StorageHelper.getCurrentUser();
        canEdit = currentUser.getUsername().equalsIgnoreCase(question.getCreatedBy());

        if (question.getAnswers() == null) {
            question.setAnswers(new ArrayList<>());
        }

        var canAdd = canEdit;
        var canChat = canEdit;
        adapter = new AnswersAdapter(question.getAnswers(), this, canChat, canAdd);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        bind();

        users = new ArrayList<>();

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
                    Intent intent = new Intent(QuestionAnswersActivity.this, MessagingActivity.class);
                    intent.putExtra(Constants.ARG_OBJECT, id);
                    startActivity(intent);
                }
            }
        });

        binding.btnAddAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer answer = new Answer("", currentUser.getUsername(), "", Calendar.getInstance().getTime(), false);
                int index = question.getAnswers() != null ? question.getAnswers().indexOf(answer) : -1;
                if (index != -1) {
                    answer = question.getAnswers().get(index);
                }
                AnswerBottomSheetDialog dialog = AnswerBottomSheetDialog.newInstance(question, answer);
                dialog.show(getSupportFragmentManager(), "");
            }
        });
    }

    @Override
    public void onPriceChangedCallback(Question question) {
        this.question = question;
        bind();
    }

    @Override
    protected void onResume() {
        super.onResume();
        usersPresenter.getUsers(0);
    }

    @Override
    public void onGetUsersComplete(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
    }

    @Override
    public void onSaveQuestionComplete() {
        Toast.makeText(this, R.string.str_message_updated_successfully, Toast.LENGTH_LONG).show();
        Notification notification = new Notification();
        notification.setMessage(getString(R.string.str_notification_message_prescription, question.getTitle(), DatesUtils.formatDate(question.getDate())));
        notification.setPrescriptionId(question.getId());
        notificationsPresenter.save(notification, users);
        bind();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, getString(R.string.str_save_fail, message), Toast.LENGTH_LONG).show();
    }

    private void bind() {
        binding.btnAddAnswer.setVisibility(!question.isAccepted() && currentUser.isDoctor() ? View.VISIBLE : View.GONE);
        binding.btnChat.setVisibility(currentUser.isDoctor() ? View.VISIBLE : View.GONE);

        binding.question.setText(question.getTitle());
        binding.username.setText(question.getCreatedBy());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAnswerEditListener(Answer answer) {
        AnswerBottomSheetDialog dialog = AnswerBottomSheetDialog.newInstance(question, answer);
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onAnswerAddDoctorListener(Answer answer) {
        if (answer.isDone()) {
            question.setAcceptedAnswer(answer);
        } else {
            question.setAcceptedAnswer(null);
        }
        for (Answer answer1 : question.getAnswers()) {
            answer1.setDone(answer1.equals(answer));
        }

        if(currentUser.isPatient()) {
            Link link = new Link();
            link.setDoctorId(answer.getDoctorId());
            link.setPatientId(currentUser.getUsername());
            linksPresenter.save(link);
        }
    }

    @Override
    public void onSaveLinkComplete() {
        questionsPresenter.save(question);
    }

    @Override
    public void onAnswerDeleteListener(Answer answer) {
        int index = question.getAnswers().indexOf(answer);
        if (index != -1) {
            question.getAnswers().remove(index);
            questionsPresenter.save(question);
        }
    }

    @Override
    public void onAnswerChatListener(Answer answer) {
        var user1 = currentUser;
        User user2 = null;
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(answer.getDoctorId())) {
                user2 = user;
                break;
            }
        }

        if (user2 != null) {
            ChatId id = new ChatId(user1, user2);
            Intent intent = new Intent(this, MessagingActivity.class);
            intent.putExtra(Constants.ARG_OBJECT, id);
            startActivity(intent);
        }
    }
}