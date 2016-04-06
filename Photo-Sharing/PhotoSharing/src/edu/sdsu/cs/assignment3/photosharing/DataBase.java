package edu.sdsu.cs.assignment3.photosharing;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBase{
	
	private static final String DATABASE_NAME= "MyDatabase.db";
	private static final String TABLE_NAME= "UserData";
	private static final String NAME ="UserName";
	private static final String ID ="UserId";
	private static final String PHOTOLIST ="JSONPhotolist";
	private static final int VERSION = 1;
	public Context ctx;
	private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (\"Index\" INTEGER PRIMARY KEY  NOT NULL , \"UserName\" TEXT, \"UserId\" TEXT, \"JSONPhotolist\" TEXT);";
	
	DBHelper userdatabse;
	SQLiteDatabase db;
	
	public DataBase(Context ctx){
        this.ctx = ctx;
		userdatabse = new DBHelper(ctx);
		Log.d("DB_read","DataBase Constructor()");
	}

	private void db_dump(){
		Cursor cur;
		if(null != (cur = db.query(TABLE_NAME, new String[] {NAME,ID,PHOTOLIST}, null, null, null, null, null, null))){
			Log.d("db_dump","===================DB DUMP BEGIN==================");
			Log.d("db_dump","Table size: ["+cur.getCount()+"]");
			for(int i=0;(cur.moveToNext() != false);i++){
				Log.d("db_dump","------Table Row: ["+i+"]------");
				Log.d("db_dump",NAME+" : "+cur.getString(0));
				Log.d("db_dump",ID+" : "+cur.getString(1));
				Log.d("db_dump",PHOTOLIST+" : "+cur.getString(2));
			}
			Log.d("db_dump","===================DB DUMP END==================");
		}
				
	}
	public DataBase open(){
		db = userdatabse.getWritableDatabase();
		userdatabse.onCreate(db);
		Log.d("DB_read","DB open() :"+db.toString());
		return this;
	}
	
	public void close(){
		Log.d("DB_read","DB close() :"+userdatabse.toString());
		userdatabse.close();
	}
	
	public void clear_all()
	{
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		Log.d("DB_read","db clear_all() : drop table and create again");
		userdatabse.onCreate(db);
		db_dump();
	}
	
	public void delete_user(String username){
		String whereclause = new String(NAME+" = \""+username+"\"");
		db.delete(TABLE_NAME, whereclause, null);
	}
	
	public void insert_user(String username, String userid){
		
		ContentValues content = new ContentValues();
		Cursor cur;
		content.put(NAME, username);
		content.put(ID, userid);
		Log.d("insert_user","DB:: insert_user() "+username);
		//return db.insertOrThrow(TABLE_NAME, null, content);
		String whereclause = new String(NAME+" = \""+username+"\"");
		cur = db.query(TABLE_NAME, new String[] {NAME,ID,PHOTOLIST}, whereclause, null, null, null, null, null);
		if(cur.getCount() == 0){
			db.insertOrThrow(TABLE_NAME, null, content);
			db_dump();
		}else{
			cur.moveToNext();
			Log.d("DB_read","Found existing user : "+cur.getString(0));
		}
			
			
		
		return;
	}
	
	public void update_userphoto(UserInfo selecteduserinfo, String JSONphotolist){
	
		ContentValues content = new ContentValues();
		content.put(PHOTOLIST, JSONphotolist);
		Log.d("DB_read","====== update JSONphotolist in DB ======");
		String whereclause = new String(NAME+" = \""+selecteduserinfo.userName+"\"");
		db.update(TABLE_NAME, content, whereclause, null);
		db_dump();
	}
	
	public UserData read_data(){
		Cursor cur;
		UserData tempuserdata = new UserData();
		JSONArray items;
		if(null != (cur = db.query(TABLE_NAME, new String[] {NAME,ID,PHOTOLIST}, null, null, null, null, null, null))){
			Log.d("DB_read","======Table size: ["+cur.getCount()+"] ======");
			for(int i=0;(cur.moveToNext() != false);i++){
				Log.d("DB_read","------Table Row: ["+i+"]------");
				
				Log.d("DB_read",NAME+" : "+cur.getString(0));
				tempuserdata.users.add(new UserInfo());
				tempuserdata.users.get(i).userName = new String(cur.getString(0));
				Log.d("DB_read",ID+" : "+cur.getString(1));
				tempuserdata.users.get(i).userId = new String(cur.getString(1));
				//Log.d("DB_read",PHOTOLIST+" : "+cur.getString(2));
				try {
					if(cur.getString(2) != null){
						items = new JSONArray(cur.getString(2));
						for(int j =0 ;j<items.length(); j++){
							tempuserdata.users.get(i).photoname.add(items.getJSONObject(j).getString("name"));
						 	tempuserdata.users.get(i).photoid.add(items.getJSONObject(j).getString("id"));
						 	Log.d("DB_read","\t"+j+"] photoname = "+items.getJSONObject(j).getString("name")+", id = "+items.getJSONObject(j).getString("id")); ;
						 }
					}
					else{
						Log.d("DB_read", "No user photos");
					}
						
				}catch (JSONException e) {
					 e.printStackTrace();
				}
			}
		}
		return tempuserdata;
	}
	
	
		
	private static class DBHelper extends SQLiteOpenHelper{
		public DBHelper(Context ctx){
			super(ctx, DATABASE_NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				db.execSQL(TABLE_CREATE);
				Log.d("DB_read","DBHelper executed :"+TABLE_CREATE);
				
			}catch(SQLException e){
				Log.d("DB_read","DBHelper Cretate table failed :"+TABLE_CREATE);
				e.printStackTrace();
			}
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
			Log.d("DB_read","db onUpgrade() : drop table and create again");
			onCreate(db);
		}
	}

}
