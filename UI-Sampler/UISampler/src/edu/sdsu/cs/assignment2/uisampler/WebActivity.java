package edu.sdsu.cs.assignment2.uisampler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class WebActivity extends ActionBarActivity implements View.OnClickListener  {
	private static final String TAG ="WebActivity";
	private WebView mBrowser;
	private EditText mUrl;
	private Button mGo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_activity);
		Log.d(TAG,TAG+" onCreate()");
		
		/*get all the handlers of the view*/
		mBrowser = (WebView)findViewById(R.id.webView);
		mBrowser.setWebViewClient(new WebViewClient());
		mUrl=(EditText)findViewById(R.id.type_url);
		mGo =(Button)findViewById(R.id.go_button);
		
		/*default loading google page*/
		mBrowser.loadUrl("https://www.google.com");
		mUrl.setOnClickListener(WebActivity.this);
		mUrl.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if(actionId ==EditorInfo.IME_ACTION_GO){
					Log.i(TAG, "Got the message");
					get_address_and_load_url();
					handled = true;
				}
				return handled;
			}
		});
	
		mGo.setOnClickListener(WebActivity.this);
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG,TAG+" onClick()");
		if (v.getId()==R.id.go_button){
			get_address_and_load_url();
		}		
	}
	
	/*Private method to handle Url */
	private void get_address_and_load_url()
	{
		Log.d(TAG,TAG+" get_address_and_load_url()");
		String url_data = mUrl.getText().toString();
		if(url_data.contentEquals(""))
		{
	    	Toast.makeText(WebActivity.this,R.string.warning_toast,Toast.LENGTH_LONG).show();
	    	return;
		}
		
		/*other preliminary checks needed*/
		if(!url_data.contains("www"))
		{
			url_data = "http://www."+url_data;
		}
		else if(!url_data.contains("http"))
		{
			url_data = "http://"+url_data;
		}
		
				
		/*on success, hide the keyboard to allow full view of screen and clear the edit text field.*/
		mBrowser.loadUrl(url_data);
		mUrl.setText("");
		InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/* Menu Navigation Up handler */
	@Override
	public Intent getSupportParentActivityIntent() {
		Log.d(TAG,TAG+" getSupportParentActivityIntent()--> to MainActivity");
		
		take_me_back();
		return null;
		
	}
	
	/*Common function to return to MainActivity */
	private void take_me_back(){
		Log.d(TAG,TAG+" take_me_back()");
		
		setResult(RESULT_OK,new Intent());
		finish();
	    return;
	}
	
	/*Menu and Action Bar for this activity */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG,TAG+" onCreateOptionsMenu()");
		
		/* Inflate the menu; this adds items to the action bar if it is present.*/
		getMenuInflater().inflate(R.menu.other_views, menu); 
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	    return super.onCreateOptionsMenu(menu);
	}
	
	/*Menu item selection handler*/
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
