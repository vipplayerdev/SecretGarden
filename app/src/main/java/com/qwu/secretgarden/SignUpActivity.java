package com.qwu.secretgarden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends AppCompatActivity {

    EditText account, password, familyCode;
    Button btnSignUp;
    TextView loginText;
    FirebaseAuth mAuth;
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("currentUser", currentUser == null ? "null":currentUser.toString());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.mAuth = FirebaseAuth.getInstance();

        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        familyCode = findViewById(R.id.familyCode);
        btnSignUp = findViewById(R.id.btnSignUp);
        loginText = findViewById(R.id.loginText);

        btnSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        signUp();

                    }
                }

//        if (task.isSuccessful()) {
//
//            //Toast.makeText(SignUpActivity.this,"Success, please login", Toast.LENGTH_LONG).show();
//
////                                            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
////                                            startActivity(i);
//
//        } else {
////                                            Toast.makeText(SignUpActivity.this," Sign up failed", Toast.LENGTH_LONG).show();
////                                            return;
//        }

        );

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }

        });

    }

    private void signUp(){

        if(account.getText().toString().isEmpty()){
            Toast.makeText(SignUpActivity.this," Please input account", Toast.LENGTH_LONG).show();
            return;
        }
        if(password.getText().toString().isEmpty()){
            Toast.makeText(SignUpActivity.this," Please input password", Toast.LENGTH_LONG).show();
            return;
        }
        if(familyCode.getText().toString().isEmpty()){
            Toast.makeText(SignUpActivity.this," Please input family code", Toast.LENGTH_LONG).show();
            return;
        }


        this.mAuth.createUserWithEmailAndPassword(account.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            tryLogin();

                        } else {
                            Toast.makeText(SignUpActivity.this," Sign up failed", Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });


    }

    private void tryLogin(){

        mAuth.signInWithEmailAndPassword(account.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            setUserDataInRealtimeDB(mAuth.getCurrentUser().getUid());

                        } else {

                            Toast.makeText(SignUpActivity.this,"Sign Up Failed", Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    private void setUserDataInRealtimeDB(String firebaseUid){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user_"+firebaseUid);

        try {


            JSONObject obj = new JSONObject();
            obj.put("id",firebaseUid);
            obj.put("account",account.getText().toString());
            obj.put("family_code",familyCode.getText().toString());
            obj.put("avatar","");
            obj.put("name","");
            myRef.setValue(obj.toString());

            // Sign in success, update UI with the signed-in user's information
            Toast.makeText(SignUpActivity.this," Sign up success", Toast.LENGTH_LONG).show();
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(i);

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

    }


}