package my_src;

import java.util.ArrayList;

public class Print_array_list
{
	public void print_array_list(ArrayList<String> array,String name)
	{
		// Get size and display.
		int count = array.size();
		System.out.println("Count: " + count);
	
		// Loop through elements.
		for (int i = 0; i < count; i++) {
		    String value = array.get(i);
		    System.out.println(name+"(" + value +").\n");
		}
		
	}
}