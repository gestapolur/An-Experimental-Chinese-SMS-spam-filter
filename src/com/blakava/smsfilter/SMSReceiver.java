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
	private static final String TEST_MSG = "测试asfd汇款+活动中!奖!@#$%^&(信息";
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
                //TODO modify it back after spam filter tested
                //Spam checking
                if( SMSFilter.isSpam( address , TEST_MSG ) ){
            		Toast.makeText( mContext , "A spam SMS received!", Toast.LENGTH_SHORT).show();
                	recordSpam( msg , address );
                }
				else{
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
