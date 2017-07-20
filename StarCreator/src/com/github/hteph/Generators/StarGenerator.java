package com.github.hteph.Generators;

import com.github.hteph.ObjectsOfAllSorts.Star;
import com.github.hteph.ObjectsOfAllSorts.StellarObject;
import com.github.hteph.Utilities.Dice;

import java.text.DecimalFormat;


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
	double randN =testDice/15.0+Math.random()/10; //turning the dice roll into a continous sligthly pushed randomnumber.
	
	
	mass = 0.04/(0.015+Math.pow(randN,4)); // <-----------------------------------------MOST IMPORTANT STARTING POINT
	diameter= Math.pow(mass,0.67);
	temperature = (int)((100*Math.round(380+4800*Math.pow(mass, 0.5))/100)*(0.8+Math.random()*0.4));	
	lumosity = Math.pow(mass, 3.5);
	double inverseMass=1/mass;
	double maxAge =10*Math.pow(inverseMass, 2.5);
	age = (0.3+Math.random()*0.6)*Math.min(maxAge,13);
	double halfAgeBalance=2*age/maxAge;
	lumosity *= Math.pow(halfAgeBalance, 0.5);
	diameter *= Math.pow(halfAgeBalance, 0.33);
	
		
	classification = findStarClass(temperature, mass);
	
	DecimalFormat df = new DecimalFormat("#.####");
	System.out.println( "StarGenerator [lumosity=" + df.format(lumosity) + ", mass=" + df.format(mass) + ", diameter=" + df.format(diameter) + ", classification="
				+ classification + ", age=" + df.format(age) + ", temperature=" + temperature + "]");
	
	StellarObject star = new Star("Random Name", "Standard text", lumosity, mass, diameter, classification, age);
	return star;
	}
	
	// Sub methods ---------------------------------------------------------------------------------
	private String findStarClass(double temperature, double mass){
		String starClass = null;
		char classificationLetter;
		
		if(temperature<3500 && mass < 0.08){
			
			starClass = "L";
		} else if(temperature<500)  {
			starClass = "Y";
			
		} else if(temperature<1300){
			starClass = "T";
			
		} else if(temperature<3700){
			classificationLetter = 'M';
			int decimal=(int)(10-(temperature-1300)/(3700-1300)*10);
			String numeral = "V";
			starClass= Character.toString(classificationLetter) + Integer.toString(decimal)+" "+numeral;
		}else if(temperature<5200){
			classificationLetter = 'K';
			int decimal=(int)(10-(temperature-3700)/(5200-3700)*10);
			String numeral = "V";
			starClass= Character.toString(classificationLetter) + Integer.toString(decimal)+" "+numeral;
		}else if(temperature<6000){
			classificationLetter = 'G';
			int decimal=(int)(10-(temperature-5200)/(6000-5200)*10);
			String numeral = "V";
			starClass= Character.toString(classificationLetter) + Integer.toString(decimal)+" "+numeral;
		}else if(temperature<7500){
			classificationLetter = 'F';
			int decimal=(int)(10-(temperature-6000)/(7500-6000)*10);
			String numeral = "V";
			starClass= Character.toString(classificationLetter) + Integer.toString(decimal)+" "+numeral;
		}else if(temperature<10000){
			classificationLetter = 'A';
			int decimal=(int)(10-(temperature-7500)/(10000-7500)*10);
			String numeral = "V";
			starClass= Character.toString(classificationLetter) + Integer.toString(decimal)+" "+numeral;
		}else if(temperature<30000){
			classificationLetter = 'B';
			int decimal=(int)(10-(temperature-10000)/(30000-10000)*10);
			String numeral = "V";
			starClass= Character.toString(classificationLetter) + Integer.toString(decimal)+" "+numeral;
		}else{
			classificationLetter = 'O';
			int decimal=(int)(10-(temperature-30000)/(60000-30000)*10);
			String numeral = "V";
			starClass= Character.toString(classificationLetter) + Integer.toString(decimal)+" "+numeral;
		}
				
		return starClass;		
	}

}
