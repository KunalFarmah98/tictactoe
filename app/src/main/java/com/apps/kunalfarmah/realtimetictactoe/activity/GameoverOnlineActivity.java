package com.apps.kunalfarmah.realtimetictactoe.activity;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.kunalfarmah.realtimetictactoe.util.Utils;
import com.example.kunalfarmah.realtimetictactoe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameoverOnlineActivity extends AppCompatActivity {

    FirebaseDatabase mdata;
    DatabaseReference closeref;
    DatabaseReference restartref;
    String token;
    ImageView close;

    TextView time;

    Activity self;
    int difficulty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.gameover);

        OnlineActivity.isover = true;

        time = findViewById(R.id.time);
        ImageView replay = findViewById(R.id.repeat);
        close = findViewById(R.id.close);

        difficulty = getIntent().getIntExtra("difficulty",2);
        token = getIntent().getStringExtra("token");

        replay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                restartref.setValue(true);
               /*  //this function will only close the game over activity and will restart from the main activity
                Intent start = new Intent(getApplicationContext(),onlineActivity.class);
                start.putExtra("isHost",ishost);
                startActivity(start);
                System.exit(0);*/
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                restartref.setValue(false);
                closeref.setValue(true);
            }
        });



        try {
            final String ishost = getIntent().getSerializableExtra("isHost").toString();

            final String timeval = getIntent().getStringExtra("Time");

            time.setText("Time : " + timeval);


            mdata = FirebaseDatabase.getInstance();
            closeref = mdata.getReference("isClosed");
            closeref.setValue(false);
            restartref = mdata.getReference("isRestarted");
            restartref.setValue(false);

            restartref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {
                        Boolean restarted = dataSnapshot.getValue(Boolean.class);
                        if (restarted) {
                            if (Utils.hasActiveInternetConnection(self)) {
                                Intent start = new Intent(getApplicationContext(), OnlineActivity.class);
                                start.putExtra("isHost", ishost);
                                start.putExtra("difficulty", difficulty);
                                start.putExtra("token",token);
                                startActivity(start);
                            } else {
                                Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }


                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            closeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {


                        Boolean closed = dataSnapshot.getValue(Boolean.class);

                        if (closed) {
                            startActivity(new Intent(getApplicationContext(),EnterActivity.class));
                            finish();
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

         catch ( Exception e){

            }

        }

//    public void restartActivity(){
//        //finishing current activity and then restarting it
//        Intent mIntent = getIntent();
//        finish();
//        startActivity(mIntent);


    @Override
    protected void onStop() {
        super.onStop();
        restartref.removeValue();
        closeref.removeValue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mdata.goOffline();

    }

    @Override
    public void onBackPressed() {
       close.performClick();
    }
}

