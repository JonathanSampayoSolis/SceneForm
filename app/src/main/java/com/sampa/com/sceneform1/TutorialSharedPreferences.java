package com.sampa.com.sceneform1;

import android.content.Context;
import android.content.SharedPreferences;

public class TutorialSharedPreferences {
	
	private static final String ATTR_HAS_SHOWN = "ATTR_HAS_SHOW";
	
	private SharedPreferences sharedPreferences;
	
	private TutorialSharedPreferences(Context context) {
		this.sharedPreferences = context.getSharedPreferences(TutorialSharedPreferences.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}
	
	private SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
	
	static boolean mustShow(Context context) {
		TutorialSharedPreferences preferences = new TutorialSharedPreferences(context);
		
		if (ATTR_HAS_SHOWN.equals(preferences.getSharedPreferences().getString(ATTR_HAS_SHOWN, "")))
			return false;
		
		preferences.getSharedPreferences().edit()
				.putString(ATTR_HAS_SHOWN, ATTR_HAS_SHOWN)
				.apply();
		
		return true;
	}
	
}
