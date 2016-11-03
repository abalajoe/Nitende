package app.facebook.android.com.nitende;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import java.util.List;

import app.facebook.android.com.nitende.datasource.Note;

public class MyNotes extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final String URL_TO_HIT = "http://159.203.104.154/nitende/mynotes.php";
    private TextView tvData, cancelCost;
    private ListView notesListView;
    private ProgressDialog dialog;
    private int toggleLikeButton = 0;
    private ImageView hotels, apartments, cottages, homes;
    private boolean spaceAvailable = true;

    private String url;
    private String urlFlag;
    private String budget;
    private String room;
    private String bed;
    private String bedroom;
    private String bathroom;
    private String wifi;
    private String tv;
    private String pool;

    // Git error fix - http://stackoverflow.com/questions/16614410/android-studio-checkout-github-error-createprocess-2-windows
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
        getSupportActionBar().setTitle(null);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.urlFlag = extras.getString("urlflag");
            this.url = extras.getString("url");
            this.budget = extras.getString("budget");
            this.room = extras.getString("space");
            this.bed = extras.getString("beds");
            this.bedroom = extras.getString("bedrooms");
            this.bathroom = extras.getString("bathrooms");
            this.wifi = extras.getString("wifi");
            this.tv = extras.getString("tv");
            this.pool = extras.getString("pool");

            Log.d("bundles ", urlFlag +":"+url+":"+budget+":"+room+":"+bed+":"+bedroom
                    +":"+bathroom+":"+wifi+":"+tv+":"+pool);
            Log.d("bn",urlFlag);
        }


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


        // To start fetching the data when app start, uncomment below line to start the async task.
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
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
                    JSONArray parentArray = parentObject.getJSONArray("spaces");

                    List<Note> notesList = new ArrayList<>();

                    Gson gson = new Gson();
                    for(int i=0; i<parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        Note note = gson.fromJson(finalObject.toString(), Note.class);

                        // adding the final object in the list
                        notesList.add(note);
                    }
                    return notesList;


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
                NotesAdapter adapter = new NotesAdapter(getApplicationContext(), R.layout.row, result);
                notesListView.setAdapter(adapter);
                /*spaceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SpaceModel spaceModel = result.get(position);
                        Intent intent = new Intent(TimelineActivity.this, TimelineDetailActivity.class);
                        intent.putExtra("spaceModel", new Gson().toJson(spaceModel));
                        startActivity(intent);
                    }
                });*/
            } else {
                Toast.makeText(getApplicationContext(), "unable to fetch data from server please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class NotesAdapter extends ArrayAdapter {

        private List<Note> note;
        private int resource;
        private LayoutInflater inflater;
        public NotesAdapter(Context context, int resource, List<Note> objects) {
            super(context, resource, objects);
            note = objects;
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
                holder.reviews = (TextView)convertView.findViewById(R.id.reviews);
                holder.cost = (TextView)convertView.findViewById(R.id.cost);
                holder.canceledCost = (TextView)convertView.findViewById(R.id.cancelcost);
                //holder.tvStory = (TextView)convertView.findViewById(R.id.tvStory);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Typeface quickSandBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
            Typeface quickSandRegular = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
            Typeface quickSandItalic = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Italic.otf");

            holder.note.setTypeface(quickSandRegular);
            holder.note.setTextColor(Color.BLACK);
            holder.noteTime.setTypeface(quickSandRegular);
            holder.noteTime.setTextColor(Color.BLACK);

            Log.d("mynotes",note.get(position).getNote());
            Log.d("mynotes",note.get(position).getNotetime());
            holder.note.setText(note.get(position).getNote());
            holder.noteTime.setText(note.get(position).getNotetime());
            holder.reviews.setText(note.get(position).getNote());
            holder.cost.setText(note.get(position).getNotetime());
            holder.canceledCost.setText(note.get(position).getNote());

            return convertView;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new JSONTask().execute(URL_TO_HIT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}
