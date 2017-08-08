package com.github.hteph.Generators;

import com.github.hteph.ObjectsOfAllSorts.Star;
import com.github.hteph.ObjectsOfAllSorts.StellarObject;
import com.github.hteph.Utilities.Dice;

import java.text.DecimalFormat;
import java.util.Arrays;

public class StarGenerator {
	
	private double lumosity;
	private double mass;
	private double diameter;
	private String classification;
	private double age; // in billion of earth years
	private int temperature;
	
	//Method --------------------------------------------------------------------
	public StellarObject Generator(){
	
	int testDice =Dice.d6()+Dice.d6()+Dice.d6()-3;
	double randN =testDice/15.0+Math.random()/10; //turning the dice roll into a continous sligthly skewed randomnumber.
	
	
	mass = 0.04/(0.015+Math.pow(randN,4)); // <-----------------------------------------MOST IMPORTANT STARTING POINT
	diameter= Math.pow(mass,2/3.0);
	temperature = 100*(int)((100*Math.round(380+4800*Math.pow(mass, 0.5))/100)*(0.8+Math.random()*0.4)/100);	
	lumosity = Math.pow(mass, 3.5);
	double inverseMass=1/mass;
	double maxAge =10*Math.pow(inverseMass, 2.5);
	age = (0.3+Math.random()*0.6)*Math.min(maxAge,13);
	double halfAgeBalance=2*age/maxAge;
	lumosity *= Math.pow(halfAgeBalance, 0.5);
	diameter *= Math.pow(halfAgeBalance, 1/3.0);
	
		
	classification = findStarClass(temperature);
	
	DecimalFormat df = new DecimalFormat("#.####");
	System.out.println( "StarGenerator [lumosity=" + df.format(lumosity) + ", mass=" + df.format(mass) + ", diameter=" + df.format(diameter) + ", classification="
				+ classification + ", age=" + df.format(age) + ", temperature=" + temperature + "]");
	
	StellarObject star = new Star("Random Name", "Standard text", lumosity, mass, diameter, classification, age);
	return star;
	}
	
//Inner Methods	-------------------------------------------------------------------------------
	private String findStarClass(int temperature){
		
		String[] classLetter ={"Y","T","M","K","G","F","A","B","O"};
		int[] temperatureClass = {500,1300,2400,3700,5200,6000,7500,10000,30000,100000};
		int decimal;
		String classification;
		
		int retValue =  Arrays.binarySearch(temperatureClass,temperature);
		
		if(retValue<0) decimal =10- (int) (10.0*(temperature-temperatureClass[-retValue-2])/(1.0*temperatureClass[-retValue-1]-temperatureClass[-retValue-2]));
		else decimal = 0;

		if(retValue<0) classification = classLetter[-retValue-2]+decimal+" V";
		else classification = classLetter[retValue]+decimal+" V";
		
		
		return classLetter[-retValue-2]+decimal+" V";
		}
		
	}

