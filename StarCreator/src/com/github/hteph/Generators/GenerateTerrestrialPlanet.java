package com.github.hteph.Generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jscience.physics.amount.Amount;

import com.github.hteph.ObjectsOfAllSorts.AmosphericGases;
import com.github.hteph.ObjectsOfAllSorts.Planet;
import com.github.hteph.ObjectsOfAllSorts.Star;
import com.github.hteph.ObjectsOfAllSorts.StellarObject;
import com.github.hteph.Utilities.Dice;

public class GenerateTerrestrialPlanet {


	private double mass;
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
	private String tectonicCore;
	private double magneticField;
	private int baseTemperature;
	private String hydrosphereDescription;
	private int hydrosphere;
	private int waterVaporFactor;
	private ArrayList<AmosphericGases> atmoshericComposition = new ArrayList<AmosphericGases>();
	private double atmoPressure;
	private double albedo;
	private double greenhouseFactor;
	private double surfaceTemp;
	private String atmoModeration ="Average";
	private double[] rangeBandTemperature =new double[10];
	private double[] rangeBandTempSummer =new double[10];
	private double[] rangeBandTempWinter =new double[10];
	private double nightTempMod;
	private double dayTempMod;
	private boolean InnerZone = false;
	private String tectonicActivityGroup;
	private double orbitalInclination;
	private boolean boilingAtmo =false;
	private boolean hasGaia;
	private String lifeType;


	public Planet Generator(String name, String description, double orbitDistance, double orbitaleccentricity,
			char orbitalObjectClass, Star orbitingAround) {

		double snowLine = 5 * Math.pow(orbitingAround.getLumosity(), 0.5);
		if(orbitDistance<snowLine) InnerZone=true;

// size may not be all, but here it is set

		int a=900;
		if(orbitalObjectClass=='j') a=20;
		radius = (Dice.d6()+Dice.d6())*a;

//density

		if(orbitDistance<snowLine){
			density = 0.3+(Dice.d6()+Dice.d6()-2)*0.127/Math.pow(0.4+(orbitDistance/Math.pow( orbitingAround.getLumosity(),0.5)),0.67);
		}else{
			density = 0.3+(Dice.d6()+Dice.d6()-2)*0.05;
		}
		mass =Math.pow(radius/6380,3)*density;
		gravity = mass/Math.pow((radius/6380),2);
		orbitalPeriod = Math.pow(Math.pow(orbitDistance,3)/orbitingAround.getMass(),0.5);

// TODO tidelocked or not should take into consideration moons too, generate moons here!

		double tidalForce = orbitingAround.getMass()*26640000/Math.pow(orbitDistance*400,3);
		double tidelock = (0.83+(Dice.d6()+Dice.d6()-2)*0.03)*tidalForce*orbitingAround.getAge()/6.6;
		if(tidelock>1) tidelocked=true;



//Eccentricity and Inclination

		int eccentryMod=1;
		if(orbitalObjectClass=='C') eccentryMod=3;
		eccentricity=eccentryMod*(Dice.d6()-1)*(Dice.d6()-1)/(100*Dice.d6());
		axialTilt = (Dice.d6()-1)+(Dice.d6()-1)/Dice.d6();
		orbitalInclination = eccentryMod*(Dice.d6()+Dice.d6())/(1+mass/10);

//Rotation - day/night cycle
		if(tidelocked){
			rotationalPeriod=orbitalPeriod*365;
		}else{
			rotationalPeriod = (Dice.d6()+Dice.d6()+8)*(1+0.1*(tidalForce*orbitingAround.getAge()-Math.pow(mass, 0.5)));
			if(Dice.d6()<2) rotationalPeriod=Math.pow(rotationalPeriod,Dice.d6());
		}

//TODO tectonics should include moons!

		tectonicCore = findTectonicGroup();
		tectonicActivity = (5+Dice.d6()+Dice.d6()-2)*Math.pow(mass, 0.5)/orbitingAround.getAge();
		tectonicActivity *=(1+0.5*tidalForce);
		tectonicActivityGroup = findTectonicActivityGroup();
//Magnetic field
		if(tectonicCore.contains("metal")){
			magneticField =10 * 1/( Math.sqrt(( rotationalPeriod / 24 ))) * Math.pow(density, 2) * Math.sqrt(mass) / orbitingAround.getAge();
			if(tectonicCore.contains("small")) magneticField *=0.5;
			if(tectonicCore.contains("medium")) magneticField *=0.75;
		}else{
			magneticField = Dice.d6()/10;
		}

//Temperature
		baseTemperature = (int) (255/Math.sqrt((orbitDistance*Math.sqrt(orbitingAround.getLumosity()))));
//Hydrosphere
		hydrosphereDescription = findHydrosphereDescription();
		hydrosphere = findTheHydrosphere();
		if(hydrosphereDescription.equals("Liquid") || hydrosphereDescription.equals("Ice Sheet")){
			waterVaporFactor = Math.max(0, (baseTemperature-240)/100 * hydrosphere * (Dice.d6()+Dice.d6()));
		}else{
			waterVaporFactor=0;
		}
//Atmoshperic details
		atmoshericComposition = makeAtmoshpere(orbitingAround);
		atmoPressure = findAtmoPressure();

//Bioshpere
		hasGaia = testLife();
		if(hasGaia) lifeType = findLifeType();
		albedo = findAlbedo();
		double greenhouseGasEffect = findGreenhouseGases();
		greenhouseFactor =  1 + Math.sqrt(atmoPressure) *0.01 * (Dice.d6()+Dice.d6()-1) + Math.sqrt(greenhouseGasEffect) * 0.1 + waterVaporFactor * 0.1;

		//TODO adding some Gaia moderation factor (needs tweaking probably)
		if (hasGaia && baseTemperature>350) greenhouseFactor *=0.9;
		if (hasGaia && baseTemperature<250) greenhouseFactor *=1.1;

		surfaceTemp = baseTemperature * albedo  * greenhouseFactor;



		if(hasGaia && lifeType.equals("Oxygen Breathing")) adjustForOxygen();



		rangeBandTemperature = findRangeBand();


// and here we return the result		
		return null;
	}
// Inner methods -------------------------------------------------------------------------------------------------	


