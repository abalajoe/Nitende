package app.facebook.android.com.nitende;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import app.facebook.android.com.nitende.datasource.LocalStore;
import app.facebook.android.com.nitende.datasource.User;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView nameText, emailText,remindMe;
    private ImageView imageView, hostImage, downloadedImage;
    private LocalStore localStore;
    private String firstname, lastname, email;
    private Button button;
    private int age;
    private ScrollView scrollView;
    private int dragThreshold = 10, downX = 0, downY = 0;
    private boolean lastPage = false;
    private boolean status = false;
    public static final String SERVER_ADDRESS = "http://159.203.104.154/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
               //         .setAction("Action", null).show();
                NewNote fragmentOne = new NewNote();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                fragmentTransaction.add(R.id.fragment_container, fragmentOne);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        // font
        Typeface quickSandBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
        Typeface quickSandRegular = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface quickSandItalic = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Italic.otf");

        localStore = new LocalStore(this);
        User user = localStore.getLoggedInUser();
        Log.d("user details [", user.getId() + user.getFirstname() + user.getEmail() + "]");
        firstname = user.getFirstname();
        email = user.getEmail();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        nameText = (TextView) header.findViewById(R.id.name);
        nameText.setTypeface(quickSandRegular, Typeface.BOLD);
        nameText.setTextColor(Color.BLUE);
        nameText.setText(firstname);

        emailText = (TextView) header.findViewById(R.id.email);
        emailText.setTypeface(quickSandRegular, Typeface.BOLD);
        emailText.setTextColor(Color.BLUE);
        emailText.setText(email);

        // set navigation font
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            // apply font to subment
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    /**
     * apply font to navigation menu
     * @param mi
     */
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface quickSandRegular = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , quickSandRegular), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onStart(){
        super.onStart();

        /*if (authenticated() == true){
            User user = localStore.getLoggedInUser();
            // init user details
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.age = user.getAge();

            Log.d("resSD", user.getFirstName() + user.getLastName() + user.getEmail() + user.getAge());

            /*Toast.makeText(getApplicationContext(), user.getFirstName() + user.getLastName()
                    + user.getEmail() + user.getAge() + user.getPassword(), Toast.LENGTH_SHORT).show();*/
        /*} else {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }*/
    }

    /**
     * checkk if user is logged in
     * @return
     */
    private boolean authenticated(){
        return localStore.getUsersLoggedIn();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        if (id == R.id.homee) {
            // Handle the camera_main action
            //fragmentManager.beginTransaction().replace(R.id.content_frame, new FirstFragment()).commit();
            startActivity(new Intent(getApplicationContext(), Home.class));
        } else if (id == R.id.logout) {
            localStore = new LocalStore(this);
            localStore.clearUserData();
            localStore.setUserLoggedIn(false);
            startActivity(new Intent(getApplicationContext(), SignIn.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
