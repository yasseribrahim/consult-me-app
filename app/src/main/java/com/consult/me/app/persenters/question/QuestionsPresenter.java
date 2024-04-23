package com.consult.me.app.persenters.question;

import androidx.annotation.NonNull;

import com.consult.me.app.Constants;
import com.consult.me.app.models.Question;
import com.consult.me.app.persenters.BasePresenter;
import com.consult.me.app.utilities.helpers.StorageHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionsPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private QuestionsCallback callback;

    public QuestionsPresenter(QuestionsCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_QUESTIONS).getRef();
        this.callback = callback;
    }

    public void save(Question question) {
        callback.onHideLoading();
        if (question.getId() == null) {
            question.setId("question-" + System.currentTimeMillis());
        }
        reference.child(question.getId()).setValue(question).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onSaveQuestionComplete();
                    callback.onHideLoading();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getMessage(), null);
                callback.onHideLoading();
            }
        });
    }

    public void getQuestion(String id) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Question question = null;
                if (snapshot.exists()) {
                    question = snapshot.getValue(Question.class);
                }

                if (callback != null) {
                    callback.onGetQuestionComplete(question);
                    callback.onHideLoading();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (callback != null) {
                    callback.onFailure("Unable to get message: " + databaseError.getMessage(), null);
                    callback.onHideLoading();
                }
            }
        };
        reference.child(id).addListenerForSingleValueEvent(listener);
    }

    public void delete(Question question) {
        reference.child(question.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onDeleteQuestionComplete(question);
                    callback.onHideLoading();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getMessage(), null);
                callback.onHideLoading();
            }
        });
    }

    public void getQuestions() {
        callback.onShowLoading();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Question> questions = new ArrayList<>();
                for (var child : snapshot.getChildren()) {
                    var question = child.getValue(Question.class);
                    if(StorageHelper.getCurrentUser().isAdmin() || !question.isAccepted()) {
                        questions.add(question);
                    }
                }

                if (callback != null) {
                    callback.onGetQuestionsComplete(questions);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onHideLoading();
            }
        });
    }

    public void getQuestionsCreated() {
        callback.onShowLoading();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Question> questions = new ArrayList<>();
                var currentUsername = StorageHelper.getCurrentUser().getUsername();
                for (var child : snapshot.getChildren()) {
                    var service = child.getValue(Question.class);
                    if (service.getCreatedBy().equalsIgnoreCase(currentUsername)) {
                        questions.add(service);
                    }
                }

                if (callback != null) {
                    callback.onGetQuestionsComplete(questions);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onHideLoading();
            }
        });
    }

    public void getQuestionsAccepted() {
        callback.onShowLoading();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Question> questions = new ArrayList<>();
                var currentUsername = StorageHelper.getCurrentUser().getUsername();
                for (var child : snapshot.getChildren()) {
                    var service = child.getValue(Question.class);
                    if (service.getAcceptedAnswer() != null && service.getAcceptedAnswer().getDoctorId().equalsIgnoreCase(currentUsername)) {
                        questions.add(service);
                    }
                }

                if (callback != null) {
                    callback.onGetQuestionsComplete(questions);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onHideLoading();
            }
        });
    }

    public void count() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onGetQuestionsCountComplete(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
