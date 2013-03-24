package gui;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import mir.SimpleNode;

public class ASTPanel extends JPanel {

	private JTree tree;	
	
	public ASTPanel() {}
	
	public void init(SimpleNode root){
		removeAll();
		
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(root.gui_print());
		
		addNodes(node, root);
		
		tree = new JTree(node.getFirstChild());
		
		JScrollPane jsp = new JScrollPane(tree);
		setLayout(new BorderLayout());
		add(jsp, BorderLayout.CENTER);
	}
	
	private void addNodes(DefaultMutableTreeNode parent, SimpleNode node){
		if (node == null) {
			return;
		}

		DefaultMutableTreeNode parent_node = new DefaultMutableTreeNode(node.gui_print());
		parent.add(parent_node);
		//System.out.println(String.format("Name: %s ; ID: %d", node.toString(), node.id));
		if (node.children != null && node.children.length > 0) {
			for (int i = 0; i < ((SimpleNode) node).children.length; ++i) {
				addNodes(parent_node, (SimpleNode) node.children[i]);
			}
		}
	}
	
}
