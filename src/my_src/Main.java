package my_src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import parser.MiniJavaParser;
import parser.ParseException;
import syntaxtree.Goal;


public class Main {
    public static void main (String [] args){
        FileInputStream fis = null;
        System.out.println(args.length);
        for(int i=0;i < args.length;i++)
    	{
	        try{
	            fis = new FileInputStream(args[i]);
	            MiniJavaParser parser = new MiniJavaParser(fis);
			    Goal root = parser.Goal();
			    System.err.println("Program parsed successfully.");
			    //handle_main
			    Handle_main eval = new Handle_main();
			    root.accept(eval);
			    
				Set<String> keys = eval.Table.keySet();
				for(Iterator<String> it = keys.iterator(); it.hasNext();)
				{
					
					String key_name = it.next().toString();
					String type1 = eval.Table.get(key_name);
					System.out.println("key_name = "+key_name+" type = "+type1);
				}
			    
			    //inside_class
			    //System.out.println("^^^^^^^^^^ before inside class ^^^^^^^^^^^^");
			    Inside_class eval2 = new Inside_class(root,eval.Table);
			    
			    
			    //////////////prints////////////////////////
			    Set<String> keys2 = eval2.Table.keySet();
				for(Iterator<String> it = keys2.iterator(); it.hasNext();)
				{
					
					String class_name = it.next().toString();
					LinkedHashMap<String,Fun_or_Ident> type1 = eval2.Table.get(class_name);
					System.out.println("class_name = "+class_name);
					
					Set<String> methods_name = type1.keySet();
					for(Iterator<String> iter = methods_name.iterator(); iter.hasNext();)
					{
						String method = iter.next().toString();
						if(method.charAt(0) == '#')
						{
							System.out.println("method = "+method);
						}
						else System.out.println("var = "+method);
					}
				}
				
				
				Set<String> decClas = eval2.DeclClasses.keySet();
				for(Iterator<String> it = decClas.iterator(); it.hasNext();)
				{
					
					String key_name = it.next().toString();
					String type1 = eval2.DeclClasses.get(key_name);
					System.out.println("class_name = "+key_name+" extended_class = "+type1);
				}
				
				/////////////////prints/////////////////
				
				HashMap_editing edit = new HashMap_editing(eval2.Table, eval2.DeclClasses);
				edit.Select_VTable_Data();
				LinkedHashMap<String,LinkedHashMap<Integer,String>>  vtables = edit.VTables;
				LinkedHashMap<String,LinkedHashMap<Integer,String>>  idtables = edit.ClassIds;
				
				System.out.println("\n******** Methods VTABLE **********\n");
				
				Set<String> classnames = vtables.keySet();
				for(Iterator<String> it = classnames.iterator(); it.hasNext();)
				{
					
					String class_name = it.next().toString();
					LinkedHashMap<Integer,String> type1 = vtables.get(class_name);
					System.out.println("class_name = "+class_name);
					
					Set<Integer> methods_name = type1.keySet();
					for(Iterator<Integer> iter = methods_name.iterator(); iter.hasNext();)
					{
						int pos = iter.next();
						String method = type1.get(pos);
						System.out.println("pos = "+pos+" method = "+method);
					}
				}
				
				System.out.println("\n******** IDS TABLE **********\n");
				
				classnames = idtables.keySet();
				for(Iterator<String> it = classnames.iterator(); it.hasNext();)
				{
					
					String class_name = it.next().toString();
					LinkedHashMap<Integer,String> type1 = idtables.get(class_name);
					System.out.println("class_name = "+class_name);
					
					Set<Integer> methods_name = type1.keySet();
					for(Iterator<Integer> iter = methods_name.iterator(); iter.hasNext();)
					{
						int pos = iter.next();
						String method = type1.get(pos);
						System.out.println("pos = "+pos+" var = "+method);
					}
				}
				
				System.out.println("\n******** finish IDS TABLE **********\n");
				
				MiniJavaToSpiglet eval3 = new MiniJavaToSpiglet(root, eval2.Table, eval2.DeclClasses, vtables, idtables);
				System.out.println(eval3.spiglet_code+"kkkkkkk");
				
				//Output
				int len = args[i].length() - 4;
				String input = new String(args[i]);
				String name = input.substring(0,len);
				System.out.println(name);
				
				File output = new File(name+"spg");
				if( !output.exists() )
					output.createNewFile();
				FileWriter out = new FileWriter(output.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(out);
				
				bw.write(eval3.spiglet_code);
				bw.close();
				
			    //System.out.println("^^^^^^^^^^ after inside class ^^^^^^^^^^^^");
			    
			    //inside_method
			    //System.out.println("^^^^^^^^^^ before inside methods ^^^^^^^^^^^^");
			    //Inside_methods eval3 = new Inside_methods(root, eval2.DeclClasses, eval2.Table);
			    //System.out.println("^^^^^^^^^^ after inside methods ^^^^^^^^^^^^");
			    
			    //type check
			    //System.out.println("^^^^^^^^^^ before type checking ^^^^^^^^^^^^");
			    //Type_check eval4 = new Type_check(root, eval3.DeclClasses, eval3.Table, eval.mainTable);
			    System.out.println("^^^File : "+args[i]+"   Success ^^^^^^^^^^");
	        }
	        catch(ParseException ex){
	        	System.out.print("^^^File : "+args[i]+"   ");
	            System.out.println(ex.getMessage());
	        }
	        catch(FileNotFoundException ex){
	            System.err.println(ex.getMessage());
	        }
	        catch(SemError ex){
	        	System.out.print("^^^File : "+args[i]+"   ");
	            System.out.println(ex.getMessage());
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