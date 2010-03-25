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
package pacchess.base;
/*
 * http://www.network-science.de/ascii/
 *
 * Things that need revision or fixing
 * Revision:
 * 		checking to see if when the king is moved if he is in check when castling. need to review rules
 * 		Improve Initialization of board pieces using loops
 * 		Implement hasmaps in valid moves with kings to slim program logic.
 * FIX:
 * 		Make sure to change Kings back to private after testing is finished
 * 		Set NotMoved in Pawn, Rooks, and Kings when they are moved
 * 		Implement EnPessant
 * 			Figure out how to set isVulnerable and then disable it next turn TODO
 * 		Implement turn based movement
 */
/*
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.lang.reflect.InvocationTargetException;
 * 
 */

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import pacchess.piece.Allegiance;
import pacchess.piece.Bishop;
import pacchess.piece.Empty;
import pacchess.piece.King;
import pacchess.piece.Knight;
import pacchess.piece.Pawn;
import pacchess.piece.Piece;
import pacchess.piece.Queen;
import pacchess.piece.Rook;

public class PacChess
{

    private Piece[][] board;
    private HashMap<Long, ArrayList<Piece>> captured;
    private HashMap<Allegiance, King> kings;
    private HashMap<Long, ArrayList<Method>> startTurnMethods;
    private boolean isWhiteTurn;
    private Pawn blackPawn, whitePawn;
    private boolean modifyBlack, modifyWhite;
    private boolean modify;
    //TODO change kings back to private after testing is done
    public King bKing;
    public King wKing;

    public PacChess()
    {
	init();
    }

    private void init()
    {
	initDataStructures();
	initBoardPieces();
	isWhiteTurn = true;
    }

    public void initDataStructures()
    {
	board = new Piece[8][8];
	for (int r = 0; r < board.length; r++)
	{
	    for (int c = 0; c < board[0].length; c++)
	    {
		board[r][c] = new Empty();
	    }
	}
	kings = new HashMap<Allegiance, King>();
	captured = new HashMap<Long, ArrayList<Piece>>();
	captured.put(Allegiance.BLACK, new ArrayList<Piece>());
	captured.put(Allegiance.WHITE, new ArrayList<Piece>());
	modifyWhite = false;
	modifyBlack = false;
	blackPawn = null;
	whitePawn = null;
    }

    private void initBoardPieces()
    {
	//Initialize White Pieces
	Allegiance c = Allegiance.AWHITE;
	insertPiece(new Rook(c), "a1");
	insertPiece(new Knight(c), "b1");
	insertPiece(new Bishop(c), "c1");
	insertPiece(new Queen(c), "d1");
	wKing = new King(c, this);
	insertPiece(wKing, "e1");
	insertPiece(new Bishop(c), "f1");
	insertPiece(new Knight(c), "g1");
	insertPiece(new Rook(c), "h1");
	//PAWNS
	for (char i = 'a'; i < 'i'; i++)
	{
	    insertPiece(new Pawn(c), "" + i + '2');
	}

	//Initialize Black Pieces
	c = Allegiance.ABLACK;
	insertPiece(new Rook(c), "a8");
	insertPiece(new Knight(c), "b8");
	insertPiece(new Bishop(c), "c8");
	insertPiece(new Queen(c), "d8");
	bKing = new King(c, this);
	insertPiece(bKing, "e8");
	insertPiece(new Bishop(c), "f8");
	insertPiece(new Knight(c), "g8");
	insertPiece(new Rook(c), "h8");
	//PAWNS
	for (char i = 'a'; i < 'i'; i++)
	{
	    insertPiece(new Pawn(c), "" + i + '7');
	}

	kings.put(Allegiance.ABLACK, bKing);
	kings.put(Allegiance.AWHITE, wKing);
    }

    public boolean insertPiece(Piece p, int[] coord)
    {
	if (isEmpty(coord))
	{
	    if (p.isKing())
	    {
		((King) p).insertInto(coord);
	    }
	    board[coord[0]][coord[1]] = p;
	    return !isEmpty(coord);
	}
	return !isEmpty(coord);
    }

    public boolean insertPiece(Piece p, String coord)
    {
	return insertPiece(p, translateCoordinate(coord));
    }

    public boolean isEmpty(String coord)
    {
	return isEmpty(translateCoordinate(coord));
    }

    public boolean isEmpty(int[] coord)
    {
	Piece p = get(coord);
	return p.getID() == Piece.EMPTY;
    }

    public boolean isEmpty(int r, int c)
    {
	return isEmpty(new int[]
		{
		    r, c
		});
    }

    public boolean isWhiteTurn()
    {
	return isWhiteTurn;
    }

    public boolean isBlackTurn()
    {
	return !isWhiteTurn;
    }

    public boolean isValid(int[] coord)
    {
	return coord[0] > -1 && coord[0] < 8 && coord[1] > -1 && coord[1] < 8;
    }

    public boolean isValid(String coord)
    {
	return isValid(translateCoordinate(coord));
    }

    public boolean isValid(int r, int c)
    {
	return isValid(new int[]
		{
		    r, c
		});
    }

    public boolean columnGreater(int[] f, int[] s)
    {
	return f[1] > s[1];
    }

    public boolean columnGreater(String f, String s)
    {
	return columnGreater(translateCoordinate(f), translateCoordinate(s));
    }

    public boolean rowGreater(int[] f, int[] s)
    {
	return f[0] > s[0];
    }

    public boolean rowGreater(String f, String s)
    {
	return rowGreater(translateCoordinate(f), translateCoordinate(s));
    }

    public Piece get(int[] coord)
    {
	return get(coord[0], coord[1]);
    }

    public Piece get(int r, int c)
    {
	return board[r][c];
    }

    public Piece[][] board()
    {
	return board;
    }

    private boolean set(int[] coord, Piece p)
    {
	board[coord[0]][coord[1]] = p;
	if (p.isKing())
	{
	    ((King) p).move(coord);
	}
	return board[coord[0]][coord[1]] == p;
    }

