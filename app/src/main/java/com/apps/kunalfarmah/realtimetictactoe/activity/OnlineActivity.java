package com.apps.kunalfarmah.realtimetictactoe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.kunalfarmah.realtimetictactoe.model.ImagesBox;
import com.example.kunalfarmah.realtimetictactoe.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class OnlineActivity extends AppCompatActivity implements View.OnClickListener {

    int[][] moves = new int[][]{{-1, -1, -1}, {-1, -1, -1}, {-1, -1, -1}};

    ImageView i1;
    ImageView i2;
    ImageView i3;
    ImageView i4;
    ImageView i5;
    ImageView i6;
    ImageView i7;
    ImageView i8;
    ImageView i9;
    
    OnlineActivity self;

    TextView player1;
    TextView player2;
    TextView movescount;
    LinearLayout timer;
    String token;


    static boolean isover;

    Timer t;


    static int minutes, seconds;
    static int timeval;

    TextView min, sec;

    ImageView hosticon;
    ImageView awayicon;


    String hostName;
    String awayName;
    String pl1;
    String pl2;

    int c = 0;

    String ishost = "";
    int difficulty;

    // String turn="";

    FirebaseDatabase mdata;
    // reference for the moves
    DatabaseReference ref, host, away, crash, movesRef;

    // DatabaseReference closeref;
    // reference for the turns
    DatabaseReference turn;

    // a reference to a victory
    DatabaseReference iswin;


    ChildEventListener movelistener;

    SharedPreferences timepref;
    SharedPreferences.Editor medit;

    boolean host_turn;
    boolean win;


    int steps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        self = this;
        
        timepref = getSharedPreferences("timeval", MODE_PRIVATE);
        medit = timepref.edit();

        isover = false;


        difficulty = getIntent().getIntExtra("difficulty", 2);
        token = getIntent().getStringExtra("token");
        //Declare the timer


        player1 = findViewById(R.id.textView);
        player2 = findViewById(R.id.textView2);

        hosticon = findViewById(R.id.host);
        awayicon = findViewById(R.id.away);


        movescount = findViewById(R.id.moves);

        timer = findViewById(R.id.timer);
        timer.setVisibility(View.VISIBLE);

        min = findViewById(R.id.minutes);
        sec = findViewById(R.id.seconds);


//        player1.setText(pl1);
//        player2.setText(pl2);

        mdata = FirebaseDatabase.getInstance();

        if (null == token || token.isEmpty()) {
            token = "default";
        }
        movesRef = mdata.getReference("Moves").child(token);

        host = mdata.getReference("HostName");

        host.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hostName = dataSnapshot.getValue(String.class);
                player1.setText((hostName == "null" || hostName == null) ? "Host" : hostName + " : X");
                Log.d("Host", hostName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        away = mdata.getReference("AwayName");

        away.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                awayName = dataSnapshot.getValue(String.class);
                player2.setText((awayName == "null" || awayName == null) ? "Away" : awayName + " : X");
                Log.d("Away", awayName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        pl1 = hostName + " : X";
        pl2 = awayName + " : O";

        if (pl1.equals("null : X")) {
            pl1 = "Host : X";
        }

        if (pl2.equals("null : O")) {
            pl2 = "Away : O";
        }

        if (player1.getText().toString().equals("null : X")) {
            player1.setText("Host : X");
        }

        if (player2.getText().toString().equals("null : O")) {
            player2.setText("Away : 0");
        }

        // finding which one is host;
        ishost = getIntent().getSerializableExtra("isHost").toString();

        // setting host to be true for host and false for joiner


        turn = mdata.getReference("Host");
        turn.setValue(true);

        iswin = mdata.getReference("Win");
        iswin.setValue(" ");

        // handling if any one device crashes during the game

        crash = mdata.getReference("Crash");
        crash.setValue(false);

        crash.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean crash = dataSnapshot.getValue(boolean.class);

                if (crash == true) {

                    t.cancel();
                    t.purge();
                    isover = true;
                    startActivity(new Intent(self,EnterActivity.class));
                    finish();
                    Toast.makeText(self, "A User left the server :( Please restart the game to play again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        i1 = findViewById(R.id.imageView1);
        i2 = findViewById(R.id.imageView2);
        i3 = findViewById(R.id.imageView3);
        i4 = findViewById(R.id.imageView4);
        i5 = findViewById(R.id.imageView5);
        i6 = findViewById(R.id.imageView6);
        i7 = findViewById(R.id.imageView7);
        i8 = findViewById(R.id.imageView8);
        i9 = findViewById(R.id.imageView9);

        setDefaults();


        t = new Timer();

        if (difficulty == 1) {
            seconds = 20;
            timeval = seconds;
        } else if (difficulty == 2) {
            seconds = 15;
            timeval = seconds;
        } else if (difficulty == 3) {
            seconds = 10;
            timeval = seconds;
        } else {
            seconds = 20;
            timeval = seconds;
        }

        sec.setText(String.valueOf(seconds));


        //Set the schedule function and rate
        // code to run a timer in game


        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        ++c;

                        Log.d("timeval", String.valueOf(timeval));
                        min.setText(String.valueOf(minutes));
                        isover = false;
                        if (timeval < 10)
                            sec.setText("0" + timeval);
                        else
                            sec.setText(String.valueOf(timeval));
                        timeval = timeval - 1;

                        if (timeval == 0) {
                            isover = true;

                            Toast.makeText(self, "Drawn!!", Toast.LENGTH_LONG).show();
                            t.cancel();
                            t.purge();
//                            finish();

//                            timeval = seconds;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent gameover = new Intent(self, GameoverOnlineActivity.class);
                                    gameover.putExtra("isHost", ishost);
                                    gameover.putExtra("Time", min.getText() + " : " + seconds);
                                    gameover.putExtra("Crash", false);
                                    gameover.putExtra("difficulty", difficulty);
                                    gameover.putExtra("token",token);
                                    isover = true;
                                    startActivity(gameover);
                                    finish();


                                }
                            }, 1400);
                        }
                    }


                });
            }

        }, 0, 1000);


        turn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean hostTurn = dataSnapshot.getValue(Boolean.class);

                host_turn = hostTurn;


                // if its host device and hosts turn , disable all the already set values

                if (ishost.equalsIgnoreCase("True") && hostTurn) {

                    awayicon.setVisibility(View.INVISIBLE);
                    hosticon.setVisibility(View.VISIBLE);

                    settingclicklisteners();

                    if (moves[0][0] != -1) {
                        i1.setClickable(false);
                    }
                    if (moves[0][1] != -1) {
                        i2.setClickable(false);
                    }
                    if (moves[0][2] != -1) {
                        i3.setClickable(false);
                    }
                    if (moves[1][0] != -1) {
                        i4.setClickable(false);
                    }
                    if (moves[1][1] != -1) {
                        i5.setClickable(false);
                    }
                    if (moves[1][2] != -1) {
                        i6.setClickable(false);
                    }
                    if (moves[2][0] != -1) {
                        i7.setClickable(false);
                    }
                    if (moves[2][1] != -1) {
                        i8.setClickable(false);
                    }
                    if (moves[2][2] != -1) {
                        i9.setClickable(false);
                    }

                    //   turn.setValue(false);

                }

                // if it is host device and aways turn disable all clicks for host
                if (ishost.equalsIgnoreCase("True") && !hostTurn) {

                    awayicon.setVisibility(View.VISIBLE);
                    hosticon.setVisibility(View.INVISIBLE);

                    settingclicklisteners();
                    i1.setClickable(false);
                    i2.setClickable(false);
                    i3.setClickable(false);
                    i4.setClickable(false);
                    i5.setClickable(false);
                    i6.setClickable(false);
                    i7.setClickable(false);
                    i8.setClickable(false);
                    i9.setClickable(false);

                    //    turn.setValue(true);

                }


                // if it is away device and away turn, disable all the already clicked values

                if (!ishost.equalsIgnoreCase("True") && !hostTurn) {

                    awayicon.setVisibility(View.VISIBLE);
                    hosticon.setVisibility(View.INVISIBLE);

                    settingclicklisteners();

                    if (moves[0][0] != -1) {
                        i1.setClickable(false);
                    }
                    if (moves[0][1] != -1) {
                        i2.setClickable(false);
                    }
                    if (moves[0][2] != -1) {
                        i3.setClickable(false);
                    }
                    if (moves[1][0] != -1) {
                        i4.setClickable(false);
                    }
                    if (moves[1][1] != -1) {
                        i5.setClickable(false);
                    }
                    if (moves[1][2] != -1) {
                        i6.setClickable(false);
                    }
                    if (moves[2][0] != -1) {
                        i7.setClickable(false);
                    }
                    if (moves[2][1] != -1) {
                        i8.setClickable(false);
                    }
                    if (moves[2][2] != -1) {
                        i9.setClickable(false);
                    }

                    //  turn.setValue(true);

                }


                // if it is away device and host turn, disable all clicks for away

                if (!ishost.equalsIgnoreCase("True") && hostTurn) {

                    awayicon.setVisibility(View.INVISIBLE);
                    hosticon.setVisibility(View.VISIBLE);

                    settingclicklisteners();

                    i1.setClickable(false);
                    i2.setClickable(false);
                    i3.setClickable(false);
                    i4.setClickable(false);
                    i5.setClickable(false);
                    i6.setClickable(false);
                    i7.setClickable(false);
                    i8.setClickable(false);
                    i9.setClickable(false);

                    // turn.setValue(false);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        if (ishost.equals("True"))
//            turn.setValue(true);
//
//        else
//            turn.setValue(false);


        /** child event listener for the data in the image views*/

        movelistener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // Get imgbox object and use the values to update the UI with either o or x
                try {

                    ImagesBox val = dataSnapshot.getValue(ImagesBox.class);

                    int o_or_x = val.value;

                    switch (val.imgvw) {
                        case 1:
                            if (o_or_x == 0) {
                                i1.setImageResource(R.drawable.o);
                                //moves[0][0] = 0;
                            } else {
                                i1.setImageResource(R.drawable.x);
                                // moves[0][0] = 1;
                            }
                            break;
                        case 2:
                            if (o_or_x == 0) {
                                i2.setImageResource(R.drawable.o);
                                // moves[0][1] = 0;
                            } else {
                                i2.setImageResource(R.drawable.x);
                                //   moves[0][1] = 1;
                            }
                            break;
                        case 3:
                            if (o_or_x == 0) {
                                i3.setImageResource(R.drawable.o);
                                //  moves[0][2] = 0;
                            } else {
                                i3.setImageResource(R.drawable.x);
                                // moves[0][2] = 1;
                            }
                            break;
                        case 4:
                            if (o_or_x == 0) {
                                i4.setImageResource(R.drawable.o);
                                //  moves[1][0] = 0;
                            } else {
                                i4.setImageResource(R.drawable.x);
                                //  moves[1][0] = 1;
                            }
                            break;
                        case 5:
                            if (o_or_x == 0) {
                                i5.setImageResource(R.drawable.o);
                                //  moves[1][1] = 0;
                            } else {
                                i5.setImageResource(R.drawable.x);
                                //  moves[1][1] = 1;
                            }
                            break;
                        case 6:
                            if (o_or_x == 0) {
                                i6.setImageResource(R.drawable.o);
                                //  moves[1][2] = 0;
                            } else {
                                i6.setImageResource(R.drawable.x);
                                //  moves[1][2] = 1;
                            }
                            break;
                        case 7:
                            if (o_or_x == 0) {
                                i7.setImageResource(R.drawable.o);
                                //   moves[2][0] = 0;
                            } else {
                                i7.setImageResource(R.drawable.x);
                                //   moves[2][0] = 1;
                            }
                            break;
                        case 8:
                            if (o_or_x == 0) {
                                i8.setImageResource(R.drawable.o);
                                //  moves[2][1] = 0;
                            } else {
                                i8.setImageResource(R.drawable.x);
                                //   moves[2][1] = 1;
                            }
                            break;
                        case 9:
                            if (o_or_x == 0) {
                                i9.setImageResource(R.drawable.o);
                                //  moves[2][2] = 0;
                            } else {
                                i9.setImageResource(R.drawable.x);
                                //  moves[2][2] = 1;
                            }
                            break;

                        default:
                            break;
                    }
                } catch (Exception e) {
                }

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // Get imgbox object and use the values to update the UI with either o or x


                ImagesBox val = dataSnapshot.getValue(ImagesBox.class);

                int o_or_x = val.value;

                switch (val.imgvw) {
                    case 1:
                        if (o_or_x == 0) {
                            i1.setImageResource(R.drawable.o);
                            //moves[0][0] = 0;
                        } else {
                            i1.setImageResource(R.drawable.x);
                            // moves[0][0] = 1;
                        }

                        i1.setEnabled(false);
                        break;
                    case 2:
                        if (o_or_x == 0) {
                            i2.setImageResource(R.drawable.o);
                            // moves[0][1] = 0;
                        } else {
                            i2.setImageResource(R.drawable.x);
                            //   moves[0][1] = 1;
                        }
                        i2.setEnabled(false);
                        break;
                    case 3:
                        if (o_or_x == 0) {
                            i3.setImageResource(R.drawable.o);
                            //  moves[0][2] = 0;
                        } else {
                            i3.setImageResource(R.drawable.x);
                            // moves[0][2] = 1;
                        }
                        i3.setEnabled(false);
                        break;
                    case 4:
                        if (o_or_x == 0) {
                            i4.setImageResource(R.drawable.o);
                            //  moves[1][0] = 0;
                        } else {
                            i4.setImageResource(R.drawable.x);
                            //  moves[1][0] = 1;
                        }
                        i4.setEnabled(false);
                        break;
                    case 5:
                        if (o_or_x == 0) {
                            i5.setImageResource(R.drawable.o);
                            //  moves[1][1] = 0;
                        } else {
                            i5.setImageResource(R.drawable.x);
                            //  moves[1][1] = 1;
                        }
                        i5.setEnabled(false);
                        break;
                    case 6:
                        if (o_or_x == 0) {
                            i6.setImageResource(R.drawable.o);
                            //  moves[1][2] = 0;
                        } else {
                            i6.setImageResource(R.drawable.x);
                            //  moves[1][2] = 1;
                        }
                        i6.setEnabled(false);
                        break;
                    case 7:
                        if (o_or_x == 0) {
                            i7.setImageResource(R.drawable.o);
                            //   moves[2][0] = 0;
                        } else {
                            i7.setImageResource(R.drawable.x);
                            //   moves[2][0] = 1;
                        }
                        i7.setEnabled(false);
                        break;
                    case 8:
                        if (o_or_x == 0) {
                            i8.setImageResource(R.drawable.o);
                            //  moves[2][1] = 0;
                        } else {
                            i8.setImageResource(R.drawable.x);
                            //   moves[2][1] = 1;
                        }
                        i8.setEnabled(false);
                        break;
                    case 9:
                        if (o_or_x == 0) {
                            i9.setImageResource(R.drawable.o);
                            //  moves[2][2] = 0;
                        } else {
                            i9.setImageResource(R.drawable.x);
                            //  moves[2][2] = 1;
                        }
                        i9.setEnabled(false);
                        break;

                    default:
                        break;
                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        iswin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    //  timeval =seconds;
                    isover = true;
                    String hostname = player1.getText().toString();
                    String awayname = player2.getText().toString();
                    int i1 = hostname.indexOf(":");
                    int i2 = awayname.indexOf(":");
                    hostname = hostName.substring(0, i1 - 1);
                    awayname = awayName.substring(0, i2 - 1);

                    String winner = dataSnapshot.getValue(String.class);
                    if (winner.equalsIgnoreCase("Host")) {
                        t.cancel();
                        t.purge();
                        isover = true;

                        Toast.makeText(self, hostname + " Wins", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent gameover = new Intent(self, GameoverOnlineActivity.class);
                                gameover.putExtra("isHost", ishost);
                                gameover.putExtra("Time", min.getText() + " : " + ((c < 10) ? ("0" + c) : c));
                                gameover.putExtra("Crash", false);
                                gameover.putExtra("difficulty", difficulty);
                                gameover.putExtra("token",token);

//                                    finish();

                                startActivity(gameover);
                                finish();
                            }
                        }, 1400);
                    } else if (winner.equalsIgnoreCase("Away")) {
                        t.cancel();
                        t.purge();
                        isover = true;

                        Toast.makeText(self, awayname + " Wins", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent gameover = new Intent(self, GameoverOnlineActivity.class);
                                gameover.putExtra("isHost", ishost);
                                gameover.putExtra("Time", min.getText() + " : " + ((c < 10) ? ("0" + c) : c));
                                gameover.putExtra("Crash", false);
                                gameover.putExtra("difficulty", difficulty);
                                gameover.putExtra("token",token);
//                                finish();

                                startActivity(gameover);
                                finish();
                            }
                        }, 1400);
                    } else if (winner.equalsIgnoreCase("Draw")) {
                        t.cancel();
                        t.purge();
                        isover = true;

                        Toast.makeText(self, "Drawn!!", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent gameover = new Intent(self, GameoverOnlineActivity.class);
                                gameover.putExtra("isHost", ishost);
                                gameover.putExtra("Time", min.getText() + " : " + ((c < 10) ? ("0" + c) : c));
                                gameover.putExtra("Crash", false);
                                gameover.putExtra("difficulty", difficulty);
                                gameover.putExtra("token",token);

//                                finish();

                                startActivity(gameover);
                                finish();
                            }
                        }, 1400);
                    }


                } catch (Exception e) {
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        movesRef.addChildEventListener(movelistener);


    }


    @Override
    public void onClick(View v) {


        movescount.setText("Moves : " + (steps + 1));


        // ref.addChildEventListener(movelistener);


        // mdata = FirebaseDatabase.getInstance();


        //implementing onClick only once for all buttons by using their IDs

        switch (v.getId()) {


            case R.id.imageView1:

                ++steps;

                // host always takes X (1)
                if (ishost.equalsIgnoreCase("True")) {

                    ImagesBox img1 = new ImagesBox(1, 1);
                    movesRef.child("img1").setValue(img1);
                    moves[0][0] = 1;


                }
                // away always takes O (0)
                else {
                    ImagesBox img1 = new ImagesBox(1, 0);
                    movesRef.child("img1").setValue(img1);
                    moves[0][0] = 0;


                }

                if (host_turn)
                    turn.setValue(false);
                else
                    turn.setValue(true);

                break;

            case R.id.imageView2:

                ++steps;


                // host always takes X (1)
                if (ishost.equalsIgnoreCase("True")) {
                    ImagesBox img2 = new ImagesBox(2, 1);
                    movesRef.child("img2").setValue(img2);
                    moves[0][1] = 1;

                }
                // away always takes O (0)
                else {
                    ImagesBox img2 = new ImagesBox(2, 0);
                    movesRef.child("img2").setValue(img2);
                    moves[0][1] = 0;


                }

                if (host_turn)
                    turn.setValue(false);
                else
                    turn.setValue(true);

                break;


            case R.id.imageView3:

                ++steps;


                // host always takes X (1)
                if (ishost.equalsIgnoreCase("True")) {
                    ImagesBox img3 = new ImagesBox(3, 1);
                    movesRef.child("img3").setValue(img3);
                    moves[0][2] = 1;

                }
                // away always takes O (0)
                else {
                    ImagesBox img3 = new ImagesBox(3, 0);
                    movesRef.child("img3").setValue(img3);
                    moves[0][2] = 0;


                }

                if (host_turn)
                    turn.setValue(false);
                else
                    turn.setValue(true);

                break;
            case R.id.imageView4:

                ++steps;


                // host always takes X (1)
                if (ishost.equalsIgnoreCase("True")) {
                    ImagesBox img4 = new ImagesBox(4, 1);
                    movesRef.child("img4").setValue(img4);
                    moves[1][0] = 1;
                }
                // away always takes O (0)
                else {
                    ImagesBox img4 = new ImagesBox(4, 0);
                    movesRef.child("img4").setValue(img4);

                    moves[1][0] = 0;
                }

                if (host_turn)
                    turn.setValue(false);
                else
                    turn.setValue(true);

                break;
            case R.id.imageView5:
                ++steps;

                // host always takes X (1)
                if (ishost.equalsIgnoreCase("True")) {
                    ImagesBox img5 = new ImagesBox(5, 1);
                    movesRef.child("img5").setValue(img5);
                    moves[1][1] = 1;
                }
                // away always takes O (0)
                else {
                    ImagesBox img5 = new ImagesBox(5, 0);
                    movesRef.child("img5").setValue(img5);
                    moves[1][1] = 0;

                }

                if (host_turn)
                    turn.setValue(false);
                else
                    turn.setValue(true);

                break;
            case R.id.imageView6:
                ++steps;
                // host always takes X (1)
                if (ishost.equalsIgnoreCase("True")) {
                    ImagesBox img6 = new ImagesBox(6, 1);
                    movesRef.child("img6").setValue(img6);
                    moves[1][2] = 1;
                }
                // away always takes O (0)
                else {
                    ImagesBox img6 = new ImagesBox(6, 0);
                    movesRef.child("img6").setValue(img6);
                    moves[1][2] = 0;
                }

                if (host_turn)
                    turn.setValue(false);
                else
                    turn.setValue(true);

                break;
            case R.id.imageView7:
                ++steps;

                // host always takes X (1)
                if (ishost.equalsIgnoreCase("True")) {
                    ImagesBox img7 = new ImagesBox(7, 1);
                    movesRef.child("img7").setValue(img7);
                    moves[2][0] = 1;
                }
                // away always takes O (0)
                else {
                    ImagesBox img7 = new ImagesBox(7, 0);
                    movesRef.child("img7").setValue(img7);

                    moves[2][0] = 0;
                }

                if (host_turn)
                    turn.setValue(false);
                else
                    turn.setValue(true);

                break;
            case R.id.imageView8:
                ++steps;

                // host always takes X (1)
                if (ishost.equalsIgnoreCase("True")) {
                    ImagesBox img8 = new ImagesBox(8, 1);
                    movesRef.child("img8").setValue(img8);
                    moves[2][1] = 1;

                }
                // away always takes O (0)
                else {
                    ImagesBox img8 = new ImagesBox(8, 0);
                    movesRef.child("img8").setValue(img8);
                    moves[2][1] = 0;

                }

                if (host_turn)
                    turn.setValue(false);
                else
                    turn.setValue(true);

                break;


            case R.id.imageView9:

                ++steps;
                // host always takes X (1)
                if (ishost.equalsIgnoreCase("True")) {
                    ImagesBox img9 = new ImagesBox(9, 1);
                    movesRef.child("img9").setValue(img9);
                    moves[2][2] = 1;
                }
                // away always takes O (0)
                else {
                    ImagesBox img9 = new ImagesBox(9, 0);
                    movesRef.child("img9").setValue(img9);
                    moves[2][2] = 0;

                }

                if (host_turn)
                    turn.setValue(false);
                else
                    turn.setValue(true);

                break;

        }


// one player needs min 3 moves to win
        if (steps >= 3) {
            win = winner(pl1, pl2);
        }

        if (win) {

            i1.setClickable(false);
            i2.setClickable(false);
            i3.setClickable(false);
            i4.setClickable(false);
            i5.setClickable(false);
            i6.setClickable(false);
            i7.setClickable(false);
            i8.setClickable(false);
            i9.setClickable(false);

            if (ishost.equalsIgnoreCase("True"))

                iswin.setValue("Host");

            else

                iswin.setValue("Away");

        }


        // if all turns are done and still no winner, then simply exit saying match drawn and start gameover activity with a delay of 1.4 seconds
        if (steps >= 5 && !win) {

            iswin.setValue("Draw");

        }
    }


    // function to check teh winning cases after 5th turn

    private boolean winner(String pl1, String pl2) {

        // check all rows

        if (moves[0][0] != -1 && moves[0][0] == moves[0][1] && moves[0][1] == moves[0][2]) {
            if (moves[0][0] == 0) {
                return true;
            } else if (moves[0][0] == 1) {
                return true;
            }
        }

        if (moves[1][0] != -1 && moves[1][0] == moves[1][1] && moves[1][1] == moves[1][2]) {
            if (moves[1][0] == 0) {
                return true;
            } else if (moves[1][0] == 1) {
                return true;
            }
        }

        if (moves[2][0] != -1 && moves[2][0] == moves[2][1] && moves[2][1] == moves[2][2]) {
            if (moves[2][0] == 0) {
                return true;
            } else if (moves[2][0] == 1) {
                return true;
            }
        }

        // check all columns

        if (moves[0][0] != -1 && moves[0][0] == moves[1][0] && moves[1][0] == moves[2][0]) {
            if (moves[0][0] == 0) {
                return true;
            } else if (moves[0][0] == 1) {
                return true;
            }
        }


        if (moves[0][1] != -1 && moves[0][1] == moves[1][1] && moves[1][1] == moves[2][1]) {
            if (moves[0][1] == 0) {
                return true;
            } else if (moves[0][1] == 1) {
                return true;
            }
        }

        if (moves[0][2] != -1 && moves[0][2] == moves[1][2] && moves[1][2] == moves[2][2]) {
            if (moves[0][2] == 0) {
                return true;
            } else if (moves[0][2] == 1) {
                return true;
            }
        }

        // checks first diagonal

        if (moves[0][0] != -1 && moves[0][0] == moves[1][1] && moves[1][1] == moves[2][2]) {
            if (moves[0][0] == 0) {
                return true;
            } else if (moves[0][0] == 1) {
                return true;
            }
        }

        // checks secondary diagonal

        if (moves[0][2] != -1 && moves[0][2] == moves[1][1] && moves[1][1] == moves[2][0]) {
            if (moves[0][2] == 0) {
                return true;
            } else return moves[0][2] == 1;
        }

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            t.cancel();
            t.purge();
            if (!isover)
                crash.setValue(true);


            // resetting the database as -1 when game finishes

            movesRef.removeEventListener(movelistener);
            iswin.removeValue();


            setDefaults();
        } catch (Exception E) {
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            t.cancel();
            t.purge();
        } catch (Exception E) {
        }
        setDefaults();
    }

    private void settingclicklisteners() {
        i1.setOnClickListener(this);
        i2.setOnClickListener(this);
        i3.setOnClickListener(this);
        i4.setOnClickListener(this);
        i5.setOnClickListener(this);
        i6.setOnClickListener(this);
        i7.setOnClickListener(this);
        i8.setOnClickListener(this);
        i9.setOnClickListener(this);
    }

    void setDefaults() {
        ImagesBox defaultvals = new ImagesBox(-1, -1);
        movesRef.child("img1").setValue(defaultvals);
        movesRef.child("img2").setValue(defaultvals);
        movesRef.child("img3").setValue(defaultvals);
        movesRef.child("img4").setValue(defaultvals);
        movesRef.child("img5").setValue(defaultvals);
        movesRef.child("img6").setValue(defaultvals);
        movesRef.child("img7").setValue(defaultvals);
        movesRef.child("img8").setValue(defaultvals);
        movesRef.child("img9").setValue(defaultvals);
    }


    @Override
    public void onBackPressed() {

        try {
            super.onBackPressed();

            t.cancel();
            t.purge();

            if (!isover)
                crash.setValue(true);

            mdata.goOnline();
            startActivity(new Intent(this,EnterActivity.class));
            finish();

        } catch (Exception e) {
            Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        isover=false;
//    }

//    @Override
//    protected void onRestart() {
//        isover = false;
//        super.onRestart();
//    }
}
