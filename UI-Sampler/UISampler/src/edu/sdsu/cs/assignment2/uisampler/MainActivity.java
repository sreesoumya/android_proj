package edu.sdsu.cs.assignment2.uisampler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
	private static final String TAG = "MainActivity";
	private static final int keyboard_activity_ret_code = 1;
	private static final int web_activity_ret_code 		= 2;
	private static final int list_activity_ret_code 	= 3;
	public static String MAIN_ACTIVITY_DATA 			= "MAIN_ACTIVITY_DATA";
	public static String m_string ; 
	public static String m_str_text;
	private Spinner spinner;
	private Button  m_Button;
	private EditText m_EditText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG,TAG+" onCreate()");
		
		create_spinner();
		m_EditText  = (EditText)findViewById(R.id.edit_text);
		m_EditText.setHint(R.string.hint);
	    m_Button = (Button)findViewById(R.id.enter_button);
	    m_Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			Log.d(TAG,TAG+" onClick()");
			gotoactivity(m_str_text);			
			}
		});
	}
	
	/*Handler of getting spinner selection */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		Log.d(TAG,TAG+" onItemSelected()");
    	m_str_text = parent.getItemAtPosition(position).toString();	
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG,TAG+" onNothingSelected()");	
	}

	/*Handling return of started activities*/	
   @Override
   	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    Log.d(TAG,TAG+" onActivityResult()");
	    
		m_EditText.setText("");
   	 	m_EditText.setHint(R.string.hint);
	    if (requestCode == keyboard_activity_ret_code) {
	    	if(resultCode == RESULT_OK){
	    		Log.d(TAG,TAG+" onActivityResult()->RESULT_OK");
	         }
	         else
	         {
	        	 Log.d(TAG,TAG+" onActivityResult()->RESULT_NOK");
	        	 Toast.makeText(this, "Keyboard activity did not return properly.",Toast.LENGTH_LONG).show();
	         }
	    }
	    if (requestCode == web_activity_ret_code) {
	         if(resultCode == RESULT_OK){
	        	 Log.d(TAG,TAG+" onActivityResult()->RESULT_OK");
	          }
	         else
	         {
	        	 Log.d(TAG,TAG+" onActivityResult()->RESULT_NOK");
	        	 Toast.makeText(this, "Web activity did not return properly.",Toast.LENGTH_LONG).show();
	         }
	    }
	    if (requestCode == list_activity_ret_code) {
	         if(resultCode == RESULT_OK){
	        	 Log.d(TAG,TAG+" onActivityResult()->RESULT_OK");
	        	 m_string =data.getStringExtra("LIST_ACTIVITY_DATA");        
	        	 m_EditText.setText(m_string);
	          }
	         else
	         {
	        	 Log.d(TAG,TAG+" onActivityResult()->RESULT_NOK");
	        	 Toast.makeText(this, "List activity did not return properly.",Toast.LENGTH_LONG).show();
	         }
	    }    
   }
	
   	/*Creating the Menu and Action Bar */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG,TAG+" onCreateOptionsMenu()");
		
		/* Inflate the menu; this adds items to the action bar if it is present.*/
		getMenuInflater().inflate(R.menu.main, menu);  
	    return super.onCreateOptionsMenu(menu);
	}
	
    /*Adding the menu items to the action bar*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,TAG+" onOptionsItemSelected()");
		
		switch (item.getItemId()){
		case R.id.menu_activity_keyboard:
			gotoactivity("Keyboard Activity");
		    return true;
		case R.id.menu_activity_web:
			gotoactivity("Web Activity");
			return true;
		case R.id.menu_activity_list:
			gotoactivity("List Activity");
			return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
    /*Method to start each activity*/
	private void gotoactivity(String activity_name){
		Log.d(TAG,TAG+" gotoactivity()->"+activity_name);
		
		if (activity_name.equals("Keyboard Activity")) {
			m_string = m_EditText.getText().toString();
			Intent i = new Intent(MainActivity.this,KeyboardActivity.class);
			i.putExtra(MAIN_ACTIVITY_DATA,m_string);
			startActivityForResult(i,keyboard_activity_ret_code);
		}
		else if (activity_name.equals("Web Activity")){	
			startActivityForResult(new Intent(MainActivity.this,WebActivity.class),web_activity_ret_code);
		}
		else if (activity_name.equals("List Activity")) {
			startActivityForResult(new Intent(MainActivity.this,ListActivity.class),list_activity_ret_code);
		}
	}

	/*Create spinner for MainActivity*/
	private void create_spinner(){
		Log.d(TAG,TAG+" create_spinner()");
		
		/* Create an ArrayAdapter using the string array and a default spinner layout*/
	    spinner     = (Spinner)findViewById(R.id.activity_spinner);
		/* Specify the layout to use when the list of choices appears*/
		ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.array_activity,android.R.layout.simple_spinner_item);
		/* Apply the adapter to the spinner*/
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		/*handler for spinner*/
		spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(this);
	    
	    return;
	}
}