    protected ArrayList<int[]> pawnValid(Piece p, Allegiance a, int[] coord)
    {
	ArrayList<int[]> valid = new ArrayList<int[]>();
	Pawn p2 = (Pawn) p;
	//allegiance to white
	if (a.isWhite())
	{
	    //straight in front
	    if (isValid(coord[0] - 1, coord[1]) && isEmpty(coord[0] - 1, coord[1]))
	    {
		valid.add(new int[]
			{
			    coord[0] - 1, coord[1]
			});
	    }
	    //two in front if pawn has not moved before
	    if (p2.notMoved() && isValid(coord[0] - 2, coord[1]) && isValid(coord[0] - 1, coord[1]) && isEmpty(coord[0] - 1, coord[1]) && isEmpty(coord[0] - 2, coord[1]))
	    {
		valid.add(new int[]
			{
			    coord[0] - 2, coord[1]
			});
	    }
	    //EnPessant when a piece is on the left
	    if (isValid(coord[0], coord[1] - 1) && get(coord[0], coord[1] - 1).isPawn() && ((Pawn) get(coord[0], coord[1] - 1)).isVulnerable())
	    {
		valid.add(new int[]
			{
			    coord[0] - 1, coord[1] - 1
			});
	    }
	    //EnPessant when a piece is on the right
	    if (isValid(coord[0], coord[1] + 1) && get(coord[0], coord[1] + 1).isPawn() && ((Pawn) get(coord[0], coord[1] + 1)).isVulnerable())
	    {
		valid.add(new int[]
			{
			    coord[0] - 1, coord[1] + 1
			});
	    }
	    //left diagonal capture
	    if (isValid(coord[0] - 1, coord[1] - 1) && get(coord[0] - 1, coord[1] - 1).getAllegiance().isBlack())
	    {

		valid.add(new int[]
			{
			    coord[0] - 1, coord[1] - 1
			});
	    }
	    //right diagonal capture
	    if (isValid(coord[0] - 1, coord[1] + 1) && get(coord[0] - 1, coord[1] + 1).getAllegiance().isBlack())
	    {
		valid.add(new int[]
			{
			    coord[0] - 1, coord[1] + 1
			});
	    }
	} //Allegiance to black
	else if (a.isBlack())
	{
	    //straight in front
	    if (isValid(coord[0] + 1, coord[1]) && isEmpty(coord[0] + 1, coord[1]))
	    {
		valid.add(new int[]
			{
			    coord[0] + 1, coord[1]
			});
	    }
	    //two in front if not moved yet
	    if (p2.notMoved() && isValid(coord[0] + 2, coord[1]) && isValid(coord[0] + 1, coord[1]) && isEmpty(coord[0] + 1, coord[1]) && isEmpty(coord[0] + 2, coord[1]))
	    {
		valid.add(new int[]
			{
			    coord[0] + 2, coord[1]
			});
	    }
	    //EnPessant when a piece is on the left
	    if (isValid(coord[0], coord[1] - 1) && get(coord[0], coord[1] - 1).isPawn() && ((Pawn) get(coord[0], coord[1] - 1)).isVulnerable())
	    {
		valid.add(new int[]
			{
			    coord[0] + 1, coord[1] - 1
			});
	    }
	    //EnPessant when a piece is on the right
	    if (isValid(coord[0], coord[1] + 1) && get(coord[0], coord[1] + 1).isPawn() && ((Pawn) get(coord[0], coord[1] + 1)).isVulnerable())
	    {
		valid.add(new int[]
			{
			    coord[0] + 1, coord[1] + 1
			});
	    }
	    //left diagonal capture
	    if (isValid(coord[0] + 1, coord[1] - 1) && get(coord[0] + 1, coord[1] - 1).getAllegiance().isWhite())
	    {
		valid.add(new int[]
			{
			    coord[0] + 1, coord[1] - 1
			});
	    }
	    //right diagonal capture
	    if (isValid(coord[0] + 1, coord[1] + 1) && get(coord[0] + 1, coord[1] + 1).getAllegiance().isWhite())
	    {
		valid.add(new int[]
			{
			    coord[0] + 1, coord[1] + 1
			});
	    }
	}
	return valid;
    }

    protected ArrayList<int[]> rookValid(Piece p, Allegiance a, int[] coord)
    {
	ArrayList<int[]> valid = new ArrayList<int[]>();
	int[] rshift = {-1,00,00,01};
	int[] cshift = {00,-1,01,00};
	for(int i=0;i<rshift.length;i++)
	{
	    int rs=rshift[i];
	    int cs=cshift[i];

	    for(int r=coord[0]+rs, c=coord[1]+cs ; isValid(r,c) && p.viableMove(get(r,c)) ; r+=rs, c+=cs)
	    {
		if(isEmpty(r,c))
		{
		    valid.add(new int[]{r,c});
		}
		else
		{
		    if(p.viableMove(get(r,c)))
		    {
			valid.add(new int[]{r,c});
			break;
		    }
		}
	    }
	}
	return valid;
    }

