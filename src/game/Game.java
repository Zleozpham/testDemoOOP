package game;

import java.awt.*;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


enum GameState { SETUP, UPDATE, DRAW, WAIT, END }


public class Game implements Runnable
{

    public static void main (String[] args)
    {
        new Game ();
    }
    

    private Image backdrop;				// background star image		
    private PathPoints line;			// path coordinates
    
    private GamePanel gamePanel;		// gamePanel object 
    private GameState state;	   		// The current game state
    
    private int frameCounter;			// keeps track of frame updates
    private long lastTime;				// keeps track of time
    
    private boolean placingBattleShip01;	// true if tower is being placed
    private Tower newBattleShip01; 		// variable to hold new tower objects
    private double elapsedTime;			// time trackers
    
    private boolean placingBattleShip02;			// true if tower is being placed
    private Tower newBattleShip02; 				// variable to hold new tower objects
    private boolean gameIsOver;			// indicates if game is lost
    private boolean gameIsWon;			// indicates if game is won
    
    int livesCounter; 					// counter for lives left
    int scoreCounter;					// points the user earns
    int killsCounter;					// number of enemies destroyed
    
    /* create enemies */
    List<Enemy> enemies;				// list of enemy objects
    
    /* create towers */
    List<Tower> towers;					// list of tower objects
    
    /* create effects */
    List<Effect> effects;				// list of effect objects
    

    public Game ()
    {

        state = GameState.SETUP;
        gamePanel = new GamePanel(this);
        // Create a thread of execution and run it.
        
        Thread t = new Thread(this);
        t.start();  // Our run method is now executing!!!
    }
    
    /**
     * The entry point for the second thread of execution.  Our
     * game loop is entirely within this method.
     */
    public void run ()
    {
        // Loop forever, or until the user closes the game window,
        //   whichever comes first.  ;)
        
        while (true)
        {
            // Test our game state, and do the appropriate action.
            
            if (state == GameState.SETUP)
            {
                doSetupStuff();
            }
            
            else if (state == GameState.UPDATE)
            {
                doUpdateTasks();
            }
            
            else if (state == GameState.DRAW)
            {
                gamePanel.repaint();  // redraw screen
                try { Thread.sleep(5); } catch (Exception e) {}
            }
            
            else if (state == GameState.WAIT)
            {
                try { Thread.sleep(100); } catch (Exception e) {}
                state = GameState.UPDATE;
            }
            
            else if (state == GameState.END)
            {

            }
        }
    }

    private void doSetupStuff ()
    {

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.setTitle("Basil Vetas's Tower Defense Game");
        f.setContentPane(gamePanel);
        f.pack();
        f.setVisible(true); 
        

		ImageLoader loader = ImageLoader.getLoader();
        backdrop = loader.getImage("resources/stars.jpg");
        
        JOptionPane.showMessageDialog(null,  "Luật chơi:\n" +
        		"1. Đặt các tháp trên bản đồ để ngăn kẻ thù tiếp cận Trái đất.\n" +
        		"2. Chiến hạm 1 bắn đạn rẻ hơn, chiến hạm 2 bắn mìn. \n" +
        		"3. Bạn kiếm được tiền để ngăn chặn kẻ thù, nhưng khi trò chơi tiếp diễn, kẻ thù mới sẽ tấn công.\n" +
        		"4. Nếu bạn ngăn chặn 500 kẻ thù bạn thắng, nhưng nếu bạn mất 10 mạng thì trò chơi đã kết thúc.");
        
        // fill counters
        livesCounter = 10;		// gives the player 10 lives
        scoreCounter = 500;		// give the user 500 points to begin
        killsCounter = 0;		// begin with 0 kills
        
        // Reset the frame counter and time 
        frameCounter = 0;
        lastTime = System.currentTimeMillis();

		ClassLoader myLoader = this.getClass().getClassLoader();
        InputStream pointStream = myLoader.getResourceAsStream("resources/path_1.txt");
        Scanner s = new Scanner (pointStream);
        line  = new PathPoints(s);

        // Fill enemy list with new LinkedList
        enemies = new LinkedList<Enemy>();
        
        // Fill tower list with new LinkedList
        towers = new LinkedList<Tower>();
        
        // Fill effects list with new LinkedList
        effects = new LinkedList<Effect>();
        
        // initialize
        placingBattleShip01 = false;
        newBattleShip01 = null;
        
        // initialize
        placingBattleShip02 = false;
        newBattleShip02 = null;
        	
        // initialize
        gameIsOver = false;
    	gameIsWon = false;
        
        // Change the game state to start the game.
        state = GameState.UPDATE;  // You could also enter the 'DRAW' state.
    }

