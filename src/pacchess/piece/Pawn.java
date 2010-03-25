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

public class Pawn extends Piece
{
	private boolean notMoved,isVulnerable;
	public Pawn(Allegiance a)
	{
		super(PAWN,a,"Pawn");
		notMoved=true;
		isVulnerable=false;
	}
	public boolean notMoved() {
		return notMoved;
	}
	public void setNotMoved(boolean notMoved) {
		this.notMoved = notMoved;
	}
	public boolean isVulnerable() {
		return isVulnerable;
	}
	public void setVulnerable(boolean isVulnerable) {
		this.isVulnerable = isVulnerable;
	}

}