	private void adjustForOxygen() {
		boolean substitutionMade =false;
		int oxygenMax = (int) ((surfaceTemp-240)/200.0 * hydrosphere * (3+Dice.d6()+Dice.d6()) * 10);

		for(int i=0;i<atmoshericComposition.size();i++){
			if(atmoshericComposition.get(i).getName().equals("CO2")){
				if(atmoshericComposition.get(i).getPercentageInAtmo()<=oxygenMax){
					atmoshericComposition.add(new AmosphericGases("O2",atmoshericComposition.get(i).getPercentageInAtmo()));
					atmoshericComposition.remove(i);

				} else {
					atmoshericComposition.add(new AmosphericGases("O2",(int) oxygenMax));
					atmoshericComposition.get(i).setPercentageInAtmo(atmoshericComposition.get(i).getPercentageInAtmo()-oxygenMax);
				}
			}
			substitutionMade=true;
		}
//If we didn't find CO2, find a gas that is of lower percentage than max oxygen.
		if(!substitutionMade){
			
			//sortering av atmosphere composition
			atmoshericComposition.sort(Comparator.comparing(AmosphericGases::getPercentageInAtmo));
			int n=0;

			Iterator<AmosphericGases> iterator = atmoshericComposition.iterator();
			while (iterator.hasNext()) {

				if(iterator.next().getPercentageInAtmo()<=oxygenMax){
					atmoshericComposition.add(new AmosphericGases("O2",iterator.next().getPercentageInAtmo()));
					atmoshericComposition.remove(iterator.next());
					substitutionMade=true;
					break;
				}					
			}	
		}
//if still no suitable gas exists take largest and use a piece of that		
		if(!substitutionMade){
			atmoshericComposition.add(new AmosphericGases("O2",(int) oxygenMax));
			atmoshericComposition.get(0).setPercentageInAtmo(atmoshericComposition.get(0).getPercentageInAtmo()-oxygenMax);
		}

	}


	private String findLifeType() {
		String tempLifeType = "Oxygen Breathing";
		for(int i=0;i<atmoshericComposition.size();i++){
			if(atmoshericComposition.get(i).getName().equals("NH3")) tempLifeType = "Amonnia Breathing";
			else if(atmoshericComposition.get(i).getName().equals("F2")) tempLifeType = "Flouride Breathing";
			else if(atmoshericComposition.get(i).getName().equals("Cl2")) tempLifeType = "Cloride Breathing";
		}

		return tempLifeType;
	}


