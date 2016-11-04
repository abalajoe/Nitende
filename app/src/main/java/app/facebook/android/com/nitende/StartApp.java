package app.facebook.android.com.nitende;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 *
 * Created by joeabala on 11/2/2016.
 *
 * First Activity when our application launches
 */
public class StartApp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        boolean  firstTime=sharedPreferences.getBoolean("start", true);
        if(firstTime) {
            editor.putBoolean("start",false);
            editor.commit();
            Intent intent = new Intent(StartApp.this, SplashScreen.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(StartApp.this, MainActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);

        // set activity content from layout resourse
        setContentView(R.layout.splashscreen);

    }

    /**
     * Gracefully clean activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
