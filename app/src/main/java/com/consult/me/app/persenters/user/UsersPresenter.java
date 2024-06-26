package com.consult.me.app.persenters.user;

import androidx.annotation.NonNull;

import com.consult.me.app.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.consult.me.app.models.User;
import com.consult.me.app.persenters.BasePresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UsersPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private UsersCallback callback;

    public UsersPresenter(UsersCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_USERS).getRef();
        this.callback = callback;
    }

    public void getUsersCount() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long adminCount = 0, caraftsCount = 0, clientsCount = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getType() == Constants.USER_TYPE_ADMIN) {
                        adminCount++;
                    } else if (user.getType() == Constants.USER_TYPE_CONSULTANT) {
                        caraftsCount++;
                    } else {
                        clientsCount++;
                    }
                }
                callback.onGetUsersCountComplete(adminCount, caraftsCount, clientsCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void signup(User user) {
        if (callback != null) {
            callback.onShowLoading();
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getUsername(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = task.getResult().getUser().getUid();
                    user.setId(id);
                    save(user);

                    if (callback != null) {
                        callback.onSignupUserComplete();
                    }
                } else {
                    if (callback != null) {
                        callback.onSignupUserFail(task.getException().getMessage());
                    }
                }
                if (callback != null) {
                    callback.onHideLoading();
                }
            }
        });
    }

    public void save(User... users) {
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.NODE_NAME_USERS);
        for (User user : users) {
            node.child(user.getId()).setValue(user);
        }
        if (callback != null) {
            callback.onSaveUserComplete();
        }
    }

    public void getUsers(int type) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getType() == type || type == 0) {
                        users.add(user);
                    }
                }

                if (callback != null) {
                    Collections.sort(users, new Comparator<User>() {
                        @Override
                        public int compare(User o1, User o2) {
                            try {
                                return o1.getFullName().compareTo(o2.getFullName());
                            } catch (Exception ex) {
                            }
                            return 0;
                        }
                    });
                    callback.onGetUsersComplete(users);
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
        reference.addListenerForSingleValueEvent(listener);
    }

    public void getUsers(List<String> ids) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (ids.contains(user.getId())) {
                        users.add(user);
                    }
                }

                if (callback != null) {
                    Collections.sort(users, new Comparator<User>() {
                        @Override
                        public int compare(User o1, User o2) {
                            return o1.getFullName().compareTo(o2.getFullName());
                        }
                    });
                    callback.onGetUsersComplete(users);
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
        reference.addListenerForSingleValueEvent(listener);
    }

    public void getUserById(String id) {
        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (callback != null) {
                    callback.onGetUserComplete(snapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserByUsername(String username) {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getUsername().equalsIgnoreCase(username)) {
                        callback.onGetUserComplete(user);
                    }
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
        reference.addListenerForSingleValueEvent(listener);
    }

    public void delete(User user) {
        reference.child(user.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onDeleteUserComplete(user);
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

    @Override
    public void onDestroy() {
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
