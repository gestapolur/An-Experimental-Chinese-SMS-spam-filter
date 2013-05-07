package com.blakava.smsfilter;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver
{
	//a test spam SMS for test use
	private static final String TEST_MSG = "测试asfd汇款+活动中!奖!@#$%^&(信息";
	private static final String TAG = "SMS_RECEIVER";
	private Context mContext;
	
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        abortBroadcast();
    	mContext=context;
		String msg , address;
        //---get the SMS message passed in--
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        
        if (bundle != null) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            
            //pdu to string
            for (int i=0; i<msgs.length; i++){           
            	msgs[ i ] = SmsMessage.createFromPdu((byte[])pdus[ i ]);
                address = msgs[ i ].getOriginatingAddress();
                msg = msgs[i].getMessageBody().toString();
                //Spam checking
                /**
                 * msg need change to TEST_MSG while testing using
                 * emulator, or it can't send SMS with ucs-2 decode
                 * properly.
                 * */
                Log.v( TAG , "msg received correctly, the msg is from " + address + " said " + msg);
                if( SMSFilter.isSpam( address , msg ) == true ){
                	Log.v( TAG , "spam message triggered.");
            		Toast.makeText( mContext , "A spam SMS received!", Toast.LENGTH_SHORT).show();
                	recordSpam( msg , address );
                }
				else{
					Log.v( TAG , "resend sms triggered.");
					resendSMS( msg , address ,"inbox");
					clearAbortBroadcast();
				}
            }            
        }
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
		mContext.getContentResolver().insert(Uri.parse("content://sms/"+box), values);
	}
	
	//TODO save spam and display on a list
	private void recordSpam( String msg , String address ){
		Log.v( "recordSpam" , "before " + msg + " " + address );
		SMSReceiverService.mSpamDBAdapter.open();
		Log.v( "recordSpam" , msg + " " + address );
		SMSReceiverService.mSpamDBAdapter.insertEntryTable( address , msg );
		return ;
	}
}
