package com.emergency.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ramkrishnan_v on 7/9/2014.
 */
public class SharedPref
{
    private static final String PREF_FILE =  "sos_prefs";

    private final String PREF_CONTACT_1 = "pref_contact_1";
    private final String PREF_CONTACT_2 = "pref_contact_2";
    private final String PREF_CONTACT_3 = "pref_contact_3";

    private final String PREF_MSG = "pref_msg";

    private static SharedPref mInstance;

    private Context mContext;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;

    /**
     *
     * @param context
     */
    private SharedPref(Context context)
    {
        this.mContext = context;
        mSettings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        mEditor = mSettings.edit();
    }

    /**
     *
     * @param context
     * @return
     */
    public static SharedPref getInstance(Context context)
    {
        if(mInstance == null)
            mInstance = new SharedPref(context);

        return mInstance;
    }

    /**
     *
     * @param phNo1
     * @param phNo2
     */
    public void saveEmergencyContactNo(String phNo1, String phNo2, String phNo3)
    {
        mEditor.putString(PREF_CONTACT_1, phNo1);
        mEditor.putString(PREF_CONTACT_2, phNo2);
        mEditor.putString(PREF_CONTACT_3, phNo3);
        mEditor.commit();
    }

    /**
     *
     * @param message
     */
    public void saveSOSMessage(String message)
    {
        mEditor.putString(PREF_MSG, message);
        mEditor.commit();
    }

    public String getEmergencyContact_1()
    {
        return mSettings.getString(PREF_CONTACT_1, "");
    }

    public String getEmergencyContact_2()
    {
        return mSettings.getString(PREF_CONTACT_2, "");
    }

    public String getEmergencyContact_3()
    {
        return mSettings.getString(PREF_CONTACT_3, "");
    }

    public String getSOSMessage()
    {
        return mSettings.getString(PREF_MSG, "");
    }
}
