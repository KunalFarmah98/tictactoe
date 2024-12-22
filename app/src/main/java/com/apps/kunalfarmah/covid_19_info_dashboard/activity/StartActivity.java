package com.kunalfarmah.covid_19_info_dashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.kunalfarmah.covid_19_info_dashboard.R;

import java.io.Serializable;

public class StartActivity extends AppCompatActivity implements Serializable {
    EditText e1;
    EditText e2;

    ImageView i1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        i1 = findViewById(R.id.gobutton);

        e1 = findViewById(R.id.player1);
        e1.setMaxLines(2);
        e2 = findViewById(R.id.player2);
        e2.setMaxLines(2);

        i1.setOnClickListener(new View.OnClickListener() {

            private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);    //fading effect on clicking go

            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(StartActivity.this , MainActivity.class);
                intent.putExtra("player1", e1.getText().toString());    // sending the edittext data to mainactivity
                intent.putExtra("player2", e2.getText().toString());
                startActivity(intent);
                finish();

            }
        });
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
