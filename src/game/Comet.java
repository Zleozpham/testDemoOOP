package game;


public class Comet extends Enemy
{
	Comet(PathPosition p)
	{
		ImageLoader loader = ImageLoader.getLoader();
		this.enemy = loader.getImage("resources/comet.png");
		this.position = p;
		this.anchorX = -25;
		this.anchorY = -25;
		this.velocity = 8;
	}
	
}
