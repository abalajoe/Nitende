package app.facebook.android.com.nitende;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    private int hour, minute;
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
       //

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_note, container, false);
        remindMeText = (TextView) view.findViewById(R.id.remind_text);
        remindMeText.setTypeface(quickSandRegular, Typeface.BOLD);

        note = (EditText) view.findViewById(R.id.edit_note);
        note.setTypeface(quickSandLight, Typeface.BOLD);

        closeIcon = (ImageView) view.findViewById(R.id.back_icon);
        remindMeIcon = (ImageView) view.findViewById(R.id.remind_icon);
        submitIcon = (ImageView) view.findViewById(R.id.submit_icon);

        closeIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "close", Toast.LENGTH_SHORT).show();
                //finish();
                //overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }

        });

        remindMeIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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
            }

        });

       submitIcon.setOnClickListener(new View.OnClickListener() {
           long time;
            @Override
            public void onClick(View view) {
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

                Log.d("alarm","alarm4");



            }

        });
        return view;
    }


}
