package com.consult.me.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.consult.me.app.Constants;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String imageProfile;
    private int type;
    private String token;
    private String description;
    private String categoryId;
    private String categoryName;
    private Contacts contacts;
    private List<String> services;
    private List<Review> reviews;

    public User() {
        this(null, null, null, null, null, null, null, null, 0, "");
    }

    public User(String id) {
        this(id, null, null, null, null, null, null, null, 0, "");
    }

    public User(String username, String password) {
        this(null, username, password, null, null, username, null, null, 0, "");
    }

    public User(String username, String password, String fullName, String phone) {
        this(null, username, password, fullName, phone, username, null, null, 0, "");
    }

    public User(String id, String username, String password, String fullName, String phone, String email, String address, String imageProfile, int type, String description) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.imageProfile = imageProfile;
        this.type = type;
        this.description = description;
        this.contacts = new Contacts();
        this.services = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isConsultant() {
        return Constants.USER_TYPE_CONSULTANT == type;
    }

    public boolean isClient() {
        return Constants.USER_TYPE_CLIENT == type;
    }

    public boolean isAdmin() {
        return Constants.USER_TYPE_ADMIN == type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Review> getReviews() {
        if (reviews == null) {
            setReviews(new ArrayList<>());
        }
        return reviews;
    }

    public float getRating() {
        float value = 0;
        if (reviews == null) {
            setReviews(new ArrayList<>());
        }
        for (var rating : reviews) {
            value += rating.getValue();
        }

        return reviews.isEmpty() ? 0 : value / reviews.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.fullName);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.address);
        dest.writeString(this.imageProfile);
        dest.writeInt(this.type);
        dest.writeString(this.token);
        dest.writeString(this.description);
        dest.writeString(this.categoryId);
        dest.writeString(this.categoryName);
        dest.writeParcelable(this.contacts, flags);
        dest.writeStringList(this.services);
        dest.writeTypedList(this.reviews);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.username = source.readString();
        this.password = source.readString();
        this.fullName = source.readString();
        this.phone = source.readString();
        this.email = source.readString();
        this.address = source.readString();
        this.imageProfile = source.readString();
        this.type = source.readInt();
        this.token = source.readString();
        this.description = source.readString();
        this.categoryId = source.readString();
        this.categoryName = source.readString();
        this.contacts = source.readParcelable(Contacts.class.getClassLoader());
        this.services = source.createStringArrayList();
        this.reviews = source.createTypedArrayList(Review.CREATOR);
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.fullName = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.address = in.readString();
        this.imageProfile = in.readString();
        this.type = in.readInt();
        this.token = in.readString();
        this.description = in.readString();
        this.categoryId = in.readString();
        this.categoryName = in.readString();
        this.contacts = in.readParcelable(Contacts.class.getClassLoader());
        this.services = in.createStringArrayList();
        this.reviews = in.createTypedArrayList(Review.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
