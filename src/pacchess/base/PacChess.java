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
import pacchess.piece.MovementSensitive;
import pacchess.piece.Pawn;
import pacchess.piece.Piece;
import pacchess.piece.Queen;
import pacchess.piece.Rook;

public class PacChess
{

    protected Piece[][] board;
    protected HashMap<Long, ArrayList<Piece>> captured;
    protected HashMap<Allegiance, King> kings;
    protected HashMap<Long, ArrayList<Method>> startTurnMethods;
    protected Pawn blackPawn, whitePawn;
    protected King bKing;
    protected King wKing;

    public PacChess()
    {
	//call main initialization
	init();
    }


    private void init()
    {
	//Initialize different parts of the logic
	initDataStructures();
	initBoardPieces();
    }
    public void initDataStructures()
    {
	//initialize an empty board of 8x8, the standard size for a chess board
	board = new Piece[8][8];
	for (int r = 0; r < board.length; r++)
	{
	    for (int c = 0; c < board[0].length; c++)
	    {
		board[r][c] = new Empty();
	    }
	}
	//hashmap to hold kings, instead of seperate variable, to allow arbitrary method calls
	kings = new HashMap<Allegiance, King>();

	//hashmaps containing Arraylists which hold captured pieces
	captured = new HashMap<Long, ArrayList<Piece>>();
	captured.put(Allegiance.BLACK, new ArrayList<Piece>());
	captured.put(Allegiance.WHITE, new ArrayList<Piece>());

	//variables used to disable the vulnerability of a pawn the turn after it has moved two spaces
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
	// WHITE PAWNS
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
	//BLACK PAWNS
	for (char i = 'a'; i < 'i'; i++)
	{
	    insertPiece(new Pawn(c), "" + i + '7');
	}

	//place the kings into the hashmap for later reference
	kings.put(Allegiance.ABLACK, bKing);
	kings.put(Allegiance.AWHITE, wKing);
    }

    
    public boolean insertPiece(Piece p, int[] coord)
    {
	//checks to make sure the place the piece is being inserted is empty
	//  also check if piece is king, if it is it sets the custom coordinate variable inside the king
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
	//convert data and call real class
	return insertPiece(p, translateCoordinate(coord));
    }


