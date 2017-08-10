package com.github.hteph.ObjectsOfAllSorts;

public class OrbitalObjects extends StellarObject {
	
	public OrbitalObjects(String name, String description, double mass) {
		super(name, description, mass);
		// TODO Auto-generated constructor stub
	}


	private double orbitDistance;
	private double orbitaleccentricity;
	private char orbitalObjectClass;
	private StellarObject orbitingAround;
	
	
	
	//Constructor ----------------------------------------------------
	



	public double getOrbitDistance() {
		return orbitDistance;
	}


	public double getOrbitaleccentricity() {
		return orbitaleccentricity;
	}


	public char getOrbitalObjectClass() {
		return orbitalObjectClass;
	}


	public StellarObject getOrbitingAround() {
		return orbitingAround;
	}
	
	
	
	


}
