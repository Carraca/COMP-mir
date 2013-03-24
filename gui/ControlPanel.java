package gui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringNameProvider;


public class ControlPanel extends JPanel {
	
	private static final long serialVersionUID = -3689467479055677094L;
	private JButton filePathButton;
	private JButton compileButton;
	private JButton saveCFGButton;
	private MyChooser fc;
	private MyChooser sc;
	private JLabel path_label;
	private String save_path;
	private String path;
	private JSpinner registers;
	private int n_registers;
	
	public ControlPanel() {
		super();
		fc = new MyChooser();
		sc = new MyChooser();
		filePathButton = new JButton("Open");
		compileButton = new JButton("Compile");
		saveCFGButton = new JButton("Save CFG");
		path = new String("");
		save_path = new String("");
		path_label = new JLabel(path);
		
		Integer value = new Integer(4); 
		 Integer min = new Integer(1);
		 Integer max = new Integer(999); 
		 Integer step = new Integer(1); 
		 SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step); 
		 
		registers = new JSpinner(model);
		
		
		filePathButton.addActionListener(new fileOpener());
		//compileButton.addActionListener(new Compile());
		saveCFGButton.addActionListener(new SaveCFG());
		
		setLayout(new FlowLayout(0,10,2));
		//setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		add(path_label);
		add(filePathButton);
		add(registers);
		add(compileButton);
		add(saveCFGButton);
		
	}
	
	public int getNRegisters(){
		return (Integer) registers.getValue();
	}
	
	public JButton getCompile(){
		return compileButton;
	}
	
	class fileOpener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			fc.showOpenDialog(filePathButton);
			try{
				path = fc.getSelectedFile().getAbsolutePath();
				path_label.setText(path);
				((gui.MirGui) getParent().getParent().getParent().getParent()).setText(path);
			}
			catch(java.lang.NullPointerException e){
				path="";
				path_label.setText(path);
			}
			
		}
	}
	
class SaveCFG implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		sc.showSaveDialog(saveCFGButton);
		try{
			save_path = sc.getSelectedFile().getAbsolutePath();

			
	        DOTExporter de = new DOTExporter(new IntegerNameProvider(), new StringNameProvider(), null);
	        try {
				de.export(new FileWriter(save_path), ((gui.MirGui) getParent().getParent().getParent().getParent()).graphPanel.g);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		catch(java.lang.NullPointerException e){
			save_path="";
		}
		
	}
}
	
	class MyChooser extends JFileChooser {
		private static final long serialVersionUID = 8536545636951264713L;

		protected JDialog createDialog(Component parent)
                throws HeadlessException {
            JDialog dlg = super.createDialog(parent);
            dlg.setLocationRelativeTo(parent.getParent().getParent());
            return dlg;
        }
    }
}
