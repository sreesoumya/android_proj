package edu.sdsu.cs.assignment2.uisampler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class KeyboardActivity extends ActionBarActivity implements OnClickListener {
	
	public static String KEYBOARD_ACTIVITY_KEY ="KEYBOARD_ACTIVITY_DATA";
	public  static final String TAG = "KeyboardActivity";
	EditText m_top_text;
	EditText m_bottom_text;
	EditText m_middle_text;
	Button   hideButton;
	Button   backButton;
	String m_received_string;
	
    @Override
     protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.keyboard_activity);
    	Log.d(TAG,TAG+" onCreate()");
	   
    	ActionBar actionBar = getSupportActionBar();
    	actionBar.setDisplayHomeAsUpEnabled(true);
	   
    	m_top_text    = (EditText)findViewById(R.id.top_edit);
    	m_middle_text = (EditText)findViewById(R.id.middle_edit);
    	m_bottom_text = (EditText)findViewById(R.id.bottom_edit); 
	   
    	m_top_text.setOnClickListener(KeyboardActivity.this);
    	m_middle_text.setOnClickListener(KeyboardActivity.this);
	        
    	/*display the string received from Main Activity*/
    	Intent i = getIntent();
    	m_received_string = i.getStringExtra("MAIN_ACTIVITY_DATA");
    	m_top_text.setText(m_received_string);
	   
    	/*handle the hide button and set OnclickListener*/
    	hideButton =(Button)findViewById(R.id.hide_button);
    	hideButton.setOnClickListener(KeyboardActivity.this);
    	/*handle the back button by setting OnclickListener*/
    	backButton = (Button)findViewById(R.id.back_button);
    	backButton.setOnClickListener(KeyboardActivity.this);
			
      }
    
    /*set OnclickListener for the all view*/
	@Override
	public void onClick(View v) {
		if(v == this.findViewById(R.id.back_button))
		{
			Log.d(TAG,TAG+" onClick()->back_button");
			take_me_back();
		}
		else if(v == this.findViewById(R.id.hide_button))
		{
			Log.d(TAG,TAG+" onClick()->hide_button");
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
		else if((v == this.findViewById(R.id.top_edit)) || (v == this.findViewById(R.id.middle_edit)))
		{
			Log.d(TAG,TAG+" onClick()->edit_text");
			showSoftKeyboard();
		}
	}
	
	/*Private method for handling keyboard display */
	private void showSoftKeyboard() {
		if (m_bottom_text.requestFocus()) {
				Log.d(TAG,TAG+" showSoftKeyboard()->requestFocus()");
				
				InputMethodManager imm = (InputMethodManager)
				getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(m_bottom_text, InputMethodManager.SHOW_IMPLICIT);
		}
	}
	
	/*Handling UP Icon*/
	@Override
	public Intent getSupportParentActivityIntent() {
		Log.d(TAG,TAG+" getSupportParentActivityIntent()--> to MainActivity");
		
		take_me_back();
		return null;	
	}
	
	/*Common method to go back to MainActivity*/
	private void take_me_back(){ 
		Log.d(TAG,TAG+" take_me_back()");
		
		Intent i = new Intent();
		this.setResult(RESULT_OK,i);
		this.finish();		
	    return;
	}
	
	/*Menu and Action Bar handling */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG,TAG+" onCreateOptionsMenu()");
		
		/* Inflate the menu; this adds items to the action bar if it is present.*/
		getMenuInflater().inflate(R.menu.other_views, menu); 
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	    return super.onCreateOptionsMenu(menu);
	}
	
	/*ActionBar selection handling */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,TAG+" onOptionsItemSelected()");
		
		switch (item.getItemId()){
		case R.id.other_menu_back:
		   take_me_back();
		    return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
}
