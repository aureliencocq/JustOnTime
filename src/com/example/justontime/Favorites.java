package com.example.justontime;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
	Document schedule;

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
	        
	        while(schedule == null){
	        	
	        }         
	        
	        NodeList childNodes = schedule.getElementsByTagName("StopTime").item(0).getChildNodes();
	        String hours = childNodes.item(2).getTextContent();
	        String minutes = childNodes.item(3).getTextContent();
	        
	        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
	        int indexPref = settings.getInt("indexPref", 0);
	        SharedPreferences.Editor editor = settings.edit();
	        String route = sourceStation.getName() + "-" + destStation.getName() + "-" + hours + "-" + minutes;
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
	
	private void setSchedule(Document doc) {
		schedule = doc;
		
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
