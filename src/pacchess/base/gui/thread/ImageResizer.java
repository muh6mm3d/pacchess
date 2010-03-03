/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pacchess.base.gui.thread;

import java.awt.Image;
import java.util.HashMap;
import pacchess.piece.Allegiance;
import pacchess.piece.Piece;

/**
 *
 * @author hrothgar
 */
public class ImageResizer extends javax.swing.SwingWorker
{
    private String imagePath;
    private Integer scale;
    private HashMap<Allegiance,HashMap<Piece,Image>> imageLocation, imageStore;

    public ImageResizer(String ip, int s,HashMap<Allegiance,HashMap<Piece,Image>> il,
	    HashMap<Allegiance,HashMap<Piece,Image>> is)
    {
	super();
	imagePath = ip;
	scale = s;
	imageLocation = il;
	imageStore = is;

    }

    @Override
    protected Object doInBackground() throws Exception
    {
	int progress = 0;
	for( Allegiance a : imageLocation.keySet() )
	{
	    for( Piece key : imageLocation.get(a).keySet() )
	    {
		Image large = imageLocation.get(a).get(key);
		imageStore.get(a).put(key, large.getScaledInstance(scale,
			scale,
			Image.SCALE_SMOOTH)); //TODO SCALING
		progress+=9;
		setProgress(Math.min(progress,100));
	    }
	}
	return null;
    }

    @Override
    public void done()
    {
	System.out.println("Done!");
    }

}
