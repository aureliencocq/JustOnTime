package com.example.justontime;

import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;

public class FirstPage extends FragmentActivity implements ActionBar.TabListener, NewRoute.OnFragmentInteractionListener, Favorites.OnFragmentInteractionListener, NextTrains.OnFragmentInteractionListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Nouveau", "Favoris", "Prochains trains" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_first_page);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page 
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabSelected(android.app.ActionBar.Tab tab,
			FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
		
	}

	@Override
	public void onTabUnselected(android.app.ActionBar.Tab tab,
			FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(android.app.ActionBar.Tab tab,
			FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		
		
	}
	
}
