package com.consult.me.app.utilities;

import com.consult.me.app.Constants;
import com.consult.me.app.models.User;
import com.consult.me.app.R;
import com.consult.me.app.models.About;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataManager {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final DatabaseReference NODE_USERS = database.getReference(Constants.NODE_NAME_USERS);

    public static void initUserAdmin() {
        User user = new User();
        user.setId("AZFehdzSocXbhHprhpmYdtn3fRZ2");
        user.setUsername("admin@medicine.com");
        user.setPassword("123456");
        user.setFullName("Admin");
        user.setType(1);
        NODE_USERS.child("AZFehdzSocXbhHprhpmYdtn3fRZ2").setValue(user);
    }
    int[] resources = new int[]{
            R.layout.activity_about_edit,
            R.layout.activity_forget_password,
            R.layout.activity_grade_details,
            R.layout.activity_home_admin,
            R.layout.activity_home_doctor,
            R.layout.activity_home_patient,
            R.layout.activity_login,
            R.layout.activity_messaging,
            R.layout.activity_profile_edit,
            R.layout.activity_registration,
            R.layout.activity_reviews,
            R.layout.activity_question,
            R.layout.activity_question_answers,
            R.layout.activity_splash,
            R.layout.activity_user,
            R.layout.bottom_sheet_review,
            R.layout.content_home,
            R.layout.fragment_bottom_sheet_category_selector,
            R.layout.fragment_bottom_sheet_dialog_answer,
            R.layout.fragment_categories,
            R.layout.fragment_contacts_bottom_sheet_dialog,
            R.layout.fragment_craftsmans,
            R.layout.fragment_grades,
            R.layout.fragment_notifications,
            R.layout.fragment_questions,
            R.layout.fragment_questions_created,
            R.layout.fragment_users,
            R.layout.item_category,
            R.layout.item_category_selector,
            R.layout.item_chat,
            R.layout.item_chat_other,
            R.layout.item_craftsman,
            R.layout.item_event_viewer,
            R.layout.item_notification,
            R.layout.item_answer,
            R.layout.item_review,
            R.layout.item_question,
            R.layout.item_student,
            R.layout.item_user,
            R.layout.layout_app_bar,
            R.layout.layout_loading,
            R.layout.nav_header_main,
    };

    public static void initAbout() {
        About aboutEn = new About();
        About aboutAr = new About();

        aboutEn.setContent("");
        aboutEn.setConditions("");
        aboutEn.setObjectives("");
        aboutAr.setContent("");
        aboutAr.setConditions("");
        aboutAr.setObjectives("");

        database.getReference(Constants.NODE_NAME_ABOUT).child("en").setValue(aboutEn);
        database.getReference(Constants.NODE_NAME_ABOUT).child("ar").setValue(aboutAr);
    }
}
