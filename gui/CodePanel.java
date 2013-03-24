package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Highlighter;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
 
public class CodePanel extends JPanel{
	private static final long serialVersionUID = -8211183180462113758L;
	
	private  String text;
	private  JTextPane jta;
	private  JTextPane lines;
	private  ArrayList<Integer[]> nums;
 
	public CodePanel(){
		super();
		JScrollPane jsp = new JScrollPane();
		jta = new JTextPane();
		lines = new JTextPane();
		
		jta.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			update();
				
			}
		});
		
		jta.setDocument(new TabDocument());
		lines.setFont(new Font("Monospaced", Font.PLAIN, 12));
		jta.setFont(new Font("Monospaced", Font.PLAIN, 12));
		lines.setBackground(Color.LIGHT_GRAY);
		lines.setEditable(false);
		jta.setEditable(true);
	
		
		jsp.getViewport().add(jta);
		jsp.setRowHeaderView(lines);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		setLayout(new BorderLayout());
		add(jsp,BorderLayout.CENTER);
	}
	
	public void setEditable(boolean b){
		jta.setEditable(b);
	}
	
	public void update(){
		
		int pos = jta.getCaretPosition();
		text = jta.getText();
		jta.setText(text);
		
		LineNumbers ln = new LineNumbers(text);
		nums = ln.getNumbers();
		
		String t="";
		for(int i = 1; i <nums.size()+1; i++){
			t += i + System.getProperty("line.separator");
		}
		lines.setText(t);
		jta.setCaretPosition(pos);
	}
	
	public String getText(){
	//	return text;
		return jta.getText();
	}
	
	public void setPreferredSize(Dimension d){
		jta.setPreferredSize(d);
	}
	
	public void setText(String regs){
		jta.setText(regs);
		text = jta.getText();
		
		LineNumbers ln = new LineNumbers(text);
		nums = ln.getNumbers();
		
		String t="";
		for(int i = 1; i <nums.size()+1; i++){
			t += i + System.getProperty("line.separator");
		}
		lines.setText(t);
	}
	
	public void setTextFromFile(String filePath){
		String text2 = FileString.filetoString(filePath);
		jta.setText(text2);
		text = jta.getText();
	
		LineNumbers ln = new LineNumbers(text);
		nums = ln.getNumbers();
		
		String t="";
		for(int i = 1; i <nums.size()+1; i++){
			t += i + System.getProperty("line.separator");
		}
		lines.setText(t);
	}
	
	public void highlight(ArrayList<Integer> lines){
		
		jta.setText(getText());
		for(int i=0; i<lines.size();++i){
			try{
				int s= nums.get(lines.get(i)-1)[0];
				int e = nums.get(lines.get(i)-1)[1];
				jta.setCaretPosition(s);
				
				Highlighter h = jta.getHighlighter();
				h.addHighlight(s, e, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
			} 
			catch (BadLocationException e1) {}
			catch (java.lang.IndexOutOfBoundsException e2) {}
		}
		
	}
	
	static class TabDocument extends DefaultStyledDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                str = str.replaceAll("\t", "   ");
               
                super.insertString(offs, str, a);
        }
    }
}