package my_src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class Print_array_list
{
	public void print_array_list(ArrayList<String> array,String name) throws Exception
	{
		// Get size and display.
		int count = array.size();
		System.out.println("Count: " + count);
	
		// Loop through elements.
		for (int i = 0; i < count; i++) {
		    String value = array.get(i);
		    System.out.println(name+"(" + value +").\n");
		}
		
		
		File output = new File("facts/"+name+".iris");
		if( !output.exists() )
			output.createNewFile();
		FileWriter out = new FileWriter(output.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(out);
		
		//bw.write(eval3.spiglet_code);
		for (int i = 0; i < count; i++) {
		    String value = array.get(i);
		    bw.write(name+"(" + value +").\n");
		}
		
		
		bw.close();
		
	}
}