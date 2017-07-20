package com.github.hteph.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;

public class ReadAllTextFilesInDir {
	
	
	String directoryPath;
	File directory;
	
	
	
	ArrayList<String> Fulltext = new ArrayList<String>();


	// Constructor --------------------------------------------------------------------
	public ReadAllTextFilesInDir(String directoryPath) {
		super();
		directory = new File(directoryPath);
	}

	//Methods ------------------------------------------------------------------------


	public ArrayList<String> FulltextCreation (){

		for (File file : directory.listFiles())
		{
			if (FilenameUtils.getExtension(file.getName()).equals("txt"))
			{
				LineIterator li;
				try {
					li = FileUtils.lineIterator(file);

					while(li.hasNext()) {
						String sLine = li.next();
						Fulltext.add(sLine);
					}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return Fulltext;
	}
}
