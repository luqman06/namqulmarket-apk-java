package com.example.marketnamqul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketnamqul.Admin.AdminCategoryActivity;
import com.example.marketnamqul.Model.Users;
import com.example.marketnamqul.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView AdmindLink, NotAdmintLink, ForgetPasswordLink;

    private String parentDbName = "Users";
    private CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_nomber_in);
        AdmindLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdmintLink = (TextView) findViewById(R.id.not_admin_panel_link);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);

        checkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);

            }
        });

        AdmindLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Admind");
                AdmindLink.setVisibility(View.INVISIBLE);
                NotAdmintLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdmintLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                AdmindLink.setVisibility(View.VISIBLE);
                NotAdmintLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });


    }

    private void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

           if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "please write your phone...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
           {
               loadingBar.setTitle("Loagin Account");
               loadingBar.setMessage("Please wait, while we are cheking chrcking creditial.");
               loadingBar.setCanceledOnTouchOutside(false);
               loadingBar.show();
               
               AllowAccesToAccount(phone,password);
           }

    }

    private void AllowAccesToAccount(final String phone, final String password)
    {
        if (checkBoxRememberMe.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                           if (parentDbName.equals("Admins"))
                           {
                               Toast.makeText(LoginActivity.this, "Welcome Admind, logged in successfully....", Toast.LENGTH_SHORT).show();


                               Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                               startActivity(intent);
                               loadingBar.dismiss();
                           }
                           else if (parentDbName.equals("Users"))
                           {
                               Toast.makeText(LoginActivity.this, "logged in successfully....", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();

                               Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                               Prevalent.currentOnlineUser = usersData;
                               startActivity(intent);
                           }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Number is incorrect", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }

                else
                {
                    Toast.makeText(LoginActivity.this, "Account with this "+phone+ "number not exits", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
