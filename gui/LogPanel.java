package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class LogPanel extends JPanel {
	
	private static final long serialVersionUID = 2397220195595371587L;
	private JTextArea log;
	
	public LogPanel() {
		super();
		
		JScrollPane jsp = new JScrollPane();
		log = new JTextArea();
		log.setEditable(false);
		log.setWrapStyleWord(true);
		log.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		jsp.getViewport().add(log);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		
		setLayout(new BorderLayout());
		add(jsp,BorderLayout.CENTER);
	}
	
	public void append(String str){
		log.append(str);
		log.setCaretPosition(log.getDocument().getLength());
	}
	
	public void setHeight(int h){
		setPreferredSize(new Dimension(0,h));
	}
}
