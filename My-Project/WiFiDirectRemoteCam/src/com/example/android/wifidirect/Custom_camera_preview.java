package com.example.android.wifidirect;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Camera Preview Class
 *
 */
public class Custom_camera_preview extends SurfaceView implements SurfaceHolder.Callback{
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	PreviewCallback cb;
	public Custom_camera_preview(Context context, Camera camera,PreviewCallback preview_cb) {
	    super(context);
	    this.mCamera = camera;
	    //implement the callback in the camera_frag
	    this.mCamera.setPreviewCallback(preview_cb);  
	    this.mSurfaceHolder = this.getHolder();
	    this.mSurfaceHolder.addCallback(this);
	}
	

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
	    try {
	        mCamera.setPreviewDisplay(surfaceHolder);
	        mCamera.startPreview();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	    mCamera.stopPreview();
	    mCamera.release();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
	        int width, int height) {
	    try {
	        mCamera.setPreviewDisplay(surfaceHolder);
	        mCamera.startPreview();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}	
	
}
