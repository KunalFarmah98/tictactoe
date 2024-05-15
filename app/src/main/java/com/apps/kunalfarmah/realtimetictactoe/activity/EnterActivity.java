package com.apps.kunalfarmah.realtimetictactoe.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.apps.kunalfarmah.realtimetictactoe.fragments.InterstitialFragment;
import com.apps.kunalfarmah.realtimetictactoe.fragments.HowToPlayFragment;
import com.apps.kunalfarmah.realtimetictactoe.util.Utils;
import com.example.kunalfarmah.realtimetictactoe.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;


import java.util.Arrays;
import java.util.List;

public class EnterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    Button offline;
    Button online;
    ImageView info;
    Activity self;

    private boolean isLoginFlowActive = false;
    Menu menu;
    // a variable to check if we are inside teh host or join screen
    public FrameLayout fragments;

    static String User;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            MenuCompat.setGroupDividerEnabled(menu, false);
            menu.removeGroup(R.id.account_group);
        }
        return true;
    }

    private void removeFragments(){
        // removing all fragments after sign in
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        // if a fragment was open during sign out, remove it
        if (fragments.getVisibility() == View.VISIBLE)
            fragments.setVisibility(View.GONE);

        invalidateOptionsMenu();
    }

    private void signout(){
        AuthUI.getInstance().signOut(this);
        FirebaseAuth.getInstance().signOut();
        removeFragments();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
                signout();
                Toast.makeText(getApplicationContext(), "Signed Out Successfully!!", Toast.LENGTH_SHORT).show();
                return true;


            case R.id.delete_account:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account?\nYou will not be able to recover it and all your data would be deleted.")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            AuthUI.getInstance().delete(this).addOnSuccessListener(
                                    aVoid -> {
                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(getApplicationContext(), "Account Deleted Successfully!!", Toast.LENGTH_SHORT).show();
                                        removeFragments();
                                        invalidateOptionsMenu();
                                    }
                            )
                                    .addOnFailureListener(
                                            e -> {
                                                if(e instanceof FirebaseAuthRecentLoginRequiredException) {
                                                    Toast.makeText(getApplicationContext(), "Account Verification Required. Please sign In again", Toast.LENGTH_SHORT).show();
                                                    signout();
                                                    online.callOnClick();
                                                }
                                                else
                                                    Toast.makeText(getApplicationContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                                            }
                                    );
                        }
                        )
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
                return true;

            case R.id.about_dev:

                Intent about = new Intent(Intent.ACTION_VIEW);
                about.setData(Uri.parse("https://www.kunalfarmah.com/"));
                startActivity(about);
                return true;


            case R.id.privacy:
                about = new Intent(Intent.ACTION_VIEW);
                about.setData(Uri.parse("https://www.kunalfarmah.com/contact-me/"));
                startActivity(about);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.activity_enter);

        setSupportActionBar((androidx.appcompat.widget.Toolbar) findViewById(R.id.my_toolbar));

        offline = findViewById(R.id.offline);
        online = findViewById(R.id.online);
        info = findViewById(R.id.how_to_play);
        fragments = findViewById(R.id.fragment_containter);


        offline.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(intent);
        });

        online.setOnClickListener(v -> {


            if (Utils.hasActiveInternetConnection(self)) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // if user is logged in continue
                if (user != null) {
                    User = user.getDisplayName();
                    if(User=="null"){
                        User = "New User";
                    }
                    Toast.makeText(getApplicationContext(), "Welcome " + User + " :)", Toast.LENGTH_SHORT).show();
                    InterstitialFragment interstitial = new InterstitialFragment();
                    fragments.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, interstitial).addToBackStack("Interstitial").commit();

                } else {
                    isLoginFlowActive = true;
                    // Choose authentication providers if user is not logged in
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setLogo(R.drawable.logo)
                                    .setIsSmartLockEnabled(false)
                                    .setTheme(R.style.AppTheme)
                                    .build(),
                            RC_SIGN_IN);


                }
            } else {
                Toast.makeText(getApplicationContext(), "Please connect your device to the internet to continue :)", Toast.LENGTH_SHORT).show();
            }
        });


        info.setOnClickListener(v -> {

            HowToPlayFragment play = new HowToPlayFragment();
            fragments.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, play).addToBackStack("info").commit();
        });
    }


    @Override
    public void onBackPressed() {
        if(isLoginFlowActive){
            isLoginFlowActive = false;
            return;
        }
        super.onBackPressed();
        // removing the fragment if back is pressed on the host or join screen
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            fragments.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                try {
                    User = user.getDisplayName();
                    if(User=="null"){
                        User = "New User";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (user != null) {
                    Toast.makeText(getApplicationContext(), "Signed In Successfully as " +User, Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();
                    InterstitialFragment interstitial = new InterstitialFragment();
                    fragments.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, interstitial).addToBackStack("Interstitial").commit();
                    isLoginFlowActive = false;
                } else {
                    Toast.makeText(getApplicationContext(), "Please Sign In", Toast.LENGTH_SHORT).show();
                    isLoginFlowActive = true;
                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    //.setIsSmartLockEnabled(true)
                                    .setLogo(R.drawable.logo)
                                    .build(),
                            RC_SIGN_IN);
                }
                // ...
            } else {

                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                onBackPressed();


            }
        }
    }





    @Override
    protected void onStop() {
        super.onStop();
    }
}






