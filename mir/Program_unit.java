package mir;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jgraph.graph.Edge;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;

public class Program_unit {
	public String name;
	public SimpleNode node;
	public HashMap<String, SimpleNode> labels;
	public HashMap<String, SimpleNode> variables;
	public int n_params;
	public ListenableUndirectedGraph regs;

	public ArrayList<SimpleNode> jumps;

	public Program_unit(String name, SimpleNode node) {
		this.name = name;
		this.node = node;
		labels = new HashMap<String, SimpleNode>();
		variables = new HashMap<String, SimpleNode>();
		jumps = new ArrayList<SimpleNode>();
		regs = new ListenableUndirectedGraph(DefaultEdge.class);
		n_params = 0;
	}
	
	public SimpleNode[] vertex_array() {
		SimpleNode[] vertexes_ary = new SimpleNode[regs.vertexSet().size()];
		Iterator it = regs.vertexSet().iterator();
		
		for(int i = 0; it.hasNext(); ++i) {
			vertexes_ary[i] = (SimpleNode) it.next();
		}
		
		return vertexes_ary;
	}

	public void build_register_allocation(int k) {
		Stack<Vertex_and_neighbors> removed_vertexes = new Stack<Vertex_and_neighbors>();
		
		SimpleNode actual_vertex;
		Vertex_and_neighbors actual_vertex_and_neighbors;
		
		SimpleNode[] vertexes_ary = vertex_array();
		
		for(int i = 0; vertexes_ary.length > 1; ++i) {
			SimpleNode node = (SimpleNode) vertexes_ary[i];
			
			
			if (regs.degreeOf(node) < k) {
				removed_vertexes.push(new Vertex_and_neighbors(node, Graphs.neighborListOf(regs, node)));
				regs.removeVertex(node);
				i = 0;
				vertexes_ary = vertex_array();
			} else if(i == vertexes_ary.length - 1) {
				// Prevent crash
				MIR_logger.log("Insufficient registers");
				return;
			}
		}
		
		actual_vertex = vertexes_ary[0];
		actual_vertex.register = 1;

		while (!removed_vertexes.empty()) {
			actual_vertex_and_neighbors = removed_vertexes.pop();
			actual_vertex = actual_vertex_and_neighbors.vertex;
			regs.addVertex(actual_vertex);
			Iterator neighbor_iterator = actual_vertex_and_neighbors.neighbors.iterator();

			while(neighbor_iterator.hasNext()) {
				SimpleNode neighbor = (SimpleNode) neighbor_iterator.next();
				try {
					regs.addEdge(actual_vertex, neighbor);
				} catch (Exception e) {
					// Ainda nao existe o vertice
				}
			}
			
			neighbor_iterator = actual_vertex_and_neighbors.neighbors.iterator();
			
			boolean[] register_occupation = new boolean[k];
			while(neighbor_iterator.hasNext()) {
				SimpleNode neighbor = (SimpleNode) neighbor_iterator.next();
				register_occupation[neighbor.register-1] = true;
			}
			
			for (int i = 0; i < register_occupation.length; i++) {
				if(!register_occupation[i]) {
					actual_vertex.register = i + 1;
					break;
				}
			}
			
			if(actual_vertex.register == 0) {
				MIR_logger.log("Insufficient registers");
			}
		}
	}

	private class Vertex_and_neighbors {
		public List<SimpleNode> neighbors;
		public SimpleNode vertex;

		public Vertex_and_neighbors(SimpleNode vertex, List<SimpleNode> neighbors) {
			this.vertex = vertex;
			this.neighbors = neighbors;
		}
	}

	public void build_interference_graph () {
		SimpleNode[] vars = variable_array();
		for (int i = 0; i < vars.length; ++i) {
			regs.addVertex(vars[i]);
		}

		for (int i = 0; i < vars.length; ++i) {
			for (int j = i+1; j < vars.length; ++j) {
				SimpleNode var1 = vars[i];
				SimpleNode var2 = vars[j];
				if (interfere(
						var1.liveness_begin, 
						var1.liveness_end, 
						var2.liveness_begin, 
						var2.liveness_end)) { 
					System.out.println("Interfere: " + var1.value + " and " + var2.value);
					regs.addEdge(var1, var2);
				}

			}
		}
	}
	
	public SimpleNode[] variable_array() {
		SimpleNode ary[] = new SimpleNode[variables.size()];
		
		int i = 0;
		Iterator it = variables.values().iterator();
		while(it.hasNext()) {
			ary[i] = (SimpleNode) it.next();
			++i;
		}
		
		return ary;
	}



	public boolean interfere(int a_init,int a_final, int b_init, int b_final){
		return (a_final >= b_init) && (a_init <= b_final);
	}
}
