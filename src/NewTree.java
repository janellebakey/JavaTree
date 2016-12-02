

import java.applet.Applet;
import java.awt.*;


public class NewTree extends Applet
{

	private final int    APPLET_WIDTH  = 800;
	private final int    APPLET_HEIGHT = 800;
	private final double START_SIZE    = 110.0;
	private final double START_ANGLE   = 180.0;
	private final double CHANGE_ANGLE  =  60.0;
	private final double FACTOR        =   2.0;
	private final double MIN_SIZE      =  10.0;
	private final int MIN_BRANCH = 4;
	private final int MAX_BRANCH = 8;
	private final int MAX_CLOUD = 50;
	private final int MIN_TREESIZE = 175;
	private final int MAX_TREESIZE = 225;

	Color leafColors[] = new Color[3];
	Color cloudColors[] = new Color[3];
	double randomAngle = pickRandom(-CHANGE_ANGLE, CHANGE_ANGLE);
	
	// Initialize the applet.
	public void init()  
	{  
		setSize(APPLET_WIDTH, APPLET_HEIGHT);
		setBackground(new Color(140,221,242));
		
	}

	// Create the drawing that displays on the applet.
	public void paint(Graphics page) 
	{
		//set array of colors
		leafColors[0] = new Color(247,234,45);
		leafColors[1] = new Color(247,86,45);
		leafColors[2] = new Color(247,146,45);
		cloudColors[0] = new Color(230,230,250);
		cloudColors[1] = new Color(23,104,238);
		cloudColors[2] = new Color(72,61,139);
		page.setColor(new Color(83,146,104));
		page.fillRect(0, 500, this.getWidth(), this.getHeight()+500);
		
		for (int i=0; i<30; i++)
		{
			int recursiveCount = (int) pickRandom(2,4);
			int cloudW = (int) pickRandom(10, 75);
			int cloudH = (int) pickRandom(10, 75);
			int cloudY = (int) pickRandom(1, getHeight()/4);
			int cloudX = (int) pickRandom(1, getWidth());
			drawCloud(page, cloudX, cloudY, cloudW, cloudH, recursiveCount);
		}
		for (int i=0; i< 5; i++)
		{
			int treeX = (int) pickRandom(20, APPLET_WIDTH-20);
			int treeY = (int) pickRandom(500, APPLET_HEIGHT);
			double treeSize = pickRandom(MIN_TREESIZE, MAX_TREESIZE);
			drawTree(page, treeX, treeY, treeSize, START_ANGLE);
		}
	}
	
	//draws tree
	//draws trunk, calls method to draw branch, calls method to draw leaf
	public void drawTree(Graphics page, int x, int y, double size, double angle )
	{  
		page.setColor(new Color(77,58,34)); 
		
		Point endPoint = calculatePoint(x, y, size, angle);
		page.drawLine(x, y, endPoint.x, endPoint.y);
		
		int width = (int) (size/10);
		int[] xPoints = {(x-(width/2)),(x+(width/2)),(endPoint.x + (width/2)),(endPoint.x-(width/2))};
		int[] yPoints = {y,y,endPoint.y,endPoint.y};
		Polygon branch = new Polygon(xPoints, yPoints, 4);
		page.fillPolygon(branch);

		int numBranches = (int) pickRandom(MIN_BRANCH,MAX_BRANCH);
 		drawBranches(numBranches,page, endPoint.x, endPoint.y, size, angle);  // drawBranches takes care of the angle
	}

	private void drawBranches(int numBranches, Graphics page, int x, int y, double size, double angle) 
	{	
		if (numBranches >= 1 && size>MIN_SIZE) 
		{
			drawBranches(numBranches-1, page, x, y, size, angle);
			double nextSize = size/FACTOR;	// all my uses of size require reducing what I was passed
			//pickRandom((size*.8), size); //
			double nextAngle = angle+ pickRandom(-CHANGE_ANGLE, CHANGE_ANGLE);// negative or positive delta	
			//int height = (int) (size/10);
			//drawLeaf(page, x, y, height, height, nextAngle, order);
			drawTree(page, x, y, nextSize, nextAngle);
		}
		
		int order=3;
		int count = order;
		while (count > 0)
		{
			double randAngle = angle + pickRandom(-CHANGE_ANGLE, CHANGE_ANGLE);
			page.setColor(leafColors[count-1]);
			drawLeaf(page, x,y, (int)(size/3), (int)(size/3), randAngle, order);
			count--;
		}	
	}

