package net.codechunk.speedofsound;

import net.codechunk.speedofsound.util.AppPreferences;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * Speed and volume preferences screen.
 */
public class PreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener
{
	/**
	 * Logging tag.
	 */
	private static final String TAG = "PreferencesActivity";

	/**
	 * Load preferences and prepare conversions.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// activate the up functionality on the action bar
		ActionBar ab = this.getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);

		// sadly, the newer fragment preference API is
		// not yet in the support library.
		addPreferencesFromResource(R.xml.preferences);

		// register change listener
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
	}

	/**
	 * Convert stored preferences when the speed units change.
	 */
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	{
		Log.v(TAG, "Preferences " + key);

		if (key.equals("low_speed_localized") || key.equals("high_speed_localized"))
		{
			// update the internal native speeds
			AppPreferences.updateNativeSpeeds(prefs);
		}
		else if (key.equals("speed_units"))
		{
			// convert localized speeds from their internal values on unit
			// change
			AppPreferences.updateLocalizedSpeeds(prefs);
		}

	}

	/**
	 * Handle the home button press on the action bar.
	 */
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				Intent intent = new Intent(this, SpeedActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
		}
		return true;
	}
}
