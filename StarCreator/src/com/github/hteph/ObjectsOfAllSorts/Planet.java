package com.github.hteph.ObjectsOfAllSorts;

import java.util.ArrayList;

public class Planet extends OrbitalObjects {
	

	
	
	private double radius;
	private double gravity;
	private double density;
	private double orbitalPeriod;
	private double axialTilt;
	private double eccentricity;
	private boolean tidelocked = false;
	private double rotationalPeriod;
	private String geoComposition;
	private double tectonicActivity;
	private double magneticField;
	private int baseTemperature;
	private String hydrosphereStructure;
	private int hydrosphere;
	private int waterVaporFactor;
	private ArrayList<String> atmoshericComposition = new ArrayList<String>();
	private double atmoPressure;
	private double albedo;
	private double greenhouseFactor;
	private double surfaceTemp;
	private double[] rangeBandTemperature =new double[10];
	private double[] rangeBandTempSummer =new double[10];
	private double[] rangeBandTempWinter =new double[10];
	private double nightTempMod;
	private double dayTempMod;
	
	
	public Planet(String name, String description, double mass, double radius, double gravity, double density,
			double orbitalPeriod, double axialTilt, double eccentricity, boolean tidelocked, double rotationalPeriod,
			String geoComposition, double tectonicActivity, double magneticField, int baseTemperature,
			String hydrosphereStructure, int hydrosphere, int waterVaporFactor, ArrayList<String> atmoshericComposition,
			double atmoPressure, double albedo, double greenhouseFactor, double surfaceTemp,
			double[] rangeBandTemperature, double[] rangeBandTempSummer, double[] rangeBandTempWinter,
			double nightTempMod, double dayTempMod) {
		
		super(name, description, mass);
		this.radius = radius;
		this.gravity = gravity;
		this.density = density;
		this.orbitalPeriod = orbitalPeriod;
		this.axialTilt = axialTilt;
		this.eccentricity = eccentricity;
		this.tidelocked = tidelocked;
		this.rotationalPeriod = rotationalPeriod;
		this.geoComposition = geoComposition;
		this.tectonicActivity = tectonicActivity;
		this.magneticField = magneticField;
		this.baseTemperature = baseTemperature;
		this.hydrosphereStructure = hydrosphereStructure;
		this.hydrosphere = hydrosphere;
		this.waterVaporFactor = waterVaporFactor;
		this.atmoshericComposition = atmoshericComposition;
		this.atmoPressure = atmoPressure;
		this.albedo = albedo;
		this.greenhouseFactor = greenhouseFactor;
		this.surfaceTemp = surfaceTemp;
		this.rangeBandTemperature = rangeBandTemperature;
		this.rangeBandTempSummer = rangeBandTempSummer;
		this.rangeBandTempWinter = rangeBandTempWinter;
		this.nightTempMod = nightTempMod;
		this.dayTempMod = dayTempMod;
	}
	
	

}
