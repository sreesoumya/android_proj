package edu.sdsu.cs.assignment3.photosharing;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListFragment extends Fragment {
	private View m_view;
	private ListView mList;
    ArrayList<String> userArrayList = new ArrayList<String>();
	private static final String url ="http://bismarck.sdsu.edu/photoserver/userList";
	private static final String TAG = "ListFragment";
	private String m_gettext;
	Context context;
	ProgressDialog dialog;
	ArrayAdapter<String> adapter;
	JSONArray items;
	PassData pass_userList;
	Util util;
	private UserData userData;
	
	
	/*Align the Fragment interface to calling Activity */
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		//assigning the instance of Interface to Activity
		pass_userList = (PassData) activity;
		
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		context = getActivity();
		util = new Util(context);
		userData = pass_userList.getUserData();
		m_view = inflater.inflate(R.layout.fragment_list,container,false);
		fillUserList();
		create_list_view(container);
		return m_view;
       
	}
	
	private void fillUserList() 
	{
		// TODO Auto-generated method stub
		Log.d("TAG", "FillUserList....");
		for(int i=0;i<userData.users.size();i++){
			Log.d("ListFragment","ListFragment :: "+userData.users.get(i).userName+" : "+userData.users.get(i).userId);
			userArrayList.add(userData.users.get(i).userName);
		}
		
		if(util.is_connected()){
			UsersList getUsers = new UsersList();
			getUsers.execute();
		}
	}

	/*Method to create a new task to using Async*/
	private class UsersList extends AsyncTask<Void, Void, Void>{


		@Override
		protected Void doInBackground(Void... params) {
			Log.d("TAG", "Connecting....");
			HttpClient client = new DefaultHttpClient() ;
			HttpGet getRequest = new HttpGet(url);
			try 
			{
				HttpResponse response = client.execute(getRequest);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				
				if (statusCode != 200)
				{
					Log.i(TAG,"MyList" );
					dialog.dismiss();
				
					return null;
				}
				
				Log.d("TAG", "Connect"+statusLine.getStatusCode());
				InputStream jsonStream = response.getEntity().getContent();
				//Convert response to string
				BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
				StringBuilder builder = new StringBuilder();
				String line;
				while((line = reader.readLine())!= null)
				{
					builder.append(line);
				}
				String jsonData = builder.toString();
				items = new JSONArray(jsonData);
				userArrayList.clear();
				userData.users.clear();
				for(int i =0 ;i<items.length(); i++)
				{				
					String username = items.getJSONObject(i).getString("name");
					String userid = items.getJSONObject(i).getString("id");
					//JSONObject list = items.getJSONObject(i);
					userArrayList.add(username);
					
					UserInfo userInfo = new UserInfo();
				    userInfo.userName = username;
					userInfo.userId = userid;
					userData.users.add(userInfo);
					
				}	
				//All users information collected.. send back to main activity for update in DB
				pass_userList.passFragment_data(userData);	
			} 
			catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				return null;
		}
		

		@Override
		protected void onPreExecute() 
		{
			Log.d("TAG", "PreExecute....");
			dialog  = new ProgressDialog(context);
			dialog.setTitle("Loading Lists");
			dialog.show();
			super.onPreExecute();
		}
		
		protected void onPostExecute(Void result) 
		{
			dialog.dismiss();
			Log.d("TAG", "PostExecute....");
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
			
		}
		
	}

/*Method to create a list view */
  private void create_list_view(ViewGroup container)
  {
		Log.d("TAG", "create_list_view....");
	mList = (ListView)m_view.findViewById(R.id.user_listView);
	/*Create an ArrayAdapter using the string array and a default list layout*/
	adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,userArrayList);
    mList.setAdapter(adapter);
	Log.d("TAG", "mlist.setAdapter....");
	
	/*set handler for listView */
	mList.setOnItemClickListener(new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			TextView m_text = (TextView) v;
			m_gettext = (String)m_text.getText();
			pass_userList.PassSelectedUser(m_gettext);

			Log.d("selected user:","" +m_gettext);
			
			
			return;
			
		}
		
	});
}
	
/*Creating an Interface for passing data back to host activity*/
public interface PassData
{
	 //pass userdata instead of string 
	// this userdata will be updated with photo id,photoname and pahotopath in the next fragment
	// keep an object in main activity and use interface to update the object in MainActivity 
	public void passFragment_data(UserData userdata );
	public UserData getUserData();
	public void PassSelectedUser(String selectedUser);
}

  



}
	