package mir;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.ListenableUndirectedGraph;

import mir.Node;
import mir.SimpleNode;
import mir.MIR_parser;

public class MIR {
	public SimpleNode root;
	public HashMap<String, Program_unit> program_units;
	// Used for semantic verification of argument number
	public ArrayList<SimpleNode> call_statements;
	public ListenableGraph cfg;
	public InputStream file;
	private int n_registers;
	private String code;
	private long parse_elapsed;
	private int nodes_processed;
	static boolean mir_parser_inited = false;
	public String generated_code;

	public static void main(String[] args) throws Exception {
		InputStream f;

		if (args.length == 1) {
			f = new FileInputStream(args[0]);
		} else if (args.length == 0) {
			f = System.in;
		} else {
			throw new IllegalArgumentException();
		}

		MIR mir = new MIR(f, 3, "");

		mir.run();
	}

	public void run() throws SemanticException, ParseException {
		long start_time, end_time;
		
		MIR_logger.log("Parsing file...");
		start_time = System.currentTimeMillis();
		root = MIR_parser.Program();
		end_time = System.currentTimeMillis();
		MIR_logger.log("Done", start_time - parse_elapsed, end_time);
		root.dump("");
		
		MIR_logger.log("Building program unit listing...");
		start_time = System.currentTimeMillis();
		build_program_unit_list();
		end_time = System.currentTimeMillis();
		MIR_logger.log("Done", start_time, end_time);
		
		MIR_logger.log("Analysing program units...");
		start_time = System.currentTimeMillis();
		analyse_program_units();
		end_time = System.currentTimeMillis();
		MIR_logger.log("Done", start_time, end_time);
		
		MIR_logger.log("Building control flow graph...");
		build_cfg();
		MIR_logger.log("Done", start_time, end_time);
		
		MIR_logger.log("Allocating registers...");
		build_register_allocations();
		MIR_logger.log("Done", start_time, end_time);
		
		MIR_logger.log("Generating code...");
		generate_code();
		MIR_logger.log("Done", start_time, end_time);

		System.out.println(cfg.toString());
	}

	public MIR(InputStream f, int n_registers, String str) {
		this.n_registers = n_registers;
		program_units = new HashMap<String, Program_unit>();
		call_statements = new ArrayList<SimpleNode>();
		cfg = new ListenableDirectedGraph(DefaultEdge.class);
		file = f;
		code = str;
		long start, end;
		start = System.currentTimeMillis();
		if(mir_parser_inited) {
			MIR_parser.ReInit(f);
		} else {
			new MIR_parser(f);
			mir_parser_inited = true;
		}
		end = System.currentTimeMillis();
		this.parse_elapsed = end - start;
		nodes_processed = 0;
		generated_code = "<No code generated>";
	}

	private void build_cfg() {
		Iterator it = program_units.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Program_unit program_unit = (Program_unit) pairs.getValue();

			SimpleNode prev_node = null;
			int first_non_null_node_index = -1;
			for(int i = 0; i < program_unit.node.children.length; ++i) {
				if(program_unit.node.children[i] != null) {
					prev_node = (SimpleNode) program_unit.node.children[i];
					first_non_null_node_index = i;
					break;
				}
			}

			cfg.addVertex(prev_node);

			SimpleNode curr_node;
			for(int i = first_non_null_node_index+1; i < program_unit.node.children.length; ++i) {
				curr_node = (SimpleNode) program_unit.node.children[i];
				cfg.addVertex(curr_node);

				if(!prev_node.tree_print().equals("Goto_instruction") &&
						!prev_node.tree_print().equals("Return_instruction")) {
					cfg.addEdge(prev_node, curr_node);
				}

				prev_node = curr_node;
			}
		}

