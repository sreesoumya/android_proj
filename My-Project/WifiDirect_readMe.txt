My project(app) uses WiFi Direct P2P communication between two android devices to take picture 
 using the camera of the remote device.
 
 	WiFi Direct is a P2P communication method similar to Bluetooth but using WiFi protocol thus giving
 much higher data rate and improved range and reliability in connection. WiFi direct is made mandatory
 feature in devices with android 4.1 and above. Although it was introduced in Android 4.0 many fixes 
 went into Android 4.1 release and WiFi Direct works better with Android 4. and above.
 
 	This Project connects two devices in a P2P manner and lets each device use the camera of the 
 other device to take a picture by sending the command to take picture using the established WiFi Direct 
 connection over a client and server socket connection. The picture is then sent back to the device 
 that sent the command for the user to see.A good use case can be to take a "selfie". After connecting 
 the two devices a user can comfortably take the time to stand in front of the camera and remotely trigger 
 taking of the picture using the device in users hand.The image that is taken is instantly sent back to 
 the user to decide if the user wants another one or not. At present the image is stored in the device
 in which it was shot and only a compressed version is sent to the triggering device.This is done to
 improve the speed of transfer so that the user gets to see the snapshot immediately. 
 The entire 12/18 mega pixel image can be pulled in the background if user  is happy with the shot, 
 this part is yet to be implemented.
 
 	An Additional feature is that the two devices once connected can send one line text messages to each 
 other.This is currently displayed at the bottom of the screen.There is no history stored and each line
 gets overwritten by the next line.
  
Working:
--------
pairing the devices:
--------------------
After starting the app on both devices.
1) Press the Discover Menu Item on both devices.
2) When both devices list on each other under PEER section select one from one of the devices.
3) On selecting "Connect" button appears on the right side, press Connect on one of the devices only.
4) An invitation appears on the other device, please "Accept" to pair the two devices.

taking picture and sending instant chat messages:
------------------------------------------------
5) Once the devices are paired "Sync Server and Client" button appears on the device on which "Connect" 
was pressed.
6) On pressing this button the two devices start communicating with each other over client server sockets.
7) Now "SendTxt" button and "Click Pic" button can be used to send instant message to other device or take
picture using the other devices camera.
8) The other device also gets the same options and can use these buttons any time.
9) Once "Click Pic" button is pressed the remote device hides its buttons and starts an in app camera to 
take the picture. Once the picture is taken it is sent back to the commanding device and the buttons 
reappear.