    private void doUpdateTasks()
    {	
    	if(gameIsOver)
    	{	state = GameState.DRAW;
    		return;
    	}
    	
    	if(gameIsWon)
    	{	state = GameState.DRAW;
    		return;
    	}
    	
    	// See how long it was since the last frame.
        long currentTime = System.currentTimeMillis();  // Integer number of milliseconds since 1/1/1970.
        elapsedTime = ((currentTime - lastTime) / 1000.0);  // Compute elapsed seconds
        lastTime = currentTime;  // Our current time is the next frame's last time
    	for(Tower t: new LinkedList<Tower>(towers))
    	{	
    		t.interact(this, elapsedTime);
    		
    	}
    	for(Effect e: new LinkedList<Effect>(effects))
    	{	
    		e.interact(this, elapsedTime);
    		if(e.isDone())
    			effects.remove(e);	// add to list that has reached the end	
    	}
    	
    	// Advance each enemy on the path.
    	for(Enemy e: new LinkedList<Enemy>(enemies))
    	{	
    		e.advance();
     		if(e.getPosition().isAtTheEnd())
    		{
    			enemies.remove(e);	// add to list that has reached the end
    			livesCounter--;		// if they have reached the end, reduce lives
    		}

    	}
    	
        // Fill elements in an enemy list
        this.generateEnemies();
        
    	// increments frame counter 
    	frameCounter++;
    	
    	// Place towers if user chooses
    	this.placeBattleShip01s();
    	this.placeBattleship02s();
    	
    	if(livesCounter <= 0)
    	{	gameIsOver = true;
    		livesCounter = 0;
    	}
    	
    	if(killsCounter >= 500)
    	{	gameIsWon = true;
    		killsCounter = 500;
    	}
        state = GameState.DRAW;
    }

    public void draw(Graphics g)
    {
        if (state != GameState.DRAW)
            return;
        g.drawImage(backdrop, 0, 0, null); 
     
        // Draw the path
        g.setColor(new Color (0,76, 153));
        int[] xPos = new int[]{0, 64, 118, 251, 298, 344, 396, 416, 437, 459, 460, 498, 542, 600, 600, 568, 535, 509, 490, 481, 456, 414, 345, 287, 227, 98, 0};
        int[] yPos = new int[]{329, 316, 291, 189, 163, 154, 165, 186, 233, 344, 364, 415, 444, 461, 410, 396, 372, 331, 226, 195, 151, 117, 105, 117, 143, 244, 280};
        g.fillPolygon(xPos, yPos, 27);
        
        // Draw planet 
        g.setColor(new Color(65,105,225));
        g.fillArc(550, 385, 100, 100, 90, 180);
        g.setColor(Color.GREEN);
        int[] xCor = new int[]{600, 588, 574, 566, 557, 557, 563, 572, 576, 584, 600};
        int[] yCor = new int[]{459, 464, 462, 453, 454, 448, 438, 435, 422, 414, 415};
        g.fillPolygon(xCor, yCor, 11);

    	for(Enemy e: new LinkedList<Enemy>(enemies))
    		e.draw(g);
    	
        // draw all towers in list
        for(Tower t: new LinkedList<Tower>(towers))
        	t.draw(g);
        	
    	// draw all towers in list
    	for(Effect s: new LinkedList<Effect>(effects))
    		s.draw(g);
    	
        // draw menu bar
        g.setColor(Color.WHITE);
        g.fillRect(600, 0, 200, 600);
        
        // draw score & life counters to menu bar
        g.setColor(Color.BLACK);
        g.drawString("Lives Remaining: " + livesCounter, 605, 100);	// lives counter
        g.drawString("Money Earned: " + scoreCounter, 605, 150);	// score counter
        g.drawString("Enemies Stopped: " + killsCounter, 605, 200);
        g.drawString("BattleShip01 Cost: 100", 610, 380);				// cost for black hole towers
        g.drawString("BattleShip02 Cost: 300", 640, 530);					// cost for sun towers
        g.drawString("Earth Defense", 600, 50);					// writes title
        g.drawLine(600, 50, 800, 50);								// underscore
        g.drawString("Towers", 640, 240);							// writes towers
        g.drawLine(620, 240, 780, 240);								// underscore	
        
        // draw box around blackhole icon
        g.setColor(new Color(224, 224, 224));
        g.fillRect(650, 250, 100, 100);
        
        // draw tower in menu area
        BattleShip01 battleShip01 = new BattleShip01(new Coordinate(700, 300));
        battleShip01.draw(g);
        
        // draw box around sun icon
        g.setColor(new Color(224, 224, 224));
        g.fillRect(650, 400, 100, 100);
        
        // draw tower in menu area
        BattleShip02 battleShip02 = new BattleShip02(new Coordinate(700, 450));
        battleShip02.draw(g);
        
        // draws blackhole object with mouse movements
        if(newBattleShip01 != null)
        	newBattleShip01.draw(g);

        // draws sun object with mouse movements
        if(newBattleShip02 != null)
        	newBattleShip02.draw(g);
        
        ImageLoader loader = ImageLoader.getLoader();	
		Image endGame = loader.getImage("resources/game_over.png"); // load game over image
    	
        if(livesCounter <= 0)										// if game is lost
        	g.drawImage(endGame, 0, 0, null);						// draw "game over"

		if(killsCounter >= 500)										// if game is lost
		{	g.setFont(new Font("Braggadocio", Font.ITALIC, 90));		
        	g.drawString("You Win!!!", 10, 250);					// draw "game over"
		}
        
        state = GameState.WAIT;
    }

