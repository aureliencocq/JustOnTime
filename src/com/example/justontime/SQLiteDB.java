package com.example.justontime;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDB extends SQLiteOpenHelper{
	private static final String TABLE_STATIONS = "table_stations";
	private static final String COL_ID = "ID";
	private static final String COL_NAME = "Name";
	private static final String COL_CODE = "Code";
	private static final String COL_COORDX = "CoordX";
	private static final String COL_COORDY = "CoordY";
 
	private static final String CREATE_BDD = "CREATE TABLE " + TABLE_STATIONS + " ("
	+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME + " TEXT NOT NULL, "
	+ COL_CODE + " TEXT NOT NULL, " + COL_COORDX + " INTEGER, " + COL_COORDY + " INTEGER);";
 
	public SQLiteDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		//on cr�� la table � partir de la requ�te �crite dans la variable CREATE_BDD
		db.execSQL(CREATE_BDD);
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//On peut fait ce qu'on veut ici moi j'ai d�cid� de supprimer la table et de la recr�er
		//comme �a lorsque je change la version les id repartent de 0
		db.execSQL("DROP TABLE " + TABLE_STATIONS + ";");
		onCreate(db);
	}
}

