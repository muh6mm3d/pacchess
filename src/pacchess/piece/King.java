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

import pacchess.base.PacChess;

public class King extends Piece
	implements MovementSensitive
{
	private int r;
	private int c;
	PacChess controller;
	private boolean notMoved;
	public King(Allegiance a,PacChess cont)
	{
		super(KING,a,"King");
		r=-1;
		c=-1;
		controller = cont;
		notMoved=true;
	}

	public boolean insertInto(String coord)
	{
		return insertInto(PacChess.translateCoordinate(coord));
	}
	public boolean insertInto(int[] coord)
	{
		if(controller.isValid(coord)&&controller.isEmpty(coord))
		{
			r=coord[0];
			c=coord[1];
		}
		return false;
	}
	
	public boolean move(int[] coord)
	{
		r=coord[0];
		c=coord[1];
		return true;
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
		System.out.print("checking...");
	   return (lateralAndDiagonalCheck(co,c)||
			   knightCheck(co,c)||
			   pawnCheck(co,c)||
			   kingCheck(co,c));
	}

	public boolean lateralAndDiagonalCheck(int[] coord, PacChess controller)
	{
	     //lateral check
	    int[] rshift = {-1,00,00,01,-1,-1,01,01};
	    int[] cshift = {00,-1,01,00,-1,01,-1,01};

	    for(int i=0; i<rshift.length; i++)
	    {
			int rs = rshift[i];
			int cs = cshift[i];

			for(int r=coord[0]+rs,c=coord[1]+cs,finished=-1;finished<0&&controller.isValid(r,c)&&this.viableMove(controller.get(r,c));c--)
			{
				//checks to see if the encountered space contains a piece
				if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
				{
					Piece p = controller.get(r,c);
					//if the piece encountered to the left is a rook or a queen,
					//of opposite allegiance, the king is in check.
					if(controller.get(r,c).getAllegiance()!=allegiance&&(p.isQueen()||p.isRook()))
					{ System.out.println("lateralanddiagonaled"); return true;  }
					break;
					//finished=1;
				}
			}
	    }
	    return false;
	}

	public boolean knightCheck(int[] coord, PacChess controller)
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
		int[] rshift = {-1,01,-2,02,-2,02,-1,01};
		int[] cshift = {-2,-2,-1,-1,01,01,02,02};
		for(int i=0;i<rshift.length;i++)
		{
			int r = coord[0]+rshift[i];
			int c = coord[1]+cshift[i];
			if(controller.isValid(r,c)&&
					controller.get(r,c).getAllegiance()!=allegiance&&
					controller.get(r,c).isKnight())
			{ System.out.println("knighted"); return true; }
		}
		return false;
	}

	protected boolean pawnCheck(int[] coord, PacChess controller)
	{
		int r;
		if(allegiance.isBlack()) r=1;
		else r=-1;

		if(controller.isValid(coord[0]+r,coord[1]-1)&&
				controller.get(coord[0]+r,coord[1]-1).isPawn())
		{
			if(!controller.get(coord[0]+r,coord[1]-1).getAllegiance().equals(allegiance))
			{System.out.println("pawned");return true;}
			
		}
		if(controller.isValid(coord[0]+r,coord[1]+1)&&controller.get(coord[0]+r,coord[1]+1).isPawn())
		{
			if(!controller.get(coord[0]+r,coord[1]+1).getAllegiance().equals(allegiance))
			{System.out.println("pawned");return true;}
			
		}
		return false;
	}
	protected boolean kingCheck(int[] coord, PacChess controller)
	{
		int[] rshift = {-1,-1,-1,00,00,01,01,01};
		int[] cshift = {-1,00,01,-1,01,-1,00,01};
		System.out.println("coord length: "+coord.length);
		for(int i=0;i<rshift.length && i<cshift.length;i++)
		{
			int r = coord[0]+rshift[i];
			int c = coord[1]+cshift[i];
			if(controller.isValid(r,c)&&
					controller.get(r,c)!=this&&
					controller.get(r,c) instanceof King)
			{System.out.println("kinged");return true;}
		}
		return false;
	}

	public boolean inCheckOld(int[] coord, PacChess controller)
	{
    //System.out.println();
            /*
             * Lateral Check
             .____            __                      .__
            |    |   _____ _/  |_  ________________  |  |
            |    |   \__  \\   __\/ __ \_  __ \__  \ |  |
            |    |___ / __ \|  | \  ___/|  | \// __ \|  |__
            |_______ (____  /__|  \___  >__|  (____  /____/
                    \/    \/          \/           \/
            _________ .__                   __
            \_   ___ \|  |__   ____   ____ |  | __
            /    \  \/|  |  \_/ __ \_/ ___\|  |/ /
            \     \___|   Y  \  ___/\  \___|    <
             \______  /___|  /\___  >\___  >__|_ \
                    \/     \/     \/     \/     \/
             */
		
		//check to the left for opponents that threaten the king
		for(int r=coord[0],c=coord[1]-1,finished=-1;finished<0&&controller.isValid(r,c)&&this.viableMove(controller.get(r,c));c--)
		{
			//checks to see if the encountered space contains a piece
			if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
			{
				Piece p = controller.get(r,c);
				//if the piece encountered to the left is a rook or a queen,
				//of opposite allegiance, the king is in check.
				if(controller.get(r,c).getAllegiance()!=allegiance&&(p.isQueen()||p.isRook()))
				{
          //System.out.println(allegiance+" king is threatened by "+controller.get(r,c).getName()+" at coord: "
               //   +controller.translateCoordinate(new int[]{r,c}));
					return true;
				}
				finished=1;
			}
		}
		//check to the right for opponents that threaten the king
		for(int r=coord[0],c=coord[1]+1,finished=-1;finished<0&&controller.isValid(r,c)&&this.viableMove(controller.get(r,c));c++)
		{
			//checks to see if the encountered space contains a piece
			if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
			{
				Piece p = controller.get(r,c);
				//if the piece encountered to the right is a rook or a queen,
				//of opposite allegiance, the king is in check.
				if(p.getAllegiance()!=allegiance&&(p.isQueen()||p.isRook()))
				{
          //System.out.println(allegiance+" king is threatened by "+controller.get(r,c).getName()+" at coord: "
               //   +controller.translateCoordinate(new int[]{r,c}));
					return true;
				}
				finished=1;
			}
		}
		//check above for opponents that threaten the king
		for(int r=coord[0]-1,c=coord[1],finished=-1;finished<0&&controller.isValid(r,c)&&this.viableMove(controller.get(r,c));r--)
		{
			//checks to see if the encountered space contains a piece
			if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
			{
				Piece p = controller.get(r,c);
				//if the piece encountered above is a rook or a queen,
				//of opposite allegiance, the king is in check.
				if(p.getAllegiance()!=allegiance&&(p.isQueen()||p.isRook()))
        {
        //System.out.println(allegiance+" king is threatened by "+controller.get(r,c).getName()+" at coord: "
               //   +controller.translateCoordinate(new int[]{r,c}));
					return true;
				}
				finished=1;
			}
		}
		//check below for opponents that threaten the king
		for(int r=coord[0]+1,c=coord[1],finished=-1;finished<0&&controller.isValid(r,c)&&this.viableMove(controller.get(r,c));r++)
		{
			//checks to see if the encountered space contains a piece
			if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
			{
				Piece p = controller.get(r,c);
				//if the piece encountered below is a rook or a queen,
				//of opposite allegiance, the king is in check.
				if(p.getAllegiance()!=allegiance&&(p.isQueen()||p.isRook()))
				{
          //System.out.println(allegiance+" king is threatened by "+controller.get(r,c).getName()+" at coord: "
                //  +controller.translateCoordinate(new int[]{r,c}));
					return true;
				}
				finished=1;
			}
		}

                
		//CHECK DIAGONALS
                /*
                 ________  .__                                    .__
                \______ \ |__|____     ____   ____   ____ _____  |  |
                 |    |  \|  \__  \   / ___\ /  _ \ /    \\__  \ |  |
                 |    `   \  |/ __ \_/ /_/  >  <_> )   |  \/ __ \|  |__
                /_______  /__(____  /\___  / \____/|___|  (____  /____/
                        \/        \//_____/             \/     \/
                _________ .__                   __
                \_   ___ \|  |__   ____   ____ |  | __
                /    \  \/|  |  \_/ __ \_/ ___\|  |/ /
                \     \___|   Y  \  ___/\  \___|    <
                 \______  /___|  /\___  >\___  >__|_ \
                        \/     \/     \/     \/     \/
                */
		
		//check to the north-west to see if opponents threaten the king
		for(int r=coord[0]-1,c=coord[1]-1,finished=-1;finished<0&&controller.isValid(r,c)&&this.viableMove(controller.get(r,c));r--,c--)
		{
			//checks to see if the encountered space contains an opposing piece
			if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
			{
				Piece p = controller.get(r,c);
				//if the piece encountered to the north-west is a bishop,
				//of opposite allegiance, the king is in check.
				if(p.getAllegiance()!=allegiance&&(p.isQueen()||p.isBishop()))
				{
          //System.out.println(allegiance+" king is threatened by "+controller.get(r,c).getName()+" at coord: "
                 // +controller.translateCoordinate(new int[]{r,c}));
					return true;
				}
				finished=1;
			}
		}
		//check to the north-east to see if opponents threaten the king
		for(int r=coord[0]-1,c=coord[1]+1,finished=-1;finished<0&&controller.isValid(r,c)&&this.viableMove(controller.get(r,c));r--,c++)
		{
			//checks to see if the encountered space contains an opposing piece
			if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
			{
				Piece p = controller.get(r,c);
				//if the piece encountered to the north-east is a bishop,
				//of opposite allegiance, the king is in check.
				if(p.getAllegiance()!=allegiance&&(p.isQueen()||p.isBishop()))
				{
          //System.out.println(allegiance+" king is threatened by "+controller.get(r,c).getName()+" at coord: "
                 // +controller.translateCoordinate(new int[]{r,c}));
					return true;
				}
				finished=1;
			}
		}
		//check to the south-east to see if opponents threaten the king
		for(int r=coord[0]+1,c=coord[1]+1,finished=-1;finished<0&&controller.isValid(r,c)&&this.viableMove(controller.get(r,c));r++,c++)
		{
			//checks to see if the encountered space contains an opposing piece
			if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
			{
				Piece p = controller.get(r,c);
				//if the piece encountered to the south-east is a bishop,
				//of opposite allegiance, the king is in check.
				if(p.getAllegiance()!=allegiance&&(p.isQueen()||p.isBishop()))
				{
          //System.out.println(allegiance+" king is threatened by "+controller.get(r,c).getName()+" at coord: "
                 // +controller.translateCoordinate(new int[]{r,c}));
					return true;
				}
				finished=1;
			}
		}
		//check to the south-west to see if opponents threaten the king
		for(int r=coord[0]+1,c=coord[1]-1,finished=-1;finished<0&&controller.isValid(r,c)&&this.viableMove(controller.get(r,c));r++,c--)
		{
			//checks to see if the encountered space contains an opposing piece
			if(controller.get(r,c)!=this&&!controller.isEmpty(r,c))
			{
				Piece p = controller.get(r,c);
				//if the piece encountered to the south-west is a bishop,
				//of opposite allegiance, the king is in check.
				if(p.getAllegiance()!=allegiance&&(p.isQueen()||p.isBishop()))
				{
          //System.out.println(allegiance+" king is threatened by "+controller.get(r,c).getName()+" at coord: "
                //  +controller.translateCoordinate(new int[]{r,c}));
					return true;
				}
                                finished=1;
			}
		}
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
		
		int r;
		int c;
		
		//check for knight in space "1"
		r=coord[0]-1;
		c=coord[1]-2;//hannah
		if(controller.isValid(r,c)&&controller.get(r,c).getAllegiance()!=allegiance&&controller.get(r,c).isKnight())
			return true;
		//check for knight in space "2"
		r=coord[0]-2;
		c=coord[1]-1;
		if(controller.isValid(r,c)&&controller.get(r,c).getAllegiance()!=allegiance&&controller.get(r,c).isKnight())
			return true;
		//check for knight in space "3"
		r=coord[0]-2;
		c=coord[1]+1;
		if(controller.isValid(r,c)&&controller.get(r,c).getAllegiance()!=allegiance&&controller.get(r,c).isKnight())
			return true;
		//check for knight in space "4"
		r=coord[0]-1;
		c=coord[1]+2;
		if(controller.isValid(r,c)&&controller.get(r,c).getAllegiance()!=allegiance&&controller.get(r,c).isKnight())
			return true;
		//check for knight in space "5"
		r=coord[0]+1;
		c=coord[1]+2;
		if(controller.isValid(r,c)&&controller.get(r,c).getAllegiance()!=allegiance&&controller.get(r,c).isKnight())
			return true;
		//check for knight in space "6"
		r=coord[0]+2;
		c=coord[1]+1;
		if(controller.isValid(r,c)&&controller.get(r,c).getAllegiance()!=allegiance&&controller.get(r,c).isKnight())
			return true;
		//check for knight in space "7"
		r=coord[0]+2;
		c=coord[1]-1;
		if(controller.isValid(r,c)&&controller.get(r,c).getAllegiance()!=allegiance&&controller.get(r,c).isKnight())
			return true;
		//check for knight in space "8"
		r=coord[0]+1;
		c=coord[1]-2;
		if(controller.isValid(r,c)&&controller.get(r,c).getAllegiance()!=allegiance&&controller.get(r,c).isKnight())
			return true;
		
		/*
		 * check for pawns who are threatening the king.
		 * this part of the code will be divided into two different sections
		 * one for black and one for white
		 * white pieces advance north, so a white pawn threatening the black king
		 * would be southwest or southeast of it
		 * 
		 * a black pawn threatening a white king would be northwest or northeast
		 */
        if(allegiance.isBlack())
		{
			//check to the south-west and south-east
			if(controller.isValid(coord[0]+1,coord[1]-1)&&controller.get(coord[0]+1,coord[1]-1).isPawn())
			{
				if(controller.get(coord[0]+1,coord[1]-1).getAllegiance().isWhite())
					return true;
			}
			if(controller.isValid(coord[0]+1,coord[1]+1)&&controller.get(coord[0]+1,coord[1]+1).isPawn())
			{
				if(controller.get(coord[0]+1,coord[1]+1).getAllegiance().isWhite())
					return true;
			}
		}
		else if(allegiance.isWhite())
		{
			//check to the north-west and north-east
			if(controller.isValid(coord[0]-1,coord[1]-1)&&controller.get(coord[0]-1,coord[1]-1).isPawn())
			{
				if(controller.get(coord[0]-1,coord[1]-1).getAllegiance().isBlack())
					return true;
			}
			if(controller.isValid(coord[0]-1,coord[1]+1)&&controller.get(coord[0]-1,coord[1]+1).isPawn())
			{
				if(controller.get(coord[0]-1,coord[1]+1).getAllegiance().isBlack())
					return true;
			}
		}
		
		/*
		 * TODO check for the opposing king who would be threatening the king.
		 * in order for this to happen, a supporting piece must be protecting
		 * the opposing king. this sounds a little hard to implement
		 */
		
		//North-west condition
		r=coord[0]-1;
		c=coord[1]-1;
		if(controller.isValid(r,c)&&controller.get(r,c)!=this&&controller.get(r,c).isKing())
			return true;
		//North Condition
		r=coord[0]-1;
		c=coord[1];
		if(controller.isValid(r,c)&&controller.get(r,c)!=this&&controller.get(r,c).isKing())
			return true;
		//North-east condition
		r=coord[0]-1;
		c=coord[1]+1;
		if(controller.isValid(r,c)&&controller.get(r,c)!=this&&controller.get(r,c).isKing())
			return true;
		//East Condition
		r=coord[0];
		c=coord[1]+1;
		if(controller.isValid(r,c)&&controller.get(r,c)!=this&&controller.get(r,c).isKing())
			return true;
		//South-east condition
		r=coord[0]+1;
		c=coord[1]+1;
		if(controller.isValid(r,c)&&controller.get(r,c)!=this&&controller.get(r,c).isKing())
			return true;
		//South condition
		r=coord[0]+1;
		c=coord[1];
		if(controller.isValid(r,c)&&controller.get(r,c)!=this&&controller.get(r,c).isKing())
			return true;
		//South-west condition
		r=coord[0]+1;
		c=coord[1]-1;
		if(controller.isValid(r,c)&&controller.get(r,c)!=this&&controller.get(r,c).isKing())
			return true;
		//West condition
		r=coord[0];
		c=coord[1]-1;
		if(controller.isValid(r,c)&&controller.get(r,c)!=this&&controller.get(r,c).isKing())
			return true;
		
		return false;
		
		
	}

	public boolean inCheckmate()
	{
		return inCheckmate(controller);
	}
	public boolean inCheckmate(PacChess controller)
	{
		ArrayList<Integer[]> moves = new ArrayList<Integer[]>();
		for(int r=0;r<8;r++)
		{
			for(int c=0;c<8;c++)
			{
				Piece p = controller.get(r,c);
				if(p.getAllegiance()==this.allegiance)
				{
					int[][] collected = controller.validMovesCoordinate(new int[]{r,c});
					for(int i=0;i<collected.length;i++)
					{
						moves.add(new Integer[]{collected[i][0],collected[i][1]});
					}
				}
			}
		}
		if(moves.size()==0){return true;}
		return false;
		
	}
	
	public boolean notMoved(){return notMoved;}
	public void setNotMoved(boolean b){notMoved=b;}
	
	
	
	
	
	
}