    protected ArrayList<int[]> rookValidOld(Piece p, Allegiance a, int[] coord)
    {
	ArrayList<int[]> valid = new ArrayList<int[]>();
	//Allegiance is not applicable in this case for movement as Rook
	//can move forward and backward

	//recurse left
	for (int c = coord[1] - 1; isValid(coord[0], c) && p.viableMove(get(coord[0], c)); c--)
	{

	    //checks to see if current spot is empty
	    if (isEmpty(coord[0], c))
	    {
		//if it is, add to valid moves arraylist and continue loop
		valid.add(new int[]
			{
			    coord[0], c
			});
	    } //a piece is there
	    else
	    {
		//checks to see if piece is a viable capture
		if (p.viableMove(get(coord[0], c)))
		{
		    //if it is, add to valid moves arraylist and break loop,
		    //because rook cannot move through pieces
		    valid.add(new int[]
			    {
				coord[0], c
			    });
		    break;
		}
	    }
	}
	//recurse up
	for (int r = coord[0] - 1; isValid(r, coord[1]) && p.viableMove(get(r, coord[1])); r--)
	{
	    //checks to see if current spot is empty
	    if (isEmpty(r, coord[1]))
	    {
		//if it is, add to valid moves arraylist and continue loop
		valid.add(new int[]
			{
			    r, coord[1]
			});
	    } //a piece is there
	    else
	    {
		//checks to see if piece is a viable capture
		if (p.viableMove(get(r, coord[1])))
		{
		    //if it is, add to valid moves arraylist and break loop,
		    //because rook cannot move through pieces
		    valid.add(new int[]
			    {
				r, coord[1]
			    });
		    break;
		}
	    }
	}
	//recurse right
	for (int c = coord[1] + 1; isValid(coord[0], c) && p.viableMove(get(coord[0], c)); c++)
	{
	    //checks to see if current spot is empty
	    if (isEmpty(coord[0], c))
	    {
		//if it is, add to valid moves arraylist and continue loop
		valid.add(new int[]
			{
			    coord[0], c
			});
	    } //a piece is there
	    else
	    {
		//checks to see if piece is a viable capture
		if (p.viableMove(get(coord[0], c)))
		{
		    //if it is, add to valid moves arraylist and break loop,
		    //because rook cannot move through pieces
		    valid.add(new int[]
			    {
				coord[0], c
			    });
		    break;
		}
	    }
	}
	//recurse down
	for (int r = coord[0] + 1; isValid(r, coord[1]) && p.viableMove(get(r, coord[1])); r++)
	{
	    //checks to see if current spot is empty
	    if (isEmpty(r, coord[1]))
	    {
		//if it is, add to valid moves arraylist and continue loop
		valid.add(new int[]
			{
			    r, coord[1]
			});
	    } //a piece is there
	    else
	    {
		//checks to see if piece is a viable capture
		if (p.viableMove(get(r, coord[1])))
		{
		    //if it is, add to valid moves arraylist and break loop,
		    //because rook cannot move through pieces
		    valid.add(new int[]
			    {
				r, coord[1]
			    });
		    break;
		}
	    }
	}
	return valid;
    }

    protected ArrayList<int[]> bishopValid(Piece p, Allegiance a, int[] coord)
    {
	ArrayList<int[]> valid = new ArrayList<int[]>();

	int[] rshift = {-1,-1,01,01};
	int[] cshift = {-1,01,-1,01};

	for(int i=0;i<rshift.length;i++)
	{
	    int rs=rshift[i];
	    int cs=cshift[i];

	    for(int r=coord[0]+rs, c=coord[1]+cs ; isValid(r,c) && p.viableMove(get(r,c)) ; r+=rs, c+=cs)
	    {
		if(isEmpty(r,c))
		{
		    valid.add(new int[]{r,c});
		}
		else
		{
		    if(p.viableMove(get(r,c)))
		    {
			valid.add(new int[]{r,c});
			break;
		    }
		}
	    }
	}
	return valid;
    }

