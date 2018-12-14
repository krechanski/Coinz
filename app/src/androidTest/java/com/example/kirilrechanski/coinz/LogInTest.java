package com.example.kirilrechanski.coinz;


import android.Manifest;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.filters.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mapbox.services.android.navigation.ui.v5.feedback.FeedbackBottomSheet.TAG;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LogInTest {

    @Rule
    public ActivityTestRule<LogInActivity> mActivityTestRule = new ActivityTestRule<>(LogInActivity.class);

    @Rule
    public GrantPermissionRule permissionsRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE);

    @Before
    public void createAccount() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword("j@gmail.com", "123456")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        if (currentUser != null) {
                            mDatabase.collection("users").document(currentUser.getUid())
                                    .set(new User("j@gmail.com",0, 25,0,0,
                                            new ArrayList<>(), new ArrayList<>(), false,
                                            false, 5, false));

                        }

                    }
                });


    }

    @Test
    public void logInTest() {
        onView(withId(R.id.fieldEmail)).perform(typeText("j@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.fieldPassword)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.emailSignInButton)).perform(click());
        SystemClock.sleep(5000);
        onView(withId(R.id.navEmail)).check(matches(withText("j@gmail.com")));
        SystemClock.sleep(5000);
    }

    @After
    public void signOutAndDelete() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user;
        user = mAuth.getCurrentUser();
        mAuth.signOut();
        if (user != null) {
            db.collection("users").document(user.getUid()).delete();
            user.delete();
        }

    }

}
