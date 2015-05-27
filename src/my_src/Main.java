package my_src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import parser.*;
import syntaxtree.Goal;


public class Main {
    public static void main (String [] args){
        FileInputStream fis = null;
        System.out.println(args.length);
        for(int i=0;i < args.length;i++)
    	{
	        try{
	            fis = new FileInputStream(args[i]);
	            SpigletParser parser = new SpigletParser(fis);
			    Goal root = parser.Goal();
			    System.err.println("Program parsed successfully.");
			    //handle_main
			    Handle_main eval = new Handle_main();
			    root.accept(eval);
			    
			    Print_array_list obj = new Print_array_list();
			    obj.print_array_list(eval.instructionArray, "instruction");
			    obj.print_array_list(eval.varUseArray, "varUse");
			    obj.print_array_list(eval.variablesArray, "var");
			    obj.print_array_list(eval.nextArray, "next");
			    
			    
			    System.out.println("^^^File : "+args[i]+"   Success ^^^^^^^^^^");
	        }
	        catch(ParseException ex){
	        	System.out.print("^^^File : "+args[i]+"   ");
	            System.out.println(ex.getMessage());
	        }
	        catch(FileNotFoundException ex){
	            System.err.println(ex.getMessage());
	        }       
			catch(Exception e){
				System.out.print("^^^File : "+args[i]+"   ");
				System.out.println("Internal Error.");
			}
	        finally{
	            try{
	                if(fis != null) fis.close();
	            }
	            catch(IOException ex){
	                System.err.println(ex.getMessage());
	            }
	        }
    	}
    }
}