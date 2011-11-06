package org.comtor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExtractJar {
	@SuppressWarnings("rawtypes")
	protected void getContents(File file, String candy) {
		// Try Block -- UnJAR programmatically
		try {
			JarFile jar = new JarFile(file);
			System.out.println("Using getName() " + file.getName());
			Enumeration wasEnum = jar.entries();

			while (wasEnum.hasMoreElements()) {

				System.out.println("Found Entry!");
				JarEntry entryFile = (JarEntry) wasEnum.nextElement();
				File f = new File(candy, entryFile.getName());

				System.out.println("Entry Name is: " + entryFile.getName());
				if (entryFile.isDirectory()) { // if its a directory, create it
					f.mkdir();
					continue;
				}
				InputStream in = jar.getInputStream(entryFile); // get the input
																// stream
				OutputStream output = new FileOutputStream(f);
				while (in.available() > 0) { // write contents of 'is' to 'fos'
					output.write(in.read());
				}
				output.close();
				in.close();
			}
		} catch (Exception ex) {
			// ------
		}
	}
}
