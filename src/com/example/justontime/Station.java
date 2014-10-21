package com.example.justontime;

public class Station {
	private int id;
	private String name;
	private String code;
	private int[] coordinates;	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int[] getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(int[] coordinates) {
		this.coordinates = coordinates;
	}
	public Station(String name, String code, int[] coordinates) {
		super();
		this.name = name;
		this.code = code;
		this.coordinates = coordinates;
	}
	
	public Station(){
		
	}
	
}
