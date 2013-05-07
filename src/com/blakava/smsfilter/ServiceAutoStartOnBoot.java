package com.blakava.smsfilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceAutoStartOnBoot extends BroadcastReceiver {   

	private Intent StartSMSReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {

		StartSMSReceiver = new Intent( context , SMSReceiverService.class );
		context.startService(StartSMSReceiver);
		//example usage:
        //Intent myIntent = new Intent(context, YourService.class);
		//context.startService(myIntent);
    }
}