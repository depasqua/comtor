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
import java.net.*;
import java.util.*;

public class BitlyServices {
	/**
	 *
	 */
	public static String shortenUrl(String longUrl) {
		InputStream urlStream = null;
		try {
			URL url = new URL("https://api-ssl.bitly.com/v3/shorten?apiKey=R_c59c4789f0bc0cfcdd5f98bc4fde003a&login=comtor&longUrl=" +
				URLEncoder.encode(longUrl, "UTF-8") + "&format=txt");
			urlStream = url.openStream();
		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}

		if (urlStream != null)
			return new Scanner(urlStream).nextLine();
		else
			return null;
	}
}