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

import com.example.justontime.NewRoute.LoadAllSchedule;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link Favorites.OnFragmentInteractionListener} interface
 * to handle interaction events. Use the {@link Favorites#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class Favorites extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	
	public static final String PREFS_NAME = "routes_prefs";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;
	ListView listView ;
	Station destStation;
	Station depStation;
	private ArrayList<Document> schedule = new ArrayList<Document>();
	private boolean finished = false;
	private static Cursor cursor;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment Favorites.
	 */
	// TODO: Rename and change types and number of parameters
	public static Favorites newInstance(String param1, String param2) {
		Favorites fragment = new Favorites();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public Favorites() {
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
		View v = inflater.inflate(R.layout.fragment_favorites, container, false);
		listView = (ListView) v.findViewById(R.id.routes);
		
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
	    int size = settings.getInt("indexPref", 0);
	    ArrayList<String> fullRoute = new ArrayList<String>();
	    for(int i = 0; i < size; i++){
	    	String str = settings.getString("route" + i, "Pas de favori");
		    String[] route = str.split("-");
		    String formatRoute = route[0].split("de")[1].replaceFirst(" ", "") + " - " + route[1].split("de")[1].replaceFirst(" ", "");
		    if(checkIfExist(fullRoute, formatRoute) == false){
		    	fullRoute.add(formatRoute);
		    }
	    }
		
	    
	    LazyAdapter adapter = new LazyAdapter(this.getActivity(), fullRoute);


        // Assign adapter to ListView
        listView.setAdapter(adapter); 
		
        // ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() {

              @Override
              public void onItemClick(AdapterView<?> parent, View view,
                 int position, long id) {
                
               
               // ListView Clicked item value
               String  itemValue = (String) listView.getItemAtPosition(position);
                  
               String departure = "gare de " + itemValue.split(" - ")[0];
               String destination = "gare de " + itemValue.split(" - ")[1];
               
               searchAddress(departure, destination);
              }

         }); 
        
		return v;
	}
	
	public void searchAddress(String departure, String destination){					
			StationsDB stationsDB = new StationsDB(this.getActivity());
	        stationsDB.open();
	        
	        Station sourceStation = stationsDB.getStationWithName(departure);
	        this.setStartStation(sourceStation);
	        Station destStation = stationsDB.getStationWithName(destination);
	        this.setDestStation(destStation); 
	        
	        stationsDB.close();
	        
	        new LoadAllSchedule().execute();
	        
	        while(finished == false){
	        	
	        }         
	        
	        if(schedule != null){
		        for(int i = 0; i < schedule.size(); i++){
		        	NodeList childNodes = schedule.get(i).getElementsByTagName("StopTime").item(0).getChildNodes();
			        String hours = childNodes.item(2).getTextContent();
			        String minutes = childNodes.item(3).getTextContent();			        
		        }
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
	        
	        getActivity().getActionBar().setSelectedNavigationItem(2);
	}
	
	private void setDestStation(Station destStation2) {
		destStation = destStation2;
		
	}

	private void setStartStation(Station sourceStation) {
		depStation = sourceStation;
		
	}
	
	private Station getDestStation() {
		return destStation;
	}

	private Station getStartStation() {
		return depStation;
	}

	private boolean checkIfExist(ArrayList<String> data, String element){		
		
		for(int i = 0; i < data.size(); i++){
			if(element.equals(data.get(i))){
				return true;
			}
		}
		
		return false;
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
