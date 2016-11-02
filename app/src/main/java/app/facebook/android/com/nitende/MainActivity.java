package app.facebook.android.com.nitende;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends Activity {

    private Button login, register, facebook;
    private TextView title, slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);


        // font family
        Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
        Typeface light = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Light.otf");
        Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(bold, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#FFFFCC"));

        slogan = (TextView) findViewById(R.id.slogan);
        slogan.setTypeface(regular, Typeface.BOLD);
        slogan.setTextColor(Color.parseColor("#FFFFCC"));

        login =(Button) findViewById(R.id.login);
        login.setText("Login");
        login.setTypeface(regular);
        login.setTextColor(Color.parseColor("#82007E"));

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
              Intent myIntent = new Intent(MainActivity.this, SignIn.class);
              startActivity(myIntent);

                //slide from down to up
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }

        });

        register =(Button) findViewById(R.id.register);
        register.setText("Create Account");
        register.setTypeface(regular);
        register.setTextColor(Color.parseColor("#FFFFCC"));

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, Register.class);
                startActivity(myIntent);

                //slide from down to up
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }

        });
    }
}