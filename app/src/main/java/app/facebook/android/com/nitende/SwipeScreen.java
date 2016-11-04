package app.facebook.android.com.nitende;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by jabala on 11/2/2016.
 */
public class SwipeScreen extends Activity {

    ImageView submitButton;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*SharedPreferences dataSave = getSharedPreferences("firstLog9", 0);

        if(dataSave.getString("firstTime9", "").toString().equals("no")){ // first run is happened
            Intent intent = new Intent(SwipeScreen.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else { //  this is the first run of application
            SharedPreferences.Editor editor = dataSave.edit();
            editor.putString("firstTime9", "no");
            editor.commit();
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipescreen);

        Typeface quickSandRegular = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface quickSandBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");


        submitButton = (ImageView) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("clickk","click");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }

        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        viewPager.setAdapter(new CustomPagerAdapter(this));
        indicator.setViewPager(viewPager);



    }

}
