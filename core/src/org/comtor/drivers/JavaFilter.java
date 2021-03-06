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
package org.comtor.drivers;

import java.io.*;

/**
 * A file filter used to find .java (Java source code files) files.
 */
public class JavaFilter implements FilenameFilter {
	/**
	 * Filters on .java files
	 *
	 * @param dir the directory of a potential file to filter
	 * @param name the filename of a potential file to filter
	 * @return a true if the specified file matches the filter, false otherwise
	 */
	public boolean accept (File dir, String name) {
		if (name.endsWith(".java"))
			return true;
		else
			return false;
	}
}
