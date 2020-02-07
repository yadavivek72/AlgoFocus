package com.vivek.algofocus;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Login extends AppCompatActivity implements View.OnClickListener{
    private SignInButton signInButton;
    private LoginButton facebook_Login_button;
    private GoogleSignInClient googleSignInClient;
    private String TAG="Login";
    private String TAG1="FacebookAuthentication";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    EditText editTextEmail,editTextPassword;
    String Email,Password;
    private DatabaseReference database;
    private DatabaseReference mref;
    private  DatabaseReference msubref;
    private int RC_SIGN_IN = 1;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    String Email_sh,Password_sh;


    public void onCheckBoxClicked(View view)
    {
        boolean checked=((CheckBox) view).isChecked();
        SharedPreferences sharedPreferences= getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Email_sh=editTextEmail.getText().toString();
        Password_sh=editTextEmail.getText().toString();
        editor.putString("Email",Email_sh);
        editor.putString("Password",Password_sh);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();
        mref=database.child("user");
        if(Email_sh!=null) {
            editTextEmail.setText(Email_sh);
            editTextPassword.setText(Password_sh);
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        facebook_Login_button=findViewById(R.id.facebook_login_button);
        facebook_Login_button.setReadPermissions("email","public_profile");
        facebook_Login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG1,"onSuccess"+ loginResult);
                handleFacebookToken(loginResult.getAccessToken());


            }

            @Override
            public void onCancel() {
                Log.d(TAG1,"onCancel");


            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG1,"onError"+ error);

            }
        });

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null)
                {

                }
                else
                {
                }

            }
        };

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken==null)
                {
                    mAuth.signOut();
                }
            }
        };

        editTextEmail=findViewById(R.id.username);
        editTextPassword=findViewById(R.id.password);
        findViewById(R.id.buttonsignup).setOnClickListener(this);
        findViewById(R.id.buttonlogin).setOnClickListener(this);
        findViewById(R.id.google_signin).setOnClickListener(this);



        GoogleSignInOptions googleSignInOptions= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);

        
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonlogin:
                userLogin();
                break;
            case R.id.buttonsignup:
                Intent intent = new Intent(this, SignUp.class);
                startActivity(intent);
                break;
            case R.id.google_signin:
                signin();
                break;

            default:
                break;
        }

    }

    private void userLogin(){
        Email=editTextEmail.getText().toString().trim();
        Password=editTextPassword.getText().toString().trim();
        if(!validation()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplication(),"Successful authentication",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this,Home.class));
                    finish();
                }
                else {
                    // If sign in fails, display a message to the user.
                    //   Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplication(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validation(){
        boolean valid =true;

        if(Email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            valid =false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            editTextEmail.setError("Please Enter valid Email address");
            editTextEmail.requestFocus();
            valid =false;
        }
        if(Password.isEmpty()){
            editTextPassword.setError("Please Enter the password");
            editTextPassword.requestFocus();
            valid=false;
        }
        if(Password.length()<6){
            editTextPassword.setError("Minimum password length is 6");
            editTextPassword.requestFocus();
            valid=false;
        }

        return valid; }

        private void signin()
        {
            Intent signInIntent=googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent,RC_SIGN_IN);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSigninResult(task);
        }
    }

    private void handleSigninResult(  Task<GoogleSignInAccount> completTask){
        try{
            GoogleSignInAccount googleSignInAccount= completTask.getResult(ApiException.class);
            Toast.makeText(Login.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            firebaseGoogleAuth(googleSignInAccount);
        }
        catch (ApiException e){
            Toast.makeText(Login.this,"Failed",Toast.LENGTH_SHORT).show();
        }

    }

    private void firebaseGoogleAuth(GoogleSignInAccount Account){
        AuthCredential authCredential= GoogleAuthProvider.getCredential(Account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Login.this,"Successfull",Toast.LENGTH_SHORT).show();
                    String id =FirebaseAuth.getInstance().getCurrentUser().getUid();
                    updateUI(id);

                }
                else
                {
                    Toast.makeText(Login.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(String id)
    {
        GoogleSignInAccount acc=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(acc!=null)
        {

            Toast.makeText(getApplication(), "id"+id, Toast.LENGTH_SHORT).show();
            msubref=mref.child(id);
            msubref.child("Email").setValue(acc.getEmail());
            msubref.child("Name").setValue(acc.getDisplayName());
            msubref.child("Contact number").setValue("");
            if(acc.getPhotoUrl()!=null)
            {
                String photoUrl=acc.getPhotoUrl().toString();
                msubref.child("urlToImage").setValue(photoUrl.trim());
            }
            else
            {
                msubref.child("urlToImage").setValue("");
            }
            googleSignInClient.signOut();
            startActivity(new Intent(Login.this,Home.class));
            finish();

        }
        else {
            Toast.makeText(getApplication(), "InsideElse of Update UI", Toast.LENGTH_SHORT).show();
        }
    }



    private void handleFacebookToken(AccessToken token)
    {
        Log.d(TAG1,"handleFacebookToken"+token);
        AuthCredential credential= FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG1,"signinwith credential: successfull");
                    FirebaseUser user= mAuth.getCurrentUser();
                    String id =FirebaseAuth.getInstance().getCurrentUser().getUid();
                    updatefbUI(user,id);
                }
                else
                {
                    Log.d(TAG1,"signinwith credential: failed",task.getException());
                    Toast.makeText(getApplicationContext(),"Authentication Failed", Toast.LENGTH_SHORT).show();
                    updatefbUI(null,null);

                }
            }
        });

    }

    private void updatefbUI(FirebaseUser user,String id)
    {
        if(user!=null)
        {

            String Name=user.getDisplayName();
            msubref=mref.child(id);
            msubref.child("Email").setValue("");
            msubref.child("Name").setValue(Name.trim());
            msubref.child("Contact number").setValue("");
            if(user.getPhotoUrl()!=null)
            {
                String photoUrl=user.getPhotoUrl().toString();
                msubref.child("urlToImage").setValue(photoUrl.trim());
            }
            else
            {
                msubref.child("urlToImage").setValue("");
            }
            startActivity(new Intent(Login.this,Home.class));
            finish();

        }
    }
    
}


