package app.facebook.android.com.nitende.datasource;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abala on 9/1/16.
 *
 * Store user data on phone data-store
 */
public class LocalStore {

    // file where user data will be stored
    public final static String SP_NAME = "userDetails";

    // store data on phone
    SharedPreferences sharedPreferences;


    /**
     * initialize shared-preference with activity
     * @param context activity
     */
    public LocalStore(Context context){
        // use activities that invoke this class to instantiate shared preference
        sharedPreferences = context.getSharedPreferences(SP_NAME, 0);
    }

    /**
     * store user data
     * @param user - user data
     */
    public void storeUserData(User user){
        // edit what contained in sharedPreference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", user.getId());
        editor.putString("firstName", user.getFirstname());
        editor.putString("emailAddress", user.getEmail());
        editor.commit();
    }

    /**
     * store user note
     * @param note - user note
     */
    public void storeNote(String time, String note){
        // edit what contained in sharedPreference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(time, note);
        editor.commit();
    }

    /**
     * store user note
     * @param time - note time
     */
    public String getNote(String time){
        // edit what contained in sharedPreference
        String note = sharedPreferences.getString(time,"");
        return note;
    }

    /**
     * get logged in user
     * @return logged in user
     */
    public User getLoggedInUser(){
        int userId = sharedPreferences.getInt("id", -1);
        String userName = sharedPreferences.getString("firstName","");
        String email = sharedPreferences.getString("emailAddress","");
        User user = new User(userId, userName, email);
        return user;
    }

    /**
     * if user logged in set to true otherwise false
     * @param loggedIn true/false
     */
    public void setUserLoggedIn (boolean loggedIn){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", loggedIn);
        editor.commit();

    }

    /**
     * get users logged in
     */
    public boolean getUsersLoggedIn(){
        if (sharedPreferences.getBoolean("loggedIn", false) == true){
            return true;
        } else{
            return false;
        }
    }
    /**
     * clear user data;shared-preference
     */
    public void clearUserData(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
