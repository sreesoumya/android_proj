package edu.sdsu.cs.assignment2.uisampler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class ListFragment extends Fragment {	
	protected static final int RESULT_OK   = -1;
	protected static final String TAG      = "ListFragment";
	public static String LIST_ACTIVITY_KEY = "LIST_ACTIVITY_DATA";
	private View m_view;
	public String m_gettext;
	private Button mBack;
	private ListView mList;
	PassText pass_list_selection;

	/*Align the Fragment interface to calling Activity */
	@Override
	public void onAttach(Activity a){
		super.onAttach(a);
		pass_list_selection = (PassText)a;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.d(TAG,TAG+" onCreateView()");
		
		m_view = inflater.inflate(R.layout.list_fragment_layout,container,false);
		create_list_view(container);
		
		/*handling back button*/
		mBack = (Button)m_view.findViewById(R.id.backButton);
		mBack.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Log.d(TAG,TAG+" backButton-onClick()");
				take_me_back();
				return;
			}
			private void take_me_back(){
				Log.d(TAG,TAG+" take_me_back()");
				Intent intent_list = new Intent();
				intent_list.putExtra("LIST_ACTIVITY_DATA",m_gettext);
				getActivity().setResult(RESULT_OK,intent_list);
				getActivity().finish();	
				return;
			}
		});	
		return m_view;	
	}
	
	/*Interface for passing data back to Calling activity */
	public interface PassText{
		public void getfragment_text(String text);
	}
	
	/*Method to create a list view */
	private void create_list_view(ViewGroup container){
		Log.d(TAG,TAG+" create_list_view()");

		mList = (ListView)m_view.findViewById(R.id.list_View);
		/*Create an ArrayAdapter using the string array and a default list layout*/
		ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(container.getContext(),
				R.array.android_versions,android.R.layout.simple_list_item_1);
		mList.setAdapter(adapter);
		
		
		/*set handler for listView */
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterview, View v, int position,
					long id) {
				TextView m_text = (TextView) v;
				m_gettext = (String)m_text.getText();
				//TODO::Commenting setting the background as it doesn't clear when multiple options are selected.
			    //v.setBackgroundResource(R.drawable.abc_ab_stacked_solid_light_holo);
				
				pass_list_selection.getfragment_text(m_gettext);
			}
		});
	}
}
