/**<ul>
 * <li>HandlerTuto</li>
 * <li>com.android2ee.tuto.thread.handler.one</li>
 * <li>14 déc. 2011</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage but can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.android2ee.tuto.thread.handler.one;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to show how to manage properly a Handler:
 *        <ul>
 *        <li>Using AtomicBoolean</li>
 *        <li></li>
 *        </ul>
 */
public class HandlerTutoActivity extends Activity {
	/******************************************************************************************/
	/** Managing the Handler and the Thread *************************************************/
	/******************************************************************************************/
	/**
	 * The Handler
	 */
	private Handler handler;
	/**
	 * The atomic boolean to set the thread run
	 */
	private AtomicBoolean isThreadRunnning = new AtomicBoolean();
	/**
	 * The atomic boolean to set the thread pause
	 */
	private AtomicBoolean isThreadPausing = new AtomicBoolean();
	/**
	 * The thread that update the progressbar
	 */
	Thread backgroundThread;
	/******************************************************************************************/
	/** Others attributes **************************************************************************/
	/******************************************************************************************/
	/**
	 * The string for the log
	 */
	private final static String TAG = "HandlerTutoActivity";
	/**
	 * The ProgressBar
	 */
	private ProgressBar progressBar;
	/**
	 * The way the progress bar increment
	 */
	private boolean reverse = false;

	/******************************************************************************************/
	/** Managing the activity **************************************************************************/
	/******************************************************************************************/

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Instantiate the progress bar
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setMax(100);
		// handler definition
		handler = new Handler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.d(TAG, "handle message called ");
				// be sure the handler is running before doing something
				if (isThreadRunnning.get()) {
					Log.w(TAG, "handle message calls updateProgress ");
					updateProgress();
				}
			}
		};
		// use a random double to give a name to the thread
		final double random = Math.random();
		// Define the Thread and the link with the handler
		backgroundThread = new Thread(new Runnable() {
			/**
			 * The message exchanged between this thread and the handler
			 */
			Message myMessage;

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				try {
					Log.d(TAG, "NewThread " + random);
					while (isThreadRunnning.get()) {
						Log.d(TAG, "Thread isThreadRunnning true " + random);
						if (isThreadPausing.get()) {
							Log.d(TAG, "Thread isThreadPausing true " + random);
							// When pausing just sleep 2 seconds
							Thread.sleep(2000);
						} else {
							Log.d(TAG, "Thread isThreadPausing false " + random);
							// For example sleep 1 second
							Thread.sleep(100);
							// Send the message to the handler (the
							// handler.obtainMessage is more
							// efficient that creating a message from scratch)
							// create a message, the best way is to use that
							// method:
							myMessage = handler.obtainMessage();
							Bundle data= new Bundle();
							data.putString("aKey", "aValue");
							myMessage.setData(data);
							// then send the message to the handler
							handler.sendMessage(myMessage);
						}
					}
				} catch (Throwable t) {
					// just end the background thread
				}
			}
		});
		backgroundThread.setName("HandlerTutoActivity " + random);
		// Initialize the threadSafe booleans
		isThreadRunnning.set(true);
		//start the thread
		backgroundThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	protected void onDestroy() {
		Log.i(TAG, "onDestroy called");
		// kill the thread
		isThreadRunnning.set(false);
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	protected void onPause() {
		Log.i(TAG, "onPause called");
		// and don't forget to stop the thread that redraw the xyAccelerationView
		isThreadPausing.set(true);
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		Log.i(TAG, "onResume called");
		// and don't forget to relaunch the thread that redraw the xyAccelerationView
		isThreadPausing.set(false);
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		//Save the state of the reverse boolean
		outState.putBoolean("reverse", reverse);
		//then save the others GUI elements state
		super.onSaveInstanceState(outState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		//Restore the state of the reverse boolean
		reverse = savedInstanceState.getBoolean("reverse");
		//then restore the others GUI elements state
		super.onRestoreInstanceState(savedInstanceState);
	}

	/******************************************************************************************/
	/** Private methods **************************************************************************/
	/******************************************************************************************/
	/**
	 * The method that update the progressBar
	 */
	private void updateProgress() {
		Log.v(TAG, "updateProgress called  ");
		// get the current value of the progress bar
		int progress = progressBar.getProgress();
		// if the max is reached then reverse the progressbar's progress
		// if the 0 is reached then set the progressbar's progress normal
		if (progress == progressBar.getMax()) {
			reverse = true;
		} else if (progress == 0) {
			reverse = false;
		}
		// increment the progress bar according to the reverse boolean
		if (reverse) {
			progressBar.incrementProgressBy(-1);
		} else {
			progressBar.incrementProgressBy(1);
		}
	}
}