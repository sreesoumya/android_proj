App Discription :

Accessing data via network
Using JSON
Database access
Interacting with Photos

The app will download and display photos. 
The server(given by my professor) is at http://bismarck.sdsu.edu/photoserver/. Anyone can access the photos on the server.
Only registered users can upload photos to the server. This app first shows a list of users. When
a user selects a user the app shows them a list of photo names from that user. When the user
selects a photo name, then the app shows the photo. This is done using three different activities.
The app is targeted for a phone and tablet. 
Downloading a photo is a three step process. 
1) First one needs to get a list of current registered users. This is done using the http GET request. 
The server response is a JSON array. The array contains a JSON object for each registered
user that has photos on the server. Users that have not uploaded photos are not listed. The
JSON object for a users contains a "name" field and an "id" field. 

2) To get a list of the photo of a user one uses the user's id in an HTTP GET request. Here is example of how
to request the list of photos for user with id "2". To get the list of photos for a different user
you replace the "2" with the user's id.
http://bismarck.sdsu.edu/photoserver/userphotos/2

The server responds with a JSON array. The array contains a JSON object for each photo the
given users has on the server. The JSON object for a photo contains a "name" field and an "id"
field.

3) To download a photo one uses another GET request. Here is an example of how to get the photo with id
"24". Again one changes the id to get a different photo.
http://bismarck.sdsu.edu/photoserver/photo/24

The server responds with a document of type "image/jpeg" and the bytes that make up the
photo. That is the server sends back the contents of the photo.

Some comments about the HTTP GET commands
1. Since these are HTTP URLs you can try them in a web browser. This can help
in debugging your app as you will be able to see the server response.
2. The HTTP URLs are case sensitive. Note that all the URLs above are all lowercase.
3. When there is a problem with a request the server responds with an HTTP response code of
404 and a message trying to describe the problem. The response code of 404 will cause the
Android HTTP clients to throw an exception. You should handle this case. The exception will
contain the error message. The first point above may be useful when this happens.
4. The ids for the users and photos are for internal use in your app. The ids have no meaning
to users so they should not be displayed to users of your app.

Included uploading photos, caching photos on devices, showing thumbnails of photos with
list of photo names and using gestures to move between photos and displaying
Uploading photos. Allowing the user to upload photos from their phone to the server. The user
is  able to select a photo from their photo library on the phone and upload it. 
The bytes of the photo are to be sent in the body of the post in a File Entity.
 If successful the server returns a JSON object contain the id of the photo just added.
For example:
{"id":"12"}

Some Other Features :
Saving Photos. Saves photos to permanent storage to allow off-line usage and to provide
faster viewing. When the app needs to display a photo you first check to see if the photo is already
saved on the device.
Saving User and photo lists. Saves the list of users and list of photos in the device database
to allow off-line usage.
Thumbnail view. When you display the list of photo names app adds a thumbnail view of the photo.
Swipe gesture. When displaying a photo ,used a swipe left or right to display the next or previous
photo from the same person.
Tablet version. The app runs on a phone and a tablet. The tablet version makes good use of the extra screen size.
For example on the phone version one would likely use one
screen to show a list of users. The list of photos from that user will be another screen. And the
photo itself will be a third screen. On the tablet here is room to show a list and a photo at the
same time.
