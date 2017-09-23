package com.example.tnovacic.novathon;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Activity2 extends AppCompatActivity {

    LinearLayout[] bars = new LinearLayout[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        bars[0] = (LinearLayout) findViewById(R.id.food_bar);
        bars[1] = (LinearLayout) findViewById(R.id.drinks_bar);
        bars[2] = (LinearLayout) findViewById(R.id.bills_bar);
        bars[3] = (LinearLayout) findViewById(R.id.fuel_bar);
        bars[4] = (LinearLayout) findViewById(R.id.other_bar);


        bars[0].setBackgroundColor(Color.parseColor("#3F51B5"));
        bars[1].setBackgroundColor(Color.parseColor("#64B5F6"));
        bars[2].setBackgroundColor(Color.parseColor("#F4511E"));
        bars[3].setBackgroundColor(Color.parseColor("#CDDC39"));
        bars[4].setBackgroundColor(Color.parseColor("#8D6E63"));

    }
}
