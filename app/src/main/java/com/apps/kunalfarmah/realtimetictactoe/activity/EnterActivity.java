package com.apps.kunalfarmah.realtimetictactoe.activity;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.apps.kunalfarmah.realtimetictactoe.fragments.InterstitialFragment;
import com.apps.kunalfarmah.realtimetictactoe.fragments.HowToPlayFragment;
import com.example.kunalfarmah.realtimetictactoe.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class EnterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    Button offline;
    Button online;
    ImageView info;
    Menu menu;
    // a variable to check if we are inside teh host or join screen
    public FrameLayout fragments;

    static String User;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            menu.removeItem(R.id.signout);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
                AuthUI.getInstance().signOut(this);
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Signed Out Successfully!!", Toast.LENGTH_SHORT).show();

                // removing all fragments after sign in
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    getSupportFragmentManager().popBackStack();
                }

                // if a fragemnt was open during sign out, remove it
                if (fragments.getVisibility() == View.VISIBLE)
                    fragments.setVisibility(View.GONE);

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


            if (hasActiveInternetConnection(getApplicationContext())) {

                InterstitialFragment interstitial = new InterstitialFragment();
                fragments.setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, interstitial).addToBackStack("Interstitial").commit();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // if user is logged in continue
                if (user != null) {
                    User = user.getDisplayName();
                    if(User=="null"){
                        User = "New User";
                    }
                    Toast.makeText(getApplicationContext(), "Welcome " + User + " :)", Toast.LENGTH_SHORT).show();

                } else {

                    // Choose authentication providers if user is not logged in
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.PhoneBuilder().build(),
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
                } else {
                    Toast.makeText(getApplicationContext(), "Please Sign In", Toast.LENGTH_SHORT).show();

                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.PhoneBuilder().build(),
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


    // checks if it is connceted to a network
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    // this checks if connection has internet access

    public boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable()) {

            try {
                // forcefully using network on main thread
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                StrictMode.setThreadPolicy(policy);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //Log.d(LOG_TAG, "No network available!");
        }
        return false;
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}






