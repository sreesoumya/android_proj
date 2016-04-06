package edu.sdsu.cs.assignment3.photosharing;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;


public class ImageFragment extends Fragment {
    protected static final String TAG = null;	
	private View v;
	private GridView mGrid;
	MyGridViewAdapter mphotosgrid_adapter;
	Context mcontext;
	private interface_fragment_two mInterface_fragment_two;
	private UserInfo muserInfo;
	private UserInfo mtempuserInfo;
	private String murl_base = new String("http://bismarck.sdsu.edu/photoserver/userPhotos/");
	private String mphotourl_base = new String("http://bismarck.sdsu.edu/photoserver/photo/");
	private RequestQueue q;
	private ImageCache imageCache;
	private WeakReference<GridView> gridViewReference;
	private WeakReference<ImageView> imageViewReference;
	TextView username;
	Util util;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mInterface_fragment_two = (interface_fragment_two)activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mcontext = getActivity();
		util = new Util(mcontext);
		v= inflater.inflate(R.layout.fragment_image,container,false);	
		muserInfo = mInterface_fragment_two.getUserInfo_mainActivity();
		username = (TextView)v.findViewById(R.id.photolist_username);
		username.setText(muserInfo.userName);
		TextView online_status = (TextView) v.findViewById(R.id.online_status);

		Log.d(TAG, "ImageFragment->onCreateView()"+muserInfo.userName);
		fill_stored_photos();
		show_thumbnail_gridview();
		
