package com.qwu.secretgarden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;


public class PostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SqlLiteController myDb;
    Button btnBack;
    ImageView uploadImage,imagePreview,submitImage;
    Uri imageUri;
    HashMap<String,String> sqlLiteUser;
    private static final int PICTURE_CODE = 86;

    EditText messageText;
    String newMessageImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        myDb = new SqlLiteController(this);
        sqlLiteUser = myDb.getUserByFirebaseUid(SqlLiteController.FirebaseUserId);

        messageText = findViewById(R.id.messageText);
        submitImage = findViewById(R.id.submitImage);
        btnBack = findViewById(R.id.btnBack);
        uploadImage = findViewById(R.id.uploadImage);
        imagePreview = findViewById(R.id.imagePreview);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(PostActivity.this, HomeActivity.class);
                startActivity(i);
            }

        });

        submitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmitClick();
            }

        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICTURE_CODE);
            }

        });


    }

    private void btnSubmitClick(){

        if(messageText.getText().toString().isEmpty() && newMessageImageUrl.isEmpty()){
            Toast.makeText(PostActivity.this,"You need type message or upload picture",Toast.LENGTH_SHORT).show();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        String msgId = "msg_"+sqlLiteUser.get("code")+"_"+String.valueOf(currentTime);
        DatabaseReference myRef = database.getReference(msgId);

        try {

            JSONObject obj = new JSONObject();
            obj.put("id",msgId);
            obj.put("msg",messageText.getText().toString());
            obj.put("thumb",newMessageImageUrl);
            obj.put("user_id",SqlLiteController.FirebaseUserId);
            obj.put("family_code",sqlLiteUser.get("code"));
            obj.put("time",currentTime);
            myRef.setValue(obj.toString());

            Toast.makeText(PostActivity.this,"Success",Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);

        }catch (JSONException e)
        {
            e.printStackTrace();
        }


    }

    private void uploadImage(){
        final String uid= SqlLiteController.FirebaseUserId;

        final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("users").child(uid);

        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){

                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            Toast.makeText(PostActivity.this,"upload success",Toast.LENGTH_SHORT).show();

                            newMessageImageUrl = task.getResult().toString();

                        }
                    });

                }else{


                    Toast.makeText(PostActivity.this,"upload failed",Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                //Setting image to ImageView
                imagePreview.setImageBitmap(bitmap);

                uploadImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}