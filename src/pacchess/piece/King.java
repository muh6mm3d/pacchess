/*
 * PacChess - A chess logic base and GUI frontend
 * Copyright (C) 2010 Thomas Petit
 *
 * This file is part of PacChess
 *
 *   PacChess is free software: you can redistribute it and/or modify
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
//TODO CLEAN KING CODE
//TODO COMMENT KING CODE
package pacchess.piece;

import pacchess.piece.allegiance.Allegiance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pacchess.base.PacChess;

public class King extends Piece
	implements MovementSensitive
{
	protected int r;
	protected int c;
	protected PacChess controller;
	private boolean notMoved;
	public King(Allegiance a,PacChess cont)
	{
		//instantiate parent class
		super(KING,a,"King");
		//default coordinates to -10
		r=-10;
		c=-10;
		controller = cont;
		notMoved=true;
	}

	public boolean insertInto(String coord)
	{
		return insertInto(PacChess.translateCoordinate(coord));
	}
	public boolean insertInto(int[] coord)
	{
		//sets the coordinates of the king to reflect coordinates in controller logic
		if(controller.isValid(coord)&&controller.isEmpty(coord))
		{
			r=coord[0];
			c=coord[1];
		}
		return false;
	}
	
	public boolean move(int[] coord)
	{
		//sets the coordinates of the king to reflect coordinates in controller logic
		if(controller.isValid(coord))
		{
			r=coord[0];
			c=coord[1];
			return true;
		}
		return false;
	}
	//Check to see if king was placed in passed coordinate, if it would be in check
	public boolean inCheck()
	{
		return inCheck(new int[]{r,c});
	}
	public boolean inCheck(int r,int c)
	{
		return inCheck(new int[]{r,c});
	}
	public boolean inCheck(int[] coord)
	{
		return inCheck(coord,this.controller);
	}
	public boolean inCheck(int[] co, PacChess c)
	{
		//compare all booleans return by different checking methods to determine if the king is in check
		//	and then return the result.
		return (lateralAndDiagonalCheck(co,c)||
			   knightCheck(co,c)||
			   pawnCheck(co,c)||
			   kingCheck(co,c));
	}

	private boolean lateralAndDiagonalCheck(int[] coord, PacChess controller)
	{
	    //define shift for every direction to check
	    int[] rshift = {-1,00,00,01,-1,-1,01,01};
	    int[] cshift = {00,-1,01,00,-1,01,-1,01};

		//iterate in each direction
	    for(int i=0; i<rshift.length; i++)
	    {
			//variables for easy access of shift values
			int rs = rshift[i];
			int cs = cshift[i];

			//loops in direction to check for pieces threatening king
			for(int r=coord[0]+rs,c=coord[1]+cs;
				controller.isValid(r,c)&&
					this.viableMove(controller.get(r,c));
				c--)
			{
				//checks to see if the encountered space contains a piece
				if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
				{
					Piece p = controller.get(r,c);
					//if the piece encountered to the left is a rook or a queen,
					//of opposite allegiance, the king is in check.
					if(!controller.get(r,c).getAllegiance().equals(allegiance)&&(p instanceof Queen||p instanceof Rook))
					{ return true; }
					break;
				}
			}
	    }
	    return false;
	}

	private boolean knightCheck(int[] coord, PacChess controller)
	{
		/*
		 * check for knights who are threatening the king
		 *
		 * 	   KNIGHT-MAP:
		 *
		 * 		.........
		 * 		...2.3...
		 * 		..1...4..
		 * 		....K....
		 * 		..8...5..
		 * 		...7.6...
		 * 		.........
		 */

		//define row and column shift for every possible knight movement
		int[] rshift = {-1,01,-2,02,-2,02,-1,01};
		int[] cshift = {-2,-2,-1,-1,01,01,02,02};

		//iterate through every spot and check for knight occupying
		for(int i=0;i<rshift.length;i++)
		{
			//variable for easy access to shift
			int r = coord[0]+rshift[i];
			int c = coord[1]+cshift[i];
			
			//check if the spot contains a knight which is of the opposite allegiance
			if(controller.isValid(r,c)&&
					!(controller.get(r,c).getAllegiance().equals(allegiance))&&
					controller.get(r,c) instanceof Knight)
			{ return true; }
		}
		return false;
	}

	protected boolean pawnCheck(int[] coord, PacChess controller)
	{
		//set r shift depending on if the player is black or white
		int r;
		if(allegiance.isBlack()) r=1;
		else r=-1;

		if(controller.isValid(coord[0]+r,coord[1]-1)&&
				controller.get(coord[0]+r,coord[1]-1) instanceof Pawn)
		{
			if(!controller.get(coord[0]+r,coord[1]-1).getAllegiance().equals(allegiance))
			{ return true; }
			
		}
		if(controller.isValid(coord[0]+r,coord[1]+1)&&
				controller.get(coord[0]+r,coord[1]+1) instanceof Pawn)
		{
			if(!controller.get(coord[0]+r,coord[1]+1).getAllegiance().equals(allegiance))
			{ return true; }
			
		}
		return false;
	}
	protected boolean kingCheck(int[] coord, PacChess controller)
	{
		//define shift for areas for the king could threaten another king
		int[] rshift = {-1,-1,-1,00,00,01,01,01};
		int[] cshift = {-1,00,01,-1,01,-1,00,01};

		//iterate through all possibilities
		for(int i=0;i<rshift.length && i<cshift.length;i++)
		{
			//variables for easy access to shift
			int r = coord[0]+rshift[i];
			int c = coord[1]+cshift[i];

			//if space is occupied by a king which isnt you,
			//	then you are in check, return true
			if(controller.isValid(r,c)&&
					controller.get(r,c)!=this&&
					controller.get(r,c) instanceof King)
			{ return true; }
		}
		return false;
	}

	public boolean inCheckmate()
	{
		return inCheckmate(controller);
	}
	public boolean inCheckmate(PacChess controller)
	{
		//create a list to hold all the moves
		List<Integer[]> moves = new LinkedList<Integer[]>();

		//iterate through every spot on the board
		for(int r=0;r<8;r++)
		{
			for(int c=0;c<8;c++)
			{
				Piece p = controller.get(r,c);

				//if the allegiance of the objects is the same
				if(p.getAllegiance().equals(allegiance))
				{
					//retrieve all moves for this piece and add them to the moves method
					int[][] collected = controller.validMovesCoordinate(new int[]{r,c});
					for(int i=0;i<collected.length;i++)
					{
						moves.add(new Integer[]{collected[i][0],collected[i][1]});
					}
				}
			}
		}
		//if there are no moves available for any of the other pieces,
		//	the king must be in checkmate, return TRUE. else return FALSE.
		return moves.size()==0;
		
	}


	//Implemented methods from MovementSensitive Interface
	public boolean notMoved(){return notMoved;}
	public void setNotMoved(boolean b){notMoved=b;}
	
	
	
	
	
	
}