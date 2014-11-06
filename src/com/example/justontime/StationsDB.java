package com.example.justontime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StationsDB {
	private static final int VERSION_BDD = 1;
	private static final String NOM_BDD = "stations.db";
 
	private static final String TABLE_STATIONS = "table_stations";
	private static final String COL_ID = "ID";
	private static final int NUM_COL_ID = 0;
	private static final String COL_NAME = "Name";
	private static final int NUM_COL_NAME = 1;
	private static final String COL_CODE = "Code";
	private static final int NUM_COL_CODE = 2;
	private static final String COL_COORDX = "CoordX";
	private static final int NUM_COL_COORDX = 3;
	private static final String COL_COORDY = "CoordY";
	private static final int NUM_COL_COORDY = 4;
 
	private SQLiteDatabase bdd;
 
	private SQLiteDB base;
 
	public StationsDB(Context context){
		//On créer la BDD et sa table
		base = new SQLiteDB(context, NOM_BDD, null, VERSION_BDD);		
	}
 
	public void open(){
		//on ouvre la BDD en écriture
		bdd = base.getWritableDatabase();
	}
 
	public void close(){
		//on ferme l'accès à la BDD
		bdd.close();
	}
 
	public SQLiteDatabase getBDD(){
		return bdd;
	}
 
	public long insertStation(Station station){
		//Création d'un ContentValues (fonctionne comme une HashMap)
		ContentValues values = new ContentValues();
		//on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
		values.put(COL_NAME, station.getName());
		values.put(COL_CODE, station.getCode());
		values.put(COL_COORDX, station.getCoordinates()[0]);
		values.put(COL_COORDY, station.getCoordinates()[1]);
		//on insère l'objet dans la BDD via le ContentValues
		return bdd.insert(TABLE_STATIONS, null, values);
	}
 
	public int updateStation(int id, Station station){
		//La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
		//il faut simple préciser quelle livre on doit mettre à jour grâce à l'ID
		ContentValues values = new ContentValues();
		values.put(COL_NAME, station.getName());
		values.put(COL_CODE, station.getCode());
		return bdd.update(TABLE_STATIONS, values, COL_ID + " = " +id, null);
	}
 
	public int removeStationWithID(int id){
		//Suppression d'un livre de la BDD grâce à l'ID
		return bdd.delete(TABLE_STATIONS, COL_ID + " = " +id, null);
	}
 
	public Station getStationWithName(String name){
		//Récupère dans un Cursor les valeur correspondant à une station contenue dans la BDD
		Cursor c = bdd.query(TABLE_STATIONS, new String[] {COL_ID, COL_NAME, COL_CODE, COL_COORDX, COL_COORDY}, COL_NAME + " LIKE \"%" + name + "%\"", null, null, null, null);
		return cursorToStation(c);
	}
	
	public Station[] getAllStations(){		
		Cursor c = bdd.query(TABLE_STATIONS, new String[] {COL_ID, COL_NAME, COL_CODE, COL_COORDX, COL_COORDY}, null, null, null, null, null);
		if(c.moveToFirst()){
			Station[] stations = new Station[c.getCount()];
			int i = 0;
			do {
				Station station;
                station = new Station(c.getString(NUM_COL_NAME), c.getString(NUM_COL_CODE), new int[] {c.getInt(NUM_COL_COORDX), c.getInt(NUM_COL_COORDY)});
                stations[i] = station;
                i++;
            } while (c.moveToNext());			
			
			return stations;
		}
		return null;
	}
	
	public String[] getAllStationsName(){
		Cursor c = bdd.query(TABLE_STATIONS, new String[] {COL_ID, COL_NAME, COL_CODE, COL_COORDX, COL_COORDY}, null, null, null, null, null);
		if(c.moveToFirst()){
			int i = 0;
			String[] stations = new String[c.getCount()];
			do {
				String station;
                station = c.getString(NUM_COL_NAME);
                stations[i] = station;
                i++;
            } while (c.moveToNext());
			
			return stations;
		}
		return null;
	}
 	
	//Cette méthode permet de convertir un cursor en un livre
	private Station cursorToStation(Cursor c){
		//si aucun élément n'a été retourné dans la requête, on renvoie null
		if (c.getCount() == 0)
			return null;
 
		//Sinon on se place sur le premier élément
		c.moveToFirst();
		//On créé un livre
		Station station = new Station();
		//on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
		station.setId(c.getInt(NUM_COL_ID));
		station.setName(c.getString(NUM_COL_NAME));
		station.setCode(c.getString(NUM_COL_CODE));
		int[] coordinates = {c.getInt(NUM_COL_COORDX), c.getInt(NUM_COL_COORDY)};
		station.setCoordinates(coordinates);
		//On ferme le cursor
		c.close();
 
		//On retourne le livre
		return station;
	}
	
	public void deleteAllRows(Context context){
		context.deleteDatabase(NOM_BDD);
	}
}
