package com.mvctc.gw.bloodhoundapp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by phelps47387 on 10/26/2017.
 */

public class WriteActivity extends AppCompatActivity implements View.OnClickListener{

    boolean mWriteMode = false;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        Button btn = (Button) findViewById(R.id.back);
        btn.setOnClickListener(this);

        Button writeTagBtn = (Button) findViewById(R.id.write_tag);
        writeTagBtn.setOnClickListener(this);
    }

    /*
     * Handles all the buttons of the class
     * Currently handles back button and writing tag button
     */
    @Override
    public void onClick(View v){
        //runs a switch based on button ID
        switch(v.getId()){
            case R.id.back:
                Intent intent = new Intent(this, MainActivity.class);
                //switches to according activity
                startActivity(intent);
                break;
            case R.id.write_tag:
                /*
                 * Case handles writing to tags
                 * Creates new intents and then enables write mode
                 * Creates dialog box that instructs user and when closed disables write mode
                 */
                mNfcAdapter = NfcAdapter.getDefaultAdapter(WriteActivity.this);
                mNfcPendingIntent = PendingIntent.getActivity(WriteActivity.this, 0,
                        new Intent(WriteActivity.this, WriteActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                enableTagWriteMode();

                //builds and displays alert for writing to tags
                //when alert is canceled, it disables tag write mode
                AlertDialog.Builder nfcWriteMsg = new AlertDialog.Builder(WriteActivity.this);
                nfcWriteMsg.setTitle("Writing ID");
                nfcWriteMsg.setMessage("Touch the tag you wish to write to the device's NFC sensor.\n\nThis is usually located by the camera");
                nfcWriteMsg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        disableTagWriteMode();
                    }
                });
                nfcWriteMsg.create();
                nfcWriteMsg.show();
                break;
        }

    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] mWriteTagFilters = new IntentFilter[] { tagDetected };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        TextView writingText = (TextView) findViewById(R.id.writing_name);
        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefRecord record = NdefRecord.createMime( "text/plain", writingText.getText().toString().getBytes());
            NdefMessage message = new NdefMessage(new NdefRecord[] { record });
            if (writeTag(message, detectedTag)) {
                Toast.makeText(this, "Success: Wrote to nfc tag", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    /*
    * Writes an NdefMessage to a NFC tag
    */
    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            //as long as the tag is formatted
            if (ndef != null) {
                //connects to tag
                ndef.connect();
                //makes sure the tag can be written to
                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(),
                            "Error: tag not writable",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                //makes sure the tag has enough room
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(getApplicationContext(),
                            "Error: tag too small",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                //writes to the tag
                ndef.writeNdefMessage(message);
                return true;
            //if tag is not formatted
            } else {
                //tries to format tag
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        //catches any Exception thrown and returns false
        } catch (Exception e) {
            return false;
        }
    }
}
