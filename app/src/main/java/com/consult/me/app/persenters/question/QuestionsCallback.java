package com.consult.me.app.persenters.question;

import com.consult.me.app.models.Question;
import com.consult.me.app.persenters.BaseCallback;

import java.util.List;

public interface QuestionsCallback extends BaseCallback {
    default void onGetQuestionsComplete(List<Question> questions) {
    }

    default void onGetQuestionsCountComplete(long count) {
    }

    default void onSaveQuestionComplete() {
    }

    default void onDeleteQuestionComplete(Question question) {
    }

    default void onGetQuestionComplete(Question question) {
    }
}
