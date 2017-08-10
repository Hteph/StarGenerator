package com.github.hteph.ObjectsOfAllSorts;

import java.util.ArrayList;

public class AsteroidBelt {

	private String name;
	private String description;
	private double mass;	
	private double orbitDistance;
	private double eccentricity;
	private String asterioidBeltType;
	private double asteroidBeltWidth;


	// Constructor ----------------------------------------------
	public AsteroidBelt(String name, String description, double orbitDistance) {
		this.name = name;
		this.description = description;
		this.orbitDistance = orbitDistance;

	}
	//Methods --------------------------------------------------

	@Override
	public String toString() {
		return "Ateroid Belt " + name + ": " + description;
	}	

	//Internal Methods ----------------------------------------

	// Getters and Setters -------------------------------------

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}	

	public double getEccentricity() {
		return eccentricity;
	}

	public void setEccentricity(double eccentricity) {
		this.eccentricity = eccentricity;
	}

	public double getOrbitDistance() {
		return orbitDistance;
	}

	public String getAsterioidBeltType() {
		return asterioidBeltType;
	}

	public void setAsterioidBeltType(String asterioidBeltType) {
		this.asterioidBeltType = asterioidBeltType;
	}

	public double getAsteroidBeltWidth() {
		return asteroidBeltWidth;
	}

	public void setAsteroidBeltWidht(double asteroidBeltWidth) {
		this.asteroidBeltWidth = asteroidBeltWidth;
	}



	


}
