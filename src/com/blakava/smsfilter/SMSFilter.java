package com.blakava.smsfilter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.blakava.datastructure.DefaultSpamWords;
import com.blakava.datastructure.Node;

/**
 * 
 * @author gestapolur
 * Filter SMS use aho-crosick automata
 */
public class SMSFilter {

	private final static String TAG = "ReadWordList";
	private static long wordNumber = 0;
	protected static int AvarageSpamWordCnt = 3;
	private static String fileName = "/wordlist.txt";
    //total node number
    protected static int tot = 0;
    private static Context mContext;
    
	public static ArrayList<Node>node = new ArrayList<Node>();	

    /**
     * @param '0' <= str[ i ] <= '9'
     * @return an int array
     * trans unicode to int array in decimal
     */
    protected static ArrayList<Integer> transCode( String str ){
        int len = str.length();
        Log.v( TAG , str.length() + " " + str );
        int n = 0;
        //int [ ]ra = new int[ 5 * len ];
        ArrayList<Integer>ra = new ArrayList<Integer>();
        String t;
        for( int i = 0 ; i < len ; ++ i ){
            t = new String( Integer.toString((int)str.charAt( i ) , 10 ) );
            //unicode char change to decimal
            for( int j = 0 ; j < t.length() ; ++ j )
                ra.add( ( int )( t.charAt( j ) - '0' ) );
        }
        
        String ttt = "";
        for( int i = 0 ; i < n ; ++ i ) ttt += Integer.toString(ra.get( i ));
        Log.v( TAG , Integer.toString( n ) + " " + ttt );
        return ra;
    }
    
    //init root
    protected static void initRoot(){
        node.add( new Node( -1 ) );
        node.get( 0 ).fail = 0;
        return ;
    }

    protected static void addNode( ArrayList<Integer> a ){
        
        int p = 0;
        for( int idx : a ){
            if( node.get( p ).next[ idx ] == 0 ){
                node.get( p ).next[ idx ] = ++ tot;
                node.add( new Node( p ) );
            }
            p = node.get( p ).next[ idx ];
        }
        node.get( p ).isEnd = true;
        
        return ;
    }

    //construct fail pointer
    protected static void constructFail(){
        int p , vp;
        LinkedList<Integer> q = new LinkedList<Integer>();
        LinkedList<Integer> vq = new LinkedList<Integer>();

        q.add( 0 );
        vq.add( 0 );
        do{
            p = q.remove();
            vp = vq.remove();
            //find p's fail pointer
            if( p > 0 ){//ensure p is not root
                node.get( p ).fail = 0;
                for( int fp = node.get( node.get( p ).parent ).fail ; ; fp = node.get( fp ).fail ){
                    if( node.get( fp ).next[ vp ] > 0 && fp != node.get( p ).parent ){
                        node.get( p ).fail = node.get( fp ).next[ vp ];
                        break;
                    }
                    else if( fp == 0 )
                        break;
                }
            }
            //add queue
            for( int i = 0 ; i < 10 ; ++ i )
                if( node.get( p ).next[ i ] > 0 ){
                    q.add( node.get( p ).next[ i ] );
                    vq.add( i );
                }
            
        }while( q.size() > 0 );
        return ;
    }
    
    protected static int PatternMatchCount( String Text ){
        
        ArrayList<Integer> sText = transCode( Text );
        int pt = 0;
        int PMCnt = 0;//the number of occured spam word
        Log.v( "PMC" , Integer.toString(sText.size()) );
        Log.v( "PMC" , "node size : " + Integer.toString(node.size()) );
        
        for( Integer i : sText ){
           if( node.get( pt ).next[ i ] > 0 )
                pt = node.get( pt ).next[ i ];
            else
                while( node.get( pt ).next[ i ] == 0 && pt > 0 ){
                    pt = node.get( pt ).fail;
                    if( node.get( pt ).next[ i ] > 0 ){
                        pt = node.get( pt ).next[ i ];
                        break;
                    }
                }
            if( node.get( pt ).isEnd == true ){
                ++ PMCnt;
                pt = 0;
            }
        }
        Log.v( "PMC" , "pattern number : " + Integer.toString(PMCnt) );
        return PMCnt;
    }

	/**
	 * read word list from internal storage
	 * constructAutomata 
	 */
	private static void readWordList( Context mContext ){
		//File in = new File(Environment.getExternalStorageDirectory() + fileName );
		File WordlistFile = new File( mContext.getFilesDir() , fileName );
		Log.v( TAG , WordlistFile.toString() );
		
        initRoot();

        try {
        	if( !WordlistFile.exists() ){//if wordlist file doesn't exist, create from default file
        		WordlistFile.createNewFile();
        		BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( WordlistFile ));
        		//write standard word list in source code
        		for( String t : DefaultSpamWords.SpamWordList ){
        			out.write( t.getBytes() , 0 , t.getBytes().length );
        		}
        		out.close();
        	}
    		Scanner in = new Scanner( new BufferedReader( new FileReader( WordlistFile ) ) );
    		while( in.hasNext() )
                addNode( transCode( new String( in.next() )) );
            in.close();
    		constructFail();
    		Log.v( TAG , Integer.toString(node.size()) );
        } catch (IOException e) {
        	Log.v( TAG , e.getMessage() );
        }
        return ;
	}
	
	protected static void init( Context mContext ){
		readWordList( mContext );
		
		SMSFilter.mContext = mContext;
		
		Toast.makeText( mContext , "word list read", Toast.LENGTH_SHORT).show();

		return ;
	}
	
	private static boolean notInContact( String address ){
	    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	    //ContentResolver cr = mContext.getContentResolver();
	    //Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
	    String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
	                        ContactsContract.CommonDataKinds.Phone.NUMBER };
	    Cursor names = mContext.getContentResolver().query(uri, projection, null, null, null);
	    int indexName = names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
	    int indexNumber = names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
	    names.moveToFirst();
	    Log.v( TAG , "enter contact reader...");
	    do {
	       String name  = names.getString(indexName);
	       Log.e("Name new:", name);
	       String number = names.getString(indexNumber);
	       Log.e("Number new:","::"+number);
	       Log.v(TAG, address + "|" + number + "|" );
	       if( address.compareTo(number) == 0 ){
	    	   Log.v(TAG, "equal");
	    	   return false;
	       }
	    	   
	    } while ( names.moveToNext() );
	    return true;
	}
	
	/**
	 * 
	 * @param address
	 * sms address
	 * @param msg
	 * sms message
	 * @return
	 * true if spam spotted
	 */
	protected static boolean isSpam( String address , String msg ){
		Log.v( "isSpam" , "node size:" + Integer.toString( node.size() ) );
		//TODO add trigger - address not in addressbook
		if( PatternMatchCount( msg ) >= AvarageSpamWordCnt || notInContact( address ) == true )
			return true;
		return false;
	}
}
