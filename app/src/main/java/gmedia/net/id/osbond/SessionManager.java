package gmedia.net.id.osbond;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "GMEDIASemargres2018";
	
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String TAG_UID = "uid";
	public static final String TAG_EMAIL = "email";
	public static final String TAG_NAMA = "nama";
	public static final String TAG_PICTURE = "picture";
	public static final String TAG_JENIS = "jenis";
	public static final String TAG_KTP = "ktp";
	public static final String TAG_SAVED = "saved";
	public static final String TAG_TOKEN = "token";

	// Constructor
	public SessionManager(Context context){
		this.context = context;
		pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Create login session
	 * */
	public void createLoginSession(String uid, String email, String jenis, String saved){

		editor.putBoolean(IS_LOGIN, true);

		editor.putString(TAG_UID, uid);

		editor.putString(TAG_EMAIL, email);

//		editor.putString(TAG_NAMA, nama);

//		editor.putString(TAG_PICTURE, picture);

//		editor.putString(TAG_KTP, ktp);

		editor.putString(TAG_JENIS, jenis);

		editor.putString(TAG_SAVED, saved);

		// commit changes
		editor.commit();
	}

	public void updateToken(String token){

		editor.putString(TAG_TOKEN, token);

		// commit changes
		editor.commit();
	}

	public void updateKTP(String ktp){

		editor.putString(TAG_KTP, ktp);

		// commit changes
		editor.commit();
	}

	public String getUserInfo(String key){
		return pref.getString(key, "");
	}

	public String getEmail(){
		return pref.getString(TAG_EMAIL, "");
	}

	public String getUid(){
		return pref.getString(TAG_UID, "");
	}

	public String getToken(){
		return pref.getString(TAG_TOKEN, "");
	}

	public String getNama(){
		return pref.getString(TAG_NAMA, "");
	}

	public String getKTP(){
		return pref.getString(TAG_KTP, "");
	}

	/**
	 * Clear session details
	 * */

	public boolean isSaved(){
		if(getUserInfo(TAG_SAVED) != null && getUserInfo(TAG_SAVED).equals("1")){

			return true;
		}else{
			return false;
		}
	}

	public void logoutUser(Intent logoutIntent){

		// Clearing all data from Shared Preferences
		try {
			editor.clear();
			editor.commit();
		}catch (Exception e){
			e.printStackTrace();
		}

		logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(logoutIntent);
		((Activity)context).finish();
		((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

}
