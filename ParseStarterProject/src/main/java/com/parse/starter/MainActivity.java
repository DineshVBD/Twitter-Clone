package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

  EditText username;
  EditText password;

  public void redirectuser()
  {
    if(ParseUser.getCurrentUser()!=null)
    {
      Intent intent=new Intent(this,UserActivity.class);
      startActivity(intent);
    }
  }

  public void signuporlogin(View view)
  {
    ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        if(e==null)
        {
          Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
          redirectuser();
        }
        else {
          ParseUser parseUser=new ParseUser();
          parseUser.setUsername(username.getText().toString());
          parseUser.setPassword(password.getText().toString());
          parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
              if(e==null)
              {
                Toast.makeText(getApplicationContext(),"Signup Successful",Toast.LENGTH_SHORT).show();
                redirectuser();
              }
              else
              {
                Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")),Toast.LENGTH_SHORT).show();
              }
            }
          });
        }
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("TWITTER:LOGIN");
    username=(EditText) findViewById(R.id.username);
    password=(EditText) findViewById(R.id.password);

    redirectuser();
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}