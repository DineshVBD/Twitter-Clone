package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    ArrayList<String> users;
    ListView userlistview;
    ArrayAdapter arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=new MenuInflater(this);
        menuInflater.inflate(R.menu.tweetmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.tweet)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Send a Tweet");
            final EditText tweetedittext=new EditText(this);
            builder.setView(tweetedittext)
                    .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ParseObject tweet=new ParseObject("Tweet");
                            tweet.put("username",ParseUser.getCurrentUser().getUsername());
                            tweet.put("tweet",tweetedittext.getText().toString());
                            tweet.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null)
                                    {
                                        Toast.makeText(getApplicationContext(),"Tweet Sent",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"Failed to send",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        }
        else if(item.getItemId()==R.id.logout)
        {
            ParseUser.logOut();
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        else
        {
            Intent intent=new Intent(getApplicationContext(),ViewFeedActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("User List");
        users=new ArrayList<String>();

        if(ParseUser.getCurrentUser().get("isfollowing")==null)
        {
            ArrayList<String> emptylist=new ArrayList<>();
            ParseUser.getCurrentUser().put("isfollowing",emptylist);
            ParseUser.getCurrentUser().saveInBackground();
        }

        userlistview=(ListView) findViewById(R.id.userlistview);
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_checked,users);
        userlistview.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        userlistview.setAdapter(arrayAdapter);

        userlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView=(CheckedTextView) view;
                if(checkedTextView.isChecked())
                {
                    ParseUser.getCurrentUser().getList("isfollowing").add(users.get(i));
                    ParseUser.getCurrentUser().saveInBackground();
                }
                else
                {
                    ParseUser.getCurrentUser().getList("isfollowing").remove(users.get(i));
                    ParseUser.getCurrentUser().saveInBackground();
                }
            }
        });

        users.clear();

        ParseQuery<ParseUser> query=ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null)
                {
                    if(objects.size()>0)
                    {
                        for(ParseUser object:objects) {
                            users.add(object.getUsername());
                        }
                        arrayAdapter.notifyDataSetChanged();
                        for(String username:users)
                        {
                            if(ParseUser.getCurrentUser().getList("isfollowing").contains(username))
                            {
                                userlistview.setItemChecked(users.indexOf(username),true);
                            }
                        }
                    }
                }
            }
        });

    }
}
