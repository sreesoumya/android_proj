package edu.sdsu.cs.assignment3.photosharing;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class UploadActivity extends Activity {
	
	
	private ImageView mImageView;	
	private static final String TAG = "UploadActivity";
	private Bitmap bitmap;
	String mHttpPostResponse;
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate()");
		setContentView(R.layout.imageview_upload);
		
		//handling image view
				mImageView =(ImageView)findViewById(R.id.image_upload);
				
			//passing the intent result
				Intent i = getIntent();
				String selectedImage = (String)i.getStringExtra("selectedImage_tag");
				Uri selectedImageUri =  Uri.parse(selectedImage);
				getPicturePathFromUri(selectedImageUri);
	}	
					
//Retrieves the result returned from selecting image, by invoking the method "selectImageFromGallery()"

public void getPicturePathFromUri(Uri selectedImage ) {
	
		  Log.d(TAG,"inside if - selected image" +selectedImage);
		  String[] filePathColumn = { MediaStore.Images.Media.DATA };

		  Cursor cursor = getContentResolver().query(selectedImage,
		    filePathColumn, null, null, null);
		  cursor.moveToFirst();

		  int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		  String picturePath = cursor.getString(columnIndex);
		  cursor.close();
		  Log.d(TAG,picturePath);
		  decodeFile(picturePath);
		        
	}
	
private void decodeFile(String filePath) {
	
	 BitmapFactory.Options o = new BitmapFactory.Options();
	 o.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(filePath, o);

	 // The new size we want to scale to
	 final int REQUIRED_SIZE = 1024;

	 // Find the correct scale value. It should be the power of 2.
	 int width_tmp = o.outWidth, height_tmp = o.outHeight;
	 int scale = 1;
	 while (true) {
	  if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
	          break;
	  width_tmp /= 2;
	  height_tmp /= 2;
	  scale *= 2;
	 }
	// Decode with inSampleSize
	 BitmapFactory.Options o2 = new BitmapFactory.Options();
	 o2.inSampleSize = scale;
	 bitmap = BitmapFactory.decodeFile(filePath, o2);
	 Log.d("TAG","setImageBitmap");
	 mImageView.setImageBitmap(bitmap);
	
		
	}
   
class ImageUploadTask extends AsyncTask<Void, Void, String> {
	 private String urlToPost = "http://bismarck.sdsu.edu/photoserver/postphoto/Soumya/2442/";
	 String filename;

	 // private ProgressDialog dialog;
	 private ProgressDialog dialog = new ProgressDialog(UploadActivity.this);

	 @Override
	 protected void onPreExecute() {
		 Log.d(TAG,"onPreExecute()  Uploading...");
	  dialog.setMessage("Uploading...");
	  dialog.show();
	 }

	 @Override
	 protected String doInBackground(Void... params) {
	  try {
		  DateFormat df = new SimpleDateFormat("EEEdMMMyyyyHHmm");
		  String date = df.format(Calendar.getInstance().getTime());
		  filename = new String("smy_"+date);
		  Log.d("doInBackground", filename);
		  
		  Log.d(TAG,"doInBackground() "+urlToPost+filename);
		  HttpClient httpClient = new DefaultHttpClient();
		  HttpContext localContext = new BasicHttpContext();
		  HttpPost httpPost = new HttpPost(urlToPost+filename);	  
		
	      MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			 

		  ByteArrayOutputStream bos = new ByteArrayOutputStream();
		  bitmap.compress(CompressFormat.JPEG, 100, bos);
		  byte[] data = bos.toByteArray();
		
	   entity.addPart("file",
               new ByteArrayBody(data, "image/jpeg", "file"));

	   
	   

	   httpPost.setEntity(entity);
	   HttpResponse response = httpClient.execute(httpPost,localContext);
	   BufferedReader reader = new BufferedReader(new InputStreamReader(
	   response.getEntity().getContent(), "UTF-8"));

	   mHttpPostResponse = reader.readLine();
	   Log.d("HTTP Response", filename+ mHttpPostResponse);
	   return mHttpPostResponse;
	   

	  } catch (Exception e) {
	   // something went wrong. connection with the server error
	  }
	  return null;
	 }

	 @Override
	 protected void onPostExecute(String result) {
	  dialog.dismiss();
	  Log.d(TAG,"onPostExecute() ");
	  Toast.makeText(UploadActivity.this, "file uploaded: "+filename +mHttpPostResponse,Toast.LENGTH_LONG).show();
	 }
	}
@Override
public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.upload_activity_menu, menu);
	return super.onCreateOptionsMenu(menu);
}
@Override
public boolean onOptionsItemSelected(MenuItem item) {
	Log.d(TAG,TAG+"UploadActivity----> onOptionsItemSelected()");
	switch (item.getItemId()){
	case R.id.uploadServer:
		 new ImageUploadTask().execute();	
	return true;
	case R.id.DoneUpload:
		finish();
		return true;
	default:
	       return super.onOptionsItemSelected(item);
	
}
	
}
}