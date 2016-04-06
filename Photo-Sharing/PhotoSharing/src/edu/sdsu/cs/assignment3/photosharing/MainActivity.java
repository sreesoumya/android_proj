package edu.sdsu.cs.assignment3.photosharing;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import edu.sdsu.cs.assignment3.photosharing.ImageFragment.interface_fragment_two;
import edu.sdsu.cs.assignment3.photosharing.ListFragment.PassData;


/************************************App Description**************************************************
 * @author soumyasreekumar
 * Data:
 * 		UserData		: List of UserInfo
 * 		UserInfo		: All information related to one user.
 * 		SQliteDB		: Table of username, userid and JSONArray formatted String of photoname and photoid
 * 		External memory	: ".photodir" directory for storing one directory per user with user photos in it.
 * Activities:
 * 		MainActivity 	:Manages all data using UserData Class.
 * 						 Implements interface to two fragments.
 *                 		 Implements SQlite interface and functions
 *                 		 Starts intent to gallery for getting picture to upload and give it to upload activity
 * 				   		 Manages different layout for small and large screens.
 *		List Fragment 	:Gets the UserData from MainActivity.
 *						 Displays ListView of usernames
 *						 If connected gets new userlist and updates the Listview updates list in DB.
 *						 Sends the selected username back to MainActivity
 * 		ImageFragment	:Gets UserInfo of the selected user from MainActivity.
 *						 Displays Gridview of available Information.
 *						 If connected updates the Userphotolist from Internet stores in DB.
 *						 Downloads the required photos and stores it in the users directory
 *						 Photos only downloaded if not present in external storage.
 *						 On selecting an item in grid FullScreenImageDispActivty called.
 *		UploadActivity	:Menu Icon for uploading.
 *						 Selected image is displayed in this activity and gives suer the option to upload or end.
 *		FullscreenImageDispActivity
 *						:Uses the FullScreenImageAdapter Class to display full screen image.
 *						 Swipe gesture supported by extending PagerAdapter.	
 ************************************************************************************************************/

public class MainActivity extends Activity implements PassData ,interface_fragment_two {
	private static final String TAG = "MainActivity";
	private static final int PICK_IMAGE = 2;
	UserData muserData = new UserData();
	UserData userdata;
	UserInfo mselectedUserInfo;
	FragmentManager fm ;
	FragmentTransaction transaction;
	Configuration configuration;
	DataBase db = new DataBase(this);
	Util util = new Util(this);

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG,"MainActivity  onCreate()");
        
		//Get Data from DB
		db.open();	
		muserData = db.read_data();
		if(muserData.users.size() ==0){
			Log.d("MAinActivity","muserData.users.size() ==0");
		}
		Log.d("MAinActivity","muserData.users.size() == "+muserData.users.size());
		for(int k=0;k<muserData.users.size();k++){
			Log.d("MainActivity",""+muserData.users.get(k).userName+ " : "+muserData.users.get(k).userId);
		}
		db.close();

		Fragment list_fragment = new ListFragment();
		fm  = getFragmentManager();
		transaction = fm.beginTransaction();
		transaction.add(R.id.list_frag,list_fragment);
		transaction.commit();	 

	}	 
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		db.open();
		muserData = db.read_data();
		if(muserData.users.size() ==0){
			Log.d("onRestoreInstanceState","muserData.users.size() ==0");
		}
		Log.d("onRestoreInstanceState","muserData.users.size() == "+muserData.users.size());
		for(int k=0;k<muserData.users.size();k++){
			Log.d("onRestoreInstanceState",""+muserData.users.get(k).userName+ " : "+muserData.users.get(k).userId);
		}
		db.close();	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("MAinActivity"," onOptionsItemSelected()");
		switch (item.getItemId()){
		case R.id.upload :
			selectImageFromGallery();	
		    return true;		
		 default:
		       return super.onOptionsItemSelected(item);				
		}
	}


