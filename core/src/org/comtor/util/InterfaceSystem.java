/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2013 The College of New Jersey
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
package org.comtor.util;

/**
 * This class represents the various operational front ends to Comtor
 *
 * @author Peter DePasquale
 */
public enum InterfaceSystem {
	WWW,		/* cloud-based Tomcat servlet */
	ECLIPSE,	/* API use from Eclipse */
	API,		/* API general use */
	WEBCAT,		/* API use from WebCat */
	NETBEANS,	/* API use from Netbeans */
}