    public boolean isEmpty(String coord)
    {
	return isEmpty(translateCoordinate(coord));
    }
    public boolean isEmpty(int[] coord)
    {
	//retrieves pieces and checks its ID to see if it represents an empty space
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


    public boolean isValid(int[] coord)
    {
	//checks to make sure piece is within the bounds of -1<R<9 and -1<C<9
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
	//return piece on board at Coord
	return get(coord[0], coord[1]);
    }
    public Piece get(int r, int c)
    {
	return board[r][c];
    }
    public Piece get(String coord)
    {
	return get(translateCoordinate(coord));
    }


    private boolean set(int[] coord, Piece p)
    {
	//absolute movement of piece, doesnt do any checking.
	//  this is called by the real movement method which does the checking
	board[coord[0]][coord[1]] = p;
	if (p.isKing())
	{
	    ((King) p).move(coord);
	}
	return board[coord[0]][coord[1]] == p;
    }
    private boolean set(String coord, Piece p)
    {
	return set(translateCoordinate(coord),p);
    }


    protected ArrayList<int[]> pawnValid(Piece p, Allegiance a, int[] coord)
    {
	//create an arraylist to return the moves in
	ArrayList<int[]> possible = new ArrayList<int[]>();
	//cast the piece to Pawn Object
	Pawn p2 = (Pawn) p;

	//Movement for a white pawn, dictated by negative row movement toward the top of the board
	if (a.isWhite())
	{
	    //checks straight in front for an empty space
	    if (isValid(coord[0] - 1, coord[1]) && isEmpty(coord[0] - 1, coord[1]))
	    {
		possible.add(new int[] {coord[0] - 1, coord[1]} );
	    }
	    //if the pawn has not moved yet, it may move two forward
	    if (p2.notMoved() && isValid(coord[0] - 2, coord[1]) && isValid(coord[0] - 1, coord[1]) && isEmpty(coord[0] - 1, coord[1]) && isEmpty(coord[0] - 2, coord[1]))
	    {
		possible.add(new int[] {coord[0] - 2, coord[1]} );
	    }
	    //check to see if En passant is available to the left of the Pawn
	    if (isValid(coord[0], coord[1] - 1) && get(coord[0], coord[1] - 1).isPawn() && ((Pawn) get(coord[0], coord[1] - 1)).isVulnerable())
	    {
		possible.add(new int[] {coord[0] - 1, coord[1] - 1} );
	    }
	    //check to see if En passant is available to the right of the Pawn
	    if (isValid(coord[0], coord[1] + 1) && get(coord[0], coord[1] + 1).isPawn() && ((Pawn) get(coord[0], coord[1] + 1)).isVulnerable())
	    {
		possible.add(new int[] {coord[0] - 1, coord[1] + 1} );
	    }
	    //standard diagonal capture to the front and left of the pawn
	    if (isValid(coord[0] - 1, coord[1] - 1) && get(coord[0] - 1, coord[1] - 1).getAllegiance().isBlack())
	    {

		possible.add(new int[] {coord[0] - 1, coord[1] - 1} );
	    }
	    //standard diagonal capture to the front and right of the pawn
	    if (isValid(coord[0] - 1, coord[1] + 1) && get(coord[0] - 1, coord[1] + 1).getAllegiance().isBlack())
	    {
		possible.add(new int[] {coord[0] - 1, coord[1] + 1} );
	    }
	}
	//Movement for a black pawn, dictated by positive row movement toward the bottom of the board
	else if (a.isBlack())
	{
	    //checks straight in front for an empty space
	    if (isValid(coord[0] + 1, coord[1]) && isEmpty(coord[0] + 1, coord[1]))
	    {
		possible.add(new int[] {coord[0] + 1, coord[1]} );
	    }
	    //if the pawn has not moved yet, it may move two forward
	    if (p2.notMoved() && isValid(coord[0] + 2, coord[1]) && isValid(coord[0] + 1, coord[1]) && isEmpty(coord[0] + 1, coord[1]) && isEmpty(coord[0] + 2, coord[1]))
	    {
		possible.add(new int[] {coord[0] + 2, coord[1]} );
	    }
	    //check to see if En passant is available to the left of the Pawn
	    if (isValid(coord[0], coord[1] - 1) && get(coord[0], coord[1] - 1).isPawn() && ((Pawn) get(coord[0], coord[1] - 1)).isVulnerable())
	    {
		possible.add(new int[] {coord[0] + 1, coord[1] - 1} );
	    }
	    //check to see if En passant is available to the right of the Pawn
	    if (isValid(coord[0], coord[1] + 1) && get(coord[0], coord[1] + 1).isPawn() && ((Pawn) get(coord[0], coord[1] + 1)).isVulnerable())
	    {
		possible.add(new int[] {coord[0] + 1, coord[1] + 1} );
	    }
	    //standard diagonal capture to the front and left of the pawn
	    if (isValid(coord[0] + 1, coord[1] - 1) && get(coord[0] + 1, coord[1] - 1).getAllegiance().isWhite())
	    {
		possible.add(new int[] {coord[0] + 1, coord[1] - 1} );
	    }
	    //standard diagonal capture to the front and right of the pawn
	    if (isValid(coord[0] + 1, coord[1] + 1) && get(coord[0] + 1, coord[1] + 1).getAllegiance().isWhite())
	    {
		possible.add(new int[] {coord[0] + 1, coord[1] + 1} );
	    }
	}
	//return possible moves for the pawn
	return possible;
    }
    protected ArrayList<int[]> rookValid(Piece p, Allegiance a, int[] coord)
    {
	//create arraylist to return possible moves
	ArrayList<int[]> possible = new ArrayList<int[]>();

	//rshift and cshift are equals to the directions that the rook is able to move in,
	//  which are the four cardinal direction. The loop will iterate through rshift and cshift and
	//	look in all directions the rook is able to move
	int[] rshift = {-1,00,00,01};
	int[] cshift = {00,-1,01,00};
	//iterate through rshift and cshift
	for(int i=0;i<rshift.length;i++)
	{
	    //convenience variables
	    int rs=rshift[i];
	    int cs=cshift[i];

	    //continue to recurse in the current direction of rs and cs until a reason arises to stop
	    for(int r=coord[0]+rs, c=coord[1]+cs ; isValid(r,c) && p.viableMove(get(r,c)) ; r+=rs, c+=cs)
	    {
		//if the current spot is empty, its a possible move, so add it to the arraylist
		if(isEmpty(r,c))
		{
		    possible.add(new int[]{r,c});
		}
		//otherwise...
		else
		{
		    //check if the spot is a viable move.
		    //	a viable move constitutes an enemy piece or an empty space
		    if(p.viableMove(get(r,c)))
		    {
			possible.add(new int[]{r,c});
			break;
		    }
		}
	    }
	}
	//return the possible moves for the rook
	return possible;
    }
    protected ArrayList<int[]> bishopValid(Piece p, Allegiance a, int[] coord)
    {
	//create an arraylist to return possible moves for the bishop or queen in
	ArrayList<int[]> possible = new ArrayList<int[]>();

	//rshift and cshift are equals to the directions that the bishop is able to move in,
	//  which are the four combinations of cardinal directions. The loop will iterate through rshift and cshift and
	//	look in all directions the bishop is able to move
	int[] rshift = {-1,-1,01,01};
	int[] cshift = {-1,01,-1,01};

	//iterate through rshift and cshift
	for(int i=0;i<rshift.length;i++)
	{
	    //convenience variables
	    int rs=rshift[i];
	    int cs=cshift[i];

	    //continue to recurse in the current direction of rs and cs until a reason arises to stop
	    for(int r=coord[0]+rs, c=coord[1]+cs ; isValid(r,c) && p.viableMove(get(r,c)) ; r+=rs, c+=cs)
	    {
		//if the current spot is empty, its a possible move, so add it to the arraylist
		if(isEmpty(r,c))
		{
		    possible.add(new int[]{r,c});
		}
		//otherwise...
		else
		{
		    //check if the spot is a viable move.
		    //	a viable move constitutes an enemy piece or an empty space
		    if(p.viableMove(get(r,c)))
		    {
			possible.add(new int[]{r,c});
			break;
		    }
		}
	    }
	}
	//return the possible moves for the bishop
	return possible;
    }
    protected ArrayList<int[]> knightValid(Piece p, Allegiance a, int[] coord)
    {
	//create an arraylist to return the possible moves for the knight
	ArrayList<int[]> possible = new ArrayList<int[]>();

	//each index pair in rows and cols represent a space relative to the knights
	//  current position that are possible moves
	int[] rows = {-2,-2,-1,-1,02,02,01,01};
	int[] cols = {-1,01,-2,02,-1,01,-2,02};

	//iterate through the knights possible moves to remove any that are obviously invalid,
	// for instance, ones that land outside the board, or ones that land on allied pieces.
	for(int i=0;i<rows.length&&i<cols.length;i++)
	{
	    //convenience variables
	    int
		    r = rows[i],
		    c = cols[i];

	    //check if the current move is inside the bounds of the board
	    //	if so, add to the possible moves
	    if(isValid(coord[0]+r, coord[1]+c)
		    && p.viableMove(get(coord[0]+rows[i],coord[1]+cols[i])))
	    {
		possible.add(new int[]{ coord[0]+rows[i],coord[1]+cols[i] });
	    }
	}
	//return the possible moves for the knight
	return possible;
    }
    protected ArrayList<int[]> kingValid(Piece p, Allegiance a, int[] coord)
    {
	//create arraylist to return possible moves for the king
	ArrayList<int[]> possible = new ArrayList<int[]>();

	//the index pairs in rshift and cshift correspond with possible moves relative
	//  to the king's current position.
	int[]
		rshift = {-1,-1,-1,00,00,01,01,01},
		cshift = {-1,00,01,-1,01,-1,00,01};

	//iterate through the king's possible moves to remove any that are obviously
	//  invalid, for instance, ones that land outside the board, or land on allied pieces.
	for(int i=0; i<rshift.length; i++)
	{
	    //convenience variables
	    int
		    r = coord[0] + rshift[i],
		    c = coord[1] + cshift[i];

	    //check if the move is inside the board and not on an allied piece
	    //	if both are satisfied, add it to the arraylist of possible moves.
	    if( isValid(r,c) && p.viableMove(get(r,c)) )
	    {
		possible.add(new int[] {r,c});
	    }
	}

	//Special conditions to handle the castling of the king.

	//cast the king piece to a king Object for easy handling.
	King p2 = (King) p;

	//convenience variables
	int r = coord[0];
	int c = coord[1];

	//check to the left of the king to make sure there is a clear path for castling.
	if (!p2.inCheck() && p2.notMoved() && isEmpty(r, c - 1) && isEmpty(r, c - 2) && isEmpty(r, c - 3) && get(r, c - 4).isRook() && ((Rook) get(r, c - 4)).notMoved())
	{
	    possible.add(new int[] {r, c - 2});
	}
	//check to the right of the king to make sure there is a clear path for castling.
	if (!p2.inCheck() && p2.notMoved() && isEmpty(r, c + 1) && isEmpty(r, c + 2) && get(r, c + 3).isRook() && ((Rook) get(r, c + 3)).notMoved())
	{
	    possible.add(new int[] {r, c + 2});
	}

	//return possible moves for the king
	return possible;
    }


    protected int[][] removeInvalid(ArrayList<int[]> valid, Piece p, Allegiance a, int[] coord)
    {
	/*
	prereqs:
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

	//boolean which determines if the for-loop's variable is to be incremented.
	//  the reason it would not be incremented would be if a move was removed,
	//	therefore pushing the new move down into the current index.
	boolean increment = true;
	
	//iterate through all moves and remove invalid ones
	for (int i = 0; i < valid.size(); i += increment ? 1 : 0, increment = true)
	{
	    //declare starting and ending spaces for the piece for the current move being checked.
	    int[] start = coord;
	    int[] end = valid.get(i);

	    //convencience variables to access starting and ending space
	    Piece startP = get(start);
	    Piece endP = get(end);

	    //move the piece to the designated ending spot
	    set(end, startP);
	    set(start, new Empty());

	    //TODO fix this to use the hashmap later after everything else is done

	    //check if the movement of the piece put the king in check.
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

	    //move the pieces back to their original places
	    set(start, startP);
	    set(end, endP);

	    //if the move put the king in check, remove it from the list as it is
	    //	 not a valid move.
	    if (inDanger)
	    {
		valid.remove(i);
		increment = false;
	    }
	}

	//return the valid moves as 2-d array of integer coordinates
	return valid.toArray(new int[][]{});
    }
    public int[][] validMovesCoordinate(int[] coord)
    {
	//convenience variables
	Piece p = get(coord);
	Allegiance a = p.getAllegiance();

	//create and arraylist to store the possible moves in
	ArrayList<int[]> moves = new ArrayList<int[]>();

	//call the appropriate method for the piece
	if(p.isEmpty())
	{
	    return new int[0][0];
	}
	else if(p.isPawn())
	{
	    moves.addAll(pawnValid(p, a, coord));
	}
	//call for rook or queen, because queen moves like a rook and a bishop
	if(p.isRook() || p.isQueen())
	{
	    moves.addAll(rookValid(p,a,coord));
	}
	//call for bishop or queen, because queen moves like a rook and bishop
	if(p.isBishop() || p.isQueen())
	{
	    moves.addAll(bishopValid(p,a,coord));
	}
	else if(p.isKnight())
	{
	    moves.addAll(knightValid(p,a,coord));
	}
	else if(p.isKing())
	{
	    moves.addAll(kingValid(p,a,coord));
	}

	//remove all the invalid moves from the arraylist, convert it to a 2-d integer array and return it.
	return removeInvalid(moves, p, a, coord);

    }
    public String[] validMovesChess(int[] coord)
    {
	//retrieve all the valid coordinates in an integer list
	int[][] coordinates = validMovesCoordinate(coord);

	//create an empty list of strings the same size as the integer list
	String[] chessCoordinates = new String[coordinates.length];

	//iterate through the integer list, converting it to equivalent string coordinates,
	//  and add them to the appropriate index in the string list
	for (int r = 0; r < coordinates.length; r++)
	{
	    chessCoordinates[r] = translateCoordinate(coordinates[r]);
	}

	//return the new list of string coordinates
	return chessCoordinates;
    }


    public Error move(String who, String where)
    {
	return move(translateCoordinate(who), translateCoordinate(where));
    }
    public Error move(int[] who, int[] where)
    {
	//if the piece being moved is a white piece, if a pawn was made vulnerable
	//  on white's last turn, take away that vulnerability
	if (get(who).getAllegiance() == Allegiance.AWHITE)
	{
	    if (whitePawn != null)
	    {
		whitePawn.setVulnerable(false);
	    }
	    whitePawn = null;
	}
	//if the piece being moved is a black piece, if a pawn was made vulnerable
	//  on black's last turn, take away that vulnerability
	if (get(who).getAllegiance() == Allegiance.ABLACK)
	{
	    if (blackPawn != null)
	    {
		blackPawn.setVulnerable(false);
	    }
	    blackPawn = null;
	}

	//check to make the sure spaces are valid, if not return an error
	if (!isValid(who) || !isValid(where))
	{
	    return new Error(false, "Who or Where is Invalid. Out of Bounds of Board. \nwho: " + Arrays.toString(who) + "\nwhere: " + Arrays.toString(where));
	}

	//retrieve the list of valid moves for the piece being moved in an integer list
	int[][] moves = validMovesCoordinate(who);

	//piece was not being moved to a valid spot, determine error
	if (!arrayContains(moves, where))
	{
	    //convenience variables
	    Piece whoP = get(who);
	    Piece whereP = get(where);

	    //player tried to move onto a piece of their own
	    if (whereP.getAllegiance() == whoP.getAllegiance())
	    {
		return new Error(false, whoP.getName() + "(" + translateCoordinate(who) + ") cannot move onto piece\nof the same allegiance"
			+ " (" + whereP.getName() + ": " + translateCoordinate(where) + ")");
	    }
	    //piece attempted an invalid move
	    else
	    {
		return new Error(false, whoP.getName() + "(" + translateCoordinate(who) + ") cannot move to " + translateCoordinate(where) + ". invalid move");
	    }
	}

	//convenience variable, retrieve the piece being moved.
	Piece moved = get(who);
	
	//handler for movement when castling. this requires extra code because two pieces
	//  must be moved on the same turn instead of just one.

	//determines if the movement is castling. if so...
	if (get(who).isKing() && Math.max(who[1], where[1]) - Math.min(who[1], where[1]) == 2)
	{
	    //handle a castling to the left side of the board, negative column shift.
	    if (columnGreater(who, where))
	    {		
		set(new int[]{who[0],who[1]-2},get(who));
		set(who,new Empty());
		set(new int[]{who[0],who[1]-1},get(new int[]{who[0],who[1]-4}));
		set(new int[]{who[0],who[1]-4},new Empty());
	    }
	    //handle a castling to the right side of the board, positive column shift.
	    else if (columnGreater(where, who))
	    {
		set(new int[]{who[0], who[1] + 2}, get(who));
		set(who, new Empty());
		set(new int[]{who[0], who[1] + 1}, get(new int[]{who[0], who[1] + 3}));
		set(new int[]{who[0], who[1] + 3}, new Empty());
	    }

	}
	//movement for all other pieces, which only require one piece to be moved
	else
	{
	    //retrieve the piece in the destination
	    Piece destination = get(where);

	    //move the piece to the destination
	    set(where, moved);
	    set(who, new Empty());

	    //if the piece in the destination was not an empty square,
	    //	place it in the arraylist of captures
	    if (!destination.isEmpty())
	    {
		captured.get(destination.getAllegiance().getID()).add(destination);
	    }

	}

	//if a king, pawn, or rook moves set that it has moved to disallow special moves
	if (moved.isKing() || moved.isRook() || moved.isPawn())
	{
	    //cast to movement sensitive interface, containing movement methods
	    ((MovementSensitive)moved).setNotMoved(false);
	}
	//return an error object saying there were no errors
	return new Error();
    }


    //This array contains method compares down to the individual data, not the memory
    //address of the "key"
    public boolean arrayContains(int[][] array, int[] key)
    {
	//iterate through the first depth of the array, comparing against the "column" arrays
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
	//iterate through array and look for key,
	//  if it is found return true, otherwise false
	for (int i = 0; i < array.length; i++)
	{
	    if (array[i].equals(key))
	    {
		return true;
	    }
	}
	return false;
    }


    //TODO translateCoordinate(string) method requires more assert checks for the argument
    public int[] translateCoordinate(String space)
    {
	//check to make sure the string coordinate is of valid length
	if (space.length() > 2)
	{
	    //if not, throws an exception.
	    throw new RuntimeException("Invalid Chess Coordinate: " + space + " called at translateCoordinate");
	}

	//create an empty integer coordinate list
	int[] coord = new int[2];

	//convert the string coordinates and store them as integer coordinates
	coord[1] = Character.toLowerCase(space.charAt(0)) - 97;
	coord[0] = 8 - Integer.parseInt("" + space.charAt(1));

	//return the integer coordinate list
	return coord;
    }
    //TODO translateCoordinate(int[]) requires more assert checks for the argument
    public String translateCoordinate(int[] coord)
    {
	//create an empty string to hold the string coordinate
	String togo = "";

	//convert the integer coordinate to a string coordinate and store them
	togo += (char) (coord[1] + 97);
	togo += "" + (8 - coord[0]);

	//return the string coordinate
	return togo;
    }
    
    public boolean inCheck(Allegiance a)
    {
	//extensor method for the King objects, to see if the appropriate king is in check
	if (a.isWhite())
	{
	    return wKing.inCheck();
	}
	return bKing.inCheck();
    }
    public boolean inCheckmate(Allegiance a)
    {
	//extensor method for the king objects, to see if the appropriate king is in checkmate
	if (a.isWhite())
	{
	    return wKing.inCheckmate();
	}
	return bKing.inCheckmate();
    }
 
}