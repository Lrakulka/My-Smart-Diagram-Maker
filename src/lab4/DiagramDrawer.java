package lab4;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;

// Class uses to draw diagram
public class DiagramDrawer extends Canvas{
	
	private static final long serialVersionUID = 1L;
    private int x0, y0, rBig1, rBig2, rSmall1, rSmall2; // Position and radius of diagram circle   
    float sum = 0; // Show summary of values     
    private int angles[] = {360};
    private String names[] = {""}; // Nemes of diagram columns
    private Graphics g;
    private BufferStrategy bs;
    
    public float getSum(){
    	return sum;
    }
    
    public Point getCenter(){
    	return new Point(x0, y0);
    }
    
    public int[] getAngles(){
    	return angles;
    }
    
    public int getBigRadiusW(){
    	return rBig1;
    }
    
    public int getBigRadiusH(){
    	return rBig2;
    }
    
    public int getSmallRadiusW(){
    	return rSmall1;
    }
    
    public int getSmallRadiusH(){
    	return rSmall2;
    }
    
    // Method draw line around active element of diagram(table event)
    public void drawSelectedPart(int index, float changeValueOn, float value){
    	int angleBegin = 0, angleEnd =(int) (1 + changeValueOn / value) * angles[index];
    	for(int i = 0; i < index; i++)
    		angleBegin += angles[i];
    	g.setColor(Color.magenta);
    	g.fillArc(x0 - rBig1 - 6, y0 - rBig2 - 6, rBig1 * 2 + 12, 
    			rBig2 * 2 + 12, angleBegin, angleEnd);    	
    }
    
    // Method draw line around active element of diagram(mouse event)
    public int drawSelectedPart(int index, int angleChengBegin, int angleChengEnd){
    	int newAngleBegin = 0, newAngleEnd = 0;
    	for(int i = 0; i < index; i++)
    		newAngleBegin += angles[i];
    	if( (angles[index] + angleChengEnd) < angleChengBegin) // New angle for diagram element  //(angleChengBegin > 180 && angleChengEnd < newAngleBegin) ||
    		newAngleEnd = angles[index] + angleChengEnd + (360 - angleChengBegin);
    	else newAngleEnd = angles[index] + angleChengEnd - angleChengBegin;
    	g.setColor(Color.blue);
    	g.fillArc(x0 - rBig1 - 4, y0 - rBig2 - 4, rBig1 * 2 + 8, 
    			rBig2 * 2 + 8, newAngleBegin, newAngleEnd);
    	return newAngleEnd;
    }
    
    // Set Size of diagram, position of center and radius
    public void setPreferredSize(Dimension dimension){
    	y0 = dimension.height / 2;
    	x0 = dimension.width / 2;
        rBig1 = (int) ((x0 + (x0 * 0.05)) / 2);
        rBig2 = (int) ((y0 + (y0 * 0.05)) / 2);
        rSmall1 = (int) (rBig1 * 0.5);
        rSmall2 = (int) (rBig2 * 0.5);
    	super.setPreferredSize(dimension);
    }
    
    //Generate special color for diagram elements
    private Color newColor(int r, int b, int g){
    	if(r > 255)
    		r = r % 255;
    	if(b > 255)
    		b = b % 255;
    	if(g > 255)
    		g = g % 255;
    	return new Color(r,b,g);
    }
    
    // Transform values of table into angles of diagram elements
    public void putValues(String names[], float values[]){
    	int max = 0, allAng = 0;
    	float minAngles = 0, valueOnAngle = 0;
    	angles = new int[values.length + 1];
    	sum = angles[values.length] = 0;
    	for(float value: values)
    		sum += value;
    	valueOnAngle = 360 / sum; // Percent factor 
    	for(int i = 0;i < values.length;i++){
    		if(values[max] < values[i])
    		{
    		  max = i;
    		}
    		angles[i] = (int) (values[i] * valueOnAngle);
    		if(angles[i] == 0){
    			minAngles += values[i];
    		}
    		allAng += angles[i];
    	}
    	if(minAngles > 0.0)// Values which angle < then 1(make special diagram element)
    	{
    		angles[values.length] = (int) Math.ceil(minAngles * valueOnAngle);
    		allAng += angles[values.length];
    	}
    	angles[max] += 360 - allAng; // Add unallocated angle to the most large diagram element
    	this.names = names;
    }
    
    // Set connection to canvas		
    public void makeCavas(){
    	bs = getBufferStrategy(); 
	    if (bs == null) {
	        createBufferStrategy(2); //создаем BufferStrategy для нашего холста
	        requestFocus();
	        bs = getBufferStrategy(); 
	    }
	    g = bs.getDrawGraphics(); 
	    g.setColor(Color.white); 
	    g.fillRect(0, 0, this.getWidth(), this.getHeight()); 
    }
    
    // Method draw diagram
	public void drawDiagram() {	    
	    int x1, y1, x2 = 0, y2 = 0, angleStr = 330, startAngle = 0, comentAngle;	    	    
	    for(int i = 0; i < angles.length; i++){  // Each element of diagram draw separately
	    	if(angles[i] != 0){
	    		comentAngle = (startAngle + angles[i]/2);
			    g.setColor(newColor(20 * i + 40, 15 * i + 50, 30 * i + 40)); // Generate color
			    // Calculate position of name and message lines of element
			    x1=(int) (x0 + rBig1 * Math.cos((Math.PI / 180) * 
			    		(360 - startAngle - angles[i] / 2)));
			    y1=(int) (y0 + rBig2 * Math.sin((Math.PI / 180) * 
			    		(360 - startAngle - angles[i] / 2)));
			 // Look in what dimension of diagram will be diagram element
			    if((comentAngle) >= 90 && (comentAngle) < 180)
			    	angleStr = 230;
			    if((comentAngle) >= 180 && (comentAngle) < 270)
			    	angleStr = 130;
			    if((comentAngle) >= 270 && (comentAngle) < 360)
			    	angleStr = 30;
			    x2=(int) (x1 + rSmall1 / 2 * Math.cos((Math.PI / 180) * angleStr));
			    y2=(int) (y1 + rSmall2 / 2 * Math.sin((Math.PI / 180) * angleStr));
			    g.drawLine(x1, y1, x2, y2);			 // Draw message line   
			    if((comentAngle) < 90 || (comentAngle) > 270){
			    	g.drawLine(x2, y2, (int) (x2 + x0 * 0.4), y2);
			    	if(i == angles.length - 1){
			    		g.setColor(Color.cyan);
			    		g.drawString(String.valueOf((int) (angles[i] / 360. * 100000) / 
				    			1000.) +"% Інші", x2, y2);
			    	}
			    	else g.drawString(String.valueOf((int) (angles[i] / 360. * 100000) / 
			    			1000.) + "% " + names[i], x2, y2);
			    }			      
			    else{
			    	g.drawLine(x2, y2, (int) (x2 - x0 * 0.4), y2);
			    	g.drawString(String.valueOf((int) (angles[i] / 360. * 100000) / 
			    			1000.) + "% " + names[i], (int) (x2 - x0 * 0.4), y2);
			    }			    
			    g.fillArc(x0 - rBig1, y0 - rBig2, rBig1 * 2, rBig2 * 2, startAngle, angles[i]);// Draw part of diagram circle
			    startAngle += angles[i];
	    	}
	    }
	    g.setColor(Color.white); 
	    g.fillArc(x0 - rSmall1, y0 - rSmall2, rSmall1 * 2, rSmall2 * 2, 0, 360); // Make from circle ring   
	    g.dispose();
	    bs.show(); 
	}
    
}
