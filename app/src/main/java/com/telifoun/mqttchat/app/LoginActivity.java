package com.telifoun.mqttchat.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


import com.telifoun.mqttchat.core.clbs.CallbackListener;
import com.telifoun.mqttchat.gui.MqttChat;
import com.telifoun.mqttchat.tools.alert_dialog.AlertDialog;

public class LoginActivity extends AppCompatActivity {
    private Button signInBtn;
    private EditText txtEmail, txtPassword, txtPhone;
    private ProgressBar mProgressBar;
    private Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtPhone=(EditText) findViewById(R.id.phone);
        txtEmail=(EditText) findViewById(R.id.inputEmail);
        txtPassword=(EditText) findViewById(R.id.inputPassword);
        mProgressBar=(ProgressBar) findViewById(R.id.login_progress);
        signInBtn =(Button) findViewById(R.id.sign_in_button);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                mqttchatLogin();
            }
        });

        Button btnRegister = (Button) findViewById(R.id.register_button);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }


    /**
     *
     */
    private void mqttchatLogin(){

        signInBtn.setEnabled(false);
        String phone = txtPhone.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(phone)) {
            txtPhone.setError(getString(R.string.error_field_required));
            focusView = txtPhone;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.error_field_required));
            focusView = txtEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getString(R.string.error_field_required));
            focusView = txtPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            signInBtn.setEnabled(true);
        } else {

           mProgressBar.setVisibility(View.VISIBLE);

            MqttChat.getInstance().logIn(getApplication(), Integer.parseInt(phone), new CallbackListener() {
                @Override
                public void onSuccess(Object o) {
                    MqttChat.getInstance().Connect(new CallbackListener() {
                        @Override
                        public void onSuccess(Object o) {
                            mProgressBar.setVisibility(View.GONE);
                            signInBtn.setEnabled(true);
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onError(String s) {
                            AlertDialog.YesOnly(LoginActivity.this,"Error",s);
                            mProgressBar.setVisibility(View.GONE);
                            signInBtn.setEnabled(true);
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        }

                    });

                }

                @Override
                public void onError(String s) {
                    signInBtn.setEnabled(true);
                    txtPhone.setError(s);
                    txtPhone.requestFocus();
                    txtEmail.setError(s);
                    txtEmail.requestFocus();
                    txtPassword.setError(s);
                    txtPassword.requestFocus();
                    mProgressBar.setVisibility(View.GONE);
                }
            });


        }

    }
}
