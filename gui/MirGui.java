package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.mxgraph.util.svg.ParseException;

import mir.MIR;
import mir.MIR_logger;
import mir.SemanticException;


public class MirGui extends JFrame{

	private static final long serialVersionUID = 4299556641216811197L;
	private CodePanel codePanel;
	private CodePanel registersPanel;
	public GraphPanel graphPanel;
	private LogPanel logPanel;
	private ControlPanel controlPanel;
	private JSplitPane horz_split;
	private JSplitPane vert_split;
	private InterferencePanel interference;
	private livenessPanel liveness;
	private ASTPanel ast;
	
	private int h,w;
	private double horzDivider = 2.0;
	private double vertDivider = 1.5;
	
	public MirGui(int w, int h) {
		super("MIR");
		this.h=h;
		this.w=w;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponentListener(new myComponentListener());
		
		codePanel = new CodePanel();
		graphPanel = new GraphPanel();
		logPanel = new LogPanel();
		controlPanel = new ControlPanel();
		registersPanel = new CodePanel();
		interference = new InterferencePanel();		
		liveness = new livenessPanel();
		ast = new ASTPanel();
		
		controlPanel.getCompile().addActionListener(new Compile());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("CFG", graphPanel);
		tabbedPane.addTab("Registers", registersPanel);                 
		tabbedPane.addTab("Interferences", interference);
		tabbedPane.addTab("Liveness", liveness);
		tabbedPane.addTab("AST", ast);
		
		horz_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codePanel, tabbedPane);       
		horz_split.setOneTouchExpandable(true);		
		vert_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horz_split, logPanel);       
		vert_split.setOneTouchExpandable(true);
		
		horz_split.setDividerLocation((int)(w/horzDivider));
		vert_split.setDividerLocation((int)(h/vertDivider));
		
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(controlPanel,BorderLayout.NORTH);
		contentPane.add(vert_split,BorderLayout.CENTER);
		
		pack();
		setSize(w,h);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {}
		setLocationRelativeTo(null);
		requestFocus();
		setVisible(true);
	}
	
	class Compile implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			MIR_logger.log("Starting compilation");
			
			long start_time = System.currentTimeMillis();
			
			codePanel.update();
			String str = codePanel.getText();
			InputStream f = new ByteArrayInputStream(str.getBytes());
			
			int n_registers = controlPanel.getNRegisters();
			
			try{
				MIR mir = new MIR(f, n_registers, str);
				mir.run();
				long graphic_start_time, graphic_end_time;
				graphic_start_time = System.currentTimeMillis();
				MIR_logger.log("Rendering visualisations...");
				graphPanel.setGraph(mir.cfg);
				interference.addUnits(mir.program_units);
				liveness.addUnits(mir.program_units);
				ast.init(mir.root);
				registersPanel.setText(mir.generated_code);
				graphic_end_time = System.currentTimeMillis();
				MIR_logger.log("Done", graphic_start_time, graphic_end_time);
				
			} catch(SemanticException ex){
				MIR_logger.log(ex.getMessage());
				ArrayList<Integer> line = new ArrayList<Integer>();
				line.add(ex.line);
				highlight(line);
				
			} catch (mir.ParseException ex) {
				
				MIR_logger.log(ex.getMessage());
				ArrayList<Integer> line = new ArrayList<Integer>();
				line.add(ex.line);
				highlight(line);
				
			}
			
			long end_time = System.currentTimeMillis();
			MIR_logger.log("Finished compilation", start_time, end_time);
		}
	}
	
	class myComponentListener implements ComponentListener{

		@Override
		public void componentHidden(ComponentEvent e) {}

		@Override
		public void componentMoved(ComponentEvent e) {}

		@Override
		public void componentResized(ComponentEvent e) {
			Dimension dim = e.getComponent().getSize();
			
			horz_split.setDividerLocation((int)(dim.getWidth()/w*horz_split.getDividerLocation()));
			vert_split.setDividerLocation((int)(dim.getHeight()/h*vert_split.getDividerLocation()));
			w=(int)dim.getWidth();
			h=(int)dim.getHeight();
			
		}

		@Override
		public void componentShown(ComponentEvent e) {}
		
	}
	
	public void setText(String filePath){
		codePanel.setTextFromFile(filePath);
	}
	
	public void setRegisters(String regs){
		registersPanel.setEditable(false);
		registersPanel.setText(regs);
		
	}
	
	public void highlight(ArrayList<Integer> lines){
		codePanel.highlight(lines);
	}
	
	public void log(String str){
		logPanel.append(str+"\n");
		
	}
	
	public JButton getCompile(){
		return controlPanel.getCompile();
	}
	

}