	private double[] findRangeBand() {

		int testModeration=0;
		testModeration = (int) ((hydrosphere -60)/10.0);
		testModeration =(atmoPressure<0.1)?-3:1;
		testModeration =(int) atmoPressure;
		testModeration =(rotationalPeriod<10)?-3:1;
		testModeration =(int) (Math.sqrt(rotationalPeriod/24.0));
		testModeration =(int) (10.0/axialTilt);

		atmoModeration = (testModeration<-2)?"Low":"Average";
		atmoModeration = (testModeration>2)?"High":"Average";
		return null;
	}

	private boolean testLife() {

		double lifeIndex = 0;
		if(baseTemperature<100 || baseTemperature>450) {
			lifeIndex -= 5;
		}else if(baseTemperature<250 || baseTemperature>350) {
			lifeIndex -= 1;
		} else {
			lifeIndex +=3;
		}

		if (atmoPressure<0.1){
			lifeIndex -=10;
		}else if((atmoPressure>5)){
			lifeIndex -=1;
		}

		if (hydrosphere<1){
			lifeIndex -=3;
		}else if((hydrosphere>3)){
			lifeIndex +=1;
		}

		if(atmoshericComposition.contains("NH3") && Dice.d6()<3) lifeIndex +=1;
		if(atmoshericComposition.contains("CL2")&& Dice.d6()<3) lifeIndex +=1;
		if(atmoshericComposition.contains("F2")&& Dice.d6()<3) lifeIndex +=3;


		return (lifeIndex<1)?false:true;
	}

	private double findGreenhouseGases() {
		double tempGreenhouseGasEffect = 0;
		for(int i= 0; i<atmoshericComposition.size();i++){
			if(atmoshericComposition.get(i).getName().equals("CO2")) tempGreenhouseGasEffect += atmoshericComposition.get(i).getPercentageInAtmo()*atmoPressure;
			if(atmoshericComposition.get(i).getName().equals("CH4")) tempGreenhouseGasEffect += atmoshericComposition.get(i).getPercentageInAtmo()*atmoPressure*4;
			if(atmoshericComposition.get(i).getName().equals("SO2")) tempGreenhouseGasEffect += atmoshericComposition.get(i).getPercentageInAtmo()*atmoPressure*8;
			if(atmoshericComposition.get(i).getName().equals("H2S")) tempGreenhouseGasEffect += atmoshericComposition.get(i).getPercentageInAtmo()*atmoPressure*8;
			if(atmoshericComposition.get(i).getName().equals("H2SO4")) tempGreenhouseGasEffect += atmoshericComposition.get(i).getPercentageInAtmo()*atmoPressure*16;
			if(atmoshericComposition.get(i).getName().equals("NO2")) tempGreenhouseGasEffect += atmoshericComposition.get(i).getPercentageInAtmo()*atmoPressure*8;
			if(atmoshericComposition.get(i).getName().equals("NH3")) tempGreenhouseGasEffect += atmoshericComposition.get(i).getPercentageInAtmo()*atmoPressure*8;

		}
		return tempGreenhouseGasEffect;
	}


	private double findAlbedo() {
		double tempAlbedo=1;
		int randAlbedo=Dice.d6()+Dice.d6();

		if(InnerZone){
			int mod = 0;
			if(atmoPressure<0.05) mod = 2;
			if(atmoPressure>50){
				mod = -4;
			}else if(atmoPressure>5){
				mod = -2;
			}
			if(hydrosphere>50 && hydrosphereDescription.equals("Ice Sheet") && mod>-2) mod=-2;
			if(hydrosphere>90 && hydrosphereDescription.equals("Ice Sheet") && mod>-4) mod=-4;

			randAlbedo +=mod;
			if(randAlbedo<2){
				tempAlbedo = 0.75+(Dice.d6()+Dice.d6()-2)*0.01;


			}else if(randAlbedo<4){
				tempAlbedo = 0.85+(Dice.d6()+Dice.d6()-2)*0.01;
			}else if(randAlbedo<7){
				tempAlbedo = 0.95+(Dice.d6()+Dice.d6()-2)*0.01;
			}else if(randAlbedo<10){
				tempAlbedo = 1.05+(Dice.d6()+Dice.d6()-2)*0.01;
			}else{
				tempAlbedo = 1.15+(Dice.d6()+Dice.d6()-2)*0.01;
			}

		}else{
			int mod = 0;
			if(atmoPressure>1) mod = 1;

			randAlbedo +=mod;
			if(randAlbedo<4){
				tempAlbedo = 0.75+(Dice.d6()+Dice.d6()-2)*0.01;
			}else if(randAlbedo<6){
				tempAlbedo = 0.85+(Dice.d6()+Dice.d6()-2)*0.01;
			}else if(randAlbedo<8){
				tempAlbedo = 0.95+(Dice.d6()+Dice.d6()-2)*0.01;
			}else if(randAlbedo<10){
				tempAlbedo = 1.05+(Dice.d6()+Dice.d6()-2)*0.01;
			}else{
				tempAlbedo = 1.15+(Dice.d6()+Dice.d6()-2)*0.01;
			}
		}
		return tempAlbedo;
	}

