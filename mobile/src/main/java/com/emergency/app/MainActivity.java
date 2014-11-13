package com.emergency.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener
{
    private EditText mPhNoEdt1, mPhNoEdt2, mPhNoEdt3, mMessageEdt;
    private Button mSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        mPhNoEdt1 = (EditText) findViewById(R.id.edt1);
        mPhNoEdt2 = (EditText) findViewById(R.id.edt2);
        mPhNoEdt3 = (EditText) findViewById(R.id.edt3);

        mMessageEdt = (EditText) findViewById(R.id.edt4);

        mSaveBtn = (Button) findViewById(R.id.saveBtn);
        mSaveBtn.setOnClickListener(this);

        mPhNoEdt1.setText(SharedPref.getInstance(this).getEmergencyContact_1());
        mPhNoEdt2.setText(SharedPref.getInstance(this).getEmergencyContact_2());
        mPhNoEdt3.setText(SharedPref.getInstance(this).getEmergencyContact_3());

        mMessageEdt.setText(SharedPref.getInstance(this).getSOSMessage());
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
        if (id == R.id.action_info) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.saveBtn:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPhNoEdt1.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPhNoEdt2.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPhNoEdt3.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mMessageEdt.getWindowToken(), 0);
                saveOrUpdateContent();
                break;
        }
    }

    /**
     *
     */
    private void saveOrUpdateContent()
    {
        String phNo1 = mPhNoEdt1.getText().toString();
        String phNo2 = mPhNoEdt2.getText().toString();
        String phNo3 = mPhNoEdt3.getText().toString();
        String msg = mMessageEdt.getText().toString();
        if(TextUtils.isEmpty(phNo1) && TextUtils.isEmpty(phNo2) && TextUtils.isEmpty(phNo3))
            Toast.makeText(this, getString(R.string.empty_no_msg), Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(msg))
        {
            Toast.makeText(this, getString(R.string.empty_sos_msg), Toast.LENGTH_SHORT).show();
        }
        else
        {
            SharedPref.getInstance(this).saveEmergencyContactNo(phNo1, phNo2, phNo3);
            SharedPref.getInstance(this).saveSOSMessage(msg);

            Toast.makeText(this, getString(R.string.saved_msg), Toast.LENGTH_SHORT).show();
        }
    }
}
