package com.consult.me.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.consult.me.app.R;
import com.consult.me.app.databinding.ItemAnswerBinding;
import com.consult.me.app.models.Answer;
import com.consult.me.app.utilities.DatesUtils;
import com.consult.me.app.utilities.helpers.StorageHelper;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {
    private List<Answer> answers;
    private OnAnswersClickListener listener;
    private boolean canChat, canAdd;

    public AnswersAdapter(List<Answer> answers, OnAnswersClickListener listener, boolean canChat, boolean canAdd) {
        this.answers = answers;
        this.listener = listener;
        this.canChat = canChat;
        this.canAdd = canAdd;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Answer answer = answers.get(position);

        holder.binding.username.setText(answer.getConsultantId());
        holder.binding.answer.setText(answer.getAnswer() + "");
        holder.binding.date.setText(DatesUtils.formatDate(answer.getDate()));
        holder.binding.btnAddDoctor.setVisibility(canAdd && !answer.isDone() ? View.VISIBLE : View.GONE);
        var canEdit = StorageHelper.getCurrentUser().getId().equalsIgnoreCase(answer.getConsultantId());
        holder.binding.containerActions.setVisibility(canEdit ? View.VISIBLE : View.GONE);
    }

    private int getSize(String id) {
        return answers.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return answers.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemAnswerBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemAnswerBinding.bind(view);
            binding.btnChat.setVisibility(canChat ? View.VISIBLE : View.GONE);
            binding.btnAddDoctor.setVisibility(canAdd ? View.VISIBLE : View.GONE);
            binding.btnAddDoctor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        var answer = answers.get(getAdapterPosition());
                        answer.setDone(true);
                        listener.onAnswerAddDoctorListener(answer);
                    }
                }
            });
            binding.containerRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onAnswerDeleteListener(answers.get(getAdapterPosition()));
                }
            });
            binding.containerEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onAnswerEditListener(answers.get(getAdapterPosition()));
                }
            });
            binding.btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onAnswerChatListener(answers.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnAnswersClickListener {

        void onAnswerEditListener(Answer answer);

        void onAnswerDeleteListener(Answer answer);

        void onAnswerAddDoctorListener(Answer answer);

        void onAnswerChatListener(Answer answer);
    }
}