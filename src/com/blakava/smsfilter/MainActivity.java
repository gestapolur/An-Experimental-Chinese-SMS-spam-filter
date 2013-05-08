package com.blakava.smsfilter;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private SpamDBAdapter mSpamDBAdapter;
	private Intent StartSMSReceiver;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main );
		mSpamDBAdapter = new SpamDBAdapter(this);
        
		mSpamDBAdapter.open();

        fillData();
        //test DBAdapter usage
		//mSpamDBAdapter.deleteTable1();
        //recordSpam( "test" , "TestSpam" );
        //mSpamDBAdapter.insertEntryTable("Test Scarlet" , "Hentai" );
		/*
		setContentView(R.layout.sms_sender);
		MainActivityDisplay t = new MainActivityDisplay();
		t.setArguments( getIntent().getExtras() );
		getSupportFragmentManager().beginTransaction().add(
	    R.id.fragment_container, t ).commit();
		*/
	}
	
	@Override
	public void onStart(){
		super.onStart();
				
		StartSMSReceiver = new Intent( getBaseContext() , SMSReceiverService.class );
		startService(StartSMSReceiver);
	}
	
	public void exitReceiver( View v ){
		this.finish();
		
		stopService(StartSMSReceiver);
		Toast.makeText(getBaseContext(), "Exiting spam filter...", 
				Toast.LENGTH_SHORT ).show();
	}
	
	/**
	 * handle the action after *delete* button chilcked
	 * @param v
	 */
    public void deleteClickHandler(View v) { 
    	//reset all the listView items background colours before we set the clicked one..
    	ListView lvItems = getListView();
    	for (int i=0; i<lvItems.getChildCount(); i++) 
			lvItems.getChildAt(i).setBackgroundColor(Color.LTGRAY);		
    	
    	//get the row the clicked button is in
    	LinearLayout vwParentRow = (LinearLayout)v.getParent();
    	
    	//delete spam sms from SpamDB
    	mSpamDBAdapter.deleteData(Integer.parseInt(((TextView)vwParentRow.getChildAt(2)).getText().toString()));
    	TextView child = (TextView)vwParentRow.getChildAt(0);
    	
    	//Log.v( "TEST" , ((TextView)vwParentRow.getChildAt(2)).getText().toString() );
    	//refresh delete button state
    	Button btnChild = (Button)vwParentRow.getChildAt(3);
    	btnChild.setText(child.getText());
    	btnChild.setText("Deleted!");
		
		vwParentRow.setBackgroundColor(Color.DKGRAY); 
		vwParentRow.refreshDrawableState();
    }
    
    /**
     * handle the action after *it's not a spam* button clicked
     * @param v
     */
    public void notSpamClickHandler(View v) { 
    	//reset all the listView items background colours before we set the clicked one..
    	ListView lvItems = getListView();
    	for (int i=0; i<lvItems.getChildCount(); i++) 
			lvItems.getChildAt(i).setBackgroundColor(Color.LTGRAY);		
    	
    	//get the row the clicked button is in
    	LinearLayout vwParentRow = (LinearLayout)v.getParent();
    	
    	//resend sms
    	resendSMS( ((TextView)vwParentRow.getChildAt(1)).getText().toString() ,
    			((TextView)vwParentRow.getChildAt(0)).getText().toString() ,"inbox");
    	//TODO is it possible to make android.provider.Telephony.SMS_RECEIVED boardcast again
    	// for it?
		Toast.makeText(this, "SMS was resend to inbox", Toast.LENGTH_SHORT).show();

    	//delete spam sms from SpamDB
    	mSpamDBAdapter.deleteData(Integer.parseInt(
    			((TextView)vwParentRow.getChildAt(2)).getText().toString()));
    	TextView child = (TextView)vwParentRow.getChildAt(0);
    	
    	//Log.v( "TEST" , ((TextView)vwParentRow.getChildAt(2)).getText().toString() );
    	//refresh delete button state
    	Button btnChild = ( Button )vwParentRow.getChildAt( 4 );
    	btnChild.setText( child.getText() );
    	btnChild.setText( "resend!");
		
		vwParentRow.setBackgroundColor(Color.DKGRAY); 
		vwParentRow.refreshDrawableState();
    }
    
    private void fillData() {
    	
    	Cursor coloursCursor;

		coloursCursor = mSpamDBAdapter.fetchAllEntriesForTable();
    		
        startManagingCursor(coloursCursor);
        
        // Create an array to specify the fields we want to display in the list
        
        String[] from = new String[]{SpamDBAdapter.KEY_ADDRESS , SpamDBAdapter.KEY_MSG , SpamDBAdapter.KEY_ROWID};
        
        // and an array of the fields we want to bind those fields to
       int[] to = new int[]{R.id.tvViewRow1 , R.id.tvViewRow2 , R.id.dataid };
               
        //create a simple cursor adapter and set it to display
        SimpleCursorAdapter colours = 
        		//new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2 , coloursCursor, from,  new int[] { android.R.id.text2, android.R.id.text1 });
        	    new SimpleCursorAdapter(this, R.layout.view_row, coloursCursor, from, to);
        setListAdapter(colours);
       
    }
    
	/**
	 * Save a SMS to the default Android SMS application.
	 * 
	 * @param message
	 * The text of the SMS.
	 * @param sender
	 * The phone number of the SMS.
	 * @param box
	 * The box where to store the message.
	 */
	private void resendSMS( String message, String sender, String box) {
		ContentValues values = new ContentValues();
		values.put("address", sender);
		values.put("body", message);
		this.getContentResolver().insert(Uri.parse("content://sms/"+box), values);
	}
    
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sm, menu);
		return true;
	}
	*/

}
