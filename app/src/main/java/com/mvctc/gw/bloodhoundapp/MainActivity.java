package com.mvctc.gw.bloodhoundapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/*
    Authors: Chris Phelps, Steve McIntosh, Troy Grant
*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO have it default to whichever activity they select after first-time run which will be saved in a config file, excluding writing to a tag

        //defines all the buttons (3) and sets their onClick to the method below
        Button writeIdBtn = (Button) findViewById(R.id.write_id);
        Button actAsIdBtn = (Button) findViewById(R.id.tie_to_id);
        Button actAsLocBtn = (Button) findViewById(R.id.act_as_loc);
        writeIdBtn.setOnClickListener(this);
        actAsLocBtn.setOnClickListener(this);
        actAsIdBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        //creates intent
        Intent intent;

        //runs a switch based on button ID and sets the intent accordingly
        switch(v.getId()){
            case R.id.write_id:
                intent = new Intent(this, WriteActivity.class);
                break;
            case R.id.tie_to_id:
                intent = new Intent(this, LinkActivity.class);
                break;
            case R.id.act_as_loc:
                intent = new Intent(this, LocActivity.class);
                break;
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }

        //switches to according activity
        startActivity(intent);
    }
}