package com.example.justontime;

import java.util.ArrayList;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link NextTrains.OnFragmentInteractionListener} interface
 * to handle interaction events. Use the {@link NextTrains#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class NextTrains extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	
	public static final String PREFS_NAME = "routes_prefs";
	private OnFragmentInteractionListener mListener;
	
	ListView listView ;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment NextTrains.
	 */
	// TODO: Rename and change types and number of parameters
	public static NextTrains newInstance(String param1, String param2) {
		NextTrains fragment = new NextTrains();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public NextTrains() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_next_trains, container, false);
		listView = (ListView) v.findViewById(R.id.trains);
		
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
	    int size = settings.getInt("indexTrain", 0);
	    ArrayList<String> fullDate = new ArrayList<String>();
	    for(int i = 0; i < size; i++){
	    	String str = settings.getString("date" + i, "Pas de date");
	    	String time = settings.getString("time" + i, "Pas d'heure");	    	
		    String[] train = str.split(" ");
		    Log.d("debug : ", train[0]);
		    String formatDate = train[2] + " " + getMonth(Integer.parseInt(train[1])) + " " + train[0] + " à " + time;		    
		    fullDate.add(formatDate);
	    }
	    
	    LazyAdapter adapter = new LazyAdapter(this.getActivity(), fullDate);


        // Assign adapter to ListView
        listView.setAdapter(adapter); 
		
        // ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() {

              @Override
              public void onItemClick(AdapterView<?> parent, View view,
                 int position, long id) {
                                              
              }

         }); 
        
		return v;
	}

	private String getMonth(int month) {
		String monthString;
		switch (month) {
	        case 1:  monthString = "Janvier";       break;
	        case 2:  monthString = "Février";      break;
	        case 3:  monthString = "Mars";         break;
	        case 4:  monthString = "Avril";         break;
	        case 5:  monthString = "Mai";           break;
	        case 6:  monthString = "Juin";          break;
	        case 7:  monthString = "Juillet";          break;
	        case 8:  monthString = "Août";        break;
	        case 9:  monthString = "Septembre";     break;
	        case 10: monthString = "Octobre";       break;
	        case 11: monthString = "Novembre";      break;
	        case 12: monthString = "Decembre";      break;
	        default: monthString = "Mois invalide"; break;
		}
		return monthString;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

}
