/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2011 The College of New Jersey
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.comtor.cloud;

import java.io.*;

import org.comtor.cloud.*;
import org.comtor.drivers.*;

/**
 * This class supports all servlets in a variety of utility methods. Mainly this can be used to
 * provide servlet-specific information such as the location of the temporary dir for downloads of 
 * dictionary files.
 */
public class ServletSupport {

	private static File tempDir;

	/**
	 * Returns a reference to the temporary directory for use in the servlet context
	 *
	 * @param servlet a reference to the current servlet context
	 * @return a reference to a temporary directory in the servlet context
	 */
	public static File getTempDir() {
		return tempDir;
	}
	
	/**
	 * Returns a reference to the temporary directory used for writing (usually for downloadable
	 * dictionaries) in the cloud (Tomcat version).
	 *
	 * @param dir a reference to the temporary directory for writing (set by the servlets).
	 */
	public static void setTempDir(File dir) {
		tempDir = dir;
	}
}
