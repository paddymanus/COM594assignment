package com.example.paddy.com594assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
    }

    public void signIn(View view) {
        try {
            String username = ((EditText) findViewById(R.id.editText_username)).getText().toString();
            String password = ((EditText) findViewById(R.id.editText_password)).getText().toString();
            if (username.equals("") || password.equals("")) {
                Toast.makeText(MainActivity.this, "Fill All Fields", Toast.LENGTH_LONG).show();
            }
            //Fetch the password from database for respective username
            if (!username.equals("")) {
                String storedPassword = loginDataBaseAdapter.getSingleEntry(username);
                //Check if the stored password matches the password entered by user
                if (password.equals(storedPassword)) {
                    Toast.makeText(MainActivity.this, "Congrats: Login Successful",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, mapWeather.class);
                    intent.putExtra("Name", username);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this,
                            "The given records are not available, please sign up",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("Error", "error login");
        }
    }

    public void signUp(View view)
    {
        Intent intent = new Intent(MainActivity.this, signUp.class);
        startActivity(intent);
    }
//
    protected void onDestroy() {
        super.onDestroy();
        loginDataBaseAdapter.close();
    }
}
