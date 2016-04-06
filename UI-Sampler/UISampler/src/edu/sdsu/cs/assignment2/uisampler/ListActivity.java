package edu.sdsu.cs.assignment2.uisampler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import edu.sdsu.cs.assignment2.uisampler.ListFragment.PassText;

public class ListActivity extends ActionBarActivity implements PassText{

	private static final String TAG = "ListActivity";
	public static String LIST_ACTIVITY_DATA ="LIST_ACTIVITY_DATA";
	public String m_gettext;
	
	/*Hander for UP in App Icon */
	@Override
	public Intent getSupportParentActivityIntent() {
		Log.d(TAG,TAG+" getSupportParentActivityIntent()--> to MainActivity");
		
		take_me_back();
		return null;
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);
		Log.d(TAG,TAG+" onCreate()");
		
		/*initializing fragments associated with list activity*/
		Fragment list_fragment = new ListFragment();
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.parent_list_activity_layout,list_fragment,"tag_list_fragment");
		transaction.commit();			
	}
	
	/*Implement Interface to get data from Fragment */
	@Override
	public void getfragment_text(String text){
		Log.d(TAG,TAG+" getfragment_text()");
		
		m_gettext = text;
		Toast.makeText(this, "Selected: "+m_gettext,Toast.LENGTH_LONG).show();
	}
	
	/*Menu and Action Bar*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG,TAG+" onCreateOptionsMenu()");
		
		/* Inflate the menu; this adds items to the action bar if it is present.*/
		getMenuInflater().inflate(R.menu.other_views, menu); 
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	    return super.onCreateOptionsMenu(menu);
	}
	
	/*Handler for the Menu Items*/
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
	
	/*Common function to go back to MainActivity*/
	private void take_me_back(){
		Log.d(TAG,TAG+" take_me_back()");
		
		Intent intent_list = new Intent();
		intent_list.putExtra(LIST_ACTIVITY_DATA,m_gettext);
		setResult(RESULT_OK,intent_list);
		finish();	
		return;
	}
}
