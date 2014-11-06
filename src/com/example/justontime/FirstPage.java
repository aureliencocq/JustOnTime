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

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FirstPage extends ActionBarActivity implements LocationListener {	
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_page);
		
		StationsDB stationsDB = new StationsDB(this);
        stationsDB.open();
        
        DESTINATIONS = stationsDB.getAllStationsName();
        
        stationsDB.close();
		
     // Get a reference to the AutoCompleteTextView in the layout
     		final AutoCompleteTextView textViewDest = (AutoCompleteTextView) findViewById(R.id.destination);
     		final AutoCompleteTextView textViewDep = (AutoCompleteTextView) findViewById(R.id.departure);
     		// Get the string array		
     		// Create the adapter and set it to the AutoCompleteTextView 
     		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, DESTINATIONS);
     		textViewDest.setAdapter(adapter);
     		textViewDep.setAdapter(adapter);
     		textViewDest.addTextChangedListener(new TextWatcher() {

     		    @Override
     		    public void onTextChanged(CharSequence s, int start, int before, int count) {
     		    	String val = textViewDest.getText() + "";
     				int test = adapter.getPosition(val);
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
     		    	String val = textViewDep.getText() + "";
     				int test = adapter.getPosition(val);
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
		
	    populateTextViews();
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.show();
	    
	}
	
	private void populateTextViews(){
		//style of textview testAddress
		TextView tv = (TextView)findViewById(R.id.testAddress);
		Spannable wordtoSpan = new SpannableString("Ajouter une gare de Destination :");        
		wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#3F3F3F")), 0, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#6E267B")), 20, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(wordtoSpan);
			    
		//style of textview departAddress
		 TextView tv2 = (TextView)findViewById(R.id.departAddress);
		Spannable wordtoSpan2 = new SpannableString("Ajouter une gare de Départ :");        
		wordtoSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#3F3F3F")), 0, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		wordtoSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#6E267B")), 20, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv2.setText(wordtoSpan2);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.first_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,
					this);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0,
				this);
	}
	
	@Override
	protected void onPause() {
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
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		TextView address = (TextView)findViewById(R.id.testAddress);
		
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
				   
				   StationsDB stationsDB = new StationsDB(this);
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
			       
			       ((EditText)findViewById(R.id.departure)).setText(currentStation.getName());
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
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		String msg = String.format(
				getResources().getString(R.string.provider_enabled), provider);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		String msg = String.format(
				getResources().getString(R.string.provider_disabled), provider);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
	}
	
	public void searchAddress(View v){
		if(((EditText)findViewById(R.id.destination)).getText() != null){
			String destination = ((EditText)findViewById(R.id.destination)).getText().toString();
			String source = ((EditText)findViewById(R.id.departure)).getText().toString();
			StationsDB stationsDB = new StationsDB(this);
	        stationsDB.open();
	        
	        //this.setStartStation(stationsDB.getStationWithName(source));
	        this.setStartStation(stationsDB.getStationWithName(source));
	        this.setDestStation(stationsDB.getStationWithName(destination)); 
	        Log.d("code : ", this.getStartStation().getCode());
	        
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
