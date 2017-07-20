package com.github.hteph.ObjectsOfAllSorts;

public abstract class StellarObject implements SpaceObjectsInterface {
	
	private String name;
	private String description;
	protected double mass;


	
	
	
	//Constructor -------------------------------------------------------------

	public StellarObject(String name, String description, double mass) {
		super();
		this.name = name;
		this.description = description;
		this.mass = mass;
	}
	
	

	@Override
	public String getName() {
	
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;		
	}

	public double getMass() {
		return mass;
	}

	
}