		if(util.is_connected())
		{
			online_status.setText("Status: Online");
			get_photolist_ids();
		}else{
			online_status.setText("Status: Offline");
			if(muserInfo.photopath.size() == 0)
				username.setText(""+muserInfo.userName+" :no photos to display");
		}
	
	
		//handling close button
		Button back = (Button)v.findViewById(R.id.back);		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mInterface_fragment_two.back_from_photolist();
				
			}
		});
						
		 return v;		
	}
	
	private void show_thumbnail_gridview(){
		mGrid =(GridView)v.findViewById(R.id.frag_user_photos_gridview);	
		gridViewReference = new WeakReference<GridView>(mGrid);
		mphotosgrid_adapter = new MyGridViewAdapter();
		if(mGrid!=null)
			mGrid.setAdapter(mphotosgrid_adapter);
        
        
		mGrid.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
                          Intent i = new Intent(mcontext, FullScreenImageDispActivity.class);
                          i.putExtra("position", position);
                          i.putStringArrayListExtra("photopath_list",muserInfo.photopath);
                          mcontext.startActivity(i);
                        
                }
        });
		
		
		
	}

	
	public class MyGridViewAdapter extends BaseAdapter {

        public MyGridViewAdapter() {
        }

        public int getCount() {
            return muserInfo.photoname.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }
        
        
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            TextView textView;
            View custom_grid_view;
            Bitmap bp;
            Log.d("Grid","Grid->GetView()");
            if (convertView == null) {  
                    custom_grid_view = getActivity().getLayoutInflater().inflate(R.layout.custom_grid_item,parent,false);
                    imageView = (ImageView) custom_grid_view.findViewById(R.id.grid_image);
                    textView = (TextView) custom_grid_view.findViewById(R.id.grid_text);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(8, 8, 8, 8);
            } else {
                    custom_grid_view = (View)convertView;
                    imageView = (ImageView) custom_grid_view.findViewById(R.id.grid_image);
                    textView = (TextView) custom_grid_view.findViewById(R.id.grid_text);
                    
            }
            if(muserInfo.photopath.size() >0){
	            imageViewReference = new WeakReference<ImageView>(imageView);
	            BitmapFactory.Options opts = new BitmapFactory.Options();
	            opts.inJustDecodeBounds = true;            
	            BitmapFactory.decodeFile(muserInfo.photopath.get(position), opts);
	            int imageHeight = opts.outHeight;
	            int imageWidth = opts.outWidth;
	            opts.inSampleSize = util.get_insamplesize(imageHeight, imageWidth, 100, 100);
	            opts.inJustDecodeBounds = false;
	            bp = BitmapFactory.decodeFile(muserInfo.photopath.get(position), opts);
	            Log.d("MyGridViewAdapter","setting grid path"+muserInfo.photopath.get(position));
	            Log.d("MyGridViewAdapter","setting grid image"+muserInfo.photoname.get(position));
	            if(bp == null){
	            	bp = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.ic_launcher, opts);
	        		textView.setText("Unabe to decode Photo");
	            	if(imageView!=null)
	            		imageView.setImageBitmap(bp);
	            }else{
	            	textView.setText(muserInfo.photoname.get(position));
	            	if(imageView!=null){
	            		imageView.setImageBitmap(bp);
	            	}else{
	            		Log.d("GridView","ImageView null");
	            	}
	            }
            }else{
            	Log.d("GridView","no photopath to display");
            }
            

            return (View)custom_grid_view;
            
        }

         
	}
	
	private void fill_stored_photos(){
		  if(util.isExternalStorageReadable()){
	        File photo_parent_dir = getAlbumStorageDir(".photodir");
	        String photodir = new String(""+photo_parent_dir.getAbsolutePath()+"/"+muserInfo.userName);
	        File dir = new File(photodir);
	        if(dir.isDirectory()){
	        	muserInfo.photdir = dir.getAbsolutePath();
				for(int i=0;i<muserInfo.photoname.size();i++){
					Log.d("Log",""+muserInfo.photoname.get(i));
					muserInfo.photopath.add(new String(muserInfo.photdir+"/"+muserInfo.photoid.get(i)));
					//check for any bad files to be removed for fresh download
					//commenting now to speed up loading of image grid, this can be done later.
				/*	File img = new File(muserInfo.photopath.get(i));
					if(img.exists()){
						Bitmap bp;
						FileInputStream f;
		                try {
		                	f = new FileInputStream(muserInfo.photopath.get(i));
		                    Log.d("fill_stored_photos","loading from external memory... "+muserInfo.photoname.get(i));
		                    bp = BitmapFactory.decodeStream(f);
		                    if(bp == null){	
		                    	Log.d("fill_stored_photos","Read file decoded to null");
		                        img.delete();
		                     }else{
		                        bp.recycle();
		                     }
		                }catch (Exception e){
		                	e.printStackTrace();
		                }
					}else{
						Log.d("fill_stored_photos","Image does not exist on external storage");
					}*/
				}
			}
		  }
	  }
						
	private void get_photolist_ids(){
		String murl = new String(murl_base+muserInfo.userId);	
		 
		q = Volley.newRequestQueue(mcontext);

		Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() { 
			public void onResponse(JSONArray response) {
				Log.d("Success", response.toString()); 
				mtempuserInfo = new UserInfo();
        		File photo_parent_dir = getAlbumStorageDir(".photodir");
        		String photodir = new String(""+photo_parent_dir.getAbsolutePath()+"/"+muserInfo.userName);
        		File dir = new File(photodir);
				dir = setup_photodir(muserInfo.userName);
				mtempuserInfo.photdir = dir.getAbsolutePath();
				String photoname;
				String photoid;
				
				for(int i=0; i<response.length();i++){
						try {
							Log.d("parseJson", i+ "  "+response.getJSONObject(i).get("name").toString());
							photoname = new String(response.getJSONObject(i).get("name").toString());
							photoid = new String(response.getJSONObject(i).get("id").toString());
							mtempuserInfo.photoname.add(photoname);
							mtempuserInfo.photoid.add(photoid);	
							mtempuserInfo.photopath.add(new String(mtempuserInfo.photdir+"/"+photoid));
						
							} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
							}

					}
					//compare muserInfo and new mtempuserInfo , delete any photos that are not present in new list
					for(int i=0;i<muserInfo.photopath.size();i++){
						if(false == mtempuserInfo.photopath.contains(muserInfo.photopath.get(i))){
							File f = new File(mtempuserInfo.photopath.get(i));
							if(f.exists()){
								Log.d("deleting...","deleting... "+f.getAbsolutePath()+"/"+f.getName() );
								f.delete();
							}
						
						}
					}
				
					//delete all old photos if any in the directory pointed by current username but not in new mtempuserInfo list
	        		if(dir.isDirectory()){
		                //this user directory already exists look for thumbnails return if found
		                Log.d("fill_stored_photos", "user directory exists"+dir.getAbsolutePath());
		                File file[] = dir.listFiles();
		                Log.d("fill_stored_photos","file.length = "+file.length);
		                if(file.length != 0){
		                	for (int i=0; i < file.length; i++) {
		                		if(false == mtempuserInfo.photopath.contains(file[i].getAbsolutePath())){
		                			Log.d("deleting...","deleting... "+file[i].getAbsolutePath());
		                			file[i].delete();
		                		}
		                	}
		                }
	        		}
	        	
				
					//cleaned old unused photos now get new photos and store
	        		mtempuserInfo.userName = muserInfo.userName;
	        		mtempuserInfo.userId = muserInfo.userId;
	        		muserInfo = mtempuserInfo;
	        		//log and check updated info
	        		Log.d("ImageFragment","===== Updated UserInfo after getting new photolist in ImageFragment=====");
	        		Log.d("ImageFragment","username : "+muserInfo.userName+" id: "+muserInfo.userId);
	        		for(int i=0;i<muserInfo.photopath.size();i++){
	        			Log.d("ImageFragment","photoname = "+muserInfo.photoname.get(i));
	        			Log.d("ImageFragment","photopath = "+muserInfo.photopath.get(i));
	        		}
	        		Log.d("ImageFragment","========================================================================");
	        		mInterface_fragment_two.update_user_photo_in_db(muserInfo,response.toString());
	        		new getBitmapFromURL().execute();
					
				}
			};
			
			Response.ErrorListener failure = new Response.ErrorListener() {
			public void onErrorResponse(VolleyError error) { 
				Log.d("Failure", error.toString());
			} 
		};
		
		JsonArrayRequest getRequest = new JsonArrayRequest(murl, success, failure);

		Log.d(TAG, getRequest.toString());
		q.add(getRequest);
	}
	
	public class getBitmapFromURL extends AsyncTask<Void,Void,Void>{

		ProgressDialog dialog;
		String photopath; 
		int i;
		

		@Override
		protected void onPreExecute(){
			dialog = new ProgressDialog(mcontext);
			dialog.setTitle("Fetching photos from server...");
			dialog.show();
			getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
	            WindowManager wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
	            Display display = wm.getDefaultDisplay();
	            Point size = new Point();
	            display.getSize(size);
	            int reqwidth = size.x;
	            int reqheight = size.y;
				for(i=0;i<muserInfo.photoid.size(); i++){
					getActivity().runOnUiThread(new Runnable(){
						public void run() {

						    username.setText("Downloading Image..."+i+" of "+muserInfo.photopath.size());
						    }
						});
					photopath = muserInfo.photopath.get(i);
					//download a new file only if not present in the local memory
					if(new File(photopath).exists() == false){
						Log.d("TAG",i+"] downloading for path :"+photopath);
			            String mphotourl = mphotourl_base+muserInfo.photoid.get(i);
			            Log.d("downloading url", mphotourl);
			            URL url = new URL(mphotourl);
			            HttpURLConnection connection;
			            InputStream input;
			            Bitmap photo;
			            BitmapFactory.Options options = new BitmapFactory.Options();
			            try{
				            
			            	
			            	connection =(HttpURLConnection) url.openConnection();
				            connection.setDoInput(true);
				            connection.connect();
				            input = connection.getInputStream();
				            options.inJustDecodeBounds = true;            
				            BitmapFactory.decodeStream(input, null, options);
				            int imageHeight = options.outHeight;
				            int imageWidth = options.outWidth;
				            connection.disconnect();
				            connection =(HttpURLConnection) url.openConnection();
				            connection.setDoInput(true);
				            connection.connect();
				            input = connection.getInputStream();
				            
				            
				            options.inJustDecodeBounds = false;
				            Log.d("doInBackground","getting image of size "+reqheight+"x"+reqwidth);
				            options.inSampleSize = util.get_insamplesize(imageHeight, imageWidth, reqheight, reqwidth);
				            photo = BitmapFactory.decodeStream(input, null, options);
				            
				            if(photo == null)
				            {
	                            Log.d(TAG, "Photodecode Failed");
				            }
				            else{
					            //store photo in the photopath
					            if(util.isExternalStorageWritable()){
				                    Log.d("doInBackground", ""+photopath);
				                    BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(photopath));
				                    if(photo.compress(Bitmap.CompressFormat.PNG,0, buf)){
				                            buf.flush();
				                            buf.close();
				                            Log.d(TAG, "Photo "+muserInfo.photoname.get(i)+" stored at "+muserInfo.photopath.get(i));
				                            photo.recycle();
				                    }
					            }else{
				                    Log.e(TAG, "External storage not writable!!!");
					            }
				            }
				            connection.disconnect();
			            }catch(Exception e){
			            	e.printStackTrace();
			            }
					}
				}
	            
	            return null;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
		}
		
		
		@Override
		protected void onPostExecute(Void Result){
			dialog.dismiss();
			mphotosgrid_adapter.notifyDataSetChanged();
			getActivity().runOnUiThread(new Runnable(){
				public void run() {

				    username.setText(muserInfo.userName);
				    }
				});
			super.onPostExecute(Result);
		}
		
	}
	
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
                if(!file.isDirectory())
            Log.e(TAG, "Directory not created");
        }
        return file;
    }	
		
    private File setup_photodir(String selectedusername){
        File photo_parent_dir = getAlbumStorageDir(".photodir");
        Log.d(TAG, ""+photo_parent_dir.getAbsolutePath());
        File photo_userdir = new File(""+photo_parent_dir.getAbsolutePath()+"/"+selectedusername);
        if (!photo_userdir.mkdirs()) {
            if(!photo_userdir.isDirectory())
        Log.e(TAG, "Directory not created");
        }
        return photo_userdir;
}	
	
	
	
	public interface interface_fragment_two {
		public UserInfo getUserInfo_mainActivity();	
		public void back_from_photolist();
		public void update_user_photo_in_db(UserInfo selecteduserinfo, String JSONphotolist);
	}

	
}
