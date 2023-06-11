package com.telifoun.mqttchat.app;

import static com.telifoun.mqttchat.app.R.id.name;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.telifoun.mqttchat.core.clbs.CallbackListener;
import com.telifoun.mqttchat.gui.MqttChat;
import com.telifoun.mqttchat.sdk.RestResponse;
import com.telifoun.mqttchat.sdk.sdkCallback;
import com.telifoun.mqttchat.sdk.sdkUser;
import com.telifoun.mqttchat.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText email,name,password,confirmPassword,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        phone=(EditText) findViewById(R.id.phone);
        name=(EditText) findViewById(R.id.inputName);
        email=(EditText) findViewById(R.id.inputEmail);
        password=(EditText) findViewById(R.id.inputPassword);
        confirmPassword=(EditText) findViewById(R.id.inputConfirmPassword);
        registerBtn=(Button) findViewById(R.id.register_button);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkEmptyField()){
                    Toast.makeText(getApplicationContext(),"Empty field error!",Toast.LENGTH_SHORT).show();
                }else{
                    addUser();
                }
            }
        });


    }


    private boolean checkEmptyField(){
        return phone.getText().equals("") || email.getText().equals("") || name.getText().equals("") || password.getText().equals("") || confirmPassword.getText().equals("");
    }


    private void addUser(){

        final ProgressDialog mProgressDialog = ProgressDialog.show(this, "Adding new User ",
                "Adding new User to MQTTCHAT ...", true);

        new Thread((new Runnable() {
            @Override
            public void run() {

                mProgressDialog.setMessage("Adding User ...");

                HashMap<String,Object> user_data=new HashMap<String,Object>();
                user_data.put("phone", Integer.parseInt(phone.getText().toString()));
                user_data.put("email", Integer.parseInt(email.getText().toString()));
                user_data.put("name", name.getText().toString());
                user_data.put("password",password.getText().toString());
                user_data.put("confirm password",confirmPassword.getText().toString());

                (new restRequest()).request(JsonObjectRequest.Method.POST, Config.URL_USER_REGISTER, user_data, new CallbackListener() {
                    @Override
                    public void onSuccess(Object o) {
                        mProgressDialog.setMessage("User add success");
                        mProgressDialog.setMessage("Login to demo application with new user ...");
                        MqttChat.getInstance().logIn(getApplication(), Integer.parseInt(email.getText().toString()), new CallbackListener() {
                            @Override
                            public void onSuccess(Object o) {
                                mProgressDialog.dismiss();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                            }

                            @Override
                            public void onError(String s) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Login Error !:"+s,Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String s) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Add new user error !:"+s,Toast.LENGTH_LONG).show();
                    }
                });
            }
        })).start();
    }
}
