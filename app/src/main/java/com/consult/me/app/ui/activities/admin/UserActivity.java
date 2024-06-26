package com.consult.me.app.ui.activities.admin;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.consult.me.app.Constants;
import com.consult.me.app.R;
import com.consult.me.app.databinding.ActivityUserBinding;
import com.consult.me.app.models.Category;
import com.consult.me.app.models.User;
import com.consult.me.app.persenters.user.UsersCallback;
import com.consult.me.app.persenters.user.UsersPresenter;
import com.consult.me.app.ui.activities.BaseActivity;
import com.consult.me.app.ui.fragments.CategorySelectorBottomSheetDialog;
import com.consult.me.app.utilities.UIUtils;
import com.consult.me.app.utilities.helpers.LocaleHelper;

public class UserActivity extends BaseActivity implements UsersCallback, CategorySelectorBottomSheetDialog.OnCategorySelectedCallback {
    private ActivityUserBinding binding;

    private UsersPresenter presenter;
    private User user;
    private Category selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new UsersPresenter(this);

        if (getIntent().getExtras().containsKey(Constants.ARG_OBJECT)) {
            user = getIntent().getParcelableExtra(Constants.ARG_OBJECT);
        } else {
            user = new User();
            int userType = getIntent().getIntExtra(Constants.ARG_ID, Constants.USER_TYPE_CLIENT);
            user.setType(userType);
        }
        bind();

        binding.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategorySelectorBottomSheetDialog dialog = CategorySelectorBottomSheetDialog.newInstance(selectedCategory);
                dialog.show(getSupportFragmentManager(), "");
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = binding.password.getText().toString().trim();
                String username = binding.username.getText().toString().trim();
                String phone = binding.phone.getText().toString().trim();
                String address = binding.address.getText().toString().trim();
                String fullName = binding.fullName.getText().toString().trim();

                if (user.getId() == null) {
                    if (password.isEmpty() || password.length() < 6) {
                        binding.password.setError(getString(R.string.str_password_length_invalid));
                        binding.password.requestFocus();
                        return;
                    }

                    if (username.isEmpty()) {
                        binding.username.setError(getString(R.string.str_username_invalid));
                        binding.username.requestFocus();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                        binding.username.setError(getString(R.string.str_username_invalid));
                        binding.username.requestFocus();
                        return;
                    }
                }

                if (fullName.isEmpty()) {
                    binding.phone.setError(getString(R.string.str_full_name_invalid));
                    binding.phone.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    binding.fullName.setError(getString(R.string.str_phone_invalid));
                    binding.fullName.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    binding.address.setError(getString(R.string.str_address_invalid));
                    binding.address.requestFocus();
                    return;
                }

                user.setUsername(username);
                user.setPassword(password);
                user.setFullName(fullName);
                user.setPhone(phone);
                user.setAddress(address);

                if (user.getId() == null) {
                    presenter.signup(user);
                } else {
                    presenter.save(user);
                }
            }
        });
    }

    @Override
    public void onSaveUserComplete() {
        Toast.makeText(this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onSignupUserComplete() {
        Toast.makeText(this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onSignupUserFail(String message) {
        Toast.makeText(this, getString(R.string.str_save_fail, message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, getString(R.string.str_save_fail, message), Toast.LENGTH_LONG).show();
    }

    private void bind() {
        binding.type.setText(UIUtils.getAccountType(user.getType()));
        binding.username.setEnabled(user.getId() == null);
        binding.password.setEnabled(user.getId() == null);

        binding.username.setText(user.getUsername());
        binding.password.setText(user.getPassword());
        binding.fullName.setText(user.getFullName());
        binding.address.setText(user.getAddress());
        binding.phone.setText(user.getPhone());
        binding.category.setText(user.getCategoryName());
        if (user.getId() != null) {
            selectedCategory = new Category(user.getCategoryId(), user.getCategoryName());
        }
    }

    @Override
    public void onCategorySelectedCallback(Category category) {
        selectedCategory = category;
        user.setCategoryId(category.getId());
        user.setCategoryName(category.getName());
        binding.category.setText(user.getCategoryName());
    }
}