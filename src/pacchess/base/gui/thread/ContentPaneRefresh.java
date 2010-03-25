/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pacchess.base.gui.thread;

import java.awt.Color;
import java.awt.Image;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import pacchess.base.PacChess;
import pacchess.base.gui.LoadingBarFrame;
import pacchess.base.gui.PacChessGUI;
import pacchess.piece.Allegiance;
import pacchess.piece.Piece;

/**
 *
 * @author hrothgar
 */
public class ContentPaneRefresh extends javax.swing.SwingWorker
{
    private PacChess logic;
    private JButton buttons[][];
    private PacChessGUI parent;
    private LoadingBarFrame progressBar;
    private HashMap<Allegiance,HashMap<Piece,Image>> images;
    private Icon defaultIcon;

    public ContentPaneRefresh(PacChess logic,
	    JButton[][] buttons,
	    PacChessGUI parent,
	    HashMap<Allegiance,HashMap<Piece,Image>> images,
	    Icon defaultIcon) {
	super();
	this.logic=logic;
	this.buttons=buttons;
	this.parent=parent;
	//this.associatedProgressBar=assoc;
	this.images=images;
	this.defaultIcon=defaultIcon;
    }

    public void addProgressBar(LoadingBarFrame bar)
    {
	progressBar = bar;
    }

    @Override
    protected Object doInBackground() throws Exception {
	refreshBoard();
	return null;
    }

    @Override
    public void done()
    {
	parent.setVisible(true);
	progressBar.dispose();
    }

    protected boolean refreshBoard()
    {
	int progress = 0;
        for(int r=0;r<buttons.length;r++)
        {
            for(int c=0;c<buttons[0].length;c++)
            {
                Piece p = logic.get(r,c);
                JButton b = buttons[r][c];
                defaultColorBoard(r,c);
		b.setSize(parent.getHeight()/8,parent.getHeight()/8);

                if(logic.isEmpty(new int[]{r,c}))
                {
                    b.setEnabled(false);
                }

                if(!p.getAllegiance().isEmpty())
                {
		    Image image = images.get(p.getAllegiance()).get(p);
		    ImageIcon icon = new ImageIcon(image);
		    b.setIcon(icon);
		}
                else
                {
                    b.setIcon(defaultIcon);
                }
		progress+=1;
		setProgress(Math.min(progress,100));
            }
        }
        return true;
    }

    protected void defaultColorBoard(int r, int c)
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

    

}