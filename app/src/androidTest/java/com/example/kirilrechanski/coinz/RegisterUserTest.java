package com.example.kirilrechanski.coinz;


import android.Manifest;
import android.content.ComponentName;
import android.os.SystemClock;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.filters.LargeTest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.init;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.protobuf.WireFormat.FieldType.MESSAGE;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RegisterUserTest {

    @Rule
    public IntentsTestRule<LogInActivity> mLoginActivityActivityTestRule =
            new IntentsTestRule<>(LogInActivity.class);

    @Rule
    public GrantPermissionRule permissionsRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE);

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

    @Test
    public void enterEmailPassword() {
        onView(withId(R.id.fieldEmail)).perform(typeText("j@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.fieldPassword)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.emailCreateAccountButton)).perform(click());
        SystemClock.sleep(2500);
        onView(withId(R.id.usernameField)).perform(typeText("ken"), closeSoftKeyboard());
        onView(withId(R.id.createUserName)).perform(click());
        SystemClock.sleep(10000);
        onView(withId(R.id.navUsername)).check(matches(withText("ken")));

    }





}
