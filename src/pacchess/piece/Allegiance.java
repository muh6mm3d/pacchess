/*
 * MChess - A chess logic base and GUI frontend
 * Copyright (C) 2010 Thomas Petit
 *
 * This file is part of MChess
 *
 *   MChess is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pacchess.piece;

public class Allegiance
{
	public static final long BLACK=65545487454L,WHITE=7879763321L,EMPTY=4155454454L;
	private long aID;
	
	public static Allegiance ABLACK = new Allegiance(BLACK);
	public static Allegiance AWHITE = new Allegiance(WHITE);
	public static Allegiance ANONE = new Allegiance(EMPTY);
	
	public Allegiance(long a)
	{
		aID=a;
	}
	
	public boolean isBlack()
	{
		return BLACK==aID;
	}
	public boolean isWhite()
	{
		return WHITE==aID;
	}
	public boolean isEmpty()
	{
		return aID==EMPTY;
	}
	public long getID()
	{
		return aID;
	}
	public boolean equals(Allegiance a)
	{
		return a.getID()==aID;
	}
	public String toString()
	{
		return isBlack()?"Black":"White";
	}
}