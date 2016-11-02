package app.facebook.android.com.nitende;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.facebook.android.com.nitende.datasource.LocalStore;
import app.facebook.android.com.nitende.datasource.User;

/**
 * Created by abala on 11/2/16.
 *
 * NiTENDE v0.1
 *
 * Register User Activity
 */
public class Register extends Activity {

    private TextView closeText, firstNameText, forgotPasswordText, signInText, emailText, passwordText, backText;
    private EditText emailEditText, passwordEditText, firstNameEdit;
    private ImageView backButton, submitButton;
    private CheckBox showPassword;
    private LocalStore localStore;
    private ProgressDialog dialog;
    private Button loginButton;
    private final String URL_TO_HIT = "http://159.203.104.154/nitende/register.php";
    HashMap location;
    private boolean connectionIssue = false;
    private boolean loginFailed = false;
    private String firstNameTyped = null;
    private String emailTyped = null;
    private String passwordTyped = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        location = new HashMap();
        // init local store
        localStore = new LocalStore(this);

        // font family
        Typeface myFont = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
        Typeface myFont2 = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait..");

        signInText = (TextView) findViewById(R.id.register);
        signInText.setTypeface(myFont, Typeface.BOLD);
        signInText.setTextColor(Color.WHITE);

        backText = (TextView) findViewById(R.id.back_text);
        backText.setTypeface(myFont2, Typeface.BOLD);
        backText.setTextColor(Color.WHITE);
        backText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }

        });

        firstNameText = (TextView) findViewById(R.id.first_name);
        firstNameText.setTypeface(myFont2, Typeface.BOLD);
        firstNameText.setTextColor(Color.WHITE);

        firstNameEdit = (EditText) findViewById(R.id.first_name_edit);
        firstNameEdit.setTypeface(myFont2, Typeface.BOLD);
        firstNameEdit.getBackground().mutate().setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);

        emailText = (TextView) findViewById(R.id.email_address);
        emailText.setTypeface(myFont2, Typeface.BOLD);
        emailText.setTextColor(Color.WHITE);

        emailEditText = (EditText) findViewById(R.id.email_text_field);
        emailEditText.setTypeface(myFont2, Typeface.BOLD);
        emailEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);

        passwordText = (TextView) findViewById(R.id.password);
        passwordText.setTypeface(myFont2, Typeface.BOLD);
        passwordText.setTextColor(Color.WHITE);

        passwordEditText = (EditText) findViewById(R.id.password_edit);
        passwordEditText.setTypeface(myFont2, Typeface.BOLD);
        passwordEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);

        loginButton =(Button) findViewById(R.id.register_button);
        loginButton.setText("Register");
        loginButton.setTypeface(myFont);
        loginButton.setTextColor(Color.parseColor("#82007E"));

        showPassword = (CheckBox) findViewById(R.id.show_pass);
        showPassword.setTypeface(myFont2, Typeface.BOLD);
        showPassword.setTextColor(Color.WHITE);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.length());
                } else {
                    // hide password
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.length());
                }
            }
        });
    }

    public void registerUser(View v) {
        Log.d("logvinn", firstNameEdit.getText().toString() +":"+emailEditText.getText().toString() + ":"+ passwordEditText.getText().toString());
        firstNameTyped = firstNameEdit.getText().toString();
        emailTyped = emailEditText.getText().toString();
        passwordTyped = passwordEditText.getText().toString();
        Log.d("signIn", emailTyped + passwordTyped);
        new registerTask().execute(URL_TO_HIT);

    }

    public class registerTask extends AsyncTask<String,String, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("firstname", firstNameTyped)
                        .appendQueryParameter("email", emailTyped)
                        .appendQueryParameter("password", passwordTyped);

                String query = builder.build().getEncodedQuery();
                Log.d("query", query);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                if (finalJson.equals("success")){
                    return "success";
                } else {
                    return null;
                }
            } catch (MalformedURLException e) {
                Log.e("error", "ME");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("error", "IO4");
                connectionIssue = true;
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.e("error", "IO");
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                Toast.makeText(getApplicationContext(), "successfully registered, please log in.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

            } else {
                if (connectionIssue){
                    Toast.makeText(getApplicationContext(), "Log in Error. You are currently offline!", Toast.LENGTH_SHORT).show();
                    connectionIssue = false;
                } else {
                    Toast.makeText(getApplicationContext(), "Problem Logging in, please try again.", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

}
