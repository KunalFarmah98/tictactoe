package com.kunalfarmah.covid_19_info_dashboard.activity;

import com.kunalfarmah.covid_19_info_dashboard.R;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int turns = -1;
    boolean win = false;

    ImageView hosticon;
    ImageView awayicon;

    ImageView i1;
    ImageView i2;
    ImageView i3;
    ImageView i4;
    ImageView i5;
    ImageView i6;
    ImageView i7;
    ImageView i8;
    ImageView i9;
    TextView t1;
    TextView t2;

    TextView movescount;
    LinearLayout timer;

    int minutes=0,seconds=0;

    TextView min,sec;

    View win1;
    View win2;
    View win3;

    View ver1;
    View ver2;
    View ver3;

    View diag00;
    View diag01;
    View diag02;
    View diag10;
    View diag11;
    View diag12;


    String pl1;
    String pl2;

    int[][] moves = {{-1, -1, -1}, {-1, -1, -1}, {-1, -1, -1}};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();   //receiving the extras supplied to MainActivity

        pl1 = (String) intent.getSerializableExtra("player1");
        pl2 = (String) intent.getSerializableExtra("player2");


        t1 = findViewById(R.id.textView);
        t2 = findViewById(R.id.textView2);

        movescount = findViewById(R.id.moves);

        timer = findViewById(R.id.timer);
        timer.setVisibility(View.VISIBLE);

        min = findViewById(R.id.minutes);
        sec = findViewById(R.id.seconds);

        if(null==pl1){
            pl1 = "Player 1";
        }

        if(null==pl2){
            pl2 = "Player 2";
        }

        if (!pl1.equals("") && !pl2.equals("")) {
            t1.setText(pl1 + " : X");
            t2.setText(pl2 + " : O");
        } else if (pl1.equals("") && pl2.equals("")) {
            pl1 = "Player 1";
            pl2 = "Player 2";
        }else if(pl1.equals("")&&!pl2.equals("")){
            pl1 = "Player 1";
            t2.setText(pl2 + " : O");
        }else if(!pl1.equals("")&&pl2.equals("")){
            t1.setText(pl1 + " : X");
            pl2 = "Player 2";
        }

        hosticon = findViewById(R.id.host);
        hosticon.setVisibility(View.VISIBLE);
        awayicon = findViewById(R.id.away);
        awayicon.setVisibility(View.INVISIBLE);

        i1 = findViewById(R.id.imageView1);
        i2 = findViewById(R.id.imageView2);
        i3 = findViewById(R.id.imageView3);
        i4 = findViewById(R.id.imageView4);
        i5 = findViewById(R.id.imageView5);
        i6 = findViewById(R.id.imageView6);
        i7 = findViewById(R.id.imageView7);
        i8 = findViewById(R.id.imageView8);
        i9 = findViewById(R.id.imageView9);


        win1=  findViewById(R.id.win1);
        win2=  findViewById(R.id.win2);
        win3=  findViewById(R.id.win3);

        ver1 = findViewById(R.id.ver1);
        ver2 = findViewById(R.id.ver2);
        ver3 = findViewById(R.id.ver3);

        diag00 = findViewById(R.id.diag00);
        diag01 = findViewById(R.id.diag01);
        diag02 = findViewById(R.id.diag02);

        diag10 = findViewById(R.id.diag10);
        diag11 = findViewById(R.id.diag11);
        diag12 = findViewById(R.id.diag12);



        Toast.makeText(getApplicationContext(), pl1 + " Goes first", Toast.LENGTH_SHORT).show();


        i1.setOnClickListener(this);
        i2.setOnClickListener(this);
        i3.setOnClickListener(this);
        i4.setOnClickListener(this);
        i5.setOnClickListener(this);
        i6.setOnClickListener(this);
        i7.setOnClickListener(this);
        i8.setOnClickListener(this);
        i9.setOnClickListener(this);


        //Declare the timer
        Timer t = new Timer();


        //Set the schedule function and rate
        // timer runs
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        min.setText(String.valueOf(minutes));
                        if(seconds<10)
                            sec.setText("0"+ seconds);
                        else
                            sec.setText(String.valueOf(seconds));
                        seconds += 1;

                        if(seconds == 59 )
                        {
                            seconds=0;
                            minutes+=1;
                            min.setText(String.valueOf(minutes));

                            if(seconds<10)
                                sec.setText("0"+ seconds);
                            else
                                sec.setText(String.valueOf(seconds));

                            seconds += 1;

                        }



                    }

                });
            }

        }, 0, 1000);


    }

    @Override
    public void onClick(View view) {
        //implementing onClick only once for all buttons by using their IDs

        ++turns;

        movescount.setText("Moves : "+ (turns+1));

        if(turns%2!=0){

            awayicon.setVisibility(View.INVISIBLE);
            hosticon.setVisibility(View.VISIBLE);

        }

        else{
            awayicon.setVisibility(View.VISIBLE);
            hosticon.setVisibility(View.INVISIBLE);

        }

        switch (view.getId()) {

            case R.id.imageView1:


                if (turns % 2 == 0) {
                    i1.setImageResource(R.drawable.x);

                    moves[0][0] = 0;
                } else {
                    i1.setImageResource(R.drawable.o);
                    moves[0][0] = 1;
                }

                i1.setEnabled(false);   // disabling the onClickListener after first click

                break;

            case R.id.imageView2:

                //++turns;
                if (turns % 2 == 0) {
                    i2.setImageResource(R.drawable.x);

                    moves[0][1] = 0;
                } else {
                    i2.setImageResource(R.drawable.o);
                    moves[0][1] = 1;
                }

                i2.setEnabled(false);

                break;

            case R.id.imageView3:
               // ++turns;
                if (turns % 2 == 0) {
                    i3.setImageResource(R.drawable.x);

                    moves[0][2] = 0;
                } else {
                    i3.setImageResource(R.drawable.o);
                    moves[0][2] = 1;
                }
                i3.setEnabled(false);

                break;

            case R.id.imageView4:
             //   ++turns;
                if (turns % 2 == 0) {
                    i4.setImageResource(R.drawable.x);

                    moves[1][0] = 0;
                } else {
                    i4.setImageResource(R.drawable.o);
                    moves[1][0] = 1;
                }
                i4.setEnabled(false);
                break;

            case R.id.imageView5:
              //  ++turns;
                if (turns % 2 == 0) {
                    i5.setImageResource(R.drawable.x);

                    moves[1][1] = 0;
                } else {
                    i5.setImageResource(R.drawable.o);
                    moves[1][1] = 1;
                }
                i5.setEnabled(false);
                break;

            case R.id.imageView6:
             //   ++turns;
                if (turns % 2 == 0) {
                    i6.setImageResource(R.drawable.x);

                    moves[1][2] = 0;
                } else {
                    i6.setImageResource(R.drawable.o);
                    moves[1][2] = 1;
                }

                i6.setEnabled(false);
                break;

            case R.id.imageView7:
             //   ++turns;
                if (turns % 2 == 0) {
                    i7.setImageResource(R.drawable.x);

                    moves[2][0] = 0;
                } else {
                    i7.setImageResource(R.drawable.o);
                    moves[2][0] = 1;
                }

                i7.setEnabled(false);
                break;

            case R.id.imageView8:
            //    ++turns;
                if (turns % 2 == 0) {
                    i8.setImageResource(R.drawable.x);

                    moves[2][1] = 0;
                } else {
                    i8.setImageResource(R.drawable.o);
                    moves[2][1] = 1;
                }
                i8.setEnabled(false);
                break;

            case R.id.imageView9:
             //   ++turns;
                if (turns % 2 == 0) {
                    i9.setImageResource(R.drawable.x);

                    moves[2][2] = 0;
                } else {
                    i9.setImageResource(R.drawable.o);
                    moves[2][2] = 1;
                }

                i9.setEnabled(false);
                break;

        }


        // in the best case, a player cann win in min 5 turns if he starts first
        if (turns >= 4 && turns <= 8)
            win = winner(pl1, pl2);

        // if a player wins, start gameover activity with a delay of 1.4 seconds

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

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent gameover = new Intent(MainActivity.this, GameoverActivity.class);
                    gameover.putExtra("Time",min.getText()+" : "+sec.getText());
                    gameover.putExtra("player1", pl1);
                    gameover.putExtra("player2", pl2);
                    startActivity(gameover);
                    finish();
                }
            }, 1400);
        }

        // if all turns are done and still no winner, then simply exit saying match drawn and start gameover activity with a delay of 1.4 seconds
        if (turns == 8 && !win) {
            Toast.makeText(getApplicationContext(), "Drawn!!", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent gameover = new Intent(MainActivity.this, GameoverActivity.class);
                    gameover.putExtra("Time",min.getText()+" : "+sec.getText());
                    gameover.putExtra("player1", pl1);
                    gameover.putExtra("player2", pl2);
                    startActivity(gameover);
                    finish();
                }
            }, 1400);
        }
    }


    // function to check teh winning cases after 5th turn

    private boolean winner(String pl1, String pl2) {

        // check all rows

        if (moves[0][0] != -1 && moves[0][0] == moves[0][1] && moves[0][1] == moves[0][2]) {
           // win1.setVisibility(View.VISIBLE);
            if (moves[0][0] == 0) {
                Toast.makeText(getApplicationContext(), pl1 + " Wins", Toast.LENGTH_SHORT).show();
                return true;
            } else if (moves[0][0] == 1) {
                Toast.makeText(getApplicationContext(), pl2 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            }


        }

        if (moves[1][0] != -1 && moves[1][0] == moves[1][1] && moves[1][1] == moves[1][2]) {
          //  win2.setVisibility(View.VISIBLE);

            if (moves[1][0] == 0) {
                Toast.makeText(getApplicationContext(), pl1 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            } else if (moves[1][0] == 1) {
                Toast.makeText(getApplicationContext(), pl2 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            }

        }

        if (moves[2][0] != -1 && moves[2][0] == moves[2][1] && moves[2][1] == moves[2][2]) {
           // win3.setVisibility(View.VISIBLE);

            if (moves[2][0] == 0) {
                Toast.makeText(getApplicationContext(), pl1 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            } else if (moves[2][0] == 1) {
                Toast.makeText(getApplicationContext(), pl2 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            }
        }

        // check all columns

        if (moves[0][0] != -1 && moves[0][0] == moves[1][0] && moves[1][0] == moves[2][0]) {
           // ver1.setVisibility(View.VISIBLE);

            if (moves[0][0] == 0) {
                Toast.makeText(getApplicationContext(), pl1 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            } else if (moves[0][0] == 1) {
                Toast.makeText(getApplicationContext(), pl2 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            }
        }


        if (moves[0][1] != -1 && moves[0][1] == moves[1][1] && moves[1][1] == moves[2][1]) {
          //  ver2.setVisibility(View.VISIBLE);

            if (moves[0][1] == 0) {
                Toast.makeText(getApplicationContext(), pl1 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            } else if (moves[0][1] == 1) {
                Toast.makeText(getApplicationContext(), pl2 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            }
        }

        if (moves[0][2] != -1 && moves[0][2] == moves[1][2] && moves[1][2] == moves[2][2]) {
          //  ver3.setVisibility(View.VISIBLE);

            if (moves[0][2] == 0) {
                Toast.makeText(getApplicationContext(), pl1 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            } else if (moves[0][2] == 1) {
                Toast.makeText(getApplicationContext(), pl2 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            }
        }

        // checks first diagonal

        if (moves[0][0] != -1 && moves[0][0] == moves[1][1] && moves[1][1] == moves[2][2]) {

//            diag00.setVisibility(View.VISIBLE);
//            diag01.setVisibility(View.VISIBLE);
//            diag02.setVisibility(View.VISIBLE);

            if (moves[0][0] == 0) {
                Toast.makeText(getApplicationContext(), pl1 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            } else if (moves[0][0] == 1) {
                Toast.makeText(getApplicationContext(), pl2 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            }

        }

        // checks secondary diagonal

        if (moves[0][2] != -1 && moves[0][2] == moves[1][1] && moves[1][1] == moves[2][0]) {

//            diag10.setVisibility(View.VISIBLE);
//            diag11.setVisibility(View.VISIBLE);
//            diag12.setVisibility(View.VISIBLE);

            if (moves[0][2] == 0) {
                Toast.makeText(getApplicationContext(), pl1 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            } else if (moves[0][2] == 1) {
                Toast.makeText(getApplicationContext(), pl2 + " Wins", Toast.LENGTH_LONG).show();
                return true;
            }


        }

            return  false;
    }

    private static final String SELECTED_ITEM_POSITION = "ItemPosition";
    private int mPosition;

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of item position
        outState.putInt(SELECTED_ITEM_POSITION, mPosition);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Read the state of item position
        mPosition = savedInstanceState.getInt(SELECTED_ITEM_POSITION);
    }
}


