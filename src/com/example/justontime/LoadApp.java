package com.example.justontime;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class LoadApp extends ActionBarActivity {
	private Document stations;
	private static String URL_ALL_STATIONS = "http://ms.api.ter-sncf.com/?action=StopAreaList";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_app);
		
		// Loading products in Background Thread
        new LoadAllStations().execute();
        while(stations == null){
        	
        }
        
        StationsDB stationsDB = new StationsDB(this);
        stationsDB.deleteAllRows(this);
        stationsDB = new StationsDB(this);
        stationsDB.open();         
        NodeList nList = stations.getElementsByTagName("StopArea");
        Station currentStation;
        int[] coords = {0, 0};
        for(int i = 0; i < nList.getLength(); i++){
        	Node nNode = nList.item(i);
        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if(eElement.getChildNodes().item(1).getFirstChild() != null){
	                coords[0] = Integer.parseInt(eElement.getChildNodes().item(1).getFirstChild().getTextContent());
	                coords[1] = Integer.parseInt(eElement.getChildNodes().item(1).getLastChild().getTextContent()); 
                }                
                currentStation = new Station(eElement.getAttribute("StopAreaName"), eElement.getAttribute("StopAreaExternalCode"), coords);
                stationsDB.insertStation(currentStation);
            }
        }               
        stationsDB.close();                
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load_app, menu);
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
	
	public Document getStations() {
		return stations;
	}

	public void setStations(Document stations) {
		this.stations = stations;
	}

	class LoadAllStations extends AsyncTask<String, String, String> {
 
        /**
         * getting All products from url
         * @return 
         * */
        protected String doInBackground(String... args) {
        	DefaultHttpClient httpClient = new DefaultHttpClient();            
            HttpGet httpGet = new HttpGet(URL_ALL_STATIONS);

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
			
			/*try {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    is, "UTF-8"));
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	            is.close();
	            stationsInfo = sb.toString();
	        } catch (Exception e) {
	            Log.e("Buffer Error", "Error converting result " + e.toString());
	        }*/
			 
			DocumentBuilder builder;
			Document doc = null;
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				doc = builder.parse(is);
				doc.getDocumentElement().normalize();
				setStations(doc);				
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