		for(int i = 0; i < call_statements.size(); ++i) {
			SimpleNode call_inst = call_statements.get(i);
			SimpleNode destination_program_unit = program_units.get(call_inst.value).node;
			SimpleNode destination_inst = null;

			for(int j = 0; j < destination_program_unit.children.length; ++j) {
				destination_inst = (SimpleNode) destination_program_unit.children[j];
				if(destination_inst != null) {
					break;
				}
			}

			System.out.println(((SimpleNode) call_inst.parent).tree_print());
			if(((SimpleNode) call_inst.parent).tree_print().equals("Assign_instruction")) {
				cfg.addEdge(call_inst.parent, destination_inst);
			} else {
				cfg.addEdge(call_inst, destination_inst);
			}


		}

		it = program_units.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();

			Program_unit program_unit = (Program_unit) pairs.getValue();

			for(int i = 0; i < program_unit.jumps.size(); ++i) {
				SimpleNode jump = program_unit.jumps.get(i);
				SimpleNode label = program_unit.labels.get(jump.value);

				System.out.println(String.format("Jump: %s ; Label: %s", jump.value.toString(), label.value.toString()));
				if(((SimpleNode) jump.jjtGetParent().jjtGetParent()).tree_print().equals("If_instruction")) {
					cfg.addEdge(jump.parent.jjtGetParent(), label);
				} else {
					cfg.addEdge(jump.jjtGetParent(), label);
				}

				System.out.println("done!");
			}
		}
	}

	// This procedure does the following:
	// - Counts 'receive' statements to check how many params a given
	//   program_unit is supposed to receive
	// - Builds a symbol table, containing identifiers for variables and labels.
	// - Builds a list of program-wide call statements. The purpose of this is
	//   to ensure their semantic correctness. This can only be done after the 
	//   first step.
	private void analyse_program_units() throws SemanticException {
		Iterator it = program_units.entrySet().iterator();
		long start_time, end_time;
		
		MIR_logger.log("  Processing AST");
		start_time = System.currentTimeMillis();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			process_AST(program_units.get(pairs.getKey()),
					((Program_unit) pairs.getValue()).node);
		}		
		end_time = System.currentTimeMillis();
		MIR_logger.log("  Done (" + new Integer(nodes_processed).toString() + " nodes processed)" , start_time, end_time);

		MIR_logger.log("  Validating call integrity");
		start_time = System.currentTimeMillis();
		check_call_integrity();
		end_time = System.currentTimeMillis();
		MIR_logger.log("  Done", start_time, end_time);
		
		MIR_logger.log("  Validating jump integrity");
		start_time = System.currentTimeMillis();
		check_jump_integrity();
		end_time = System.currentTimeMillis();
		MIR_logger.log("  Done", start_time, end_time);
	}

	private void check_jump_integrity() throws SemanticException {
		Iterator it = program_units.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Program_unit program_unit = (Program_unit) pairs.getValue();

			for(SimpleNode jump : program_unit.jumps) {
				if(label_in_table(program_unit, jump) == null) {
					throw new SemanticException(
							"No such label '" + jump.value + "'", 
							jump.line_number);
				}
			}
		}

	}

	private void check_call_integrity() throws SemanticException {
		int n_args, n_params;
		Program_unit callee; 
		for(int i = 0; i < call_statements.size(); ++i) {
			callee = program_units.get(call_statements.get(i).value);

			if(callee == null) {
				throw new SemanticException(
						"No such program unit '" + call_statements.get(i).value + "'", 
						call_statements.get(i).line_number);
			}

			n_params = callee.n_params;

			if(call_statements.get(i).children == null) {
				n_args = 0;
			} else {
				n_args = call_statements.get(i).children.length;
			}

			if(n_args != n_params) {
				String cause = String.format(
						"Wrong number of arguments on call to %s: %d for %d",
						call_statements.get(i).value, n_args, n_params);
				throw new SemanticException(cause, call_statements.get(i).line_number);
			}
		}
	}

	// Depth-first traverse the tree for a program unit
	private void process_AST(Program_unit program_unit, SimpleNode node)
			throws SemanticException {
		if (node == null) {
			return;
		}

		process_node(program_unit, node);
		//System.out.println(String.format("Name: %s ; ID: %d", node.toString(), node.id));
		if (node.children != null && node.children.length > 0) {
			for (int i = 0; i < ((SimpleNode) node).children.length; ++i) {
				process_AST(program_unit,(SimpleNode) ((SimpleNode) node).children[i]);
			}
		}
	}

	private SimpleNode var_in_symbol_table(Program_unit program_unit,
			SimpleNode node) {
		return program_unit.variables.get(node.value); 
	}

	private SimpleNode label_in_table(Program_unit program_unit, SimpleNode node) {
		return program_unit.labels.get(node.value); 
	}

	private void process_node(Program_unit program_unit, SimpleNode node)
			throws SemanticException {
		this.nodes_processed++;
		if (node.tree_print().equals("Receive_instruction")) {
			program_unit.n_params++;
		} else if (node.tree_print().equals("Variable")) {
			process_variable_node(program_unit, node);
		} else if (node.tree_print().equals("Label")) {
			process_label_node(program_unit, node);
		} else if (node.tree_print().equals("Call_instruction")) {
			call_statements.add(node);
		}
	}

	private void process_label_node(Program_unit program_unit, SimpleNode node) {
		switch (node.access_type) {
		case WRITE:
			if (label_in_table(program_unit, node) == null) {
				program_unit.labels.put((String) node.value, node);
			}
			break;

		case READ:
			// TODO process jumps on a 2nd pass DONE!
			program_unit.jumps.add(node);
			break;
		}
	}

	private void process_variable_node(Program_unit program_unit,
			SimpleNode node) throws SemanticException {
		boolean found = var_in_symbol_table(program_unit, node) != null;
		switch (node.access_type) {
		case WRITE:
			if (!found) {
				program_unit.variables.put((String) node.value, node);
				node.liveness_begin = node.line_number;
				node.liveness_end = node.line_number;
			} else {
				program_unit.variables.get(node.value).liveness_end = node.line_number;
			}

			break;

		case READ:
			if (!found) {
				throw new SemanticException(
						"Access to undeclared variable '" + node.value
						+ "'", node.line_number);
			} else {
				program_unit.variables.get(node.value).liveness_end = node.line_number;
			}

			break;
		}
	}

	private void build_program_unit_list() throws SemanticException {
		boolean main_found = false;
		for (int i = 0; i < root.children.length; ++i) {
			SimpleNode node = (SimpleNode) root.children[i];
			if (node == null)
				continue;

			if (node.value == null) {
				throw new SemanticException("Unnamed program unit found",
						node.line_number);
			}

			if (node.value.toString().equals("main")) {
				main_found = true;
			}

			// important to use `root.children[i]' and not `node' because we
			// want a reference
			program_units.put(node.value.toString(), new Program_unit(
					node.value.toString(), (SimpleNode) root.children[i]));
			MIR_logger.log("  Found '" + node.value.toString() + "'");
		}

		if (!main_found) {
			throw new SemanticException(
					"Program unit named 'main' is required", -1);
		}
	}

	public void build_register_allocations(){
		Iterator it = program_units.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Program_unit program_unit = (Program_unit) pairs.getValue();

			if(program_unit.variables.size() > 0) {
				program_unit.build_interference_graph();
				program_unit.build_register_allocation(n_registers);
			}
		}

	}
	
	public void generate_code() {
		String[] lines = this.code.split("\n");
		
		Iterator program_unit_iterator = program_units.values().iterator();
		while(program_unit_iterator.hasNext()) {
			Program_unit program_unit = (Program_unit) program_unit_iterator.next();
			
			Iterator variable_iterator = program_unit.variables.values().iterator();
			while(variable_iterator.hasNext()) {
				SimpleNode variable = (SimpleNode) variable_iterator.next();
				
				if(variable.register == 0) {
					generated_code = "<Insufficient registers>";
					return;
				}
				
				for(int i = variable.liveness_begin - 1; i <= variable.liveness_end - 1; ++i) {
					lines[i] = lines[i].replaceAll(" " + variable.value.toString() + " ", " REG" + variable.register + " ");
				}
			}
		}
		
		generated_code = "";
		for (int i = 0; i < lines.length; i++) {
			generated_code += lines[i] + "\n";
		}
	}
}
