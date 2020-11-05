package com.example.marketnamqul;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketnamqul.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity
{
private ImageView profileImageView;
private EditText fullNameEditText, userPhoneEditText, addressEditText;
private TextView profileChangeTextBtn, closeTextBtn, saveTextBtn;
private Button securityQuestionsBtn;

private Uri imageUri;
private String myUri = "";
private StorageTask uploadTask;
private StorageReference storageProfilePrictureRef;
private String checker = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile picture");

        profileImageView = (ImageView) findViewById(R.id.setting_profile_imaage);
        fullNameEditText = (EditText) findViewById(R.id.setting_full_name);
        userPhoneEditText = (EditText) findViewById(R.id.setting_phone_number);
        addressEditText = (EditText) findViewById(R.id.setting_address);
        profileChangeTextBtn = (TextView) findViewById(R.id.profile_imaage_change_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_aetting_btn);
        saveTextBtn = (TextView) findViewById(R.id.update_account_setting_btn);
        securityQuestionsBtn = (Button) findViewById(R.id.security_question_btn);

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (checker.equals("clicked"))
                {
                    userInfosaved();
                }
                else 
                {
                    upfateOnlyUserInfo();
                }
            }
        });

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        securityQuestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingActivity.this);
            }
        });
    }

    private void upfateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        Toast.makeText(SettingActivity.this, "Profile info update succesfull", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Try Again", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingActivity.this, SettingActivity.class));
            finish();
        }
    }






    private void userInfosaved()   
    {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is Address", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your accunt information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri !=null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(Prevalent.currentOnlineUser.getPhone()+ ".jpg");


            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUri = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", fullNameEditText.getText().toString());
                        userMap.put("address", addressEditText.getText().toString());
                        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
                        userMap.put("image", myUri);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                        Toast.makeText(SettingActivity.this, "Profile info update succesfull", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else
        {
            Toast.makeText(this, "image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final ImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
