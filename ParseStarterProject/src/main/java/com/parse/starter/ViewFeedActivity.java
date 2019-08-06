package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewFeedActivity extends AppCompatActivity {

    ListView feedlistview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feed);
        feedlistview=(ListView) findViewById(R.id.feedlistview);
        setTitle("Your Feed");
        final List<Map<String,String>> tweetdata=new ArrayList<Map<String, String>>();

        ParseQuery<ParseObject> query=ParseQuery.getQuery("Tweet");
        query.whereContainedIn("username",ParseUser.getCurrentUser().getList("isfollowing"));
        query.orderByDescending("createdAt");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null)
                {
                    if(objects.size()>0)
                    {
                        for(ParseObject object:objects)
                        {
                            Map<String,String> tweetinfo =new HashMap<String, String>();
                            tweetinfo.put("content",object.getString("tweet"));
                            tweetinfo.put("username",object.getString("username"));
                            tweetdata.add(tweetinfo);
                        }
                        SimpleAdapter simpleAdapter=new SimpleAdapter(ViewFeedActivity.this,tweetdata,android.R.layout.simple_list_item_2,new String[] {"content","username"},new int[]{android.R.id.text1,android.R.id.text2});
                        feedlistview.setAdapter(simpleAdapter);
                    }
                }
            }
        });

    }
}