	public Point calculatePoint( int x, int y, double size, double degree )
	{  
		Point point = new Point(x, y);
		double radians = Math.PI/180 * degree;

		point.x += (int)(size * Math.sin(radians));
		point.y += (int)(size * Math.cos(radians)); 
		return point;
	}

	public double pickRandom(double min, double max) //return random number between min and max
	{
		return (Math.random() * (max-min) + min);	
	}

	public void drawLeaf(Graphics g, int x, int y, int w, int h, double angle, int order)
	{
		Point startP = new Point(x+w/2, y);
		int sideLength = (int) Math.round(Math.min(w, h)/2.0*Math.sqrt(3));

		// left point is 60 degrees left of center point + whatever angle the user wants the leaf
		Point leftP = calculatePoint(startP.x, startP.y, sideLength, angle-CHANGE_ANGLE);
		Point rightP = calculatePoint(startP.x, startP.y, sideLength, angle+CHANGE_ANGLE);
		
		int[] xPoints = {x, leftP.x, rightP.x, startP.x, leftP.x, x};
		int[] yPoints = {y, leftP.y, rightP.y, startP.y, leftP.y, y};
		Polygon leaf = new Polygon(xPoints, yPoints, 6);

		fractal(g, leftP, rightP, order);
		fractal(g, rightP, startP, order);
		fractal(g, startP, leftP, order);
		
		g.fillPolygon(leaf);
	}

	private void fractal(Graphics g, Point p1, Point p2, int order)
	{	
		if (order == 0)
		{
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		else
		{
			int deltaX = p2.x-p1.x;
			int deltaY = p2.y-p1.y;

			// trisect the line
			int x2 = p1.x + deltaX / 3;
			int y2 = p1.y + deltaY / 3;
			

			// some hexagon identities
			int x3 = (int) (0.5 * (p1.x+p2.x) + Math.sqrt(3) * (p1.y-p2.y)/6);
			int y3 = (int) (0.5 * (p1.y+p2.y) + Math.sqrt(3) * (p2.x-p1.x)/6);

			int x4 = p1.x + 2 * deltaX /3;
			int y4 = p1.y + 2 * deltaY /3;
			
			int[] xFractal = {p1.x, x2, x3, x4, p1.x};
			int[] yFractal = {p1.y, y2, y3, y4, p1.y};
			Polygon fractal = new Polygon(xFractal, yFractal, 5);
			g.fillPolygon(fractal);
			
			fractal(g, p1, new Point(x2, y2), order-1);
			fractal(g, new Point(x2, y2), new Point(x3, y3), order-1);
			fractal(g, new Point(x3, y3), new Point(x4, y4), order-1);
			fractal(g, new Point(x4, y4), p2, order-1);
		}
	}

	void drawCloud( Graphics page, int x, int y, int w, int h, int order)
	{	
		if (order <= 1)
			return;
		page.setColor(cloudColors[(int) (pickRandom(0,3))]);
		page.fillOval(x, y, w, h);
		int centerX = x+(w/2) - (h/2);
		int centerY = y+(w/2) - (h/2);
		for (int i=0; i < order; i++)
		{
			double newAngle = pickRandom(-CHANGE_ANGLE, CHANGE_ANGLE);
			int newDistance = (int) pickRandom(-MIN_SIZE,MIN_SIZE);
			Point newP = calculatePoint((centerX+newDistance), (centerY+newDistance), h, newAngle);
			int newW = w + (int) pickRandom(1,MAX_CLOUD);
			int newH = h + (int) pickRandom(1,MAX_CLOUD);
			drawCloud(page, newP.x, newP.y, newW, newH, order-1);
		}
	}

}
