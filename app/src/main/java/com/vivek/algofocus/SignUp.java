package com.vivek.algofocus;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail, editTextContact_No, editTextPassword, editTextConfirmPassword, editTextName;
    String Email, password,id,Name,Contact_Number,Confirm_Password;

    public FirebaseAuth mAuth;
    private DatabaseReference database;
    private DatabaseReference mref;
    private DatabaseReference msubref;

    ArrayList<User> Userlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("SignUp");
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();
        mref=database.child("user");
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextContact_No=(EditText) findViewById(R.id.editTextContact_No);

        findViewById(R.id.buttonsignup).setOnClickListener(this);
    }

    public void createuser() {

        Name=editTextName.getText().toString().trim();
        Email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        Confirm_Password=editTextConfirmPassword.getText().toString().trim();
        Contact_Number=editTextContact_No.getText().toString().trim();
        if (!validation()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(Email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplication(), "user Registered", Toast.LENGTH_SHORT).show();
                    id =FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Toast.makeText(getApplication(), "id"+id, Toast.LENGTH_SHORT).show();
                    msubref=mref.child(id);
                    msubref.child("Email").setValue(Email);
                    msubref.child("Name").setValue(Name);
                    msubref.child("Contact number").setValue(Contact_Number);
                    msubref.child("urlToImage").setValue("");
                    //startActivity(new Intent(Signup.this , RecruiterMain.class));
                    startActivity(new Intent(SignUp.this,Home.class));

                } else {
                    Toast.makeText(getApplication(), "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validation() {
        boolean valid = true;

        if (Name.isEmpty()) {
            editTextName.setError("Please Enter the Name");
            editTextName.requestFocus();
            valid = false;
        }

        if (Email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            valid = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            editTextEmail.setError("Please Enter valid Email address");
            editTextEmail.requestFocus();
            valid = false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Please Enter the password");
            editTextPassword.requestFocus();
            valid = false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Minimum password length is 6");
            editTextPassword.requestFocus();
            valid = false;
        }
        if (Confirm_Password.isEmpty()) {
            editTextConfirmPassword.setError("Please confirm the password");
            editTextConfirmPassword.requestFocus();
            valid = false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Minimum password length is 6");
            editTextPassword.requestFocus();
            valid = false;
        }
        if (Contact_Number.isEmpty()) {
            editTextContact_No.setError("Please Enter the Contact Number");
            editTextContact_No.requestFocus();
            valid = false;
        }
        return valid;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonsignup:
                createuser();
                break;
            default:
                break;

        }
    }

}
