package edu.osu.speedofsound;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class VolumeThread extends Thread
{
	private static final String TAG = "VolumeThread";
	private static final int UPDATE_DELAY = 200; // ms
	private static final int VOLUME_THRESHOLD = 3;
	private static final float APPROACH_RATE = 0.35f;
	private static final int MAX_APPROACH = 8;

	private final Object lock = new Object();
	private AudioManager audioManager;
	private int targetVolume;

	public VolumeThread(Context context)
	{
		this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		this.setName(TAG);
	}

	public void setTargetVolume(int volume)
	{
		Log.v(TAG, "Setting target volume to " + volume);
		synchronized (this.lock)
		{
			this.targetVolume = volume;
		}
	}

	public void run()
	{
		Log.d(TAG, "Thread starting");

		// get the current system volume
		int currentVolume = this.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		this.setTargetVolume(currentVolume);
		Log.d(TAG, "Current volume is " + currentVolume);

		while (!this.isInterrupted())
		{
			// sleep for a while
			try
			{
				Thread.sleep(UPDATE_DELAY);
			}
			catch (InterruptedException e)
			{
				break;
			}

			// safely grab the target volume
			int targetVolume;
			synchronized (this.lock)
			{
				targetVolume = this.targetVolume;
			}

			Log.v(TAG, "Thread-read target is " + targetVolume);

			// don't do anything if the target is already matched
			if (currentVolume == targetVolume)
			{
				Log.v(TAG, "Volumes matched, skipping");
				continue;
			}

			// if the target is close enough, just use it
			int newVolume = 0;
			if (Math.abs(currentVolume - targetVolume) < VOLUME_THRESHOLD)
			{
				newVolume = targetVolume;
			}

			// otherwise, gently approach the target
			else
			{
				// approach the target, but not more quickly than the max
				int approach = Math.min((int) ((targetVolume - currentVolume) * APPROACH_RATE), MAX_APPROACH);
				newVolume = currentVolume + approach;
			}

			Log.v(TAG, "New volume is " + newVolume);

			// set the volume
			this.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
			currentVolume = newVolume;
		}

		Log.d(TAG, "Thread exiting");
	}

}