package com.consult.me.app.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.consult.me.app.models.Contacts;
import com.consult.me.app.models.User;
import com.consult.me.app.persenters.user.UsersCallback;
import com.consult.me.app.persenters.user.UsersPresenter;
import com.consult.me.app.utilities.ToastUtils;
import com.consult.me.app.utilities.helpers.StorageHelper;
import com.consult.me.app.Constants;
import com.consult.me.app.R;
import com.consult.me.app.databinding.FragmentContactsBottomSheetDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ContactsBottomSheetDialog extends BottomSheetDialogFragment implements UsersCallback {
    private FragmentContactsBottomSheetDialogBinding binding;
    private UsersPresenter presenter;
    private User user;
    private boolean canEdit;

    public ContactsBottomSheetDialog() {
    }

    public static ContactsBottomSheetDialog newInstance(User user, boolean canEdit) {
        ContactsBottomSheetDialog fragment = new ContactsBottomSheetDialog();
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_OBJECT, user);
        args.putInt("can_edit", canEdit ? 1 : 0);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new UsersPresenter(this);
        user = getArguments().getParcelable(Constants.ARG_OBJECT);
        canEdit = getArguments().getInt("can_edit", 0) == 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactsBottomSheetDialogBinding.inflate(inflater);
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
        var defaultValue = canEdit ? "" : "N/A";
        var contacts = user.getContacts();
        if(contacts == null) {
            contacts = new Contacts();
            user.setContacts(contacts);
        }
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                var contacts = new Contacts();
                contacts.setPhone(binding.phone.getText().toString());
                contacts.setEmail(binding.email.getText().toString());
                contacts.setFacebook(binding.facebook.getText().toString());
                contacts.setWhatsApp(binding.whatsApp.getText().toString());
                user.setDescription(binding.description.getText().toString());
                user.setContacts(contacts);
                presenter.save(user);
            }
        });
        binding.btnSave.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        binding.phone.setEnabled(canEdit);
        binding.containerPhone.setVisibility(canEdit ? View.GONE : View.VISIBLE);
        binding.email.setEnabled(canEdit);
        binding.containerEmail.setVisibility(canEdit ? View.GONE : View.VISIBLE);
        binding.facebook.setEnabled(canEdit);
        binding.containerFacebook.setVisibility(canEdit ? View.GONE : View.VISIBLE);
        binding.whatsApp.setEnabled(canEdit);
        binding.containerWhatsApp.setVisibility(canEdit ? View.GONE : View.VISIBLE);
        binding.description.setEnabled(canEdit);
        Glide.with(getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(binding.image);
        binding.name.setText(user.getFullName());
        binding.phone.setText(contacts.getPhone() != null && !contacts.getPhone().isEmpty() ? contacts.getPhone() : defaultValue);
        binding.email.setText(contacts.getEmail() != null && !contacts.getEmail().isEmpty() ? contacts.getEmail() : defaultValue);
        binding.facebook.setText(contacts.getFacebook() != null && !contacts.getFacebook().isEmpty() ? contacts.getFacebook() : defaultValue);
        binding.whatsApp.setText(contacts.getWhatsApp() != null && !contacts.getWhatsApp().isEmpty() ? contacts.getWhatsApp() : defaultValue);
        binding.description.setText(user.getDescription() != null && !user.getDescription().isEmpty() ? user.getDescription() : defaultValue);
    }

    @Override
    public void onSaveUserComplete() {
        ToastUtils.longToast(R.string.str_message_updated_successfully);
        StorageHelper.setCurrentUser(user);
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