package edu.sdsu.cs.assignment3.photosharing;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class FullScreenImageDispActivity extends Activity{

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private ArrayList<String> mphotopaths = new ArrayList<String>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_slide);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0); 
        mphotopaths = i.getStringArrayListExtra("photopath_list");
        //need to get photopaths from MainActivity
        
        adapter = new FullScreenImageAdapter(FullScreenImageDispActivity.this,
                mphotopaths);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }
}