package com.blakava.smsfilter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivityDisplay extends Fragment {	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState){
			super.onCreateView(inflater, container, savedInstanceState);
		 return inflater.inflate(R.layout.activity_main, container, false);		
	}
}
