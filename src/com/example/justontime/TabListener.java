package com.example.justontime;

import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;


public class TabListener<T extends Fragment> extends ActionBarActivity implements ActionBar.TabListener {
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<? extends Fragment> mClass;

    /** Constructor used each time a new tab is created.
      * @param activity  The host Activity, used to instantiate the fragment
      * @param tag  The identifier tag for the fragment
      * @param class1  The fragment's Class, used to instantiate the fragment
      */
    public TabListener(Activity activity, String tag, Class<? extends Fragment> class1) {
        mActivity = activity;
        mTag = tag;
        mClass = class1;
    }

    /* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized    	
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            //ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            //ft.attach(mFragment);
            getActionBar().setSelectedNavigationItem(tab.getPosition());
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            //ft.detach(mFragment);
        }
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {				
		if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
            getActionBar().setSelectedNavigationItem(tab.getPosition());
        }
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
