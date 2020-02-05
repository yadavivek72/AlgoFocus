package com.vivek.algofocus;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Login extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    EditText editTextEmail,editTextPassword;

    private DatabaseReference database;
    private DatabaseReference mref;
    private  DatabaseReference msubref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmail=(EditText)findViewById(R.id.username);
        editTextPassword=(EditText)findViewById(R.id.password);
        findViewById(R.id.buttonsignup).setOnClickListener(this);
        findViewById(R.id.buttonlogin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonlogin:
                userLogin();
                // Toast.makeText(getApplication(),"User successfully login",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonsignup:
                Intent intent = new Intent(this, SignUp.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    private void userLogin(){

    }
}
