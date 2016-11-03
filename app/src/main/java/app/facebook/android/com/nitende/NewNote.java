package app.facebook.android.com.nitende;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;

/**
 * Created by jabala on 11/3/2016.
 */
public class NewNote extends Fragment {
    private static TextView remindMeText;
    private EditText note;
    private ImageView closeIcon, remindMeIcon, submitIcon;
    private CalendarView calendar;
    private View view;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private final String URL_TO_HIT = "http://159.203.104.154/nitende/note.php";
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    private int hour, minute;
    private ProgressDialog dialog;
    private boolean connectionIssue = false;
    String noteText = null;
    public static TextView getTextView() {
        return remindMeText;
    }
    public NewNote(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);

        Typeface quickSandBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
        Typeface quickSandRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface quickSandItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Italic.otf");
        Typeface quickSandLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Light.otf");

        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait..");

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_note, container, false);
        remindMeText = (TextView) view.findViewById(R.id.remind_text);
        remindMeText.setTypeface(quickSandRegular, Typeface.BOLD);

        note = (EditText) view.findViewById(R.id.edit_note);
        note.setTypeface(quickSandLight, Typeface.BOLD);

        closeIcon = (ImageView) view.findViewById(R.id.back_icon);
        remindMeIcon = (ImageView) view.findViewById(R.id.remind_icon);
        submitIcon = (ImageView) view.findViewById(R.id.submit_icon);

        remindMeIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                noteText = (String) note.getText().toString();
                if (!noteText.trim().equals("")){
                    Log.d("txt","not empty");
                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minuteOfHour) {

                                    remindMeText.setText(hourOfDay + ":" + minuteOfHour);
                                    hour = hourOfDay;
                                    minute = minuteOfHour;
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                } else {
                    Log.d("TAG","note not set");
                    remindMeText.setText("remind me");
                    Toast.makeText(getActivity(), "Write TODO first before you set reminder", Toast.LENGTH_LONG).show();
                }
            }

        });

        submitIcon.setOnClickListener(new View.OnClickListener() {
            long time;
            @Override
            public void onClick(View view) {

                noteText = (String) note.getText().toString();

                Log.d("textview",remindMeText.getText().toString());
                String timeText = remindMeText.getText().toString().trim();

                if(timeText.matches(".*\\d.*") && !noteText.trim().equals("")){
                    Toast.makeText(getActivity(), "ALARM ON", Toast.LENGTH_SHORT).show();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    Log.d("hor",hour+"");
                    Log.d("minute",minute+"");
                    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

                    Intent cancellationIntent = new Intent(getActivity(), CancelAlarmReceiver.class);
                    cancellationIntent.putExtra("key", pendingIntent);
                    PendingIntent cancellationPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                    time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
                    Log.d("time", time + "");
                    if(System.currentTimeMillis()>time)
                    {
                        if (calendar.AM_PM == 0)
                            time = time + (1000*60*60*12);
                        else
                            time = time + (1000*60*60*24);
                    }

                    //alarmManager.set(AlarmManager.RTC_WAKEUP, 5000, cancellationPendingIntent);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

                    new noteTask().execute(URL_TO_HIT);
                } else {
                    Log.d("TAG","note and time not set");
                    remindMeText.setText("remind me");
                    Toast.makeText(getActivity(), "Write TODO and set reminder before you submit", Toast.LENGTH_LONG).show();
                }
               /*
                Log.d("alarm","alarm4");

*/

            }

        });
        closeIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                ft.remove(NewNote.this);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
                //Toast.makeText(getActivity(), "close", Toast.LENGTH_SHORT).show();
                //finish();
                //overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }

        });


        return view;
    }

    public class noteTask extends AsyncTask<String,String, String > {

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
                        .appendQueryParameter("note", noteText)
                        .appendQueryParameter("notetime", hour + ":" +minute);

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
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                ft.remove(NewNote.this);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();

                Toast.makeText(getActivity(), "successfully added note", Toast.LENGTH_SHORT).show();

            } else {
                if (connectionIssue){
                    Toast.makeText(getActivity(), "Log in Error. You are currently offline!", Toast.LENGTH_SHORT).show();
                    connectionIssue = false;
                } else {
                    Toast.makeText(getActivity(), "Problem Logging in, please try again.", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}
