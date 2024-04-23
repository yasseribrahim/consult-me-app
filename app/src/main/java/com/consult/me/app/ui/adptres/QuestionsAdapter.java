package com.consult.me.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.consult.me.app.R;
import com.consult.me.app.databinding.ItemQuestionBinding;
import com.consult.me.app.models.Question;
import com.consult.me.app.utilities.DatesUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {
    private List<Question> questions;
    private OnQuestionsClickListener listener;
    private boolean canEdit;

    public QuestionsAdapter(List<Question> questions, OnQuestionsClickListener listener, boolean canEdit) {
        this.questions = questions;
        this.listener = listener;
        this.canEdit = canEdit;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question question = questions.get(position);

        holder.binding.title.setText(question.getTitle());
        holder.binding.answers.setText(holder.binding.answers.getContext().getString(R.string.str_answers, question.getAnswers() != null ? question.getAnswers().size() + "" : "0"));
        holder.binding.description.setText(question.getDescription());
        holder.binding.description.setVisibility(question.getDescription() != null && !question.getDescription().isEmpty() ? View.VISIBLE : View.GONE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(question.getDate().getTime());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        holder.binding.date.setText(format.format(calendar.getTime()));
        holder.binding.username.setText(question.getCreatedBy());

        holder.binding.date.setText(DatesUtils.formatDateOnly(question.getDate()));
    }

    private int getSize(String id) {
        return questions.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return questions.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemQuestionBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemQuestionBinding.bind(view);
            binding.containerActions.setVisibility(canEdit ? View.VISIBLE : View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onQuestionViewListener(questions.get(getAdapterPosition()));
                }
            });
            binding.containerRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onQuestionDeleteListener(questions.get(getAdapterPosition()));
                }
            });
            binding.containerEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onQuestionEditListener(questions.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnQuestionsClickListener {
        void onQuestionViewListener(Question question);

        void onQuestionEditListener(Question question);

        void onQuestionDeleteListener(Question question);
    }
}