    public void generateEnemies()
    {	
    	// adds enemies to list dependent on how many frames have passed
    	if(frameCounter % 30 == 0)								// slow 
    	{
    		enemies.add(new Asteroid(line.getStart()));
    	}
 		else if(frameCounter % 25 == 0 && frameCounter >= 50)	// slow
 		{
 			enemies.add(new Asteroid(line.getStart())); 
 		}
	 	else if(frameCounter % 20 == 0 && frameCounter >= 100)	// medium
	 	{
	 		enemies.add(new Asteroid(line.getStart())); 
	 		enemies.add(new Alien(line.getStart()));
	 	}	
 		else if(frameCounter % 15 == 0 && frameCounter >= 150)	// medium
 		{
 			enemies.add(new Asteroid(line.getStart())); 
 			enemies.add(new Alien(line.getStart()));
 		}
	 	else if(frameCounter % 10 == 0 && frameCounter >= 200)	// fast
	 	{
	 		enemies.add(new Asteroid(line.getStart())); 
	 		enemies.add(new Alien(line.getStart()));
	 		enemies.add(new Comet(line.getStart()));
	 	}
	 	else if(frameCounter % 5 == 0 && frameCounter >= 250)	// fast
	 	{
	 		enemies.add(new Asteroid(line.getStart())); 
	 		enemies.add(new Alien(line.getStart()));
	 		enemies.add(new Comet(line.getStart()));
	 	}
    }
    
    /**
     * Method for placing black holes on the screen
     */
    public void placeBattleShip01s()
    {
    	/* I need to make it so you can't place towers on path or off the screen */
    	
    	 // variable to hold mouse location
    	Coordinate mouseLocation = new Coordinate(gamePanel.mouseX, gamePanel.mouseY);
    	
    	// moves the tower object as mouse moves
    	if(gamePanel.mouseX > 650 && gamePanel.mouseX < 750 && 
    		gamePanel.mouseY > 250 && gamePanel.mouseY < 350 && 
    		gamePanel.mouseIsPressed && scoreCounter >= 100)
    	{	// if mouse is pressed on tower icon, create a new object
	    		placingBattleShip01 = true;
	    		newBattleShip01 = new BattleShip01(mouseLocation);
    	}    
    	else if(gamePanel.mouseX > 0 && gamePanel.mouseX < 600 && 
        	gamePanel.mouseY > 0 && gamePanel.mouseY < 600 && 
        	gamePanel.mouseIsPressed && placingBattleShip01
        	&& line.distanceToPath(gamePanel.mouseX, gamePanel.mouseY) > 60)
    	{	// if mouse is pressed on game screen, place tower on game screen
	    		newBattleShip01.setPosition(mouseLocation);
	    		towers.add(new BattleShip01(mouseLocation));
	    		scoreCounter -= 100;
	    		newBattleShip01 = null;
	    		placingBattleShip01 = false;
    	}
    	
    	// moves tower object with mouse movements
    	if(newBattleShip01 != null)
    	{
    		newBattleShip01.setPosition(mouseLocation);
    	}	
    }
    
    /**
     * Method for placing suns on the screen
     */
    public void placeBattleship02s()
    {
    	/* I need to make it so you can't place towers on path or off the screen */
    	
    	 // variable to hold mouse location
    	Coordinate mouseLocation = new Coordinate(gamePanel.mouseX, gamePanel.mouseY);
    	
    	// moves the tower object as mouse moves
    	if(gamePanel.mouseX > 650 && gamePanel.mouseX < 750 && 
    		gamePanel.mouseY > 400 && gamePanel.mouseY < 500 && 
    		gamePanel.mouseIsPressed && scoreCounter >= 300)
    	{	// if mouse is pressed on tower icon, create a new object
	    		placingBattleShip02 = true;
	    		newBattleShip02 = new BattleShip02(mouseLocation);
    	}    
    	else if(gamePanel.mouseX > 0 && gamePanel.mouseX < 600 && 
        	gamePanel.mouseY > 0 && gamePanel.mouseY < 600 && 
        	gamePanel.mouseIsPressed && placingBattleShip02
        	&& line.distanceToPath(gamePanel.mouseX, gamePanel.mouseY) > 60)
    	{	// if mouse is pressed on game screen, place tower on game screen
	    		newBattleShip02.setPosition(mouseLocation);
	    		towers.add(new BattleShip02(mouseLocation));
	    		scoreCounter -= 300;
	    		newBattleShip02 = null;
	    		placingBattleShip02 = false;
    	}
    	
    	// moves tower object with mouse movements
    	if(newBattleShip02 != null)
    	{
    		newBattleShip02.setPosition(mouseLocation);
    	}	
    }
}	