package game;

import java.util.List;

public class PathPosition
{	
	// Instance variables
	private int segment;			// The segment this position is on
	private double percentage;		// Distance along the current segment
	private List <Coordinate> path;	// List of path coordinates for current path

	PathPosition(List<Coordinate> points)
	{	// Initializes variables
		
		this.segment = 0;		//starting segment
		this.percentage = 0;	//starting percentage across segment
		this.path = points;		// stores list of coordinate points in path
	}
	

    public boolean isAtTheEnd ()
    {   
    	return segment == path.size()-1;	// ask how this works
    }

    public Coordinate getCoordinate ()
    {	
    	if(isAtTheEnd())
    		return path.get(path.size()-1);
    	
    	// variables for starting and ending position of the X coordinate
    	int startX = path.get(segment).x;
    	int endX = path.get(segment + 1).x;
    	
    	// variables for starting and ending position of the Y coordinate
    	int startY = path.get(segment).y;
    	int endY = path.get(segment + 1).y;
    	
    	// calculates the change in the X and Y position by taking the difference
    	int dX = endX - startX;
    	int dY = endY - startY;
 	
    	// sets the new position of the ball by adding change to start
    	int ballX = startX + (int) (dX*percentage);
    	int ballY = startY + (int) (dY*percentage);
    	
        return new Coordinate(ballX, ballY);    // returns new position
    }

    public void advance (double distance)
    {	
    	// checks if the object is at the end of the path
        if(isAtTheEnd())
        	return;		// path does not advance
    	
    	// variables for starting and ending position of the X coordinate
    	int startX = path.get(segment).x;
    	int endX = path.get(segment + 1).x;
    	
    	// variables for starting and ending position of the Y coordinate
    	int startY = path.get(segment).y;
    	int endY = path.get(segment + 1).y;
    	
    	// calculates the change in the X and Y position by taking the difference
    	int dX = endX - startX;
    	int dY = endY - startY;
    	
    	// uses the Pythagorean theorem to calculate the new position
    	double length = Math.sqrt((double) (dX*dX) + (double) (dY*dY));
    	double unit = 1/length;	// variable for the unit change in position as a percent of length
    	
    	percentage += unit*distance;
    	if(percentage > 1)
    	{
    		segment ++;										// increment segment
    		distance = distance-(1-percentage)*length;	// decrease distance by length
    		percentage = 0;									// set percent to 0
    		advance(distance);								// advance remaining distance
    	}
    }
}