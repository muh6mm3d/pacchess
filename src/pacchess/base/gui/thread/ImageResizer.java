/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pacchess.base.gui.thread;

import java.awt.Image;
import java.util.HashMap;
import pacchess.base.gui.PacChessGUI;
import pacchess.piece.Allegiance;
import pacchess.piece.Piece;

/**
 *
 * @author hrothgar
 */
public class ImageResizer extends javax.swing.SwingWorker
{
    private static final int IMAGE_SCALING = Image.SCALE_SMOOTH;
    private String imagePath;
    private Integer scale;
    private HashMap<Allegiance,HashMap<Piece,Image>> imageLocation, imageStore;
    private PacChessGUI parent;

    public ImageResizer(String ip, int s,HashMap<Allegiance,HashMap<Piece,Image>> il,
	    HashMap<Allegiance,HashMap<Piece,Image>> is, PacChessGUI gui)
    {
	super();
	imagePath = ip;
	scale = s;
	imageLocation = il;
	imageStore = is;
	parent = gui;
    }

    @Override
    protected Object doInBackground() throws Exception
    {
	System.out.println("starting...");
	int progress = 0;
	for( Allegiance a : imageLocation.keySet() )
	{
	    for( Piece key : imageLocation.get(a).keySet() )
	    {
		Image large = imageLocation.get(a).get(key);
		/*if(imageStore.get(a).get(key)!=null && scale)
		{

		}*/
		imageStore.get(a).put(key, large.getScaledInstance(scale,
			scale,
			IMAGE_SCALING));
		progress+=3;
		setProgress(Math.min(progress,100));
	    }
	}
	setProgress(100);
	return null;
    }

    @Override
    public void done()
    {
	System.out.println("Done!");
    }

}