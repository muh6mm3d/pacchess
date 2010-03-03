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
package pacchess.base.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacchess.base.*;
import pacchess.piece.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


/**
 *
 * @author hrothgar
 */
public class PacChessGUI extends JFrame implements ActionListener,ComponentListener
{
    public static void main(String[] args)
    {
        PacChessGUI gui = new PacChessGUI();
    }
    private boolean isWhiteTurn;
    private JButton[][] buttons;
    private JButton active;
    private int[][] activeMoves;
    private PacChess logic;
    private Dimension size;
    private Icon defaultIcon;

    private HashMap<Allegiance,HashMap<Piece,Image>> images;


    private static final String IMAGE_PATH = "image/scaled/";

    public PacChessGUI()
    {
        this(true);
    }
    public PacChessGUI(boolean white)
    {
        super("Mobile Chess");
        isWhiteTurn=white;
        init();
    }
    public void init()
    {

	addComponentListener(this);
	Toolkit.getDefaultToolkit().setDynamicLayout(true);
        //set size of window and retrieve container
        size = new Dimension(800,800);
        Container cont = getContentPane();
	cont.addComponentListener(this);

        //set Layout to Grid: 8x8
        cont.setLayout(new GridLayout(8,8));

        //initializing logic class
        logic = new PacChess();

        //create a default imageicon and store it
        defaultIcon = new JButton().getIcon();

        //create hashmaps to hold images
        images = new HashMap<Allegiance,HashMap<Piece,Image>>();
	images.put(Allegiance.AWHITE,new HashMap<Piece,Image>());
	images.put(Allegiance.ABLACK,new HashMap<Piece,Image>());

        //ARRAY INITIALIZATION
            //Button array
        buttons = new JButton[8][8];
        for(int r=0;r<buttons.length;r++)
        {
            for(int c=0;c<buttons[0].length;c++)
            {
                buttons[r][c]=new JButton();
                buttons[r][c].addActionListener(this);
                Piece piece = logic.get(r,c);

                if(!piece.getAllegiance().isEmpty())
		{
                        //Add imageicon to array for reference
			initImage(piece);
                }
                
                defaultColorBoard(r,c);

                cont.add(buttons[r][c]);
            }
        }
	

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(size);
        setLocation(50, 0);
        setVisible(true);
	refreshBoard();
    }

    public void initImage(Piece p)
    {
	String path = p.getAllegiance()+p.getName()+".png";
//	System.out.println(path);

	URL url = this.getClass().getResource(IMAGE_PATH+path);
	Image piece = this.getToolkit().getImage(url);

	images.get(p.getAllegiance()).put(p, piece);
    }

    public void defaultColorBoard(int r, int c)
    {
        if(r%2==0&&c%2==0)
        {
            buttons[r][c].setBackground(Color.white);
        }
        else if(r%2==0&&c%2==1)
        {
            buttons[r][c].setBackground(Color.gray);
        }
        else if(r%2==1&&c%2==0)
        {
             buttons[r][c].setBackground(Color.gray);
        }
        else if(r%2==1&&c%2==1)
        {
            buttons[r][c].setBackground(Color.white);
        }
    }

    public int[] findButtonCoord(JButton button)
    {
        for(int r=0;r<buttons.length;r++)
        {
            for(int c=0;c<buttons.length;c++)
            {
                if(button == buttons[r][c])
                return new int[]{r,c};
            }
        }
        return new int[]{-1,-1};
    }

    public boolean refreshBoard()
    {
        for(int r=0;r<buttons.length;r++)
        {
            for(int c=0;c<buttons[0].length;c++)
            {
                Piece p = logic.get(r,c);
                JButton b = buttons[r][c];
                defaultColorBoard(r,c);
		
                if(logic.isEmpty(new int[]{r,c}))
                {
                    b.setEnabled(false);
                }

                if(!p.getAllegiance().isEmpty())
                {
		    Image image = images.get(p.getAllegiance()).get(p).getScaledInstance(b.getWidth(), b.getHeight()
			    , Image.SCALE_SMOOTH);
		    ImageIcon icon = new ImageIcon(image);
		    b.setIcon(icon);
		}
                else
                {
                    b.setIcon(defaultIcon);
                }
                /*
                if(p.getID() == Piece.EMPTY)
                {
                    buttons[r][c].setEnabled(false);
                }
                
                else
                {
                    if(logic.get(r,c).getAllegiance().isWhite())
                    {
                        buttons[r][c].setEnabled(isWhiteTurn);
                    }
                    else if(logic.get(r,c).getAllegiance().isBlack())
                    {
                        buttons[r][c].setEnabled(!isWhiteTurn);
                    }
                }
                 * 
                 */
            }
        }
        return true;
    }

    public void actionPerformed(ActionEvent e)
    {
    	Color moveColor = Color.orange;
        if(e.getSource() instanceof JButton)
        {
            int[] loc = findButtonCoord((JButton)e.getSource());
            if(active == null&&logic.get(loc).getAllegiance()==(isWhiteTurn?Allegiance.AWHITE:Allegiance.ABLACK))
            {
                active = (JButton)e.getSource();
                
                if(loc[0]==-1)
                {
                    throw new RuntimeException("button you clicked somehow doesnt exist on board. that means tom is a bad programmer.");
                }

                int[][] moves  = logic.validMovesCoordinate(loc);
                active.setBackground(moveColor);
                for(int[] coord:moves)
                {
                    buttons[coord[0]][coord[1]].setBackground(moveColor);
                    buttons[coord[0]][coord[1]].setEnabled(true);
                }
            }
            else if(e.getSource()==active)
            {
                refreshBoard();
                active=null;
            }
            else if(((JButton)e.getSource()).getBackground()==moveColor)
            {
                pacchess.base.Error success = logic.move(findButtonCoord(active), findButtonCoord((JButton)e.getSource()));
                if(!success.successful())
                {
                    System.out.println(success.toString());
                }
                active=null;
                isWhiteTurn=!isWhiteTurn;
                refreshBoard();

                //Check to see if game is finished
                if(isWhiteTurn)
                {
                    if(logic.inCheckmate(Allegiance.AWHITE)&&logic.inCheck(Allegiance.AWHITE))
                    {
                        JOptionPane.showMessageDialog(this, "Wow, White...how could you possibly suck so much at chess? That was a disgusting display of ineptitude...");
                    }
                }
                else
                {
                    if(logic.inCheckmate(Allegiance.ABLACK)&&logic.inCheck(Allegiance.ABLACK))
                    {
                        JOptionPane.showMessageDialog(this, "Black got Owned!");
                    }
                }
            }
	
        }
        //throw new UnsupportedOperationException("Not supported yet.");//TODO finish Action performed, remove unsuppported exception

    }

    public void componentResized(ComponentEvent ce) {
	    setSize(getHeight(),getHeight());
	    refreshBoard();
//	    System.out.println(buttons[0][0].getHeight());
    }

    public void componentMoved(ComponentEvent ce) {
	//throw new UnsupportedOperationException("Not supported yet.");
    }

    public void componentShown(ComponentEvent ce) {
	///throw new UnsupportedOperationException("Not supported yet.");
    }

    public void componentHidden(ComponentEvent ce) {
	//throw new UnsupportedOperationException("Not supported yet.");
    }
}
