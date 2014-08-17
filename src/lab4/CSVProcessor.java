package lab4;
import java.io.*;
import java.util.ArrayList;

/*
 * Class for parse csv files
 */

public class CSVProcessor {
   private ArrayList<String> list;  //Contents all data from csv file
   
   //Automaton for parse data from list to table format 
   public String[][] parse() throws CSVParseException{
	   String table[][] = new String[list.size()][];	   
	   byte states[][] = {{0,2,1,16}, //16 - error state 
						 {0,16,1,1},
						 {2,3,2,2},
						 {0,2,16,16}};
	   byte privState = 16, state = 0;
	   String line;
	   String parseitem = "";// Accumulate item
	   int privSize = -1;
	   ArrayList<String> parseline = new ArrayList<String>();
	   char sym; // current processed symbol
	   for(int i = 0; i < list.size(); i++){
		   line = list.get(i) + ";";
		   for(int j = 0; j < line.length() ; j++){
			   sym = line.charAt(j);
			   if(sym == ';'){
				   if(state == 2)
					   parseitem += sym;
				   else{
				   parseline.add(parseitem);
				   parseitem = "";
				   }
				   state = states[state][0];
			   }else 
				   if(sym == '"'){
					   if(state == 3)
						   parseitem += sym;
					   state = states[state][1];
				   }
				   else
					   if(sym != '"' && sym != ';'){
						   state = states[state][2];
						   parseitem += sym;
					   }else
						   if(sym != '"'){
							   state = states[state][3];
							   parseitem += sym;
						   }
			  if(state == 16)
				  throw new CSVParseException("Error: no correct state", privState, sym);
			  privState = state;
		   }
		   table[i] = new String[parseline.size()];
		   if(privSize == parseline.size() || privSize == -1) // Check for corrupted csv file
			   privSize = parseline.size();
		   else throw new CSVParseException("Error: corupted file");
		   for(int j = 0; j < parseline.size() ; j++)
			   table[i][j] = parseline.get(j);
		   parseline.clear();
	   }
	return table;
   }
   
   // Read data from csv file to list
   public ArrayList<String> CsvRead(String pathCsv) throws IOException{
   	   try(BufferedReader reader = new BufferedReader(new FileReader(pathCsv))){
	   list = new ArrayList<String>();
	   String line;
		while ((line = reader.readLine()) != null)    
		       list.add(line);			
   	   } catch(IOException e){
   		   e.printStackTrace();
   	   }
	   return list;
   }
   
   // Put data from  list to csv file 
   public void CsvWrite(String pathCsv){
	   try(BufferedWriter writer =  new BufferedWriter(new FileWriter(pathCsv))){
	   for(String line: list)
		   {
				writer.write(line);
				writer.newLine();					   
		   }
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
   }
   
   // Save in dat file data from csv file as serializing object of class ArrayList
   public void DatFileWrite(String pathDat){
	   try(FileOutputStream file = new FileOutputStream(pathDat)){
		   try(ObjectOutputStream serial = new ObjectOutputStream(file)){
		   serial.writeObject(list);
		   } catch(IOException e1){
			   e1.printStackTrace();
		   }
	   } catch(IOException e){
		   e.printStackTrace();
	   }
  }
   
   // Read serializing object from file to list
   @SuppressWarnings("unchecked")
   public ArrayList<String> DatFileRead(String pathDat) throws FileNotFoundException, 
   IOException, ClassNotFoundException {
	   try(ObjectInputStream  serial = new ObjectInputStream(new FileInputStream(pathDat))){
	   list = (ArrayList<String>) serial.readObject();
	   } catch(IOException e){
		   e.printStackTrace();
	   }
	   return list;   
   }
   
   // Check if exists file
   public boolean ExistsFile(String path){
	   File f = new File(path);
	   if(f.exists())
		   return true;
	   else return false;
   }
}
