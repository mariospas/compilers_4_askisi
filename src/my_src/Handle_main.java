package my_src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import syntaxtree.CJumpStmt;
import syntaxtree.Goal;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.PrintStmt;
import syntaxtree.Stmt;
import syntaxtree.StmtList;
import syntaxtree.Temp;
import visitor.DepthFirstVisitor;

import java.lang.Object;

public class Handle_main extends DepthFirstVisitor
{
	String instruction = new String();
	String variables = new String();
	String next = new String();
	String varMove = new String();
	String constMove = new String();
	String varUse = new String();
	String varDef = new String();
	
	ArrayList<String> instructionArray = new ArrayList<>();
	ArrayList<String> variablesArray = new ArrayList<>();
	ArrayList<String> nextArray = new ArrayList<>();
	ArrayList<String> varMoveArray = new ArrayList<>();
	ArrayList<String> constMoveArray = new ArrayList<>();
	ArrayList<String> varUseArray = new ArrayList<>();
	ArrayList<String> varDefArray = new ArrayList<>();
	
	int instr_count = 0;
	String method_name = new String();
	String keepLabel = new String();
	int keepInstr = 0;
	String temp = new String();
	String from = new String();
	
	LinkedHashMap<Integer, String> cjump_jump = new LinkedHashMap<Integer,String>();
	
	
	public int AssignInstr()
	{
		instr_count++;
		return instr_count;
	}
	
	public int CurrentInstr()
	{
		return instr_count;
	}
	
	public int ResetInstr()
	{
		instr_count = 0;
		return instr_count;
	}
	
	
	/**
	 * Grammar production:
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public void visit(Goal n) throws Exception
	{
		method_name = new String("MAIN");
		n.f1.accept(this);
		n.f3.accept(this);
	}
	
	/**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public void visit(StmtList n) throws Exception {
	   from = "StmtList";
	   
	   instruction = new String();
	   n.f0.accept(this);
   }
   
   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
   public void visit(Stmt n) throws Exception {
	   from = "Stmt";
      n.f0.accept(this);
   }
   
   
   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public void visit(CJumpStmt n) throws Exception {
	  from = "CJUMP";
	   
	  instruction = "\""+method_name+"\""+", "+AssignInstr()+", "+"\"";
      instruction += "CJUMP ";
      n.f1.accept(this);
      String copy = new String(variables);
      variablesArray.add(copy);
      instruction += " ";
      
      n.f2.accept(this);
      keepInstr = CurrentInstr();
      cjump_jump.put(keepInstr, keepLabel);
      instruction += "\"";
      
      next = "\""+method_name+"\", "+CurrentInstr()+", "+(CurrentInstr()+1);
      copy = new String(next);
      nextArray.add(copy);
      
      varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
      copy = new String(varUse);
      varUseArray.add(copy);
      
      copy = new String(instruction);
      instructionArray.add(copy);
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public void visit(JumpStmt n) throws Exception {
	  from = "JUMP";
	  String copy;
	  
	  instruction = "\""+method_name+"\""+", "+AssignInstr()+", "+"\"";
      instruction += "JUMP ";
      n.f1.accept(this);
      keepInstr = CurrentInstr();
      cjump_jump.put(keepInstr, keepLabel);
      instruction += "\"";
      
      copy = new String(instruction);
      instructionArray.add(copy);
      
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public void visit(HStoreStmt n) throws Exception {
	   from = "HSTORE";
	   
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public void visit(HLoadStmt n) throws Exception {
	   from = "HLOAD";
	   
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public void visit(MoveStmt n) throws Exception {
	   from = "MOVE";
	   
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public void visit(PrintStmt n) throws Exception {
	   from = "PRINT";
	   
      n.f0.accept(this);
      n.f1.accept(this);
   }

   
   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public void visit(Temp n) throws Exception {
	  instruction += "TEMP "+n.f1.f0.toString();
	  variables = "\""+method_name+"\""+", "+"\"TEMP "+n.f1.f0.toString()+"\"";
	  temp = "TEMP "+n.f1.f0.toString();
   }
   
   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Label n) throws Exception {
	  String copy;
	  System.out.println(n.f0.toString() +" "+ from);
	  if(from.equals("CJUMP") || from.equals("JUMP"))
	  {
		    System.out.println("in jump");
		  	instruction += n.f0.toString();
		  	keepLabel = n.f0.toString();
	  }
	  else
	  {
		    System.out.println("out jump");
		  	Set<Integer> keys = cjump_jump.keySet();
			for(Iterator<Integer> it = keys.iterator(); it.hasNext();)
			{
				
				int k = it.next();
				String type1 = cjump_jump.get(k);
				if(n.f0.toString().equals(type1))
				{
					System.out.println(n.f0.toString() + " " + type1 + " " + k);
					next = "\""+method_name+"\", "+k+", "+(CurrentInstr()+1);
				    copy = new String(next);
				    nextArray.add(copy);
				}
			}
	  }
   }
   
   
	
}