	private double findAtmoPressure() {
		double pressure;
		int mod=0;
		if(tectonicActivityGroup.equals("Dead")) mod +=1;
		if(tectonicActivityGroup.equals("Extreme")) mod +=1;
		if(boilingAtmo) mod -=1;

		switch (Dice.d6()+Dice.d6()+mod) {
		case 2:
			pressure = (Dice.d6()+Dice.d6())*0.01;
			break;
		case 3:case 4:
			pressure = (Dice.d6()+Dice.d6())*0.1;
			break;
		case 5:case 6:case 7:
			pressure = (Dice.d6()+Dice.d6())*0.2;
			break;
		case 8:case 9:
			pressure = (Dice.d6()+Dice.d6())*0.5;
			break;
		case 10:
			pressure = (Dice.d6()+Dice.d6())*1;
			break;
		case 11:
			pressure = (Dice.d6()+Dice.d6())*2;
			break;
		case 12:case 13:
			pressure = (Dice.d6()+Dice.d6())*20;
			break;
		default:
			pressure =(Dice.d6()+Dice.d6())*0.01;
			break;
		}
		if(atmoshericComposition.isEmpty()) pressure=0.001;
		pressure *= mass;
		return pressure;
	}

	private ArrayList<AmosphericGases> makeAtmoshpere(Star star) {

		Set<String> makeAtmoshpere =new TreeSet<String>();
		ArrayList<AmosphericGases> atmoArray = new ArrayList<AmosphericGases>();

		if (baseTemperature>400){
			switch (Dice.d6()+Dice.d6()) {
			case 2:
				makeAtmoshpere.add("N2");
				if(Dice.d6()<4) makeAtmoshpere.add("CO2");
				if(Dice.d6()<4) makeAtmoshpere.add("NO2");
				if(Dice.d6()<4) makeAtmoshpere.add("SO2");
				if(Dice.d6()<2) makeAtmoshpere.add("Cl2");
				if(Dice.d6()<2) makeAtmoshpere.add("F2");
				if(Dice.d6()<2) makeAtmoshpere.add("Ne");
				if(Dice.d6()<2) makeAtmoshpere.add("Ar");
				if(tectonicActivityGroup== "Extreme"){
					makeAtmoshpere.add("SO2");
					makeAtmoshpere.add("H2S");
				}

				break;
			case 3:case 4:case 5:
				makeAtmoshpere.add("CO2");
				break;
			case 6:case 7:case 8:
				makeAtmoshpere.add("N2");
				makeAtmoshpere.add("CO2");
				break;
			case 9:case 10:
				makeAtmoshpere.add("NO2");
				makeAtmoshpere.add("SO2");
				break;
			case 11:case 12:
				makeAtmoshpere.add("SO2");
				break;
			default:
				makeAtmoshpere.add("N2");
				break;
			};
		}else if(baseTemperature>240){
			switch (Dice.d6()+Dice.d6()) {
			case 2:
				makeAtmoshpere.add("N2");
				if(Dice.d6()<4) makeAtmoshpere.add("CO2");
				if(Dice.d6()<4) makeAtmoshpere.add("NO2");
				if(Dice.d6()<4) makeAtmoshpere.add("SO2");
				if(Dice.d6()<2) makeAtmoshpere.add("Cl2");
				if(Dice.d6()<2) makeAtmoshpere.add("F2");
				if(Dice.d6()<2) makeAtmoshpere.add("Ne");
				if(Dice.d6()<2) makeAtmoshpere.add("Ar");
				if(Dice.d6()<2) makeAtmoshpere.add("He");
				if(Dice.d6()<2) makeAtmoshpere.add("NH3");
				if(tectonicActivityGroup== "Extreme"){
					makeAtmoshpere.add("SO2");
					makeAtmoshpere.add("H2S");
				}

				break;
			case 3:case 4:case 5:
				makeAtmoshpere.add("CO2");
				break;
			case 6:case 7:case 8:
				makeAtmoshpere.add("N2");
				makeAtmoshpere.add("CO2");
				break;
			case 9:case 10:
				makeAtmoshpere.add("N2");
				makeAtmoshpere.add("CH4");
				break;
			case 11:case 12:
				makeAtmoshpere.add("CO2");
				makeAtmoshpere.add("CH4");
				makeAtmoshpere.add("NH3");
				break;
			default:
				makeAtmoshpere.add("N2");
				break;
			};
		}else if(baseTemperature>150){
			switch (Dice.d6()+Dice.d6()) {
			case 2:
				makeAtmoshpere.add("N2");
				if(Dice.d6()<4) makeAtmoshpere.add("CO2");
				if(Dice.d6()<4) makeAtmoshpere.add("NO2");
				if(Dice.d6()<4) makeAtmoshpere.add("SO2");
				if(Dice.d6()<2) makeAtmoshpere.add("Cl2");
				if(Dice.d6()<2) makeAtmoshpere.add("F2");
				if(Dice.d6()<2) makeAtmoshpere.add("Ne");
				if(Dice.d6()<2) makeAtmoshpere.add("Ar");
				if(Dice.d6()<2) makeAtmoshpere.add("He");
				if(Dice.d6()<2) makeAtmoshpere.add("H2");
				if(Dice.d6()<2) makeAtmoshpere.add("NH3");
				if(tectonicActivityGroup== "Extreme"){
					makeAtmoshpere.add("SO2");
					makeAtmoshpere.add("H2S");
				}

				break;
			case 3:case 4:case 5:
				makeAtmoshpere.add("CO2");
				break;
			case 6:case 7:case 8:
				makeAtmoshpere.add("N2");
				makeAtmoshpere.add("CO2");
				break;
			case 9:case 10:
				makeAtmoshpere.add("N2");
				makeAtmoshpere.add("CH4");
				break;
			case 11:case 12:
				makeAtmoshpere.add("H2");
				makeAtmoshpere.add("He");
				break;
			default:
				makeAtmoshpere.add("N2");
				break;
			};
		}else if(baseTemperature>50){
			switch (Dice.d6()+Dice.d6()) {
			case 2:
				makeAtmoshpere.add("N2");
				if(Dice.d6()<4) makeAtmoshpere.add("CO2");
				if(Dice.d6()<4) makeAtmoshpere.add("NO2");
				if(Dice.d6()<4) makeAtmoshpere.add("SO2");
				if(Dice.d6()<2) makeAtmoshpere.add("Cl2");
				if(Dice.d6()<2) makeAtmoshpere.add("F2");
				if(Dice.d6()<2) makeAtmoshpere.add("Ne");
				if(Dice.d6()<2) makeAtmoshpere.add("Ar");
				if(Dice.d6()<2) makeAtmoshpere.add("He");
				if(Dice.d6()<2) makeAtmoshpere.add("H2");
				if(Dice.d6()<2) makeAtmoshpere.add("CO");
				if(Dice.d6()<2) makeAtmoshpere.add("NH3");
				if(tectonicActivityGroup== "Extreme"){
					makeAtmoshpere.add("SO2");
					makeAtmoshpere.add("H2S");
				}

				break;
			case 3:case 4:case 5:
				makeAtmoshpere.add("H2");
				makeAtmoshpere.add("He");;
				break;
			case 6:case 7:case 8:
				makeAtmoshpere.add("H2");
				makeAtmoshpere.add("He");
				makeAtmoshpere.add("N2");
				break;
			case 9:case 10:
				makeAtmoshpere.add("N2");
				makeAtmoshpere.add("CH4");
				break;
			case 11:case 12:
				makeAtmoshpere.add("N2");
				makeAtmoshpere.add("CO");
				break;
			default:
				makeAtmoshpere.add("N2");
				break;
			};
		}else{
			switch (Dice.d6()+Dice.d6()) {
			case 2:
				makeAtmoshpere.add("N2");
				if(Dice.d6()<4) makeAtmoshpere.add("CO2");
				if(Dice.d6()<4) makeAtmoshpere.add("NO2");
				if(Dice.d6()<4) makeAtmoshpere.add("SO2");
				if(Dice.d6()<2) makeAtmoshpere.add("Cl2");
				if(Dice.d6()<2) makeAtmoshpere.add("F2");
				if(Dice.d6()<2) makeAtmoshpere.add("Ne");
				if(Dice.d6()<2) makeAtmoshpere.add("Ar");
				if(Dice.d6()<2) makeAtmoshpere.add("He");
				if(Dice.d6()<2) makeAtmoshpere.add("H2");
				if(Dice.d6()<2) makeAtmoshpere.add("NH3");
				if(tectonicActivityGroup== "Extreme"){
					makeAtmoshpere.add("SO2");
					makeAtmoshpere.add("H2S");
					if(Dice.d6()<2) makeAtmoshpere.add("H2SO4");
				}

				break;
			case 3:case 4:case 5:
				makeAtmoshpere.add("Ne");
				break;
			case 6:case 7:case 8:
				makeAtmoshpere.add("He");
				makeAtmoshpere.add("H2");
				break;
			case 9:case 10:
				makeAtmoshpere.add("H2");
				break;
			case 11:case 12:
				makeAtmoshpere.add("He");
				break;
			default:
				makeAtmoshpere.add("N2");
				break;
			};
		}

		double retainedGases = 0.02783 * baseTemperature / Math.pow(Math.pow((19600 * gravity * radius),0.5) /11200, 2);

		if(retainedGases>2) boilingAtmo=makeAtmoshpere.remove("H2");
		if(retainedGases>4) boilingAtmo=makeAtmoshpere.remove("He");
		if(retainedGases>16) boilingAtmo=makeAtmoshpere.remove("CH4");
		if(retainedGases>17) boilingAtmo=makeAtmoshpere.remove("NH3");
		if(retainedGases>17) boilingAtmo=makeAtmoshpere.remove("H2O");
		if(retainedGases>20) boilingAtmo=makeAtmoshpere.remove("Ne");
		if(retainedGases>28) boilingAtmo=makeAtmoshpere.remove("N2");
		if(retainedGases>28) boilingAtmo=makeAtmoshpere.remove("CO");
		if(retainedGases>30) boilingAtmo=makeAtmoshpere.remove("NO");
		if(retainedGases>34) boilingAtmo=makeAtmoshpere.remove("H2S");
		if(retainedGases>38) boilingAtmo=makeAtmoshpere.remove("F2");
		if(retainedGases>40) boilingAtmo=makeAtmoshpere.remove("Ar");
		if(retainedGases>44) boilingAtmo=makeAtmoshpere.remove("CO2");
		if(retainedGases>46) boilingAtmo=makeAtmoshpere.remove("NO2");
		if(retainedGases>62) boilingAtmo=makeAtmoshpere.remove("SO2");
		if(retainedGases>70) boilingAtmo=makeAtmoshpere.remove("Cl2");
		if(retainedGases>98) boilingAtmo=makeAtmoshpere.remove("H2SO4");

		if((star.getClassification().contains("A") || star.getClassification().contains("B")) && baseTemperature>150) {
			makeAtmoshpere.remove("H2O");
			makeAtmoshpere.remove("NH3");
			makeAtmoshpere.remove("CH4");
			makeAtmoshpere.remove("H2S");
		}
		if(star.getClassification().contains("F") && baseTemperature>180) {
			makeAtmoshpere.remove("H2O");
			makeAtmoshpere.remove("NH3");
			makeAtmoshpere.remove("CH4");
			makeAtmoshpere.remove("H2S");
		}
		if(star.getClassification().contains("G") && baseTemperature>200) {
			makeAtmoshpere.remove("H2O");
			makeAtmoshpere.remove("NH3");
			makeAtmoshpere.remove("CH4");
			makeAtmoshpere.remove("H2S");
		}
		if(star.getClassification().contains("K") && baseTemperature>230) {
			makeAtmoshpere.remove("H2O");
			makeAtmoshpere.remove("NH3");
			makeAtmoshpere.remove("CH4");
			makeAtmoshpere.remove("H2S");
		}
		if((star.getClassification().contains("M") || star.getClassification().contains("L")) && baseTemperature>200) {
			makeAtmoshpere.remove("H2O");
			makeAtmoshpere.remove("NH3");
			makeAtmoshpere.remove("CH4");
			makeAtmoshpere.remove("H2S");
		}

		if(!makeAtmoshpere.isEmpty()){
			int percentage =100;
			int[] part = new int[makeAtmoshpere.size()];
			part[0] = (5*(Dice.d6()+Dice.d6())+30);
			percentage = 100-part[0];
			for(int i = 1; i<part.length;i++){
				part[i]=(int) (percentage/2.0);
				percentage -=(int)(percentage/2.0);
			}
			ArrayList<String> randGas = new ArrayList<String>();
			Iterator<String> it = makeAtmoshpere.iterator();

			while(it.hasNext()){			
				randGas.add(it.next());
			}

			Collections.shuffle(randGas);
			for(int i = 1; i<part.length;i++){
				atmoArray.add(new AmosphericGases(randGas.get(i), part[i]));	
			}

		}

		return atmoArray;
	}

