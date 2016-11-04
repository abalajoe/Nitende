package app.facebook.android.com.nitende;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.facebook.android.com.nitende.datasource.LocalStore;
import app.facebook.android.com.nitende.datasource.Note;
import app.facebook.android.com.nitende.datasource.User;

public class MyNotes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView nameText, emailText,remindMe,notesUnavailableSnack, connectionProblemSnack;
    private ImageView imageView, hostImage, downloadedImage;
    private LocalStore localStore;
    private String id, firstname, lastname, email;
    private Button button;
    private ProgressDialog dialog;
    private int age;
    private ScrollView scrollView;
    private int dragThreshold = 10, downX = 0, downY = 0;
    private boolean lastPage = false;
    private boolean status = false;
    private ListView notesListView;
    public static final String SERVER_ADDRESS = "http://159.203.104.154/";
    private final String URL_TO_HIT = "http://159.203.104.154/nitende/mynotes.php";
    private DateFormat dateFormat;
    private Date date;
    private String parseDate[];
    private String dateToString;
    private String dateNow;
    private String timeNow;
    private String parseTime[];
    private String hour;
    private String minutes;
    private String parsedTime;
    private int intParsedTime;
    private boolean notesAvailable = true;
    private Snackbar connectionProblem;
    private Snackbar notesUnavailable;
    private LinearLayout linearLayout;
    class ViewHolder{
        private TextView note;
        private TextView noteTime;
        private TextView reviews;
        private TextView cost;
        private TextView canceledCost;
        private RatingBar hotelRating;
        private TextView tvStory;
    }

    ViewHolder holder = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mynotes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // font
        Typeface quickSandBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
        Typeface quickSandRegular = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface quickSandItalic = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Italic.otf");

        linearLayout = (LinearLayout) findViewById(R.id.linear);
        connectionProblem = Snackbar.make(linearLayout, "Log in Error. You are currently offline!", Snackbar.LENGTH_LONG)
                .setAction("Action", null);

        notesUnavailable = Snackbar.make(linearLayout, "You do not have any Tasks", Snackbar.LENGTH_LONG)
                .setAction("Action", null);

        notesUnavailableSnack = (TextView) notesUnavailable.getView().findViewById(android.support.design.R.id.snackbar_text);
        notesUnavailableSnack.setTypeface(quickSandItalic);

        connectionProblemSnack = (TextView) connectionProblem.getView().findViewById(android.support.design.R.id.snackbar_text);
        connectionProblemSnack.setTypeface(quickSandItalic);

        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        date = new Date();
        dateToString = dateFormat.format(date);
        parseDate = dateToString.split(" ");
        dateNow = parseDate[0];
        timeNow = parseDate[1];

        parseTime = timeNow.split(":");
        hour = parseTime[0];
        minutes = parseTime[1];
        parsedTime = hour + minutes;
        intParsedTime = Integer.parseInt(parsedTime);

        System.out.println(hour + " " + minutes);

        localStore = new LocalStore(this);
        User user = localStore.getLoggedInUser();
        Log.d("user details [", user.getId() + user.getFirstname() + user.getEmail() + "]");
        id = String.valueOf(user.getId());
        firstname = user.getFirstname();
        email = user.getEmail();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        nameText = (TextView) header.findViewById(R.id.name);
        nameText.setTypeface(quickSandRegular, Typeface.BOLD);
        nameText.setTextColor(Color.BLUE);
        nameText.setText("Welcome " + firstname);

        emailText = (TextView) header.findViewById(R.id.email);
        emailText.setTypeface(quickSandRegular, Typeface.BOLD);
        emailText.setTextColor(Color.BLUE);
        emailText.setText("");

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

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        notesListView = (ListView)findViewById(R.id.notesList);

        new JSONTask().execute(URL_TO_HIT);

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

    public class JSONTask extends AsyncTask<String,String, List<Note>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<Note> doInBackground(String... params) {
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
                        .appendQueryParameter("id", id);

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
                Log.d("finaljson", finalJson);

                JSONObject parentObject = new JSONObject(finalJson);
                String jsonResult = parentObject.getString("spaces");
                Log.d("jsonObject ", jsonResult);
                if (jsonResult == "false"){
                    Log.d("jsonObject ", "=== false ===");
                    notesAvailable = false;
                    return null;
                } else {
                    JSONArray parentArray = parentObject.getJSONArray("spaces");

                    List<Note> notesList = new ArrayList<>();

                    Gson gson = new Gson();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        Note note = gson.fromJson(finalObject.toString(), Note.class);

                        // adding the final object in the list
                        notesList.add(note);
                    }
                    return notesList;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(final List<Note> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                SpaceAdapter adapter = new SpaceAdapter(getApplicationContext(), R.layout.row, result);
                notesListView.setAdapter(adapter);
            } else if (!notesAvailable){
                notesUnavailable.show();
                notesAvailable = true;
            }else {
                connectionProblem.show();
                //Toast.makeText(getApplicationContext(), "unable to fetch data from server please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class SpaceAdapter extends ArrayAdapter{

        private List<Note> spaceModelList;
        private int resource;
        private LayoutInflater inflater;
        public SpaceAdapter(Context context, int resource, List<Note> objects) {
            super(context, resource, objects);
            spaceModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {



            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.note = (TextView)convertView.findViewById(R.id.note);
                holder.noteTime = (TextView)convertView.findViewById(R.id.notetime);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Typeface quickSandBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
            Typeface quickSandRegular = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
            Typeface quickSandItalic = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Italic.otf");

            holder.note.setTypeface(quickSandRegular);
            holder.note.setTextColor(Color.WHITE);
            holder.noteTime.setTypeface(quickSandRegular);
            holder.noteTime.setTextColor(Color.WHITE);

            String notetime = spaceModelList.get(position).getNotetime();
            String parseNotetime[] = notetime.split(":");
            String hour = parseNotetime[0];
            String minutes = parseNotetime[1];
            String time = hour + minutes;
           // int intNotetime = Integer.parseInt(notetime);
            int intNotetime = Integer.parseInt(time);

            if (intParsedTime > intNotetime ){
                holder.noteTime.setText(spaceModelList.get(position).getNotetime());
                holder.noteTime.setPaintFlags(holder.noteTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.noteTime.setText(spaceModelList.get(position).getNotetime());
            }
            Log.d("time - :", time);
            Log.d("parsedTime", parsedTime);
            Log.d("note:",spaceModelList.get(position).getNote());
            Log.d("note2:",spaceModelList.get(position).getNotetime());
            holder.note.setText(spaceModelList.get(position).getNote());

            return convertView;
        }
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
            startActivity(new Intent(getApplicationContext(), Home.class));
        } else if (id == R.id.notes) {
            startActivity(new Intent(getApplicationContext(), MyNotes.class));
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
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
