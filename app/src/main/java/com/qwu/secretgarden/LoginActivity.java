package com.qwu.secretgarden;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText account, password;
    Button btnLogin;
    TextView signUpText;
    private FirebaseAuth mAuth;
    SqlLiteController myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        signUpText = findViewById(R.id.signUp);

        myDb = new SqlLiteController(this);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(account.getText().toString().isEmpty()){
                            Toast.makeText(LoginActivity.this," Please input account", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(password.getText().toString().isEmpty()){
                            Toast.makeText(LoginActivity.this," Please input password", Toast.LENGTH_LONG).show();
                            return;
                        }

                        fbLogin(account.getText().toString(),password.getText().toString());

                    }
                }
        );

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);

            }

        });

    }

    private void fbLogin(String email,String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            myDb.FirebaseUserId = mAuth.getCurrentUser().getUid();
                            realtimeDatabaseGetUser();

                        } else {

                           Toast.makeText(LoginActivity.this,"Sign In Failed", Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    private void realtimeDatabaseGetUser(){

        // Get a reference to our posts
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user_"+myDb.FirebaseUserId);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                try {
                    JSONObject obj = new JSONObject(value);
                    sqlLiteGetUser(obj);

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    private void sqlLiteGetUser(JSONObject obj){

        try {

            myDb.FamilyCode = obj.getString("family_code");

            SQLiteDatabase db = myDb.getWritableDatabase();
            Cursor res = db.rawQuery("select * from " + SqlLiteController.TABLE_NAME + " where id = '" + obj.getString("id") + "' limit 0,1", null);

            StringBuffer buffer = new StringBuffer();

            Boolean isGotData = false;
            while (res.moveToNext()) { //cycle thru result set
                isGotData = true;
            }

            if (!isGotData) {

                this.sqlLiteNewUser(obj.getString("id"), obj.getString("account"), obj.getString("family_code"));

            } else {

                this.sqlLiteUpdateUser(obj.getString("id"),obj.getString("account"),obj.getString("name"),obj.getString("family_code"),obj.getString("avatar"));

            }
        }catch (JSONException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void sqlLiteNewUser(String  firebaseUserId,String account,String code) throws InterruptedException {

        myDb = new SqlLiteController(this);

        myDb.insertData(firebaseUserId,account,code,"","");
        sleep(2000);
        this.jump();

    }

    private void sqlLiteUpdateUser(String  firebaseUserId,String account,String name,String code,String avatar) throws InterruptedException {

        myDb = new SqlLiteController(this);

        boolean affected = myDb.updateData(firebaseUserId,account,name,code,avatar);

        sleep(2000);
        this.jump();

    }

    private void jump(){
        Toast.makeText(LoginActivity.this,"Sign In Success", Toast.LENGTH_LONG).show();

        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
    }
}