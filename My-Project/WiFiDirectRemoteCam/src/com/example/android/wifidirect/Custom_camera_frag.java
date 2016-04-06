package com.example.android.wifidirect;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Fragment to display camera preview and capture image.
 */
public class Custom_camera_frag extends Fragment implements Camera.PreviewCallback,Camera.PictureCallback{
public static Context mcontext;
private static View mContentView = null;
private Camera mCamera;
private Custom_camera_preview mCameraPreview;
public Callback cb;
public static DeviceDetailFragment fragmentDetails;
public static int count_down = 30;  

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mCamera.stopPreview();
		mCamera.unlock();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.camera_frag, null);
		mcontext = getActivity();
		fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
		
		Log.d("MyCameraApp", "onCreateView()");
		if(checkCameraHardware(mcontext))
		{
			Log.d("MyCameraApp", "Device has camera");
			mCamera = getCameraInstance();
		}
		
        mCameraPreview = new Custom_camera_preview(mcontext, mCamera,preview_cb);
        FrameLayout preview = (FrameLayout) mContentView.findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);
		        
		return mContentView;
	}
	
	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Log.d("MyCameraApp", "getCameraInstance");
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    	Log.d("MyCameraApp", "cannot get camera");
	    	e.printStackTrace();
	    }
	    return c; // returns null if camera is unavailable
	}
	
    public PreviewCallback preview_cb = new PreviewCallback(){
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			count_down --;
			Log.d("preview_cb","preview_cb called clicking photo in "+count_down);
			if (count_down == 0){
				mCamera.takePicture(null, null, mPicture);
			}
		}
    	
    };
    
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		//preview frame received.. can click the pic now.
		
		count_down --;
		if (count_down == 0){
			 mCamera.takePicture(null, null, mPicture);
		}
	}	
	
	AutoFocusCallback af_cb = new  AutoFocusCallback(){

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(success == true){
				camera.takePicture(null, null, mPicture);
			}
			
			Log.d("af_cb","autofocus CB called success ="+success);
			
		}
		
	};
	PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        	mCamera.stopPreview();
        	count_down = 30;
        	fragmentDetails.handle_camera_image(data);
        }
    };

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
    	Log.d("MyCameraApp", "release camera instance and set image");
    	fragmentDetails.handle_camera_image(data);
    	camera.stopPreview();
    	camera.release();
		
	}
}
