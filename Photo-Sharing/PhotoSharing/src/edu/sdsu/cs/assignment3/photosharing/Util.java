package edu.sdsu.cs.assignment3.photosharing;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Util {
	private Context mcontext;
	
	public Util(Context ctx){
		mcontext = ctx;
	}
	public boolean is_phone_form(){
		Configuration config;
		
		config = mcontext.getResources().getConfiguration();
		
		if(config.smallestScreenWidthDp >=600){
			//consider it as a tablet configuration
			return false;
		}
		return true;
	}
	
	public boolean is_connected(){
		ConnectivityManager cm =
		        (ConnectivityManager)mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting());
	}
	
	  /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    
    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    public int get_insamplesize(int imageHeight, int imageWidth, int reqheight, int reqwidth){
       
        int inSampleSize = 1;
        if (imageHeight > reqheight || imageWidth > reqwidth) {
        final int halfHeight = imageHeight / 2;
        final int halfWidth = imageWidth / 2;
        while ((halfHeight / inSampleSize) > reqheight
            && (halfWidth / inSampleSize) > reqwidth) {
            	inSampleSize *= 2;
        	}	
        }
        return inSampleSize;
    }

}
