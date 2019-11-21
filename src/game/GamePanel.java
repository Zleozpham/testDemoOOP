package game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;


public class GamePanel extends JPanel implements MouseListener, MouseMotionListener
{
    
    private static final long serialVersionUID = -266426690684141363L;
    private Game enclosingGame;  	// A reference back to the Game object that created 'this' object.
    public int mouseX;				// Tracks X position of mouse events
    public int mouseY;				// Tracks Y position of mouse events
    public boolean mouseIsPressed;	// Determines if mouse has been clicked or not
    public GamePanel (Game enclosingGame)
    {

    	this.addMouseListener(this); 			// Listen to our own mouse events.
    	this.addMouseMotionListener(this);		// Listen to mouse movements
        this.enclosingGame = enclosingGame;
    }

    public void paintComponent (Graphics g)
    {
        enclosingGame.draw (g);
    }

    public Coordinate getCoordinate()
    {
    	return new Coordinate(mouseX, mouseY);
    }
    public Dimension getMinimumSize ()
    {
        return new Dimension(800,600);
    }
    public Dimension getMaximumSize ()
    {
        return new Dimension(800,600);
    }
    public Dimension getPreferredSize ()
    {
        return new Dimension(800,600);
    }

	public void mouseClicked(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = true;
	}

	public void mouseEntered(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseExited(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mousePressed(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = true;
	}

	public void mouseReleased(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = true;
		
	}

	public void mouseDragged(MouseEvent e) 
	{	
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = false;
	}

	public void mouseMoved(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = false;

	}
}