	private int findTheHydrosphere() {
		int tempHydro=0;
		if(hydrosphereDescription.equals("Liquid") || hydrosphereDescription.equals("Ice Sheet")){
			if(radius<2000){
				switch (Dice.d6()+Dice.d6()) {
				case 2:case 3:case 4:case 5:case 6:
					tempHydro = 0;
					break;
				case 7:
					tempHydro = Dice.d6()+Dice.d6()-1;
					break;
				case 8:case 9:
					tempHydro = Dice.d6()+Dice.d6()+10;
					break;
				case 10:case 11:
					tempHydro = 2 * (Dice.d6()+Dice.d6())+25;
					break;
				case 12:
					tempHydro = 5 * (Dice.d6()+Dice.d6())+40;
					break;
				default:
					tempHydro = 0;
					break;
				}

			}else if(radius<4000){
				switch (Dice.d6()+Dice.d6()) {
				case 2:
					tempHydro = Dice.d6();
					break;
				case 3:case 4:
					tempHydro = Dice.d6()+Dice.d6()-1;
					break;
				case 5:case 6:
					tempHydro = Dice.d6()+Dice.d6()+10;
					break;
				case 7:case 8:case 9:
					tempHydro = 2 * (Dice.d6()+Dice.d6())+25;
					break;
				case 10:case 11:case 12:
					tempHydro = 4 * (Dice.d6()+Dice.d6())+40;
					break;
				default:
					tempHydro = 0;
					break;
				}

			}else if(radius<7000){
				switch (Dice.d6()+Dice.d6()) {
				case 2:
					tempHydro = Dice.d6();
					break;
				case 3:
					tempHydro = Dice.d6()+Dice.d6()-1;
					break;
				case 4:case 5:
					tempHydro = Dice.d6()+Dice.d6()+10;
					break;
				case 6:case 7:
					tempHydro = 2 * (Dice.d6()+Dice.d6())+25;
					break;
				case 8:case 9:case 10:case 11:
					tempHydro = 4 * (Dice.d6()+Dice.d6())+40;
					break;
				case 12:
					tempHydro = 100;
					break;
				default:
					tempHydro = 0;
					break;
				}
			}else{
				switch (Dice.d6()+Dice.d6()) {
				case 2:
					tempHydro = Dice.d6();
					break;
				case 3:
					tempHydro = Dice.d6()+Dice.d6()-1;
					break;
				case 4:
					tempHydro = Dice.d6()+Dice.d6()+10;
					break;
				case 5:
					tempHydro = 2 * (Dice.d6()+Dice.d6())+25;
					break;
				case 6:case 7:case 8:case 9:
					tempHydro = 4 * (Dice.d6()+Dice.d6())+40;
					break;
				case 10:case 11:case 12:
					tempHydro = 100;
					break;
				default:
					tempHydro = 0;
					break;
				}
			}
		}
		return tempHydro;
	}

