package com.example.misicchime;
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
import com.example.misicchime.Model.Users;
import com.example.misicchime.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.paperdb.Paper;
public class VhodActivity extends AppCompatActivity   {

    public Button loginBtn;
    public EditText number_user, password_user;
    public ProgressDialog loadingBar;
    public TextView admin_panel_link1, not_admin_panel_link1;

    public String parentDbName = "Users";
    public CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vhod);

        loginBtn = (Button) findViewById(R.id.login_btn);
        number_user = (EditText) findViewById(R.id.number_user);
        password_user = (EditText) findViewById(R.id.password_user);
        loadingBar = new ProgressDialog(this);
        //checkBoxRememberMe = (CheckBox) findViewById(R.id.checkbox);
        Paper.init(this);

        admin_panel_link1 = (TextView)findViewById(R.id.admin_panel_link);
        not_admin_panel_link1 = (TextView)findViewById(R.id.not_admin_panel_link);

        loginBtn.setOnClickListener(v -> loginUser());

        admin_panel_link1.setOnClickListener(v -> {
            admin_panel_link1.setVisibility(View.INVISIBLE);
            not_admin_panel_link1.setVisibility(View.VISIBLE);
            loginBtn.setText("Вход для админа");
            parentDbName ="Admins";
        });

        not_admin_panel_link1.setOnClickListener(v -> {
            admin_panel_link1.setVisibility(View.VISIBLE);
            not_admin_panel_link1.setVisibility(View.INVISIBLE);
            loginBtn.setText("Войти");
            parentDbName ="Users";
        });
    }

    private void loginUser() {
        String phone = number_user.getText().toString();
        String password = password_user.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone, password);
        }
    }

    private void ValidateUser(final String phone, final String password) {

        if(checkbox.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone))
                        if (usersData.getPassword().equals(password)) {
                            if (parentDbName.equals("Users")) {
                                loadingBar.dismiss();
                                Toast.makeText(VhodActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();

                                Intent homeIntent = new Intent(VhodActivity.this, HomeActivity.class);
                                startActivity(homeIntent);
                            } else if (parentDbName.equals("Admins")) {
                                loadingBar.dismiss();
                                Toast.makeText(VhodActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();

                                Intent homeIntent = new Intent(VhodActivity.this, AdminCategoryActivity.class);
                                startActivity(homeIntent);
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(VhodActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(VhodActivity.this, "Аккаунт с номером " + phone + "не существует", Toast.LENGTH_SHORT).show();

                    Intent registerIntent = new Intent(VhodActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}




