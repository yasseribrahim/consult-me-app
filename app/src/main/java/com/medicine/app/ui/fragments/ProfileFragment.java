package com.medicine.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicine.app.Constants;
import com.medicine.app.R;
import com.medicine.app.databinding.FragmentProfileBinding;
import com.medicine.app.models.User;
import com.medicine.app.ui.activities.ProfileEditActivity;
import com.medicine.app.ui.activities.SplashActivity;
import com.medicine.app.utilities.UIUtils;
import com.medicine.app.utilities.helpers.StorageHelper;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private User user;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener valueEventListenerUser;
    private String userPath;

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        user = StorageHelper.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        userPath = Constants.NODE_NAME_USERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = database.getReference(userPath);

        valueEventListenerUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                bind();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userReference.addValueEventListener(valueEventListenerUser);

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProfileEditActivity.class));
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                StorageHelper.clearCurrentUser();
                startActivity(new Intent(ProfileFragment.this.getContext(), SplashActivity.class));
                ProfileFragment.this.getActivity().finishAffinity();
            }
        });

        bind();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            userReference.removeEventListener(valueEventListenerUser);
            userReference = null;
            valueEventListenerUser = null;
        } catch (Exception ex) {
        }
    }

    private void bind() {
        binding.username.setText("@" + user.getUsername());
        binding.type.setText(UIUtils.getAccountType(user.getType()));
        binding.nameTextView.setText(user.getFullName());
        binding.emailTextView.setText(user.getUsername());
        binding.phoneTextView.setText(user.getPhone());
        binding.addressTextView.setText(user.getAddress());

        Glide.with(ProfileFragment.this).load(user.getImageProfile()).placeholder(R.drawable.ic_account_circle).into(binding.profileImage);
    }
}