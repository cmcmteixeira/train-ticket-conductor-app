package com.railway.railwayconductor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.analytics.Logger;
import com.railway.railwayconductor.DI;
import com.railway.railwayconductor.R;
import com.railway.railwayconductor.activity.listener.LoginActivityLoginClick;
//import com.railway.railwayconductor.activity.listeners.LoginActivityLoginClick;
//import com.railway.railwayconductor.activity.listeners.LoginActivityRegisterClick;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    private Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //add handler for login
        this.login    = (Button)findViewById(R.id.login_btn_sign_in);
        login.setOnClickListener(new LoginActivityLoginClick());
        if (DI.get().provideStorage().getInspector() != null) {
            Log.println(Logger.LogLevel.ERROR, "DEBUG", "Changing Activity");
            Intent intent = new Intent(this, SelectStationsActivity.class);
            this.startActivity(intent);
            int h = 2;
        }
    }

    public void disableButtons(){
        this.login.setEnabled(false);
    }

    public void enableButtons(){
        this.login.setEnabled(true);
    }

}

