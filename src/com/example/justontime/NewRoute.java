package com.example.justontime;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link NewRoute.OnFragmentInteractionListener} interface
 * to handle interaction events. Use the {@link NewRoute#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class NewRoute extends Fragment implements LocationListener{
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	private static Cursor cursor;
	
	public static final String PREFS_NAME = "routes_prefs";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	
	private LocationManager lm;
	private String cityPosition;
	
	private double latitude;
	private double longitude;
	private double altitude;
	private float accuracy;
	
	private ArrayList<Document> schedule = new ArrayList<Document>();
	private ArrayList<String> times = new ArrayList<String>();
	private ArrayList<String> dates = new ArrayList<String>();
	Station startStation;
	Station destStation;
	private boolean finished = false;
	
	private String[] DESTINATIONS;

	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment NewRoute.
	 */
	// TODO: Rename and change types and number of parameters
	public static NewRoute newInstance(String param1, String param2) {
		NewRoute fragment = new NewRoute();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public NewRoute() {
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
		View V =  inflater.inflate(R.layout.fragment_new_route, container, false);
		((Button)V.findViewById(R.id.SearchButton)).setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(final View v) {
	            searchAddress(v);
	        }
	    });
		StationsDB stationsDB = new StationsDB(this.getActivity());
        stationsDB.open();
        
        DESTINATIONS = stationsDB.getAllStationsName();
        
        stationsDB.close();       
        
     // Get a reference to the AutoCompleteTextView in the layout
 		final AutoCompleteTextView textViewDest = (AutoCompleteTextView) V.findViewById(R.id.destination);
 		final AutoCompleteTextView textViewDep = (AutoCompleteTextView) V.findViewById(R.id.departure);
 		// Get the string array		
 		// Create the adapter and set it to the AutoCompleteTextView 
 		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, DESTINATIONS);
 		textViewDest.setAdapter(adapter);
 		textViewDep.setAdapter(adapter);
 		textViewDest.addTextChangedListener(new TextWatcher() {

 		    @Override
 		    public void onTextChanged(CharSequence s, int start, int before, int count) {     		    	     				
 				 if(adapter.getCount() == 0){
 					 textViewDest.setError("Destination invalide");
 				 }
 				 else{
 					 textViewDest.setError(null);
 				 }
 		    }

 		    @Override
 		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {                

 		    }

 		    @Override
 		    public void afterTextChanged(Editable s) {

 		    }
 		});		
 		
 		textViewDep.addTextChangedListener(new TextWatcher() {

 		    @Override
 		    public void onTextChanged(CharSequence s, int start, int before, int count) {     		    			
 				 if(adapter.getCount() == 0){
 					 textViewDep.setError("Départ invalide");
 				 }
 				 else{
 					 textViewDep.setError(null);
 				 }
 		    }

 		    @Override
 		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {                

 		    }

 		    @Override
 		    public void afterTextChanged(Editable s) {

 		    }
 		});
 		
 		populateTextViews(V);
 		
 		return V;
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
	
	private void populateTextViews(View V){
		//style of textview testAddress
		TextView tv = (TextView)V.findViewById(R.id.testAddress);
		Spannable wordtoSpan = new SpannableString("Ajouter une gare de Destination :");        
		wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#3F3F3F")), 0, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#6E267B")), 20, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(wordtoSpan);
			    
		//style of textview departAddress
		 TextView tv2 = (TextView)V.findViewById(R.id.departAddress);
		Spannable wordtoSpan2 = new SpannableString("Ajouter une gare de Départ :");        
		wordtoSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#3F3F3F")), 0, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		wordtoSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#6E267B")), 20, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv2.setText(wordtoSpan2);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity();
		lm = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,
					this);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0,
				this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		lm.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		altitude = location.getAltitude();
		accuracy = location.getAccuracy();

		String msg = String.format(
				getResources().getString(R.string.new_location), latitude,
				longitude, altitude, accuracy);
		Toast.makeText(this.getActivity(), msg, Toast.LENGTH_LONG).show();
		
		Geocoder geocoder = new Geocoder(this.getActivity(), Locale.getDefault());
		TextView address = (TextView)getView().findViewById(R.id.testAddress);
		
		try {
			  List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			 
			  if(addresses != null) {
				   Address returnedAddress = addresses.get(0);
				   StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
				   for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
					   strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				   }				   
				   address.setText(strReturnedAddress.toString());
				   cityPosition = returnedAddress.getLocality();
				   
				   StationsDB stationsDB = new StationsDB(this.getActivity());
			       stationsDB.open();
			       Station currentStation = stationsDB.getStationWithName("gare de " + cityPosition);
			       			       			    	   			      
			       if(currentStation == null){
			    	   Station[] stations = stationsDB.getAllStations();
			    	   currentStation = stations[0];
			    	   int[] gap = {(int)(latitude - currentStation.getCoordinates()[0]), (int)(longitude - currentStation.getCoordinates()[1])};
			    	   int[] tmpGap = new int[2];
			    	   for(int i = 1; i < stations.length; i++){				    		   
			    		   tmpGap[0] = (int)(latitude - stations[i].getCoordinates()[0]);
			    		   tmpGap[1] = (int)(longitude - stations[i].getCoordinates()[1]);
			    		   if(tmpGap[0] < gap[0] && tmpGap[1] < gap[1]){
			    			   currentStation = stations[i];
			    			   gap = tmpGap;
			    		   }
			    	   }
			       }
			       
			       ((EditText)getView().findViewById(R.id.departure)).setText(currentStation.getName());
			  }
			  else{
				  address.setText("no address found !");
			  }
			 } catch (IOException e) { 
				 e.printStackTrace();
				 address.setText("cannot get address !");
			 }
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		String newStatus = "";
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			newStatus = "OUT_OF_SERVICE";
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			newStatus = "TEMPORARILY_UNAVAILABLE";
			break;
		case LocationProvider.AVAILABLE:
			newStatus = "AVAILABLE";
			break;
		}
		String msg = String.format(
				getResources().getString(R.string.provider_disabled), provider,
				newStatus);
		Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		String msg = String.format(
				getResources().getString(R.string.provider_enabled), provider);
		Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		String msg = String.format(
				getResources().getString(R.string.provider_disabled), provider);
		Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
		
	}
	
	public void searchAddress(View v){
		if(((EditText)getView().findViewById(R.id.destination)).getText() != null && ((EditText)getView().findViewById(R.id.departure)).getText() != null){
			String destination = ((EditText)getView().findViewById(R.id.destination)).getText().toString();
			String source = ((EditText)getView().findViewById(R.id.departure)).getText().toString();
			StationsDB stationsDB = new StationsDB(this.getActivity());
	        stationsDB.open();
	        
	        Station sourceStation = stationsDB.getStationWithName(source);
	        this.setStartStation(sourceStation);
	        Station destStation = stationsDB.getStationWithName(destination);
	        this.setDestStation(destStation); 
	        
	        stationsDB.close();
	        
	        new LoadAllSchedule().execute();
	        
	        while(finished == false){
	        	
	        }         
	        	        
	        for(int i = 0; i < schedule.size(); i++){
	        	NodeList childNodes = schedule.get(i).getElementsByTagName("StopTime").item(0).getChildNodes();
		        times.add(childNodes.item(2).getTextContent() + ":" + childNodes.item(3).getTextContent());			        
		        
	        }	        	        
	        
	        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
	        int indexPref = settings.getInt("indexPref", 0);
	        SharedPreferences.Editor editor = settings.edit();
	        String route = sourceStation.getName() + "-" + destStation.getName();
	        editor.putString("route" + indexPref, route);
	        indexPref++;
	        editor.putInt("indexPref", indexPref);
	        // Commit the edits!
	        editor.commit();
	        
	        
	        
	        ((EditText)getView().findViewById(R.id.destination)).setText("");
	        ((EditText)getView().findViewById(R.id.departure)).setText("");
	        
	        getActivity().getActionBar().setSelectedNavigationItem(2);
		}
		else{
			Toast.makeText(this.getActivity(), "Trajet innexistant", Toast.LENGTH_LONG).show();
		}
	}
	
	public void setSchedule(ArrayList<Document> doc){
		schedule = doc;
	}		
	
	public LocationManager getLm() {
		return lm;
	}

	public void setLm(LocationManager lm) {
		this.lm = lm;
	}

	public String getCityPosition() {
		return cityPosition;
	}

	public void setCityPosition(String cityPosition) {
		this.cityPosition = cityPosition;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public Station getStartStation() {
		return startStation;
	}

	public void setStartStation(Station startStation) {
		this.startStation = startStation;
	}

	public Station getDestStation() {
		return destStation;
	}

	public void setDestStation(Station destStation) {
		this.destStation = destStation;
	}

	public ArrayList<Document> getSchedule() {
		return schedule;
	}



	class LoadAllSchedule extends AsyncTask<String, String, String> {
		 
        /**
         * getting All products from url
         * @return 
         * */
        protected String doInBackground(String... args) {
        	for(int i = 0; i < 5; i++){
        		String day = getANextDay(i);
            	String fullDate = getLastEventOfDate(day);
            	
            	if(fullDate != null){            		
            		String date = fullDate.split(",")[0];
                	String time = fullDate.split(",")[1];
                	dates.add(formatDate(date));
                	DefaultHttpClient httpClient = new DefaultHttpClient(); 
                	StringBuilder sb = new StringBuilder();
                	sb.append("http://ms.api.ter-sncf.com/?action=nextdeparture&StopAreaExternalCode=");
                	sb.append(getStartStation().getCode());
                	sb.append("&DestinationExternalCode=");
                	sb.append(getDestStation().getCode());
                	sb.append("&Time=");
                	sb.append(formatTime(time));
                	sb.append("&Date=");
                	sb.append(formatDate(date));        	
                	sb.append("&nbstop=1");
                	
                    HttpGet httpGet = null;		
                    URL url;
        			try {
        				url = new URL(sb.toString());
        				URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());	            
        	            httpGet = new HttpGet(uri);
        			} catch (MalformedURLException e1) {
        				// TODO Auto-generated catch block
        				e1.printStackTrace();
        			} catch (URISyntaxException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}            

                    HttpResponse httpResponse;
                    InputStream is = null;
        			try {
        				httpResponse = httpClient.execute(httpGet);
        				HttpEntity httpEntity = httpResponse.getEntity();
        	            is = httpEntity.getContent();
        			} catch (ClientProtocolException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        			 
        			DocumentBuilder builder;
        			Document doc = null;
        			try {
        				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        				doc = builder.parse(is);
        				doc.getDocumentElement().normalize();
        				schedule.add(doc);			
        			} catch (ParserConfigurationException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        				catch (SAXException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
            	}
            	
        	}
        		
        	finished = true;
        	return schedule.toString();
			
			
        }

		private String formatDate(String date) {
			return date.split(" ")[0] + "|" + date.split(" ")[1] + "|" + date.split(" ")[2];	
		}

		private String formatTime(String time) {
			return time.split(":")[0] + "|" + time.split(":")[1];						
		}
    }
	
	
	/**get the current or a next day with the format  "mm/dd/yy". 0 is for today, 1 for tomorrow etc**/
	private String getANextDay(int nb){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, nb);
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
		Date date = c.getTime();
		String formattedDate = df.format(date);
		return formattedDate;
	}
	
	/****getting last Event of a date  ****/
	private String getLastEventOfDate(String formattedDate){
		Context context = getView().getContext();
		
		Uri l_eventUri;
	    Calendar calendar = Calendar.getInstance();
	    if (Build.VERSION.SDK_INT >= 8) {
	        l_eventUri = Uri.parse("content://com.android.calendar/events");
	    } else {
	        l_eventUri = Uri.parse("content://calendar/events");
	    }
	    ContentResolver contentResolver = context.getContentResolver();

	    String dtstart = "dtstart";
	    String dtend = "dtend";

	    String[] l_projection = new String[] { "title", "dtstart", "dtend" };

	    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
	    Date dateCC;
		
	    try {
			dateCC = formatter.parse(formattedDate);
			calendar.setTime(dateCC);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    long after = calendar.getTimeInMillis();

	    SimpleDateFormat formatterr = new SimpleDateFormat("MM/dd/yy hh:mm:ss");

	    Calendar endOfDay = Calendar.getInstance();
	    Date dateCCC;
		try {
			dateCCC = formatterr.parse(formattedDate +" 23:59:59");
			endOfDay.setTime(dateCCC);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	    cursor = contentResolver.query(l_eventUri, new String[] { "title",
	            "dtstart", "dtend" }, "(" + dtstart + ">" + after + " and "
	            + dtend + "<" + endOfDay.getTimeInMillis() + ")", null,
	            "dtstart ASC");
	    
	    if((cursor != null) && (cursor.getCount() > 0)){
	    	cursor.moveToLast();
			String e_end;
		    
			int e_colEnd = cursor.getColumnIndex(l_projection[2]);
			e_end = getDateTimeStr(cursor.getString(e_colEnd));
			
		    StringBuilder l_displayText = new StringBuilder();
		    l_displayText.append( e_end );
		    
		    System.out.println(l_displayText);
		    /*return the date of the last event with the format : 2014 nov. 11, 17:00:00*/
		    return l_displayText.toString(); 
	    }
	    else{
	    	return null;
	    }
    }
	
	/**utility functions in order to format the date**/
	private static final String DATE_TIME_FORMAT = "yyyy MM dd,HH:mm:ss";
    public static String getDateTimeStr(int p_delay_min) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		if (p_delay_min == 0) {
			return sdf.format(cal.getTime());
		} else {
			Date l_time = cal.getTime();
			l_time.setMinutes(l_time.getMinutes() + p_delay_min);
			return sdf.format(l_time);
		}
	}
    public static String getDateTimeStr(String p_time_in_millis) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
    	Date l_time = new Date(Long.parseLong(p_time_in_millis));
    	return sdf.format(l_time);
    }

}
