package game;

public class Coordinate
{

	public int x, y;
	

	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}


	public int getX()
	{
		return this.x; 
	}
	

	public int getY()
	{
		return this.y; 
	}
	
	public String toString()
	{
		return ("" + x + " " + y);
	}

}