package lab4;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

// Class which make graphic interface for data from csv file 
public class Interface  extends Thread{
	private String data[][];
	private JFrame frame;
	private JTable table;
	private DiagramDrawer diagram;
	
	public void putData(String data[][]){
		this.data = data;
	}
	
	private float[] getColumnOfValues(int column){
		float values[] = new float[data.length - 1];
	    for(int i = 0; i < data.length - 1; i++){
	    	try{
	    	values[i] = Float.parseFloat(data[i + 1][column]);	// Gets values from diagram from data
	    	}catch(Exception e){
	    		values[i] = 0;
	    		table.setValueAt("Error ####", i + 1, column);
	    	}
	    	try{
		    	if(values[i] < 0)   			
	    			throw new Exception("Signed number" + values[i]); 
	    	}catch(Exception e){
	    		values[i] = 0;
	    		table.setValueAt(e.getMessage(), i, column);
	    	}
	    }
	    return values;
	}
	
	// Make canvas and put on it table, button, diagram
	@Override
	public void run() {
		// Table Creation
		String columns[], items[][] = new String[data.length - 1][];
		float values[]; // Values for diagram
	    String names[] = new String[data.length - 1]; // Names for columns
		columns = data[0];
		for(int i = 1; i < data.length; ++i){
			items[i - 1] = data[i];
	    	names[i - 1] = data[i][0];		// Gets names for table columns
		}	       
		table = new JTable(items, columns);
		table.getTableHeader().setReorderingAllowed(false); // No changes for position of table columns
		JScrollPane scrollPane = new JScrollPane(table);// Make scroll panel for table
	    scrollPane.setPreferredSize(new Dimension(500, 100));	    
	    values = getColumnOfValues(1);		    
	    // Diagram creation
        diagram = new DiagramDrawer();
        diagram.setPreferredSize(new Dimension(500, 500));
        diagram.putValues(names, values);        
        // Button creation
	    JButton button = new JButton("Make image");
	    button.addActionListener(new ActionListener(){ // Make jpg of diagram when button pressed
	    	public void actionPerformed(ActionEvent e) {
				try {
				BufferedImage bufferedImage = new Robot().createScreenCapture(new Rectangle(3 + frame.getX(),   // Capture picture
						25 + frame.getY(), diagram.getWidth() - 2, diagram.getHeight()));
				ImageIO.write(bufferedImage, "jpg", new File("Diagram.jpg")); // Save picture as jpg file
				} catch (AWTException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
	    	}	    		
	    });
	    // Creation canvas
        frame = new JFrame("MiniExcel");
        // Added on canvas elements
	    frame.add(diagram, BorderLayout.NORTH); 
	    frame.add(scrollPane, BorderLayout.AFTER_LINE_ENDS);
	    frame.add(button, BorderLayout.AFTER_LAST_LINE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	    frame.pack();
	    frame.setResizable(false); // No resizable for canvas
	    frame.setVisible(true);
	    changesValue(names, values);// Check for updates data
	}		
	
	// Method for updates data
	private void changesValue(String names[], float values[]){
		int x = -1, y = -1, colum = -1; // Response for changes in table
		int indexChangeValue = -1, angleBegin = -1, angleEnd = -1; // Response for changes in diagram
		CustomListener mouseEvent = new CustomListener(); // Class which response for mouse events on diagram
	    boolean changeValue = false, mouseDrag = false;
	    diagram.addMouseListener(mouseEvent);
	    for(;frame != null;){
	    	diagram.makeCavas();
		    try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		    //Look in table changes
		    colum = table.getSelectedColumn();
		    if(!changeValue && colum != -1){ // User activated on of the table item
		    	y = table.getSelectedRow();
		    	x = colum;
		    	changeValue = true;
		    	if(x != 0){		    	
	    			for(int i = 0; i < values.length ; ++i){
				    	try{
				    		values[i] = Float.parseFloat(table.getValueAt(i, x).toString()); // Try to save new value
				    	}catch(Exception e){
				    		values[i] = 0;
				    		table.setValueAt("Error ####", i, x);
				    	}
				    	try{
					    	if(values[i] < 0)   			
				    			throw new Exception("Signed number " + values[i]); 
				    	}catch(Exception e){
				    		values[i] = 0;
				    		table.setValueAt(e.getMessage(), i, x);
				    	}
	    			}
	    			diagram.putValues(names, values); // Updates values in table
		    	}
		    }
		    if(changeValue){
		    	diagram.drawSelectedPart(y, 0, values[y]); // Shows selected item on diagram
		    	if (y != table.getSelectedRow() || colum != x){
		    		if(x != 0){		    	
				    	try{
				    		values[y] = Float.parseFloat(table.getValueAt(y, x).toString()); // Try to save new value
				    	}catch(Exception e){
				    		values[y] = 0;
				    		table.setValueAt("Error ####", y, x);
				    	}
				    	try{
					    	if(values[y] < 0)   			
				    			throw new Exception("Signed number " + values[y]); 
				    	}catch(Exception e){
				    		values[y] = 0;
				    		table.setValueAt(e.getMessage(), y, x);
				    	}		    			
		    		}else{
		    			names[y] = (String) table.getValueAt(y, 0);
		    		}
			    	changeValue = false;
			    	
			    	diagram.putValues(names, values); // Updates values in table
		    	}
		    }		 
		    //Changes on diagram by mouse
		    if(mouseEvent.getMouseStatus() && !mouseDrag){ // Mouse press on diagram.
		    	indexChangeValue = 0;
		    	angleBegin = getAngleFromDot(mouseEvent.getStartMousePosition()); // Remember start angle.
		    	// Get index of selected element of diagram
		    	for(int startAngle = 0; indexChangeValue < 							
		    			values.length; indexChangeValue++){
		    		if(angleBegin >= startAngle 
		    			&& angleBegin < startAngle + diagram.getAngles()[indexChangeValue])
		    			break;
		    		startAngle += diagram.getAngles()[indexChangeValue];	
		    	}
		    	mouseDrag = true; // Set that mouse drag
		    }
		    if(mouseDrag){ // Mouse release on diagram
		    	try{
			    	angleEnd = getAngleFromDot(diagram.getMousePosition()); // Get angle where mouse was release.
			    	angleEnd = diagram.drawSelectedPart(indexChangeValue, angleBegin, angleEnd); // Draw active line on selected part of diagram 
			    	if(!mouseEvent.getMouseStatus()){
			    		mouseDrag = false; 
			    		if(angleEnd >= 360) // Protection of unacceptable errors of angleEnd calculation	
			    			angleEnd = 359;
			    		if(diagram.getAngles()[indexChangeValue] != angleEnd)
			    			values[indexChangeValue] = angleEnd * (diagram.getSum() - values[indexChangeValue]) / (360 - angleEnd); // Put new value to diagram
			    		if(colum > 0)
			    			 table.setValueAt(String.valueOf(values[indexChangeValue]), indexChangeValue, colum); // Set new value in table
			    		else table.setValueAt(String.valueOf(values[indexChangeValue]), indexChangeValue, 1); // Set new value in table
			    		diagram.putValues(names, values);// Set new value in diagram
			    	}
		    	}
		    	catch(Exception e){
		    		mouseDrag = false;
		    	}
		    }
   	    
		    diagram.drawDiagram(); // Update diagram graphic
		    
	    }
	}
	
	// Method calculate angle from dot position to position of dot of circle beginning
	private int getAngleFromDot(Point dot){
		double a, b, c;
        int angle;
    	a = dot.distance(diagram.getCenter());
    	b = diagram.getCenter().distance(new Point(diagram.getCenter().x +
    			diagram.getBigRadiusW(), diagram.getCenter().y));		    	
    	c = dot.distance(new Point(diagram.getCenter().x +
    			diagram.getBigRadiusW(), diagram.getCenter().y));
    	angle = (int) Math.floor((180 / Math.PI) * Math.acos((c * c - a * a - b * b) / (2 * a * b)));
    	if(dot.y > diagram.getCenter().y)
    		angle = 180 + angle;
    	else angle = 180 - angle;
    	return angle;
	}
	
	// Class response for mouse events
	public class CustomListener implements MouseListener {
		private boolean mouseStatus = false;
		private Point beginMousePosition;
		
		public boolean getMouseStatus(){
			return mouseStatus;
		}
		public Point getStartMousePosition(){
			return beginMousePosition;
		}
		
        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        	mouseStatus = false;
        }
        
        // Method calculate if mouse pressed on diagram circle
        public void mousePressed(MouseEvent e) {
        	Point mousePosition = diagram.getMousePosition();
        	if(mousePosition != null && mousePosition.distance(diagram.getCenter()) < diagram.getBigRadiusW() - 10 && 
 		    		mousePosition.distance(diagram.getCenter()) > diagram.getSmallRadiusW() + 10){
        		mouseStatus = true;
        		beginMousePosition = mousePosition;
        	}
        }

        public void mouseReleased(MouseEvent e) {
        	mouseStatus = false; 
        }

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub           
		}
   }
}
