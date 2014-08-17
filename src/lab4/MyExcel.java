package lab4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MyExcel {
	// Attachment class which make new thread for parsing csv file
	private static class Mythread extends Thread{
		private String table[][];
		private CSVProcessor csvprocessor;
		
		String[][] GetTable(){
			return table;
		}
		
		Mythread(final CSVProcessor csvprocessor){
			this.csvprocessor = csvprocessor;
		}
		
		@Override
		public void run() {
            try {
				table = csvprocessor.parse();
			} catch (CSVParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}                
        }
	}
	// Main method  
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CSVProcessor csvProcessor = new CSVProcessor();
		String pathCSV, pathDat = "serialized.dat"; 
		ArrayList<String> list = null;
		
		if(csvProcessor.ExistsFile(pathDat))
		{
			list = csvProcessor.DatFileRead(pathDat);			
		}
		else
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			byte kol = 0;
			do
			{
				try{
					System.out.println("Put path to file");
					pathCSV = reader.readLine();   // input.csv
					list = csvProcessor.CsvRead(pathCSV);
				}
				catch(Exception e){
					kol++;
				}
			}
			while (list == null && kol != 3);
			reader.close();
			if(kol == 3)
				throw new Exception("Enter path right!");
			csvProcessor.DatFileWrite(pathDat);
		}
		// Put on console data from csv file
		for(String line: list)
			System.out.println(line);
		System.out.println();
		Mythread thread = new Mythread(csvProcessor);
		thread.start();
		thread.join(); // Stop main thread until daughter thread not ended
		String table[][] = thread.GetTable();
		// Put on console data from csv file in format table 
		for(String line[]: table){
			for(String item: line)
				System.out.format("%30s", item);
			System.out.println();
		}		
		// Start draw graphics interface
		Interface inter = new Interface();
		inter.putData(table);
		//inter.setDaemon(true);
		inter.start();
	}
}
