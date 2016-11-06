package com.example.edawg.bizconnect_poc;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TradeCardsActivity extends AppCompatActivity  {

    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;
    GetUserTask mUserTask;
    ArrayList<BusinessCard> mCards;
    GestureDetector mDetector;

    private final static int NFC_ENABLE = 0xF;

    public final static String CARDS_LIST_EXTRA = "com.example.edsalva.cardslist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trade_cards);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.hide();
        }

        mCards = new ArrayList<>();
        mDetector = new GestureDetector(this, new SwipeListener());

        String email = getIntent().getStringExtra(LoginActivity.EXTRA_EMAIL);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.nfc_not_enabled)
                    .setMessage(R.string.plz_enable_nfc)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(Settings.ACTION_NFC_SETTINGS), NFC_ENABLE);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

        mPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        NdefRecord record = NdefRecord.createMime(getString(R.string.nfc_mime), email.getBytes(Charset.defaultCharset()));

        mNfcAdapter.setNdefPushMessage(new NdefMessage(record), this);
    }


    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            processIntent(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    public void onPause() {
        super.onPause();

        mNfcAdapter.disableForegroundDispatch(this);
    }

    private class EmailListener implements DialogInterface.OnClickListener {
        private String mEmail;
        private EditText input;

        public EmailListener(String email, EditText in) {
            mEmail = email;
            input = in;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String event = input.getText().toString();
            mUserTask = new GetUserTask(mEmail, event);
            mUserTask.execute();
        }
    }

    private void processIntent(Intent intent) {
        NdefMessage msg = (NdefMessage) intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];

        String email = new String(msg.getRecords()[0].getPayload());

        final EditText input = new EditText(TradeCardsActivity.this);

        DialogInterface.OnClickListener listener = new EmailListener(email, input);

        new AlertDialog.Builder(TradeCardsActivity.this)
                .setTitle(R.string.enter_event)
                .setView(input)
                .setMessage(R.string.plz_enter_event)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

    public class GetUserTask extends AsyncTask<Void, Void, BusinessCard> {

        private String mEmail, mEvent;

        GetUserTask(String email, String event) {
            mEmail = email;
            mEvent = event;
        }

        @Override
        protected BusinessCard doInBackground(Void... params) {
            try {

                URL url = new URL(getString(R.string.http_host) + getString(R.string.http_get_user)
                        + "email=" + mEmail);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                conn.connect();

                InputStream is = conn.getInputStream();
                Reader reader = new InputStreamReader(is, getString(R.string.utf_8));
                char[] buffer = new char[1 << 15];
                reader.read(buffer);

                JSONObject obj = new JSONObject(new String(buffer));

                return new BusinessCard(obj.getString("name"),
                        obj.getString("number"), obj.getString("email"),
                        obj.getString("industry"), obj.getString("linkedin"),
                        mEvent);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BusinessCard n) {
            mUserTask = null;
            mCards.add(n);
        }

        @Override
        protected void onCancelled() {
            mUserTask = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NFC_ENABLE && (mNfcAdapter == null || !mNfcAdapter.isEnabled())) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.nfc_not_enabled)
                    .setMessage(R.string.nfc_not_enabled_exit)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class SwipeListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
            //This is all bad practice but i havent slept in a full day and a half so fuck off
            if ((e1.getX() - e2.getX() > 100) && vx < -10) {
                Intent intent = new Intent(TradeCardsActivity.this, CardViewActivity.class);
                intent.putParcelableArrayListExtra(CARDS_LIST_EXTRA, mCards);
                startActivity(intent);
                return true;
            }
            return false;
        }
    }
}
