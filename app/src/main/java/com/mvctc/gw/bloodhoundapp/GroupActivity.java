package com.mvctc.gw.bloodhoundapp;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by phelps47387 on 11/10/2017.
 */

public class GroupActivity extends AppCompatActivity implements View.OnClickListener {

    private NfcAdapter mNfcAdapter;
    boolean isCard = false; /* tells whether they're currently emulating a card which is needed for all the functionality in this activity
    TODO create if statement that checks if theyve set up their user in card emulation
    */
    boolean inGroup = false; //TODO run if statement that checks if they're in a group yet based on card being emulated
    int id = 0; //TODO set this to their ID when running query for inGroup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);


        if (mNfcAdapter == null) { // Stops if there is no NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


        if(!isCard) { //stops if they don't have card emulation set up and redirects them to EmulateActivity
            Toast.makeText(this, "You must set up your ID first", Toast.LENGTH_LONG).show();
            finish();
            Intent intent = new Intent(this, EmulateActivity.class);
            startActivity(intent);
        }
        else{ //if they have card emulation set up
            int groupID = 0; //TODO set this to the users' group ID, if they don't have one keep it at 0
            if(groupID == 0){ //TODO make this check if they're already in a group
                ArrayList<Integer> groupMemIDs = new ArrayList<Integer>(); //TODO fill this array with the users in the groupID
            }
            else{ //if they're not in a group
                //TODO add option to create group or instructions in joining a group
            }
        }
    }

    @Override
    public void onClick(View v){
        //handle buttons
    }
}
