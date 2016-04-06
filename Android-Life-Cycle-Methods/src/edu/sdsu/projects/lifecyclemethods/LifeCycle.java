package edu.sdsu.projects.lifecyclemethods;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LifeCycle extends Activity {
	
	public  static final String TAG = "LifeCycle";
	private static final String KEY_CREATE  = "create";
	private static final String KEY_RESTART = "restart";
	private static final String KEY_START   = "start";
	private static final String KEY_RESUME  = "resume";
	private static final String KEY_PAUSE   = "pause";
	
	
	private Button   m_resetButton;
	private EditText m_onCreate;
	private EditText m_onRestart;
	private EditText m_onStart;
	private EditText m_onResume;
	private EditText m_onPause;
	int m_create_count  = 0;
	int m_restart_count = 0;
	int m_start_count   = 0;
	int m_resume_count  = 0;
	int m_pause_count   = 0;

	private void m_reset_view(){
		m_onCreate.setText(""+ 0);
		m_onPause.setText(""+ 0);
		m_onRestart.setText(""+ 0);
		m_onResume.setText(""+ 0);
		m_onStart.setText(""+ 0);	
		m_create_count  = 0;
		m_restart_count = 0;
		m_start_count   = 0;
		m_resume_count  = 0;
		m_pause_count   = 0;
	}
 
	private void m_update_view(){
		m_onCreate.setText(""+ m_create_count);
		m_onRestart.setText(""+ m_restart_count);
		m_onStart.setText(""+ m_start_count);
		m_onResume.setText(""+ m_resume_count);
		m_onPause.setText(""+ m_pause_count);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_life_cycle);
		Log.d(TAG,"onCreate()");
				
	    /*get handlers for all required View widgets */
		m_onCreate    = (EditText)findViewById(R.id.EditText_onCreate);
		m_onRestart   = (EditText)findViewById(R.id.EditText_onRestart);
		m_onStart     = (EditText)findViewById(R.id.EditText_onStart);
		m_onResume    = (EditText)findViewById(R.id.EditText_onResume);
		m_onPause     = (EditText)findViewById(R.id.EditText_onPause);
		m_resetButton = (Button)findViewById(R.id.reset_button);
				
		//handle the reset button and set OnclickListener
		m_resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG,"ResetButton.OnClick()");
				m_reset_view();
			}
		});
		
		/*
		 * Retrieve data necessary for onCreate() and onStart() here, since 
		 * onRestoreInstanceState() is called only after onStart(). Rest of the
		 * saved data for the application can be restored there.
		 */
		if (savedInstanceState != null) {
			Log.d(TAG,"savedInstanceState is not NULL");
			//default 0xFF to indicate if TAG not found when retrieving the value
			m_create_count = savedInstanceState.getInt(KEY_CREATE,0xFF);
			m_start_count =savedInstanceState.getInt(KEY_START,0xFF);
			
		}
		else{
			Log.d(TAG,"savedInstanceState is NULL");
		}
		
		// Main activities for onCreate()
		m_create_count++;	
		m_update_view();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.d(TAG,"onStart()");
		m_start_count++;
		m_update_view();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG,"onResume()");
		m_resume_count ++;
		m_update_view();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.d(TAG,"onPause()");
		m_pause_count++;
		m_update_view();
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		Log.d(TAG,"onRestart()");
		m_restart_count++;
		m_update_view();
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState){
		Log.d(TAG,"onSaveInstanceState()");	
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_CREATE,m_create_count);
		outState.putInt(KEY_RESTART,m_restart_count);
		outState.putInt(KEY_START,m_start_count);
		outState.putInt(KEY_RESUME,m_resume_count);
		outState.putInt(KEY_PAUSE,m_pause_count);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(TAG,"onRestoreInstanceState()");
		if (savedInstanceState != null) {
			//Retrieve the rest of saved values
			//default 0xFF to indicate if TAG not found when retrieving the value
			m_resume_count=savedInstanceState.getInt(KEY_RESUME,0xFF);
			m_pause_count=savedInstanceState.getInt(KEY_PAUSE,0xFF);
			m_restart_count =savedInstanceState.getInt(KEY_RESTART,0XFF);
		}
		m_update_view();
	
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.life_cycle, menu);
		return true;
	}

}
