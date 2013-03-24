package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mir.Program_unit;

import org.jgrapht.graph.ListenableUndirectedGraph;

public class InterferencePanel extends JPanel {

	private static final long serialVersionUID = 8577786916156826425L;

	private Set<String> units;
	
	//mir.program_units.get(KEY_DO_USER).interference_graph;
	private JPanel control;
	private GraphPanel panel;
	private HashMap<String,Program_unit> info;
	private JComboBox drop;
	
	
	public InterferencePanel() {
		super();
		
		panel = new GraphPanel();
		
		units = new HashSet<String>();	
		
		control = new JPanel();
		drop = new JComboBox(units.toArray());
		
	
		JLabel l1 = new JLabel("L: Line Number, V: Variable, R: Register");
		
		l1.setMinimumSize(new Dimension(60, 30));
		
	
	
		//label.setPreferredSize(new Dimension(400, 30));
		
		
		control.setLayout(new FlowLayout(FlowLayout.LEFT));
		control.add(drop);
		control.add(l1);
		
		
				
		drop.addActionListener(new DropList());
		
		setLayout(new BorderLayout());
		add(control, BorderLayout.NORTH);
		add(panel,BorderLayout.CENTER);
		
		
	}
	
	class DropList implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshGraph((JComboBox) (e.getSource()));
		}
		
	}
	
	public void refreshGraph(JComboBox c){
		String choice = (String) c.getSelectedItem();
		System.out.println(choice);
		remove(panel);
		panel = new GraphPanel();
		add(panel,BorderLayout.CENTER);
		if(info.get(choice) != null) {
			System.out.println(info.toString());
			System.out.println(info.get(choice).toString());
			System.out.println(info.get(choice).regs.toString());
			
			panel.setGraph(info.get(choice).regs);
			System.out.println("REGS: " + info.get(choice).regs.toString());
		}
		revalidate();
	}
	
	public void addUnits(HashMap<String,Program_unit> info){
		drop.removeAllItems();
		this.info = info;
		for(String i : info.keySet())
			drop.addItem(i);
		drop.validate();
		refreshGraph(drop);
	}
}
