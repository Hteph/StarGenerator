package com.github.hteph.MainModules;

import com.github.hteph.Generators.GenerateTerrestrialPlanet;
import com.github.hteph.Generators.StarGenerator;
import com.github.hteph.ObjectsOfAllSorts.Planet;
import com.github.hteph.ObjectsOfAllSorts.Star;
import com.github.hteph.ObjectsOfAllSorts.StellarObject;

public class MainConnectorOfStuff {

	public static void main(String[] args) {
		
		
		StarGenerator testGenerator = new StarGenerator();
		
		GenerateTerrestrialPlanet testPlanetGeneration = new GenerateTerrestrialPlanet();
			
		StellarObject testStar = testGenerator.Generator();
		
		Planet testPlanet = testPlanetGeneration.Generator("Testus", "SomethingSomething", 1, 'J', (Star) testStar);
		
		System.out.println(testStar.toString());
		System.out.println(testPlanet.toString());

	}

}
