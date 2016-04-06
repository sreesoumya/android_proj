package edu.sdsu.cs.assignment3.photosharing;

import java.util.ArrayList;

public class UserInfo{
	public String userName;
	public String userId;
	public String photdir;
	ArrayList<String> photoname;
	ArrayList<String> photoid;
	ArrayList<String> photopath;
	//constructor		
	public UserInfo(){
		userName = new String("");
		userId = new String("");
		photdir = new String("");
		photoname = new ArrayList<String>();
		photoid = new ArrayList<String>();
		photopath = new ArrayList<String>();
	}
}