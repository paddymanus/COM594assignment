package com.example.paddy.com594assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class signUp extends AppCompatActivity {
    LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get instance of database adapter
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
    }

    public void signUp_OK(View view) {
        String userName = ((EditText) findViewById(R.id.editText_ca_uname)).getText().toString();
        String password = ((EditText) findViewById(R.id.editText_ca_pword)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.editText_ca_cpword)).getText().toString();
        String email = ((EditText) findViewById(R.id.editText_ca_email)).getText().toString();

        if (userName.equals("") || password.equals("") || confirmPassword.equals("") || email.equals("")) {
            Toast.makeText(signUp.this, "Fill All Fields", Toast.LENGTH_LONG).show();
            return;
        }
        //Check if both password matches
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
            return;
        } else {
            //Save the data in database
            loginDataBaseAdapter.insertEntry(userName, password);
            Toast.makeText(getApplicationContext(),
                    "Your account has been successfully created. You can sign in now", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(signUp.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void alreadySignedUp(View view)
    {
        Intent intent = new Intent(signUp.this, MainActivity.class);
        startActivity(intent);
    }

    protected void onDestroy() {
        super.onDestroy();
        loginDataBaseAdapter.close();
    }
}
