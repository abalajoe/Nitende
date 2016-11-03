package app.facebook.android.com.nitende;

/**
 * Created by abala on 11/2/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Window;
import android.view.WindowManager;
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
 * Created by jabala on 8/31/2016.
 */
public class SignIn extends Activity {

    private TextView closeText, forgotPasswordText, signInText, emailText, passwordText, backText;
    private EditText emailEditText, passwordEditText;
    private ImageView backButton, submitButton;
    private CheckBox showPassword;
    private LocalStore localStore;
    private ProgressDialog dialog;
    private Button loginButton;
    private final String URL_TO_HIT = "http://159.203.104.154/nitende/login.php";
    private boolean connectionIssue = false;
    private boolean loginFailed = false;
    HashMap location;
    private String emailTyped = null;
    private String passwordTyped = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        location = new HashMap();
        // init local store
        localStore = new LocalStore(this);

        // font family
        Typeface myFont = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
        Typeface myFont2 = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        signInText = (TextView) findViewById(R.id.sign_in);
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
        emailText = (TextView) findViewById(R.id.email);
        emailText.setTypeface(myFont2, Typeface.BOLD);
        emailText.setTextColor(Color.WHITE);

        emailEditText = (EditText) findViewById(R.id.email_text_field);
        emailEditText.setTypeface(myFont2, Typeface.BOLD);
        emailEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);

        passwordText = (TextView) findViewById(R.id.password);
        passwordText.setTypeface(myFont2, Typeface.BOLD);
        passwordText.setTextColor(Color.WHITE);

        passwordEditText = (EditText) findViewById(R.id.password_text_field);
        passwordEditText.setTypeface(myFont2, Typeface.BOLD);
        passwordEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);

        forgotPasswordText = (TextView) findViewById(R.id.forgot_password);
        forgotPasswordText.setTypeface(myFont2, Typeface.BOLD);
        forgotPasswordText.setTextColor(Color.WHITE);

        loginButton =(Button) findViewById(R.id.login_button);
        loginButton.setText("Sign in");
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

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Intent myIntent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
               // startActivity(myIntent);
                //slide from right to left
                //overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                //overridePendingTransition(0, R.anim.anim_test);
               // overridePendingTransition(R.anim.fade_in2,R.anim.fade_out2);
            }

        });
    }

    public void loginUser(View v) {
        Log.d("logvinn", emailEditText.getText().toString() + ":"+ passwordEditText.getText().toString());
        emailTyped = emailEditText.getText().toString();
        passwordTyped = passwordEditText.getText().toString();
        Log.d("signIn", emailTyped + passwordTyped);
        new LoginTask().execute(URL_TO_HIT);

    }

    public class LoginTask extends AsyncTask<String,String, List<User> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<User> doInBackground(String... params) {
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

                Log.d("finalJson ", finalJson);
                JSONObject parentObject = new JSONObject(finalJson);

                String jsonResult = parentObject.getString("users");
                Log.d("jsonObject ", jsonResult);
                if (jsonResult == "false"){
                    Log.d("jsonObject ", "=== false ===");
                    loginFailed = true;
                    return null;
                } else {
                    JSONArray parentArray = parentObject.getJSONArray("users");

                    List<User> userModelList = new ArrayList<>();

                    Gson gson = new Gson();
                    for(int i=0; i<parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        Log.d("finalObject ", finalObject.toString());
                        /**
                         * below single line of code from Gson saves you from writing the json parsing yourself which is commented below
                         */
                        User userModel = gson.fromJson(finalObject.toString(), User.class);

                        userModelList.add(userModel);
                    }
                    return userModelList;
                }
            } catch (MalformedURLException e) {
                Log.e("error", "ME");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("error", "IO 2");
                connectionIssue = true;
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("error", "JSON");
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
        protected void onPostExecute(final List<User> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            int arrayCount = 0;
            int id = 0;
            String firstName = null;
            String email = null;
            if(result != null) {
                //Toast.makeText(getApplicationContext(), "data -> " + result.toString(), Toast.LENGTH_SHORT).show();
                for (User user : result){
                    Log.d("usermodel", user.getId() + user.getFirstname() + user.getEmail());

                    id = user.getId();
                    firstName = user.getFirstname();
                    email = user.getEmail();
                }

                User user = new User(id, firstName, email);
                // store user info in local store
                localStore.storeUserData(user);
                localStore.setUserLoggedIn(true);
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

            } else {
                if (connectionIssue){
                    Toast.makeText(getApplicationContext(), "Log in Error. You are currently offline!", Toast.LENGTH_SHORT).show();
                    connectionIssue = false;
                } else if (loginFailed == true){
                    Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
                    loginFailed = false;
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
