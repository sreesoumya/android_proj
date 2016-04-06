/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wifidirect;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.wifidirect.DeviceListFragment.DeviceActionListener;


/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener,Callback{
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	public static boolean command_received = false;
	public static String clientIP;
	public static TextView statusText;
	public static final String IP_SERVER = "192.168.49.1";
	public static int PORT = 8988;
	public AsyncTask<Void, byte[], Boolean> clienttask;
	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	private static View mContentView = null;
	private WifiP2pDevice device;
	public static WifiP2pInfo info;
	ProgressDialog progressDialog = null;
	public static Activity mactivity;  
	/** Client socket variables to be accessed in asynctask also **/
	public static Socket clientsocket = null; 
	public static InputStream clientis; 
	public static OutputStream clientos;
	/** Server socket variables  **/
	public static ServerSocket serversocket;
	public static InputStream serveris;
	public static OutputStream serveros;
	
	public static boolean server_client_sync_done = false;
	public static boolean ImgComing = false;
	public static boolean Click_received = false;
	public static byte[] pic_to_send;
	public static BufferedOutputStream buf;
	public static FileInputStream fis;
	public static FileOutputStream fos;
	public static int offset =0;
	public static File pictureFile;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		statusText = (TextView) mContentView.findViewById(R.id.status_text);
		mactivity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.device_detail, null);		
		mContentView.findViewById(R.id.btn_send_cmd).setVisibility(View.GONE);
		mContentView.findViewById(R.id.btn_click).setVisibility(View.GONE);
		/** handle each button **/ 
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				/** let the other device be group owner **/
				config.groupOwnerIntent = 0;  
				config.wps.setup = WpsInfo.PBC;
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
						"Connecting to :" + device.deviceAddress, true, true);
				((DeviceActionListener) getActivity()).connect(config);

			}
		});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						 server_client_sync_done = false;
						 if(clientsocket != null){
							 try {
								clientsocket.close();
								while(!clientsocket.isClosed()){
									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						 }
						 if(serversocket != null){
							 try {
								serversocket.close();
								while(!serversocket.isClosed()){
									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						 }
						 
						((DeviceActionListener) getActivity()).disconnect();
					}
				});
		
		mContentView.findViewById(R.id.btn_send_cmd).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {			
						TextView txt_to_send = (TextView) mContentView.findViewById(R.id.text_to_send);
						SendCmd(""+txt_to_send.getText());
						txt_to_send.setText("");
					}
				});
		
		mContentView.findViewById(R.id.btn_click).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						SendCmd("Click");
					}
				});
		
		mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						@SuppressWarnings("unused")
						String localIP = Utils.getLocalIPAddress();
						String client_mac_fixed = new String(device.deviceAddress).replace("99", "19");
						clientIP = Utils.getIPFromMac(client_mac_fixed);
						int count = 30;
						if(!info.isGroupOwner)
						{
							// create new task for client
							clienttask = new ClientTask(DeviceDetailFragment.this); 
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								clienttask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					     	} else {
					     		clienttask.execute();
					     	}
							/** Wait until underlying socket connection is done before sending command **/
							while(true){
								try {
									Log.d(WiFiDirectActivity.TAG, "while waiting ");
									Thread.sleep(1000);
									count --;
									if(clientsocket!= null){
										Log.d(WiFiDirectActivity.TAG, "nsocket!= null");
										if(clientsocket.isConnected()){
											Log.d(WiFiDirectActivity.TAG, "nsocket.isConnected()");
											//non group owner starts the client connection and sends 
											//initial token command "Sync" to server
									     	String cmd = new String("Sync");
									     	SendCmd(cmd);
											break;
										}else if(count ==0){
											statusText.setText("Client conenction failed.");
											break;
										}
									}
									
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}							
						}
						/** Once connected change the view accordingly **/
						mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
						mContentView.findViewById(R.id.btn_send_cmd).setVisibility(View.VISIBLE);
						mContentView.findViewById(R.id.text_to_send).setVisibility(View.VISIBLE);
					}
				});

		return mContentView;
	}

	public static void SendCmd(String cmd){
		/** Create new thread for each sending of command **/
		AsyncTask<Void, Void, String> sendtask = new SendData(mactivity, mContentView.findViewById(R.id.status_text),cmd.getBytes());
     	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
     		sendtask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
     	} else {
     		sendtask.execute();
     	}
	}
	
	
	public static void SendCamImg(byte[] bmp){
		/** To send the image first send "ImgComing" command to let server know the following data is to be saved in file
		 *  Once the image is sent send cmd "imgEnd" to signify the end of file transfer **/
		SendCmd("ImgComing");
     	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int read =0;
		Log.d("PicTransfer","sending image of bytes = "+read);
		AsyncTask<Void, Void, String> sendtask = new SendData(mactivity, mContentView.findViewById(R.id.status_text),bmp);
     	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
     		sendtask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
     	} else {
     		sendtask.execute();
     	}
     	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
     	SendCmd("ImgEnd");
	}
	
	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		DeviceDetailFragment.info = info;
		this.getView().setVisibility(View.VISIBLE);
		if(server_client_sync_done == false){
			mContentView.findViewById(R.id.btn_send_cmd).setVisibility(View.GONE);
			mContentView.findViewById(R.id.text_to_send).setVisibility(View.GONE);
		}
		// The owner IP is now known.
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text)
				+ ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
						: getResources().getString(R.string.no)));

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
		if(!info.isGroupOwner){
			if(clientsocket== null){
				mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
			}
		}

		if(info.isGroupOwner){
			//start a server only if you are group owner
			AsyncTask<Void, byte[], String> servertask = new ServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text),this);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				servertask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	     	} else {
	     		servertask.execute();
	     	}
			
		}else if(!info.isGroupOwner && (server_client_sync_done == false))
		{
			mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
		}

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
		
	}

	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());

	}

	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_ip);
		view.setText(R.string.empty);
		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
		mContentView.findViewById(R.id.clicked_photo).setVisibility(View.GONE);
		mContentView.findViewById(R.id.text_to_send).setVisibility(View.GONE);
		mContentView.findViewById(R.id.btn_click).setVisibility(View.GONE);
		mContentView.findViewById(R.id.btn_send_cmd).setVisibility(View.GONE);
		mContentView.findViewById(R.id.clicked_photo).setVisibility(View.GONE);
		
		this.getView().setVisibility(View.GONE);
	}

	/**
	 * A simple server socket that accepts connection and reads data on
	 * the stream.
	 */
	public static class ServerAsyncTask extends AsyncTask<Void, byte[], String> {
		
		@SuppressWarnings("unused")
		private final Context context;
		private final TextView statusText;
		private DeviceDetailFragment fragment;

		public ServerAsyncTask(Context context, View statusText, DeviceDetailFragment deviceDetailFragment) {
			this.context = context;
			this.statusText = (TextView) statusText;
			this.fragment = deviceDetailFragment;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				serversocket = new ServerSocket(PORT);
				Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
				serversocket.setReuseAddress(true);
				Socket client = serversocket.accept();
				client.setReuseAddress(true);
				Log.d(WiFiDirectActivity.TAG, "Server: Socket Accepted connection");
				serveris = client.getInputStream();
				serveros = client.getOutputStream();

                byte[] buffer = new byte[4096];
                while(true){
	                int read = serveris.read(buffer, 0, 4096);
	                while(read != -1){
	                    byte[] tempdata = new byte[read];
	                    System.arraycopy(buffer, 0, tempdata, 0, read);
	                    Log.d("PicTransfer","Incoming bytes = "+read);
	                    publishProgress(tempdata);
	                    read = serveris.read(buffer, 0, 4096); 
	                }
                }
			} catch (IOException e) {
				Log.e(WiFiDirectActivity.TAG, e.getMessage());
				return null;
			}
		}
        @Override
        protected void onProgressUpdate(byte[]... values) {
            if (values.length > 0) {
                Log.i("AsyncTask", "onProgressUpdate: " + values[0].length + " bytes received.");
                fragment.Server_Received_data(values[0]);
            }
        }

		@Override
		protected void onPostExecute(String result) {
			Log.i("ServerAsyncTask", "onPostExecute: " + result);
		}

		@Override
		protected void onPreExecute() {
			statusText.setText("Opening a server socket");
		}
	}

    /**
     * Intermediate task to send data using the outputstream obtained from client/server socket
     */
	public static class SendData extends AsyncTask<Void, Void, String> {
     
		@SuppressWarnings("unused")
		private final Context context;
		private final TextView statusText;
		private final byte[] datatosend;


		public SendData(Context context, View statusText,byte[] data_byte_arr) {
			this.context = context;
			this.statusText = (TextView) statusText;
			this.datatosend = data_byte_arr;
		}

		@Override
		protected String doInBackground(Void... params) {
				Log.i("AsyncTask", "doInBackground: Sending data");
				if(info.isGroupOwner){
					if(serversocket.isBound()){
						try {
							serveros.write(datatosend);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				
				}else{
			     	if(clientsocket.isConnected()){
			     		try {
							clientos.write(datatosend);
						} catch (IOException e) {
							e.printStackTrace();
						}
			     	}
				}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("SendData", "onPostExecute: ");
			statusText.setText("Sent: "+new String(datatosend));
			pic_to_send = null;
		}

		@Override
		protected void onPreExecute() {
			Log.i("AsyncTask", "onPreExecute of SendData "+new String(datatosend));
		}

	}
	
	/**
	 * A simple client socket that initiates connection and reads data on
	 * the stream.
	 */
	 public class ClientTask extends AsyncTask<Void, byte[], Boolean>  {
		 	DeviceDetailFragment fragment;
		 	
	        public ClientTask(DeviceDetailFragment deviceDetailFragment) {
			this.fragment = deviceDetailFragment;
		}

		@Override
        protected void onPreExecute() {
            Log.i("ClientTask", "onPreExecute of ClientTask");
        }

        @Override
        protected Boolean doInBackground(Void... params)  {
            try {
                Log.i("ClientTask", "doInBackground: Creating socket");
                SocketAddress sockaddr = new InetSocketAddress(IP_SERVER,PORT);
                clientsocket = new Socket();
                clientsocket.connect(sockaddr, 50000);
                if (clientsocket.isConnected()) { 
                    clientis = clientsocket.getInputStream();
                    clientos = clientsocket.getOutputStream();
                    Log.i("ClientTask", "doInBackground: Socket created, streams assigned");
                    Log.i("ClientTask", "doInBackground: Waiting for inital data...");
                    byte[] buffer = new byte[4096];
                    while(true){
	                    int read = clientis.read(buffer, 0, 4096); 
	                    while(read != -1){
	                        byte[] tempdata = new byte[read];
	                        System.arraycopy(buffer, 0, tempdata, 0, read);
	                        publishProgress(tempdata);
	                        Log.i("ClientTask", "doInBackground: Got some data");
	                        read = clientis.read(buffer, 0, 4096); 
	                    }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("ClientTask", "doInBackground: IOException");
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("ClientTask", "doInBackground: Exception");
            } finally {
                try {
                    clientis.close();
                    clientos.close();
                    clientsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("ClientTask", "doInBackground: Finished");
            }
			return null;
        }

        public void SendDataToNetwork(String cmd) {
            try {
                if (clientsocket.isConnected()) {
                    Log.i("ClientTask", "SendDataToNetwork: Writing received message to socket");
                    clientos.write(cmd.getBytes());
                } else {
                    Log.i("ClientTask", "SendDataToNetwork: Cannot send message. Socket is closed");
                }
            } catch (Exception e) {
                Log.i("ClientTask", "SendDataToNetwork: Message send failed. Caught an exception");
            }
        }
	        	
        @Override
        protected void onProgressUpdate(byte[]... values) {
            if (values.length > 0) {
                Log.i("AsyncTask", "onProgressUpdate: " + values[0].length + " bytes received.");
                fragment.Client_Received_data(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            statusText.setText("onPostExecute");
            Log.i("AsyncTask", "onPostExecute: result:" + result);

        }
	 }


	/**
	 * Handle Commands and Data received by server
	 */
	@Override
	public void Server_Received_data(byte[] data) {
		String cmd =  new String(data);
		if(ImgComing == true){
			handle_incoming_image(data);
		}else{
			Log.i("Server_Received", "server_received "+cmd);
			statusText.setText("Received: " +cmd );
			if(cmd.equals("Sync")){
				Log.d("PicTransfer","server sending SYNC Done");
				SendCmd("SYNC_Done");
				server_client_sync_done = true;
				mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
				mContentView.findViewById(R.id.device_address).setVisibility(View.GONE);
				mContentView.findViewById(R.id.device_info).setVisibility(View.GONE);
				mContentView.findViewById(R.id.group_owner).setVisibility(View.GONE);
				mContentView.findViewById(R.id.group_ip).setVisibility(View.GONE);
				
				mContentView.findViewById(R.id.btn_send_cmd).setVisibility(View.VISIBLE);
				mContentView.findViewById(R.id.text_to_send).setVisibility(View.VISIBLE);
				mContentView.findViewById(R.id.btn_click).setVisibility(View.VISIBLE);
			}else{
				statusText.setText("Received: "+cmd);
				if(cmd.equals("Click")){
					Log.d("PicTransfer","server received click taking picture");
					Click_received = true;
					take_picture();
				}
				if(cmd.equals("ImgComing")){
					Log.d("PicTransfer","Incoming = true");
                    pictureFile = getOutputMediaFile();
                    try {
						fos = new FileOutputStream(pictureFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					ImgComing = true;
				}				
			}
		}
	}
	/**
	 * Handle Commands and data received by Client
	 */
	@Override
	public void Client_Received_data(byte[] data) {

		String cmd =  new String(data);
		if(ImgComing == true){
			handle_incoming_image(data);
		}else{
			Log.i("Client_Received", "client_received "+cmd);
			statusText.setText("Received: " +cmd );
			if(cmd.equals("SYNC_Done")){
				server_client_sync_done = true;
				mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
				mContentView.findViewById(R.id.device_address).setVisibility(View.GONE);
				mContentView.findViewById(R.id.device_info).setVisibility(View.GONE);
				mContentView.findViewById(R.id.group_owner).setVisibility(View.GONE);
				mContentView.findViewById(R.id.group_ip).setVisibility(View.GONE);
				
				mContentView.findViewById(R.id.btn_send_cmd).setVisibility(View.VISIBLE);
				mContentView.findViewById(R.id.text_to_send).setVisibility(View.VISIBLE);
				mContentView.findViewById(R.id.btn_click).setVisibility(View.VISIBLE);
			}
			if(cmd.equals("Click")){
				Click_received = true;
				take_picture();
			}
			if(cmd.equals("ImgComing")){
				Log.d("PicTransfer","Incoming = true");
                pictureFile = getOutputMediaFile();
                try {
					fos = new FileOutputStream(pictureFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				ImgComing = true;
			}				
		}	
	}

	/**
	 * Clear out visible info and start camera fragment
	 */
	public void take_picture(){
		mContentView.findViewById(R.id.device_address).setVisibility(View.GONE);
		mContentView.findViewById(R.id.device_info).setVisibility(View.GONE);
		mContentView.findViewById(R.id.group_owner).setVisibility(View.GONE);
		mContentView.findViewById(R.id.group_ip).setVisibility(View.GONE);
		mContentView.findViewById(R.id.text_to_send).setVisibility(View.GONE);
		mContentView.findViewById(R.id.clicked_photo).setVisibility(View.GONE);
		
		
		Fragment camera_fragment = new Custom_camera_frag();
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.app_details,camera_fragment,"camera_preview_fragment");
		transaction.commit();
	}

	/**
	 * Handle the camera image received from camera fragment. Store locally and then send compressed version of stored file.
	 */
	@Override
	public void handle_camera_image(byte[] data) {
		Log.d("handle_camera_image","handle_camera_image called");
        File pictureFile_camera = getOutputMediaFile();
        try {
			FileOutputStream fos_camera = new FileOutputStream(pictureFile_camera);
			fos_camera.write(data);
			data = null;
			fos_camera.close();
			System.gc();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        Bitmap bmp = get_bitmap_from_file(pictureFile_camera);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	bmp.compress(Bitmap.CompressFormat.JPEG , 50, stream);
        
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.remove(manager.findFragmentByTag("camera_preview_fragment"));
		transaction.commit();
		
		mContentView.findViewById(R.id.btn_click).setVisibility(View.VISIBLE);
		mContentView.findViewById(R.id.text_to_send).setVisibility(View.VISIBLE);
		mContentView.findViewById(R.id.clicked_photo).setVisibility(View.VISIBLE);
		ImageView clicked_pic = (ImageView) mContentView.findViewById(R.id.clicked_photo);
		pic_to_send = stream.toByteArray();
		clicked_pic.setImageBitmap(bmp);
		Log.d("handle_camera_image","sending ImgComing");
		SendCamImg(pic_to_send);
		
	}
	
	/**
	 * Handle the incoming image data from other device
	 */
	private void handle_incoming_image(byte[] data){
		String cmd =  new String(data);
		Log.d("Imageincoming","Incoming image bytes");
		if(cmd.equals("ImgEnd")){
			ImgComing = false;
			Log.d("Imageincoming","ImgEnd");
			
			Bitmap bmp = get_bitmap_from_file(pictureFile);
		    Log.d("Imageincoming","created bitmap");
			
			mContentView.findViewById(R.id.device_address).setVisibility(View.GONE);
			mContentView.findViewById(R.id.device_info).setVisibility(View.GONE);
			mContentView.findViewById(R.id.group_owner).setVisibility(View.GONE);
			mContentView.findViewById(R.id.group_ip).setVisibility(View.GONE);
			mContentView.findViewById(R.id.clicked_photo).setVisibility(View.GONE);
			
			mContentView.findViewById(R.id.text_to_send).setVisibility(View.VISIBLE);
			mContentView.findViewById(R.id.btn_click).setVisibility(View.VISIBLE);
			mContentView.findViewById(R.id.clicked_photo).setVisibility(View.VISIBLE);
			ImageView clicked_pic = (ImageView) mContentView.findViewById(R.id.clicked_photo);
			clicked_pic.setImageBitmap(bmp);
			  try {
				  fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
					fos.write(data);
					data = null;
					System.gc();
					Log.d("PicTransfer","writing to file ");
					fos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
	}
	
	/**
	 * Get compressed 200x200 image for display, for now, later to scale it to actual size.
	 */
	private Bitmap get_bitmap_from_file(File pictureFile){
		
		BitmapFactory.Options opts = new BitmapFactory.Options();
        
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), opts);
		int imageHeight = opts.outHeight;
		int imageWidth = opts.outWidth;
		int reqwidth =200;
		int reqheight =200;
		
	    int inSampleSize = 1;

	    if (imageHeight > reqheight || imageWidth > reqwidth) {

	        final int halfHeight = imageHeight / 2;
	        final int halfWidth = imageWidth / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqheight
	                && (halfWidth / inSampleSize) > reqwidth) {
	            inSampleSize *= 2;
	        }
	    }
	    opts.inJustDecodeBounds = false;
	    opts.inSampleSize = inSampleSize ;
	    Log.d("Imageincoming","decode bounds");
	    Bitmap bmp = BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), opts);
	    
		return bmp;
		
	}
	
	 private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
	 }
}
