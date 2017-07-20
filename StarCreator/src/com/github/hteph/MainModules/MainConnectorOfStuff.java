package com.github.hteph.MainModules;

import com.github.hteph.Generators.StarGenerator;
import com.github.hteph.ObjectsOfAllSorts.StellarObject;

public class MainConnectorOfStuff {

	public static void main(String[] args) {
		
		
		StarGenerator testGenerator = new StarGenerator();
		
		StellarObject test = testGenerator.Generator();
		
		System.out.println(test.toString());

	}

}
