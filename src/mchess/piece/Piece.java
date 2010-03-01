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
package mchess.piece;

abstract public class Piece 
{
	public static final long KING=11125,QUEEN=54875,BISHOP=65975,KNIGHT=87245,ROOK=24887,PAWN=57423,EMPTY=88542;
	protected long ID;
	protected Allegiance allegiance;
	protected String name;
	
	//PIECE CONSTRUCTOR
	public Piece(long mID, Allegiance a,String n)
	{
		ID = mID;
		allegiance = a;
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public long getID()
	{
		return ID;
	}

	public void setID(long iD) 
	{
		ID = iD;
	}

	public Allegiance getAllegiance() 
	{
		return allegiance;
	}

	public void setAllegiance(Allegiance allegiance) 
	{
		this.allegiance = allegiance;
	}
	
	public boolean viableMove(Piece p)
	{
		return p.getAllegiance()!=this.getAllegiance();
	}
	
	public boolean isPawn(){return ID==PAWN;}
	public boolean isRook(){return ID==ROOK;}
	public boolean isKnight(){return ID==KNIGHT;}
	public boolean isBishop(){return ID==BISHOP;}
	public boolean isKing(){return ID==KING;}
	public boolean isQueen(){return ID==QUEEN;}
	public boolean isEmpty(){return ID==EMPTY;}


}
