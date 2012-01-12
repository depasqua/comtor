package org.comtor.cloud;

import java.io.*;
import java.util.*;
import java.util.jar.*;

public class ExtractJar {
	@SuppressWarnings("rawtypes")
	public void getContents(File file, String path) {
		// Try Block: UnJAR Programmatically
		try {
			JarFile jar = new JarFile(file);
			Enumeration wasEnum = jar.entries();

			while (wasEnum.hasMoreElements()) {
				JarEntry entryFile = (JarEntry) wasEnum.nextElement();
				File f = new File(path, java.io.File.separator + entryFile.getName());
				// If it's a directory, create it
				if (entryFile.isDirectory()) { 
					f.mkdir();
					continue;
				}
				// Get the input stream
				InputStream in = jar.getInputStream(entryFile); 
				OutputStream output = new FileOutputStream(f);
				// write contents of 'is' to 'fos'
				while (in.available() > 0) {
					output.write(in.read());
				}
				output.close();
				in.close();
			}
		} catch (Exception ex) {
			System.err.println("Error encountered while unjarring");
		}
	}
}
