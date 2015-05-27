package my_src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import syntaxtree.BinOp;
import syntaxtree.CJumpStmt;
import syntaxtree.Call;
import syntaxtree.Goal;
import syntaxtree.HAllocate;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.IntegerLiteral;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.Operator;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.SimpleExp;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
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
	LinkedHashMap<Integer, String> jump_labels_up = new LinkedHashMap<Integer,String>();
	int l_count = 0;
	LinkedHashMap<Integer, Boolean> def_vars = new LinkedHashMap<Integer,Boolean>();
	int temp_num;
	String label_name;
	
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
		String copy;
		method_name = new String("MAIN");
		n.f1.accept(this);
        
		ResetInstr();
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
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
   public void visit(StmtExp n) throws Exception {
	   String copy;
	   
      n.f0.accept(this);
      n.f1.accept(this);
      
      from = "RETURN";
      instruction = "\""+method_name+"\""+", "+AssignInstr()+", "+"\"";
      instruction += "RETURN ";
      n.f3.accept(this);
      
      if(from.equals("TEMP"))
	  {
		  varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
          copy = new String(varUse);
          varUseArray.add(copy);
	  }
      
      instruction += "\"";
      copy = new String(instruction);
      instructionArray.add(copy);
      
      next = "\""+method_name+"\", "+CurrentInstr()+", "+(CurrentInstr()+1);
      copy = new String(next);
      nextArray.add(copy);
      
      n.f4.accept(this);
      ResetInstr();
   }
   
   
   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public void visit(Procedure n) throws Exception {
	   from = "Procedure";
      n.f0.accept(this);
      method_name = new String(label_name);
      
      n.f4.accept(this);
   }
   
   
   
   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public void visit(CJumpStmt n) throws Exception {
	  from = "CJUMP";
	  String copy;
	  
	  instruction = "\""+method_name+"\""+", "+AssignInstr()+", "+"\"";
      instruction += "CJUMP ";
      n.f1.accept(this);
      instruction += " ";
      
      from = "CJUMP";
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
	  boolean not_eq = true;
	  
	  instruction = "\""+method_name+"\""+", "+AssignInstr()+", "+"\"";
      instruction += "JUMP ";
      n.f1.accept(this);
      
      Set<Integer> keys = jump_labels_up.keySet();
	  for(Iterator<Integer> it = keys.iterator(); it.hasNext();)
	  {		
		  int k = it.next();
		  String type1 = jump_labels_up.get(k);
		  if(keepLabel.equals(type1))
		  {
			  next = "\""+method_name+"\", "+CurrentInstr()+", "+k;
		      copy = new String(next);
		      nextArray.add(copy);
		      not_eq = false;
		      break;
		  }
	  }
      
	  if(not_eq)
	  {
	      keepInstr = CurrentInstr();
	      cjump_jump.put(keepInstr, keepLabel);
	  }
      
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
	   String copy;
	   boolean not_eq = true;
		  
	  instruction = "\""+method_name+"\""+", "+AssignInstr()+", "+"\"";
      instruction += "HSTORE ";
      n.f1.accept(this);     
      instruction += " ";
      instruction += n.f2.f0.toString()+" ";
      varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
      copy = new String(varUse);
      varUseArray.add(copy);
      
      from = "HSTORE";
      n.f3.accept(this); 
      varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
      copy = new String(varUse);
      varUseArray.add(copy);
      
      instruction += "\"";
      copy = new String(instruction);
      instructionArray.add(copy);
      
      next = "\""+method_name+"\", "+CurrentInstr()+", "+(CurrentInstr()+1);
      copy = new String(next);
      nextArray.add(copy);
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public void visit(HLoadStmt n) throws Exception {
	   from = "HLOAD";
	   String copy;
	   boolean not_eq = true;
		  
	  instruction = "\""+method_name+"\""+", "+AssignInstr()+", "+"\"";
      instruction += "HLOAD ";
      n.f1.accept(this);     
      instruction += " ";
      varDef = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
      copy = new String(varDef);
      varDefArray.add(copy);
      def_vars.put(temp_num, true);
      
      from = "HLOAD";
      n.f2.accept(this); 
      varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
      copy = new String(varUse);
      varUseArray.add(copy);
      instruction += " ";
      instruction += n.f2.f0.toString();
      
      instruction += "\"";
      copy = new String(instruction);
      instructionArray.add(copy);
      
      next = "\""+method_name+"\", "+CurrentInstr()+", "+(CurrentInstr()+1);
      copy = new String(next);
      nextArray.add(copy);
      
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public void visit(MoveStmt n) throws Exception {
	   from = "MOVE"; 
	   String copy;
	   boolean not_eq = true;
		  
	  instruction = "\""+method_name+"\""+", "+AssignInstr()+", "+"\"";
      instruction += "MOVE ";
      n.f1.accept(this);  
      instruction += " ";
      String keepTemp = new String(temp);
      //System.out.println("^^^^^^^^^^^edo^^^^^^^^");
      varDef = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
      //System.out.println(varDef);
      copy = new String(varDef);
      varDefArray.add(copy);
      def_vars.put(temp_num, true);

     /* else
      {
    	  varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
          copy = new String(varUse);
          varUseArray.add(copy);
      }
      */
      
      from = "MOVE"; 
      n.f2.accept(this);
      
      if(from.equals("TEMP"))
      {
    	  varMove = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+keepTemp+"\""+", "+"\""+temp+"\"";
          copy = new String(varMove);
          varMoveArray.add(copy);
          
          varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
          copy = new String(varUse);
          varUseArray.add(copy);
      }
      else if(from.equals("INTEGER_LITERAL"))
      {
    	  constMove = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+keepTemp+"\""+", "+temp;
          copy = new String(constMove);
          constMoveArray.add(copy);
      }
      
      
      
      
      instruction += "\"";
      copy = new String(instruction);
      instructionArray.add(copy);
      
      next = "\""+method_name+"\", "+CurrentInstr()+", "+(CurrentInstr()+1);
      copy = new String(next);
      nextArray.add(copy);
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public void visit(PrintStmt n) throws Exception {
	   from = "PRINT";
	   String copy;
	   boolean not_eq = true;
		  
	  instruction = "\""+method_name+"\""+", "+AssignInstr()+", "+"\"";
      instruction += "PRINT ";
      n.f1.accept(this); 
      if(from.equals("TEMP"))
      {
    	  varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
          copy = new String(varUse);
          varUseArray.add(copy);
      }
      
      instruction += "\"";
      copy = new String(instruction);
      instructionArray.add(copy);
      
      next = "\""+method_name+"\", "+CurrentInstr()+", "+(CurrentInstr()+1);
      copy = new String(next);
      nextArray.add(copy);
   }

   
   
   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
   public void visit(Call n) throws Exception {
	   from = "CALL"; 
	   String copy;
		  
	  instruction += "CALL ";
	  n.f1.accept(this);
	  
	  instruction += " ( ";
	  from = "CALL";
	  n.f3.accept(this);
	  instruction += " )";
	  
	  from = "CALL";
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public void visit(HAllocate n) throws Exception {
	   from = "HALLOCATE"; 
		  
	  instruction += "HALLOCATE ";
	  n.f1.accept(this);
	  
	  from = "HALLOCATE";
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public void visit(BinOp n) throws Exception {
	   from = "BINOP";
	   String copy;
	   
      n.f0.accept(this);
      
      n.f1.accept(this);
      varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
      copy = new String(varUse);
      varUseArray.add(copy);
      
      n.f2.accept(this);
      if(from.equals("TEMP"))
	  {
		  varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
          copy = new String(varUse);
          varUseArray.add(copy);
	  }
      
      from = "BINOP";
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public void visit(Operator n) throws Exception {
	   from = "OPERATOR"; 
		  
	  instruction += n.f0.choice.toString()+" ";
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public void visit(SimpleExp n) throws Exception {
      n.f0.accept(this);
   }

   
   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public void visit(Temp n) throws Exception {
	  
	  instruction += "TEMP "+n.f1.f0.toString()+" ";
	  variables = "\""+method_name+"\""+", "+"\"TEMP "+n.f1.f0.toString()+"\"";
	  temp = "TEMP "+n.f1.f0.toString();
	  String copy = new String(variables);
      variablesArray.add(copy);
      temp_num = Integer.parseInt(n.f1.f0.toString());
      
      if(from.equals("CALL"))
      {
    	  varUse = "\""+method_name+"\""+", "+CurrentInstr()+", "+"\""+temp+"\"";
          copy = new String(varUse);
          varUseArray.add(copy);
          return;
      }
      
      from = "TEMP"; 
   }
   
   
   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public void visit(IntegerLiteral n) throws Exception {
	   from = "INTEGER_LITERAL";
	   instruction += n.f0.toString()+" ";
	   temp = n.f0.toString();
   }
   
   
   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Label n) throws Exception {
	  String copy;
	  boolean not_eq = true;
	  //System.out.println(n.f0.toString() +" "+ from);
	  if(from.equals("CJUMP") || from.equals("JUMP"))
	  {
		   // System.out.println("in jump");
		  	instruction += n.f0.toString();
		  	keepLabel = n.f0.toString();
	  }
	  else
	  {
		    //System.out.println("out jump");
		  	Set<Integer> keys = cjump_jump.keySet();
			for(Iterator<Integer> it = keys.iterator(); it.hasNext();)
			{
				
				int k = it.next();
				String type1 = cjump_jump.get(k);
				if(n.f0.toString().equals(type1))
				{
					//System.out.println(n.f0.toString() + " " + type1 + " " + k);
					next = "\""+method_name+"\", "+k+", "+(CurrentInstr()+1);
				    copy = new String(next);
				    nextArray.add(copy);
				    not_eq = false;
				    break;
				}
			}
			if(not_eq)
			{
				jump_labels_up.put((CurrentInstr()+1), n.f0.toString());
			}
	  }
	  
	  from = "LABEL";
	  label_name = n.f0.toString();
   }
   
   
	
}