/**************Interface definitions for ListFragment***************************************************************/
	//implementing method for interface PassData(ListFragnent)
   @Override
	public void passFragment_data(UserData tempuserdata) {
	     	//if connected this could have a whole new list of userInfo.
	   userdata = tempuserdata;
	    new updateDB().execute();
	   		return;
	}
   
   public class updateDB extends AsyncTask<Void,Void,Void>{
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			boolean user_found = false;
	     	//Add new users into DB
	     	db.open();
			for(int i =0; i<userdata.users.size(); i++){
				Log.d("updateDB",userdata.users.get(i).userName +" "+userdata.users.get(i).userId );
				db.insert_user(userdata.users.get(i).userName, userdata.users.get(i).userId);	
			}
			
			//Remove deleted user from DB, delete all stored photos to save space
			for(int i=0;i<muserData.users.size();i++){
				user_found = false;
				for(int j=0; j<userdata.users.size(); j++){
					if(muserData.users.get(i).userName.equals(userdata.users.get(j).userName)){
						user_found = true;
					}
				}
				if(user_found == false){
					//muserData user not found in the new list.
					db.delete_user(muserData.users.get(i).userName);
					File f;
					if(util.isExternalStorageWritable()){
						for(int j=0; j<muserData.users.get(i).photopath.size(); j++ ){
							if((f = new File(muserData.users.get(i).photopath.get(i))) != null){
								if(f.exists())
									f.delete();
							}
						}
					}
				}
			}
			muserData = db.read_data();
			db.close();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void Result){
			Log.d("updateDB","onPostExecute() : DB Updated");
			super.onPostExecute(Result);
		}
   }
		
   
   
   //implementing method for interface PassData(ListFragnent)
	@Override
	public UserData getUserData() {
		// Pass mainActivity userdata to list fragment
		return muserData;
	}

   //implementing method for interface PassData(ListFragnent)
	@Override
	public void PassSelectedUser(String selectedUser) {
		for(int i=0;i<muserData.users.size();i++)
		{
			if(muserData.users.get(i).userName.equals(selectedUser)){
				mselectedUserInfo = muserData.users.get(i);
			}
		}
		Fragment image_fragment;
		if(getFragmentManager().findFragmentById(R.id.photolist_frag) == null)
		{		
			image_fragment = new ImageFragment();
		}else{
			image_fragment = getFragmentManager().findFragmentById(R.id.photolist_frag);
			transaction = fm.beginTransaction();
			transaction.remove(getFragmentManager().findFragmentById(R.id.photolist_frag));
			transaction.commit();
			image_fragment = new ImageFragment();
		}
		transaction = fm.beginTransaction();

		if(util.is_phone_form()){
			transaction.replace(R.id.list_frag,image_fragment);

		}else{
			transaction.add(R.id.photolist_frag,image_fragment);
		}
		transaction.commit();
	}

	
/**************Interface definitions for ImageFragment***************************************************************/
	
	//implementing method for interface interface_fragment_two(ImageFragment)
	@Override
	public UserInfo getUserInfo_mainActivity() {
		
		return mselectedUserInfo;
	}

	public void back_from_photolist(){
		
		Fragment list_fragment = new ListFragment();
		 fm  = getFragmentManager();
		 transaction = fm.beginTransaction();
		if(util.is_phone_form()){
			transaction.replace(R.id.list_frag,list_fragment);

		}else{
			
			transaction.remove(getFragmentManager().findFragmentById(R.id.photolist_frag));
		}
		transaction.commit();
		return;
	}
	
	public void  update_user_photo_in_db(UserInfo selecteduserinfo, String JSONphotolist){
		//update photolist in DB for selected user
		Log.d("update_user_photo_in_db","update_user_photo_in_db:: "+selecteduserinfo.userName);
		db.open();
		db.update_userphoto(selecteduserinfo,JSONphotolist);
		db.close();
	}
	
	//Opens dialog picker, so the user can select image from the gallery.
		public void selectImageFromGallery(){
			Log.d("selectImageFromGallery", "called selectImageFromGallery()");
			Intent intent = new Intent(); 
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);

			//directing user to specific gallery application 
			PackageManager pm = getPackageManager();
			List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
				
			for (int i = 0; i < resInfo.size(); i++) {
				ResolveInfo ri = resInfo.get(i);
			    String packageName = ri.activityInfo.packageName;
			    Log.d("TAG",ri.activityInfo.packageName);
			    if(packageName.contains(".gallery")) {
			    	intent.setPackage(packageName);
			     }else if (packageName.contains(".album")) {
			    	 intent.setPackage(packageName);
			     }
			 }
			 startActivityForResult(Intent.createChooser(intent,"Select the file you want to upload"),PICK_IMAGE);
         
		}
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
		   Log.d("TAG","calling onActivity Result");

			if (null != data) {
				  Uri selectedImage = data.getData();
				  Intent intent = new Intent(MainActivity.this,UploadActivity.class);
				  intent.putExtra("selectedImage_tag",selectedImage.toString());
				  startActivity(intent);
				  
			}
		}
		
		
}


	

	

	


