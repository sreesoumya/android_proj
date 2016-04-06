package com.example.android.wifidirect;

/**
 * Interface class for communication between tasks and fragments.
 */
public interface Callback {
	public void Server_Received_data(byte[] data);
	public void Client_Received_data(byte[] data);
	public void handle_camera_image(byte[] data);
}