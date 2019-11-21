package game;

import java.awt.Graphics;
import java.awt.Image;

abstract public class Enemy 
{
	/* instance variables */
	protected PathPosition position;	// holds current position of enemy
	protected Image enemy;				// holds image of enemy
	protected int anchorX;				// shifts position on x axis
	protected int anchorY;				// shifts position on y axis
	protected double velocity; 			// increases or decreases advance speed

	public void advance()
	{
		position.advance(10 + velocity);	// advances position 10 units plus velocity
	}

	public void draw(Graphics g)
	{
		// Draws Enemy object
		Coordinate c = position.getCoordinate();
		g.drawImage(enemy, c.x + anchorX, c.y + anchorY, null);

	}

	public PathPosition getPosition()
	{
		return position;
	}
	
}
