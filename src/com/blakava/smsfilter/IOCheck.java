//TODO use to check whether intern/external storage works
package com.blakava.smsfilter;

import android.os.Environment;
/**
 * not used yet
 * use to check whether read word list correctly from wordlist.txt
 * use to check whether intern/external storage works
 * @author gestapolur
 */
public class IOCheck {

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
}
