package com.mvctc.gw.bloodhoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by phelps47387 on 10/26/2017.
 */

public class SendActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Button btn = (Button) findViewById(R.id.back);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        //creates intent
        Intent intent;

        //runs a switch based on button ID and sets the intent accordingly
        switch(v.getId()){
            case R.id.back:
                intent = new Intent(this, MainActivity.class);
                break;
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }

        //switches to according activity
        startActivity(intent);
    }
}
