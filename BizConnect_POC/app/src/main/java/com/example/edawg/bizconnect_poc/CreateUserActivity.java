package com.example.edawg.bizconnect_poc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CreateUserActivity extends AppCompatActivity {

    private boolean created;

    private CreateUserTask mUserTask;
    private View mFormView, mProgressView;
    private HashMap<String, String> mParamsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        getSupportActionBar().setTitle(getString(R.string.create_user));

        mFormView = findViewById(R.id.formView);
        mProgressView = findViewById(R.id.creation_prog);

        String email = getIntent().getStringExtra(LoginActivity.EXTRA_EMAIL);
        String pass = getIntent().getStringExtra(LoginActivity.EXTRA_PASS);
        ((EditText) findViewById(R.id.editTextEmail)).setText(email);
        ((EditText) findViewById(R.id.editTextPassword)).setText(pass);
    }

    @Override
    protected void onResume() {
        super.onResume();
        created = false;

    }

    public void createUser(View view) {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if(!created) {
            created = true;

            mParamsMap = new HashMap<>();
            mParamsMap.put("name", ((EditText) findViewById(R.id.editTextName)).getText().toString());
            mParamsMap.put("email", ((EditText) findViewById(R.id.editTextEmail)).getText().toString());
            mParamsMap.put("industry", ((EditText) findViewById(R.id.editTextInd)).getText().toString());
            mParamsMap.put("number", ((EditText) findViewById(R.id.editTextNum)).getText().toString());
            mParamsMap.put("password", ((EditText) findViewById(R.id.editTextPassword)).getText().toString());
            mParamsMap.put("linkedin", ((EditText) findViewById(R.id.editTextLinkedIn)).getText().toString());
        }

        showProgress(true);
        mUserTask = new CreateUserTask(mParamsMap);
        mUserTask.execute((Void) null);
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous task to create the user
     */
    public class CreateUserTask extends AsyncTask<Void, Void, Void> {

        private final HashMap<String, String> userData;

        CreateUserTask(HashMap<String, String> data) {
            userData = data;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                StringBuilder urlString = new StringBuilder(getString(R.string.http_host)
                        + getString(R.string.http_make_user));
                boolean first = true;
                for (Map.Entry<String, String> entry : userData.entrySet()) {
                    if (first)
                        first = false;
                    else
                        urlString.append("&");

                    urlString.append(URLEncoder.encode(entry.getKey(), getString(R.string.utf_8)));
                    urlString.append("=");
                    urlString.append(URLEncoder.encode(entry.getValue(), getString(R.string.utf_8)));
                }

                URL url = new URL(urlString.toString());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                conn.connect();

                InputStream is = conn.getInputStream();
                Reader reader = new InputStreamReader(is, getString(R.string.utf_8));
                char[] buffer = new char[1];
                reader.read(buffer);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void n) {

            showProgress(false);
            mUserTask = null;
            Intent intent = new Intent(CreateUserActivity.this, TradeCardsActivity.class);
            intent.putExtra(LoginActivity.EXTRA_EMAIL, userData.get("email"));
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mUserTask = null;
        }
    }

}