    protected ArrayList<int[]> bishopValidOld(Piece p, Allegiance a, int[] coord)
    {
	ArrayList<int[]> valid = new ArrayList<int[]>();
	//recurse north-west
	    for (int r = coord[0] - 1, c = coord[1] - 1; isValid(r, c) && p.viableMove((get(r, c))); r--, c--)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, c
				});
			break;
		    }
		}
	    }
	    //recurse north-east
	    for (int r = coord[0] - 1, c = coord[1] + 1; isValid(r, c) && p.viableMove((get(r, c))); r--, c++)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, c
				});
			break;
		    }
		}
	    }
	    //recurse south-west
	    for (int r = coord[0] + 1, c = coord[1] + 1; isValid(r, c) && p.viableMove((get(r, c))); r++, c++)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, c
				});
			break;
		    }
		}
	    }
	    //recurse south-east
	    for (int r = coord[0] + 1, c = coord[1] - 1; isValid(r, c) && p.viableMove(get(r, c)); r++, c--)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, c
				});
			break;
		    }
		}
	    }
	    return valid;
    }

    protected ArrayList<int[]> knightValid(Piece p, Allegiance a, int[] coord)
    {

	ArrayList<int[]> valid = new ArrayList<int[]>();
	int[] rows = {-2,-2,-1,-1,02,02,01,01};
	int[] cols = {-1,01,-2,02,-1,01,-2,02};

	for(int i=0;i<rows.length&&i<cols.length;i++)
	{
	    if(isValid(coord[0]+rows[i],coord[1]+cols[i])
		    && p.viableMove(get(coord[0]+rows[i],coord[1]+cols[i])))
	    {
		valid.add(new int[]{ coord[0]+rows[i],coord[1]+cols[i] });
	    }
	}
	return valid;
    }

    public ArrayList<int[]> knightValidOld(Piece p, Allegiance a, int[] coord)
    {
	ArrayList<int[]> valid = new ArrayList<int[]>();
	int r;
	    int c;
	    //Space "1" on map
	    r = coord[0] - 1;
	    c = coord[1] - 2;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "2" on map
	    r = coord[0] - 2;
	    c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "3" on map
	    r = coord[0] - 2;
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "4" on map
	    r = coord[0] - 1;
	    c = coord[1] + 2;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "5" on map
	    r = coord[0] + 1;
	    c = coord[1] + 2;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "6" on map
	    r = coord[0] + 2;
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "7" on map
	    r = coord[0] + 2;
	    c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "8" on map
	    r = coord[0] + 1;
	    c = coord[1] - 2;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    return valid;
    }

    public ArrayList<int[]> kingValid(Piece p, Allegiance a, int[] coord)
    {
	ArrayList<int[]> valid = new ArrayList<int[]>();
	//north-west
	    //	King p2 = (King) p;
	    int r = coord[0] - 1;
	    int c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //north
	    r = coord[0] - 1;
	    c = coord[1];
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //north-east
	    r = coord[0] - 1;
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //east
	    r = coord[0];
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //south-east
	    r = coord[0] + 1;
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //south
	    r = coord[0] + 1;
	    c = coord[1];
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //south-west
	    r = coord[0] + 1;
	    c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //west
	    r = coord[0];
	    c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }


	    /*CASTLING
	    _________                  __  .__  .__
	    \_   ___ \_____    _______/  |_|  | |__| ____    ____
	    /    \  \/\__  \  /  ___/\   __\  | |  |/    \  / ___\
	    \     \____/ __ \_\___ \  |  | |  |_|  |   |  \/ /_/  >
	    \______  (____  /____  > |__| |____/__|___|  /\___  /
	    \/     \/     \/                    \//_____/
	     */
	    King p2 = (King) p;
	    //check to the left for clear path --- castling
	    r = coord[0];
	    c = coord[1];
	    if (!p2.inCheck() && p2.notMoved() && isEmpty(r, c - 1) && isEmpty(r, c - 2) && isEmpty(r, c - 3) && get(r, c - 4).isRook() && ((Rook) get(r, c - 4)).notMoved())
	    {
		valid.add(new int[]
			{
			    r, c - 2
			});
	    }
	    //check to the right for clear path --- castling
	    if (!p2.inCheck() && p2.notMoved() && isEmpty(r, c + 1) && isEmpty(r, c + 2) && get(r, c + 3).isRook() && ((Rook) get(r, c + 3)).notMoved())
	    {
		valid.add(new int[]
			{
			    r, c + 2
			});
	    }
	    return valid;
    }

    protected int[][] removeInvalid(ArrayList<int[]> valid, Piece p, Allegiance a, int[] coord)
    {
	/*
	 * remove all moves from the arraylist which would keep the king in check. pseudocode is located belo
	 * prereqs:
	start - array - 2 - array containing coordinates of piece being moved
	end - array - 2 - array containing coordinates of place pieece is being moved

	save the piece at coordinates(end) in variable temp
	move piece at (start) to (end)
	make sure to replace (start) with an empty marker
	check to see if the king for the current piece moved is in check because of the move. store in variable(inDanger)
	move piece in (end) back to (start)
	place piece stored in temp back in (end)

	IF king was put in danger by the move
	THEN remove the possible movement from the arraylist containing them
	choose not to increment the index because the size of the arraylist
	shrank

	REPEAT FOR ALL MOVES INSIDE ARRAYLIST
	 */


	boolean increment = true;
	for (int i = 0; i < valid.size(); i += increment ? 1 : 0, increment = true)
	{
	    int[] start = coord;
	    int[] end = valid.get(i);
	    Piece startP = get(start);
	    Piece endP = get(end);
	    set(end, startP);
	    set(start, new Empty());
	    //TODO fix this to use the hashmap later after everything else is done
	    boolean inDanger = false;
	    if (a.isWhite())
	    {
		if (wKing.inCheck())
		{
		    inDanger = true;
		}
	    } else if (a.isBlack())
	    {
		if (bKing.inCheck())
		{
		    inDanger = true;
		}
	    }
	    set(start, startP);
	    set(end, endP);
	    /*if(p.isKing())
	    {
	    ((King)p).move(start);
	    }*/
	    if (inDanger)
	    {
		valid.remove(i);
		increment = false;
	    }
	}
	return valid.toArray(new int[][]{});
    }

    public int[][] validMovesCoordinate(int[] coord)
    {
	Piece p = get(coord);
	Allegiance a = p.getAllegiance();
	ArrayList<int[]> moves = new ArrayList<int[]>();

	if(p.isEmpty())
	{
	    return new int[0][0];
	}
	if(p.isPawn())
	{
	    moves.addAll(pawnValid(p, a, coord));
	}
	if(p.isRook() || p.isQueen())
	{
	    moves.addAll(rookValid(p,a,coord));
	}
	if(p.isBishop() || p.isQueen())
	{
	    moves.addAll(bishopValid(p,a,coord));
	}
	if(p.isKnight())
	{
	    moves.addAll(knightValid(p,a,coord));
	}
	if(p.isKing())
	{
	    moves.addAll(kingValid(p,a,coord));
	}
	return removeInvalid(moves, p, a, coord);

    }

    public int[][] validMovesCoordinateOld(int[] coord)
    {
	Piece p = get(coord);
	ArrayList<int[]> valid = new ArrayList<int[]>();
	Allegiance a = p.getAllegiance();
	//Empty space condition, return 0 valid moves
	if (p.isEmpty())
	{
	    return new int[0][0];
	} /*
	 * Pawn condition:
	 * is conditional on whether the pawn belongs to black or white.
	 * as a rigid structure in this game, white always starts on the "closest" side
	 * which means white's rooks are placed at [a1] and [h1]
	 * therefore white's pawns move from 2-8 before they are turned into a new piece
	 * and black's pawns move from 7-1 before they are turned into a new piece
	 */ else if (p.isPawn())
	{
	    Pawn p2 = (Pawn) p;
	    //allegiance to white
	    if (a.isWhite())
	    {
		//straight in front
		if (isValid(coord[0] - 1, coord[1]) && isEmpty(coord[0] - 1, coord[1]))
		{
		    valid.add(new int[]
			    {
				coord[0] - 1, coord[1]
			    });
		}
		//two in front if pawn has not moved before
		if (p2.notMoved() && isValid(coord[0] - 2, coord[1]) && isValid(coord[0] - 1, coord[1]) && isEmpty(coord[0] - 1, coord[1]) && isEmpty(coord[0] - 2, coord[1]))
		{
		    valid.add(new int[]
			    {
				coord[0] - 2, coord[1]
			    });
		}
		//EnPessant when a piece is on the left
		if (isValid(coord[0], coord[1] - 1) && get(coord[0], coord[1] - 1).isPawn() && ((Pawn) get(coord[0], coord[1] - 1)).isVulnerable())
		{
		    valid.add(new int[]
			    {
				coord[0] - 1, coord[1] - 1
			    });
		}
		//EnPessant when a piece is on the right
		if (isValid(coord[0], coord[1] + 1) && get(coord[0], coord[1] + 1).isPawn() && ((Pawn) get(coord[0], coord[1] + 1)).isVulnerable())
		{
		    valid.add(new int[]
			    {
				coord[0] - 1, coord[1] + 1
			    });
		}
		//left diagonal capture
		if (isValid(coord[0] - 1, coord[1] - 1) && get(coord[0] - 1, coord[1] - 1).getAllegiance().isBlack())
		{

		    valid.add(new int[]
			    {
				coord[0] - 1, coord[1] - 1
			    });
		}
		//right diagonal capture
		if (isValid(coord[0] - 1, coord[1] + 1) && get(coord[0] - 1, coord[1] + 1).getAllegiance().isBlack())
		{
		    valid.add(new int[]
			    {
				coord[0] - 1, coord[1] + 1
			    });
		}
	    } //Allegiance to black
	    else if (a.isBlack())
	    {
		//straight in front
		if (isValid(coord[0] + 1, coord[1]) && isEmpty(coord[0] + 1, coord[1]))
		{
		    valid.add(new int[]
			    {
				coord[0] + 1, coord[1]
			    });
		}
		//two in front if not moved yet
		if (p2.notMoved() && isValid(coord[0] + 2, coord[1]) && isValid(coord[0] + 1, coord[1]) && isEmpty(coord[0] + 1, coord[1]) && isEmpty(coord[0] + 2, coord[1]))
		{
		    valid.add(new int[]
			    {
				coord[0] + 2, coord[1]
			    });
		}
		//EnPessant when a piece is on the left
		if (isValid(coord[0], coord[1] - 1) && get(coord[0], coord[1] - 1).isPawn() && ((Pawn) get(coord[0], coord[1] - 1)).isVulnerable())
		{
		    valid.add(new int[]
			    {
				coord[0] + 1, coord[1] - 1
			    });
		}
		//EnPessant when a piece is on the right
		if (isValid(coord[0], coord[1] + 1) && get(coord[0], coord[1] + 1).isPawn() && ((Pawn) get(coord[0], coord[1] + 1)).isVulnerable())
		{
		    valid.add(new int[]
			    {
				coord[0] + 1, coord[1] + 1
			    });
		}
		//left diagonal capture
		if (isValid(coord[0] + 1, coord[1] - 1) && get(coord[0] + 1, coord[1] - 1).getAllegiance().isWhite())
		{
		    valid.add(new int[]
			    {
				coord[0] + 1, coord[1] - 1
			    });
		}
		//right diagonal capture
		if (isValid(coord[0] + 1, coord[1] + 1) && get(coord[0] + 1, coord[1] + 1).getAllegiance().isWhite())
		{
		    valid.add(new int[]
			    {
				coord[0] + 1, coord[1] + 1
			    });
		}
	    }
	}
	/*
	 * Rook Condition:
	 * a rook moves in a four cardinal directions as far as possible
	 * his boundaries are defined by the board edges or by encountering another piece
	 * if the piece and the rook share the same allegiance (ex: both belong to black)
	 * then the rooks farthest move in that direction is one space before the encountered piece.
	 * however, if the rook does not share allegiance with the piece (They are opponents),
	 * and the opposing piece is not the opponent's king, then the rook may move on top
	 * of that piece, effectively 'capturing' it. the opposing piece is removed from the board.
	 */
	if (p.isRook() || p.isQueen())
	{
	    //Allegiance is not applicable in this case for movement as Rook
	    //can move forward and backward

	    //recurse left
	    for (int c = coord[1] - 1; isValid(coord[0], c) && p.viableMove(get(coord[0], c)); c--)
	    {

		//checks to see if current spot is empty
		if (isEmpty(coord[0], c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				coord[0], c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(coord[0], c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    coord[0], c
				});
			break;
		    }
		}
	    }
	    //recurse up
	    for (int r = coord[0] - 1; isValid(r, coord[1]) && p.viableMove(get(r, coord[1])); r--)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, coord[1]))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, coord[1]
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, coord[1])))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, coord[1]
				});
			break;
		    }
		}
	    }
	    //recurse right
	    for (int c = coord[1] + 1; isValid(coord[0], c) && p.viableMove(get(coord[0], c)); c++)
	    {
		//checks to see if current spot is empty
		if (isEmpty(coord[0], c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				coord[0], c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(coord[0], c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    coord[0], c
				});
			break;
		    }
		}
	    }
	    //recurse down
	    for (int r = coord[0] + 1; isValid(r, coord[1]) && p.viableMove(get(r, coord[1])); r++)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, coord[1]))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, coord[1]
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, coord[1])))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, coord[1]
				});
			break;
		    }
		}
	    }
	}
	/*
	 * Bishop condition:
	 * a bishop moves diagonally in four directions:
	 * north-west
	 * north-east
	 * south-west
	 * south-east
	 *
	 * The Bishops boundaries are defined by the edges of the board, or
	 * by encountering other pieces. if the encountered piece shares allegiance with
	 * the bishop (Ex: they both belong to white) then the bishop's valid movement ends
	 * one space before encountering the allied piece. if the piece does not share allegiance
	 * (belongs to the opponent)and is not the opponent's king, then the bishop's valid moves
	 * end on top of the piece. if the bishop moved to that square the piece he moved on top
	 * of would be 'captured' and removed from the board.
	 */
	if (p.isBishop() || p.isQueen())
	{
	    //recurse north-west
	    for (int r = coord[0] - 1, c = coord[1] - 1; isValid(r, c) && p.viableMove((get(r, c))); r--, c--)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, c
				});
			break;
		    }
		}
	    }
	    //recurse north-east
	    for (int r = coord[0] - 1, c = coord[1] + 1; isValid(r, c) && p.viableMove((get(r, c))); r--, c++)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, c
				});
			break;
		    }
		}
	    }
	    //recurse south-west
	    for (int r = coord[0] + 1, c = coord[1] + 1; isValid(r, c) && p.viableMove((get(r, c))); r++, c++)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, c
				});
			break;
		    }
		}
	    }
	    //recurse south-east
	    for (int r = coord[0] + 1, c = coord[1] - 1; isValid(r, c) && p.viableMove(get(r, c)); r++, c--)
	    {
		//checks to see if current spot is empty
		if (isEmpty(r, c))
		{
		    //if it is, add to valid moves arraylist and continue loop
		    valid.add(new int[]
			    {
				r, c
			    });
		} //a piece is there
		else
		{
		    //checks to see if piece is a viable capture
		    if (p.viableMove(get(r, c)))
		    {
			//if it is, add to valid moves arraylist and break loop,
			//because rook cannot move through pieces
			valid.add(new int[]
				{
				    r, c
				});
			break;
		    }
		}
	    }
	} /*
	 * Queen Condition:
	 * !!IMPORTANT!!
	 * QUEEN DOES NOT HAVE INDIVIDUAL CODE, SHE COMBINES THE BISHOP AND ROOK CODE
	 * !!!!!!!!!!!!!
	 * the Queen is a very powerful piece. to describe the queens movement pattern
	 * you would take a rook and a bishop and combine their movement capabilities.
	 * the queen can move laterally and diagonally in all 8 cardinal directions:
	 * north
	 * north-east
	 * east
	 * south-east
	 * south
	 * south-west
	 * west
	 * north-west
	 *
	 * The queen'ss boundaries are defined by the edges of the board, or
	 * by encountering other pieces. if the encountered piece shares allegiance with
	 * the queen (Ex: they both belong to white) then the queen's valid movement ends
	 * one space before encountering the allied piece. if the piece does not share allegiance
	 * (belongs to the opponent)and is not the opponent's king, then the queen's valid moves
	 * end on top of the piece. if the queen moved to that square the piece he moved on top
	 * of would be 'captured' and removed from the board.
	 */ /*
	 * Knight Condition:
	 * The knight moves very oddly compared to other pieces in chess.
	 * he can move regardless of if pieces are in his way.
	 * however his movement is limited in distance.
	 * The knight moves in an L Shape.
	 * one leg of the L is two space long and the other is one space long
	 * Using the shorter leg first or the longer leg right are both viable moves in chess
	 * for instance in the below diagram, X represents places the knight could move
	 * Key:
	 * 1-8 - Viable Moves made by knight
	 * K - The Knight Himself
	 * . - Empty Space
	 *
	 * 		.........
	 * 		...2.3...
	 * 		..1...4..
	 * 		....K....
	 * 		..8...5..
	 * 		...7.6...
	 * 		.........
	 */
	else if (p.isKnight())
	{
	    int r;
	    int c;
	    //Space "1" on map
	    r = coord[0] - 1;
	    c = coord[1] - 2;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "2" on map
	    r = coord[0] - 2;
	    c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "3" on map
	    r = coord[0] - 2;
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "4" on map
	    r = coord[0] - 1;
	    c = coord[1] + 2;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "5" on map
	    r = coord[0] + 1;
	    c = coord[1] + 2;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "6" on map
	    r = coord[0] + 2;
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "7" on map
	    r = coord[0] + 2;
	    c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //Space "8" on map
	    r = coord[0] + 1;
	    c = coord[1] - 2;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	} //KING MOVEMENT
	else if (p.isKing())
	{
	    //north-west
	    //	King p2 = (King) p;
	    int r = coord[0] - 1;
	    int c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //north
	    r = coord[0] - 1;
	    c = coord[1];
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //north-east
	    r = coord[0] - 1;
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //east
	    r = coord[0];
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //south-east
	    r = coord[0] + 1;
	    c = coord[1] + 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //south
	    r = coord[0] + 1;
	    c = coord[1];
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //south-west
	    r = coord[0] + 1;
	    c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }
	    //west
	    r = coord[0];
	    c = coord[1] - 1;
	    if (isValid(r, c) && p.viableMove(get(r, c)))
	    {
		valid.add(new int[]
			{
			    r, c
			});
	    }


	    /*CASTLING
	    _________                  __  .__  .__
	    \_   ___ \_____    _______/  |_|  | |__| ____    ____
	    /    \  \/\__  \  /  ___/\   __\  | |  |/    \  / ___\
	    \     \____/ __ \_\___ \  |  | |  |_|  |   |  \/ /_/  >
	    \______  (____  /____  > |__| |____/__|___|  /\___  /
	    \/     \/     \/                    \//_____/
	     */
	    King p2 = (King) p;
	    //check to the left for clear path --- castling
	    r = coord[0];
	    c = coord[1];
	    if (!p2.inCheck() && p2.notMoved() && isEmpty(r, c - 1) && isEmpty(r, c - 2) && isEmpty(r, c - 3) && get(r, c - 4).isRook() && ((Rook) get(r, c - 4)).notMoved())
	    {
		valid.add(new int[]
			{
			    r, c - 2
			});
	    }
	    //check to the right for clear path --- castling
	    if (!p2.inCheck() && p2.notMoved() && isEmpty(r, c + 1) && isEmpty(r, c + 2) && get(r, c + 3).isRook() && ((Rook) get(r, c + 3)).notMoved())
	    {
		valid.add(new int[]
			{
			    r, c + 2
			});
	    }

	}

	/*
	 * remove all moves from the arraylist which would keep the king in check. pseudocode is located belo
	 * prereqs:
	start - array - 2 - array containing coordinates of piece being moved
	end - array - 2 - array containing coordinates of place pieece is being moved

	save the piece at coordinates(end) in variable temp
	move piece at (start) to (end)
	make sure to replace (start) with an empty marker
	check to see if the king for the current piece moved is in check because of the move. store in variable(inDanger)
	move piece in (end) back to (start)
	place piece stored in temp back in (end)

	IF king was put in danger by the move
	THEN remove the possible movement from the arraylist containing them
	choose not to increment the index because the size of the arraylist
	shrank

	REPEAT FOR ALL MOVES INSIDE ARRAYLIST
	 */


	boolean increment = true;
	for (int i = 0; i < valid.size(); i += increment ? 1 : 0, increment = true)
	{
	    int[] start = coord;
	    int[] end = valid.get(i);
	    Piece startP = get(start);
	    Piece endP = get(end);
	    set(end, startP);
	    set(start, new Empty());
	    //TODO fix this to use the hashmap later after everything else is done
	    boolean inDanger = false;
	    if (a.isWhite())
	    {
		if (wKing.inCheck())
		{
		    inDanger = true;
		}
	    } else if (a.isBlack())
	    {
		if (bKing.inCheck())
		{
		    inDanger = true;
		}
	    }
	    set(start, startP);
	    set(end, endP);
	    /*if(p.isKing())
	    {
	    ((King)p).move(start);
	    }*/
	    if (inDanger)
	    {
		valid.remove(i);
		increment = false;
	    }


	}

	int[][] togo = valid.toArray(new int[valid.size()][]);
	return togo;

    }

    public String[] validMovesChess(int[] coord)
    {
	int[][] coordinates = validMovesCoordinate(coord);
	String[] chessCoordinates = new String[coordinates.length];
	for (int r = 0; r < coordinates.length; r++)
	{
	    chessCoordinates[r] = reverseCoordinate(coordinates[r]);
	}
	return chessCoordinates;
    }

    public Error move(String who, String where)
    {
	return move(translateCoordinate(who), translateCoordinate(where));
    }

    public Error move(int[] who, int[] where)
    {
	if (get(who).getAllegiance() == Allegiance.AWHITE)
	{
	    if (whitePawn != null)
	    {
		whitePawn.setVulnerable(false);
	    }
	    whitePawn = null;
	}
	if (get(who).getAllegiance() == Allegiance.ABLACK)
	{
	    if (blackPawn != null)
	    {
		blackPawn.setVulnerable(false);
	    }
	    blackPawn = null;
	}
	//TODO CHECK TO MAKE SURE ENPESSANT IS IMPLEMENTED CORRECTLY
	if (!isValid(who) || !isValid(where))
	{
	    return new Error(false, "Who or Where is Invalid. Out of Bounds of Board. \nwho: " + Arrays.toString(who) + "\nwhere: " + Arrays.toString(where));
	}
	int[][] moves = validMovesCoordinate(who);
	if (!arrayContains(moves, where))
	{
	    Piece whoP = get(who);
	    Piece whereP = get(where);
	    //player tried to move onto a piece of their own
	    if (whereP.getAllegiance() == whoP.getAllegiance())
	    {
		return new Error(false, whoP.getName() + "(" + reverseCoordinate(who) + ") cannot move onto piece\nof the same allegiance"
			+ " (" + whereP.getName() + ": " + reverseCoordinate(where) + ")");
	    } else
	    {
		return new Error(false, whoP.getName() + "(" + reverseCoordinate(who) + ") cannot move to " + reverseCoordinate(where) + ". invalid move");
	    }

	}

	Piece moved = get(who);

	//movement when castling
	if (get(who).isKing() && Math.max(who[1], where[1]) - Math.min(who[1], where[1]) == 2)
	{

	    //castle left
	    if (columnGreater(who, where))
	    {
		//set variables inside king
		((King) get(who)).move(new int[]
			{
			    who[0], who[1] - 2
			});

		//System.out.println(get(who).isKing());
		//System.out.println("set space to equal king: "+set(new int[]{who[0],who[1]-2},get(who)));
		//System.out.println("set space who to be empty: "+set(who,new Empty()));
		//System.out.println("move rook: "+set(new int[]{who[0],who[1]-1},get(new int[]{who[0],who[1]-4})));
		//System.out.println("Make rooks old space empty: "+set(new int[]{who[0],who[1]-4},new Empty()));
	    } //castle right
	    else if (columnGreater(where, who))
	    {
		//set variables inside king
		((King) get(who)).move(new int[]
			{
			    who[0], who[1] - 2
			});

		set(new int[]
			{
			    who[0], who[1] + 2
			}, get(who));
		set(who, new Empty());
		set(new int[]
			{
			    who[0], who[1] + 1
			}, get(new int[]
			{
			    who[0], who[1] + 3
			}));
		set(new int[]
			{
			    who[0], who[1] + 3
			}, new Empty());
	    }

	} //Movement for when pawn moves two spaces forward
	//TODO BROKEN/UNESSECARY CODE
	else if (get(who).isPawn() && Math.max(who[0], where[0]) - Math.min(who[0], where[0]) == 2)
	{
	    //TODO figure out how to turn this parameter off
	    ((Pawn) get(who)).setVulnerable(true);
	    if (get(who).getAllegiance() == Allegiance.AWHITE)
	    {
		whitePawn = (Pawn) get(who);
	    } else
	    {
		blackPawn = (Pawn) get(who);
	    }
	    Piece destination = get(where);
	    board[where[0]][where[1]] = board[who[0]][who[1]];
	    board[who[0]][who[1]] = new Empty();
	    if (!destination.isEmpty())
	    {
		captured.get(destination.getAllegiance().getID()).add(destination);
	    }
	} //movement for a pawn committing EnPessant
	else if (get(who).isPawn() && (columnGreater(who, where) || columnGreater(where, who)) && isEmpty(where))
	{
	    //enPessant left
	    if (columnGreater(who, where))
	    {
		set(where, get(who));
		set(who, new Empty());
		captured.get(get(new int[]
			{
			    who[0], who[1] - 1
			}).getAllegiance().getID()).add(get(new int[]
			{
			    who[0], who[1] - 1
			}));
		set(new int[]
			{
			    who[0], who[1] - 1
			}, new Empty());
	    } //EnPessant right
	    else if (columnGreater(where, who))
	    {
		set(where, get(who));
		set(who, new Empty());
		captured.get(get(new int[]
			{
			    who[0], who[1] + 1
			}).getAllegiance().getID()).add(get(new int[]
			{
			    who[0], who[1] + 1
			}));
		set(new int[]
			{
			    who[0], who[1] + 1
			}, new Empty());
	    }
	} //movement for everyone else
	else
	{
	    Piece destination = get(where);
	    board[where[0]][where[1]] = board[who[0]][who[1]];
	    board[who[0]][who[1]] = new Empty();
	    if (!destination.isEmpty())
	    {
		captured.get(destination.getAllegiance().getID()).add(destination);
	    }

	}

	//Set that a king rook or pawn have moved, disallowing special moves
	if (moved.isKing() || moved.isRook() || moved.isPawn())
	{
	    if (moved.isKing())
	    {
		((King) moved).setNotMoved(false);
	    } else if (moved.isRook())
	    {
		((Rook) moved).setNotMoved(false);
	    } else if (moved.isPawn())
	    {
		((Pawn) moved).setNotMoved(false);
	    }
	}
	return new Error();
    }

    //This array contains method compares down to the individual data, not the memory
    //address of the "key"
    public boolean arrayContains(int[][] array, int[] key)
    {

	for (int r = 0; r < array.length; r++)
	{
	    boolean match = true;
	    for (int c = 0; array[r].length == key.length && c < array[r].length; c++)
	    {
		match = match & array[r][c] == key[c];
	    }
	    if (match)
	    {
		return true;
	    }
	}
	return false;
    }

    public boolean arrayContains(String[] array, String key)
    {
	for (int i = 0; i < array.length; i++)
	{
	    if (array[i].equals(key))
	    {
		return true;
	    }
	}
	return false;
    }
    /*
    public void startTurn()
    {
    Long id = isWhiteTurn?Allegiance.WHITE:Allegiance.BLACK;
    //run for blacks turn beginning
    if(id==Allegiance.WHITE)
    {
    //vulnerable turn is over for pawn, set it back to normal
    if(modifyWhite)
    {
    whitePawn.setVulnerable(false);
    whitePawn=null;
    modifyWhite=false;
    }
    }
    //run for whites turn beginning
    else if(modifyBlack)
    {
    blackPawn.setVulnerable(false);
    blackPawn=null;
    modifyBlack=false;
    }
    }
     */

    public void endTurn()
    {
	isWhiteTurn = !isWhiteTurn;
    }

    public int[] translateCoordinate(String space)
    {
	if (space.length() > 2)
	{
	    throw new RuntimeException("Invalid Chess Coordinate: " + space + " called at translateCoordinate");
	}
	int[] coord = new int[2];
	coord[1] = Character.toLowerCase(space.charAt(0)) - 97;
	coord[0] = 8 - Integer.parseInt("" + space.charAt(1));
	return coord;
    }

    public String reverseCoordinate(int[] coord)
    {
	String togo = "";
	togo += (char) (coord[1] + 97);
	togo += "" + (8 - coord[0]);
	return togo;
    }

    public void destroy(PacChess togo)
    {
	for (int r = 0; r < togo.board.length; r++)
	{
	    for (int c = 0; c < togo.board[0].length; c++)
	    {
		togo.board[r][c] = null;
	    }
	}
	togo.board = null;
	togo.bKing = null;
	togo.captured = null;
	togo.kings = null;
    }

    public boolean inCheck(Allegiance a)
    {
	if (a.isWhite())
	{
	    return wKing.inCheck();
	}
	return bKing.inCheck();
    }

    public boolean inCheckmate(Allegiance a)
    {
	if (a.isWhite())
	{
	    return wKing.inCheckmate();
	}
	return bKing.inCheckmate();
    }
    /*
    public static void main(String[] args)
    {
    //System.out.println("Welcome to chess\nnew game started.");
    PacChess game = new PacChess();
    while(true)
    {


    for(char c='a';c<='h';c++)
    {
    for(int i=1;i<=8;i++)
    {
    for(Piece[] col: game.board())
    {
    for(Piece p:col)
    {
    if(p.getAllegiance()==Allegiance.ABLACK)
    {
    System.out.print("b");
    }
    else if(p.getAllegiance()==Allegiance.AWHITE)
    {
    System.out.print("w");
    }
    else
    {
    System.out.print(" ");
    }

    if(p.getID()==Piece.PAWN)
    {
    System.out.print("p ");
    }
    if(p.getID()==Piece.KNIGHT)
    {
    System.out.print("k ");
    }
    if(p.getID()==Piece.BISHOP)
    {
    System.out.print("b ");
    }
    if(p.getID()==Piece.ROOK)
    {
    System.out.print("r ");
    }
    if(p.getID()==Piece.KING)
    {
    System.out.print("K ");
    }
    if(p.getID()==Piece.QUEEN)
    {
    System.out.print("Q ");
    }
    if(p.getID()==Piece.EMPTY)
    {
    System.out.print("  ");
    }
    }
    //System.out.println();
    }
    //System.out.println("move "+c+i);
    String[] moves = game.validMovesChess(game.translateCoordinate(""+c+i));
    for(String s:moves)
    {
    //System.out.println("\t"+s);
    }
    (new Scanner(System.in)).nextLine();
    for(int b=0;b<20;b++){//System.out.println();}
    }
    }

    }

    }
     */
}