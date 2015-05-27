package my_src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
			    
				
			    boolean success = (new File("generated-facts")).mkdirs();
				if (!success) {
					System.out.print("Directory allready exist\n");
				}
				
				String path = "generated-facts/" ;
			    
				
				File f = new File(args[i]);
				System.out.println(f.getName());
				String name = f.getName();
				
				int len = name.length() - 4;
				String input = new String(name);
				name = input.substring(0,len);
				
				success = (new File(path+name)).mkdirs();
				if (!success) {
					System.out.print("Directory allready exist\n");
				}
				
				path += name;
				
			    
			    Print_array_list obj = new Print_array_list();
			    obj.print_array_list(eval.instructionArray, "instruction",path);
			    obj.print_array_list(eval.varUseArray, "varUse",path);
			    
			    obj.print_array_list(eval.nextArray, "next",path);
			    obj.print_array_list(eval.varDefArray, "varDef",path);
			    obj.print_array_list(eval.varMoveArray, "varMove",path);
			    obj.print_array_list(eval.constMoveArray, "constMove",path);
			    
			    Set<String> hs = new LinkedHashSet<>();
			    hs.addAll(eval.variablesArray);
			    eval.variablesArray.clear();
			    eval.variablesArray.addAll(hs);
			    obj.print_array_list(eval.variablesArray, "var",path);
			    
			    System.out.println("^^^File : "+args[i]+"   Success ^^^^^^^^^^");
			    
			    ///creating makefile
			    
			    String filename= "Makefile";
			    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
			    fw.write("run-"+name+":\n\t$(JVM) -cp $(CLASSPATH) iris.Main $(current_dir) /generated-facts/"+name+" /analysis-logic /queries\n");//appends the string to the file
			    fw.close();
			    
			     
			    java.lang.Runtime rt = java.lang.Runtime.getRuntime();
		        // Start a new process: UNIX command ls
		        java.lang.Process p = rt.exec("make");
		        // You can or maybe should wait for the process to complete
		        p.waitFor();
		        //System.out.println("Process exited with code = " + rt.exitValue());
		        // Get process' output: its InputStream
		        java.io.InputStream is = p.getInputStream();
		        java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
		        // And print each line
		        String s = null;
		        while ((s = reader.readLine()) != null) {
		            System.out.println(s);
		        }
		        is.close();
		        
		        rt = java.lang.Runtime.getRuntime();
		        // Start a new process: UNIX command ls
		        p = rt.exec("make run-"+name);
		        // You can or maybe should wait for the process to complete
		        p.waitFor();
		        //System.out.println("Process exited with code = " + rt.exitValue());
		        // Get process' output: its InputStream
		        is = p.getInputStream();
		        reader = new java.io.BufferedReader(new InputStreamReader(is));
		        // And print each line
		        s = null;
		        while ((s = reader.readLine()) != null) {
		            System.out.println(s);
		        }
		        is.close();
			    
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