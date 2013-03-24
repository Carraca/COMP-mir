package gui;

import gui.InterferencePanel.DropList;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import mir.Program_unit;
import mir.SimpleNode;



public class livenessPanel extends JPanel {
	
	private Set<String> units;
	private JPanel control;
	private JTable panel;
	private HashMap<String,Program_unit> info;
	private JComboBox drop;	
	private JScrollPane jsb;
	
	public livenessPanel() {
		super();
		
		panel = new JTable();
		
		jsb = new JScrollPane(panel);
		
		
		
		units = new HashSet<String>();	
		
		control = new JPanel();
		drop = new JComboBox(units.toArray());
		
		control.setLayout(new FlowLayout(0));
		control.add(drop);
				
		drop.addActionListener(new DropList());
		
		setLayout(new BorderLayout());
		add(control, BorderLayout.NORTH);
		add(jsb,BorderLayout.CENTER);
		
		
	}
	
	public void initTable(){
		String[] column_names =  {
				"Name", "Liveness Begin (line no.)", "Liveness End (line no.)", "Assigned Register"
			};
		
		Program_unit unit = info.get(drop.getSelectedItem());
		
		if(unit == null)
			return;
			
		HashMap<String, SimpleNode> vars = unit.variables;
		Iterator<SimpleNode> it = vars.values().iterator();
		SimpleNode var;
		
		String[][] table_rows = new String[vars.size()][4];
		int i=0;
		
		while(it.hasNext()){
			var = it.next();
			
			table_rows[i][0] = (String)var.value;
			table_rows[i][1] = String.valueOf(var.liveness_begin);
			table_rows[i][2] = String.valueOf(var.liveness_end);
			if(var.register == 0) {
				table_rows[i][3] = "Not assigned";
			} else {
				table_rows[i][3] = String.valueOf(var.register);
			}
			
			++i;
		}
				
		panel.setModel(new DefaultTableModel(table_rows,column_names) {
			boolean[] canEdit = new boolean [] {
	                false, false, false, false
	            };

	            @Override
	            public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit[columnIndex];
	            }
		});
	}
	
	class DropList implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshTable((JComboBox) (e.getSource()));
		}
		
	}
	
	public void refreshTable(JComboBox c){
		String choice = (String) c.getSelectedItem();
		
		remove(jsb);
		panel = new JTable();
		jsb = new JScrollPane(panel);
		add(jsb,BorderLayout.CENTER);
		
		//panel.setGraph(info.get(choice).regs);
		initTable();
		
		
		revalidate();
	}
	
	public void addUnits(HashMap<String,Program_unit> info){
		drop.removeAllItems();
		this.info = info;
		for(String i : info.keySet())
			drop.addItem(i);
		drop.validate();
		refreshTable(drop);
	}
}

