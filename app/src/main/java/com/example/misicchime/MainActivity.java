package com.example.misicchime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.misicchime.Model.Users;
import com.example.misicchime.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonLogin = (Button) findViewById(R.id.main_login_btn);
        Button buttonJoin = (Button) findViewById(R.id.main_join_btn);
        loadingBar = new ProgressDialog(this);
        buttonLogin.setOnClickListener(this);
        buttonJoin.setOnClickListener(this);


        Paper.init(this);

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if(UserPhoneKey !=""&& UserPasswordKey !=""){
            if(!TextUtils.isEmpty(UserPhoneKey)&&!TextUtils.isEmpty(UserPhoneKey)){
                ValidateUser(UserPhoneKey,UserPasswordKey);

                loadingBar.setTitle("Создание аккаунта");
                loadingBar.setMessage("Пожалуйста, полождите");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                
            }
        }
    }

    private void ValidateUser(final String number,final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(number).exists())
                {
                    Users usersData = snapshot.child("Users").child(number).getValue(Users.class);

                    if(usersData.getPhone().equals(number))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();

                            Intent homeIntent;
                            homeIntent = new Intent(MainActivity.this,  HomeActivity.class);
                            startActivity(homeIntent);

                        }
                        else {
                            loadingBar.dismiss();
                        }
                    }
                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Аккаунт с номером" + number + "не существует", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
              Intent intent;
            switch (v.getId()) {
            case R.id.main_login_btn:
                Intent loginBtN = new Intent(MainActivity.this,VhodActivity.class);
                startActivity(loginBtN);
                break;
            case R.id.main_join_btn:
                intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
}
