import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

public class TopscoreDB {
		
	private final String filename = "scores.txt";
	
	private HashMap<Integer,Integer> topscores;
	
	public TopscoreDB() throws IOException{
		readTopscores();
	}
	
	public int getTopscore(int fieldSize){
		if(topscores.containsKey(fieldSize)){
			return topscores.get(fieldSize);
		}
		return 0;
	}
	
	public void newTopscore(int fieldSize, int score){
		if(!topscores.containsKey(fieldSize) || score > topscores.get(fieldSize)){
			topscores.put(fieldSize, score);
			writeTopscores();
		}
	}
	
	private void readTopscores() throws IOException{
		try {
			FileInputStream is = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			topscores = new HashMap<Integer,Integer>();
			String nextLine;
			while((nextLine = br.readLine()) != null){
				String[] splittedString = nextLine.split(" ");
				topscores.put(Integer.parseInt(splittedString[0]), Integer.parseInt(splittedString[1]));
			}
		} catch (FileNotFoundException e) {
			Writer writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("scores.txt"), "utf-8"));
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				if(writer != null) {
					try {
						writer.close();
					} catch (IOException e1) {}
				}
			}
		}
	}
		
	private void writeTopscores(){
		try {
			FileWriter fw = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fw);
			for(int fieldSize : topscores.keySet()){
				out.write(fieldSize + " " + topscores.get(fieldSize) + "\n");
			}
			out.close();
		} catch (IOException e) {
			System.out.println("Could not write file.");
			e.printStackTrace();
		}
	}
	
	private void resetTopscores(){
		try {
			FileWriter fw = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fw);
			out.close();
		} catch (IOException e) {
			System.out.println("Could not write file.");
			e.printStackTrace();
		}
	}

	
	
}
