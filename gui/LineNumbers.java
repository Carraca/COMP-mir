package gui;
import java.util.ArrayList;


public class LineNumbers {
	
	private ArrayList<Integer[]> numbers;
	
	public LineNumbers(String text) {
		
		numbers = new ArrayList<Integer[]>();
		int start=0;
		int end=0;
		
		String[] lines = text.split("\n");
		
		for(int i=0; i<lines.length; ++i){
			Integer[] pair = new Integer[2];
			pair[0]=start;
			end=start+lines[i].length();
			pair[1]=end;
			numbers.add(pair);
			start=end+1;
		}
		
	}
	
	public ArrayList<Integer[]> getNumbers(){
		return numbers;
	}
}
