package com.example.misicchime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    public Button login_btn;
    private EditText number_user,name_user,password_user;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login_btn = findViewById(R.id.login_btn);
        number_user = (EditText) findViewById(R.id.number_user);
        name_user = (EditText) findViewById(R.id.name_user);
        password_user = (EditText) findViewById(R.id.password_user);
        loadingBar = new ProgressDialog(this);

        login_btn.setOnClickListener(v -> CreateAccount());
    }

    private void CreateAccount() {
        String username = name_user.getText().toString();
        String number = number_user.getText().toString();
        String password = password_user.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(number))
        {
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Пожалуйста, полождите");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhone(username,number,password);
        }
    }

    private void ValidatePhone(String username, String number, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(number).exists())) {

                    {
                        HashMap<String,Object> userDataMap = new HashMap<>();
                        userDataMap.put("phone",number);
                        userDataMap.put("name",username);
                        userDataMap.put("password",password);

                        RootRef.child("Users").child(number).updateChildren(userDataMap)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){

                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                                        Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(loginIntent);
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Ошибка", Toast.LENGTH_SHORT).show();
                                    }

                                });
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Номер " + number + " Уже зарегистрирован", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
                    startActivity(loginIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}