	private String findHydrosphereDescription() {
		String tempHydroD;
		if (!InnerZone){
			tempHydroD="Crustal";
		}else if(baseTemperature>500){
			tempHydroD ="None";
		}else if(baseTemperature>370){
			tempHydroD ="Vapor";
		}else if(baseTemperature>245){
			tempHydroD ="Liquid";
		}else{
			tempHydroD ="Ice Sheet";
		}
		return tempHydroD;
	}

	private String findTectonicActivityGroup() {

		String tempTectonicActivityGroup;

		if(tectonicActivity<0.5){
			tempTectonicActivityGroup = "Dead";
		}else if(tectonicActivity<1){

			switch (Dice.d6()+Dice.d6()) {
			case 2:case 3:case 4:case 5:case 6:case 7:case 8:
				tempTectonicActivityGroup = "Dead";
				break;
			case 9:case 10:case 11:
				tempTectonicActivityGroup = "Hot Spot";
				break;
			case 12:
				tempTectonicActivityGroup = "Plastic";
				break;


			default:
				tempTectonicActivityGroup = "Dead";
				break;
			}
		}else if(tectonicActivity<2){
			switch (Dice.d6()+Dice.d6()) {
			case 2:case 3:case 4:
				tempTectonicActivityGroup = "Dead";
				break;
			case 5:case 6:case 7:
				tempTectonicActivityGroup = "Hot Spot";
				break;
			case 8:case 9:
				tempTectonicActivityGroup = "Plastic";
				break;
			case 10:case 11:case 12:
				tempTectonicActivityGroup = "Plate Tectonics";
				break;
			default:
				tempTectonicActivityGroup = "Plastic";
				break;
			}	

		}else if(tectonicActivity<3){
			switch (Dice.d6()+Dice.d6()) {
			case 2:case 3:case 4:
				tempTectonicActivityGroup = "Hot Spot";
				break;
			case 5:case 6:case 7:
				tempTectonicActivityGroup = "Plastic";
				break;
			case 8:case 9:case 10:case 11:case 12:
				tempTectonicActivityGroup = "Plate Tectonics";
				break;
			default:
				tempTectonicActivityGroup = "Plate Tectonics";
				break;
			}	

		}else if(tectonicActivity<3){
			switch (Dice.d6()+Dice.d6()) {
			case 2:case 3:case 4:
				tempTectonicActivityGroup = "Hot Spot";
				break;
			case 5:case 6:case 7:
				tempTectonicActivityGroup = "Plastic";
				break;
			case 8:case 9:case 10:case 11:case 12:
				tempTectonicActivityGroup = "Plate Tectonics";
				break;
			default:
				tempTectonicActivityGroup = "Plate Tectonics";
				break;
			}	

		}else if(tectonicActivity<5){
			switch (Dice.d6()+Dice.d6()) {
			case 2:case 3:
				tempTectonicActivityGroup = "Hot Spot";
				break;
			case 4:case 5:
				tempTectonicActivityGroup = "Plastic";
				break;
			case 6:case 7:case 8:case 9:
				tempTectonicActivityGroup = "Plate Tectonics";
				break;
			case 10:case 11:case 12:
				tempTectonicActivityGroup = "Platelet Tectonics";
				break;
			default:
				tempTectonicActivityGroup = "Plate Tectonics";
				break;
			}
		}else{
			switch (Dice.d6()+Dice.d6()) {
			case 2:case 3:
				tempTectonicActivityGroup = "Plastic";
				break;
			case 4:case 5:
				tempTectonicActivityGroup = "Plate Tectonics";
				break;
			case 6:case 7:case 8:case 9:
				tempTectonicActivityGroup = "Platelet Tectonic";
				break;
			case 10:case 11:case 12:
				tempTectonicActivityGroup = "Extreme";
				break;
			default:
				tempTectonicActivityGroup = "Platelet Tectonics";
				break;
			}

		}

		return tempTectonicActivityGroup;
	}

	private String findTectonicGroup() {

		String tempTectonics;
		if(InnerZone){
			if(density<0.7){
				if(Dice.d6()<4){
					tempTectonics = "Silicates core";
				}else{
					tempTectonics = "Silicates, small metal core";
				}
			}
			if(density<1){
				tempTectonics = "Iron-nickel, medium metal core";
			}else{
				tempTectonics = "Iron-nickel, large metal core";
			}
		}else{
			if(density<0.3){
				tempTectonics = "Ice core";
			}
			if(density<1){
				tempTectonics = "Silicate core";
			}else{
				if(Dice.d6()<4){
					tempTectonics = "Silicates core";
				}else{
					tempTectonics = "Silicates, small metal core";
				}
			}	
		}
		return tempTectonics;
	}




}