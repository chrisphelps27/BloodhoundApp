package com.mvctc.gw.bloodhoundapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

/**
 * Created by phelps47387 on 10/26/2017.
 */

public class LocActivity extends AppCompatActivity implements View.OnClickListener {
    public static String response;

    public static final String MIME_TEXT_ID = "text/plain";
    public static final String TAG = "NfcDemo";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private EditText Eco, Loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Button btn = (Button) findViewById(R.id.button);
        Button btn2 = (Button) findViewById(R.id.Start);
        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
        mTextView = (TextView) findViewById(R.id.explanation);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stops if there is no NFC
            Toast.makeText(this, R.string.nfc_missing, Toast.LENGTH_LONG).show();
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText(R.string.nfc_disabled);
        } else {
            mTextView.setText(R.string.loc_explanation);
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            String type = intent.getType();
            if (MIME_TEXT_ID.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                try {
                    if ((response = new NdefReaderTask().execute(tag).get()) != null) {
                        String result = new connection().execute("getCurr.php", "i=" + response).get();
                        Integer[] data = FileManager.GetData(this);
                        String address = data[0] + "." + data[1];
                        Log.d("Bloodhound", "Result = " + result);
                        Log.d("Bloodhound", "Address = " + address);
                        if (result.equals(address)) {
                            result = new connection().execute("tagOut.php", "i=" + response).get();
                        } else {
                            result = new connection().execute("tagIn.php", "i=" + response + "&e= " + data[0] + "&l=" + data[1]).get();
                        }
                    }
                } catch (Exception e) {
                }


            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    try {
                        if ((response = new NdefReaderTask().execute(tag).get()) != null) {
                            String result = new connection().execute("getCurr.php", "i=" + response).get();
                            Integer[] data = FileManager.GetData(this);
                            String address = data[0] + "." + data[1];
                            Log.d("Bloodhound", "Result = " + result);
                            Log.d("Bloodhound", "Address = " + address);
                            if (result.equals(address)) {
                                result = new connection().execute("tagOut.php", "i=" + response).get();
                            } else {
                                result = new connection().execute("tagIn.php", "i=" + response + "&e= " + data[0] + "&l=" + data[1]).get();
                            }
                        }
                    } catch (Exception e) {
                    }
                    break;
                }
            }
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_ID);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link LocActivity} requesting to stop the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }


    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_MIME_MEDIA /*&& Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)*/) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();

            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Text
            return new String(payload, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            /*try {
                String HI = new connection().execute("tagOut.php", "i=" + result, "http://gw.mvctc.com/Class2018/smcintosh/UNHACKABLE/").get();
            } catch (Exception e) {
            }*/
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.Start:
                Eco = (EditText) findViewById(R.id.Ecosystem);
                Loc = (EditText) findViewById(R.id.Location);
                Log.d("Bloodhound", "Starting");
                FileManager.FileWrite(Integer.parseInt(Eco.getText().toString()), Integer.parseInt(Loc.getText().toString()), 000000, this); //000000 represents no stored ID
                Log.d("Bloodhound", "Wrote");
                break;
        }

        //switches to according activity

    }
}