package com.example.justontime;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	
	private LocationManager lm;
	private String cityPosition;
	
	private double latitude;
	private double longitude;
	private double altitude;
	private float accuracy;
	
	private Document schedule;
	Station startStation;
	Station destStation;
	
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
		if(((EditText)getView().findViewById(R.id.destination)).getText() != null){
			String destination = ((EditText)getView().findViewById(R.id.destination)).getText().toString();
			String source = ((EditText)getView().findViewById(R.id.departure)).getText().toString();
			StationsDB stationsDB = new StationsDB(this.getActivity());
	        stationsDB.open();
	        
	        //this.setStartStation(stationsDB.getStationWithName(source));
	        this.setStartStation(stationsDB.getStationWithName(source));
	        this.setDestStation(stationsDB.getStationWithName(destination)); 
	        
	        stationsDB.close();
	        
	        new LoadAllSchedule().execute();
	        
	        while(schedule == null){
	        	
	        }         
	        
	        NodeList childNodes = schedule.getElementsByTagName("StopTime").item(0).getChildNodes();
	        String hours = childNodes.item(2).getTextContent();
	        String minutes = childNodes.item(3).getTextContent();
	        
	        Log.d("Time : ", hours + " - " + minutes);
		}
	}
	
	public void setSchedule(Document doc){
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

	public Document getSchedule() {
		return schedule;
	}



	class LoadAllSchedule extends AsyncTask<String, String, String> {
		 
        /**
         * getting All products from url
         * @return 
         * */
        protected String doInBackground(String... args) {        	
        	DefaultHttpClient httpClient = new DefaultHttpClient(); 
        	StringBuilder sb = new StringBuilder();
        	sb.append("http://ms.api.ter-sncf.com/?action=nextdeparture&StopAreaExternalCode=");
        	sb.append(getStartStation().getCode());
        	sb.append("&DestinationExternalCode=");
        	sb.append(getDestStation().getCode());
        	sb.append("&Time=17|15");
        	//sb.append(getTime());
        	sb.append("&Date=2014|11|15");
        	//sb.append(getDate());        	
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
				setSchedule(doc);				
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
			return doc.toString();
			
        }
    }

}
