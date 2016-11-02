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
public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences dataSave = getSharedPreferences("firstLog7", 0);

        if(dataSave.getString("firstTime7", "").toString().equals("no")){ // first run is happened
            Intent intent = new Intent(SplashScreen.this,SwipeScreen.class);
            startActivity(intent);
            finish();
        }
        else{ //  this is the first run of application
            SharedPreferences.Editor editor = dataSave.edit();
            editor.putString("firstTime7", "no");
            editor.commit();
        }

        super.onCreate(savedInstanceState);

        // set activity content from layout resourse
        setContentView(R.layout.splashscreen);

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
