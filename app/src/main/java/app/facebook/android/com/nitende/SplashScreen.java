package app.facebook.android.com.nitende;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

/**
 *
 * Created by joeabala on 11/2/2016.
 *
 * First Activity when our application launches
 */
public class SplashScreen extends Activity {
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set activity content from layout resourse
        setContentView(R.layout.splashscreen);

        // font family
        Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
        Typeface light = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Light.otf");
        Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(light, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#FFFFCC"));

        /**
         * Display activity for t time and move to next activity
         */
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    // start new activity
                    Intent intent = new Intent(SplashScreen.this,SwipeScreen.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
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
