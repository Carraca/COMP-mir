package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.layout.CircleGraphLayout;
import org.jgraph.layout.JGraphLayoutAlgorithm;
import org.jgraph.layout.SugiyamaLayoutAlgorithm;
import org.jgraph.layout.TreeLayoutAlgorithm;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;



public class GraphPanel extends JPanel {

	private static final long serialVersionUID = 4363083545734504454L;

	private static final Color     DEFAULT_BG_COLOR = Color.decode( "#FAFBFF" );

    private JGraphModelAdapter m_jgAdapter;
	private JGraph jgraph;
	public ListenableGraph g;
	private JScrollPane jpane;
	
	
	public void init(){
		
       
        m_jgAdapter = new JGraphModelAdapter( g );        
      

        jgraph = new JGraph( m_jgAdapter );
        jgraph.setMoveable(true);
        jgraph.setEditable(false);
       
        
        //org.jgraph.layout.AnnealingLayoutAlgorithm layout = new  org.jgraph.layout.AnnealingLayoutAlgorithm();
        //SugiyamaLayoutAlgorithm layout = new SugiyamaLayoutAlgorithm();
        //TreeLayoutAlgorithm layout = new TreeLayoutAlgorithm();
        //org.jgraph.layout.
        CircleGraphLayout layout = new CircleGraphLayout();
        JGraphLayoutAlgorithm.applyLayout(jgraph, jgraph.getRoots(), layout);
       		
		jgraph.getGraphLayoutCache().reload();
		jgraph.repaint();
        jgraph.setScale(1);
        
        jpane = new JScrollPane(jgraph);
        
        setLayout(new BorderLayout());
        removeAll();
        add(jpane, BorderLayout.CENTER);
        jpane.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.isControlDown())
					jgraph.setScale(jgraph.getScale()-e.getWheelRotation()/4.5);
				
			}
		});
	}

	public void setGraph(ListenableGraph cfg) {
		g= cfg;
		
		init();
		revalidate();
		jgraph.validate();
		
	}
	
}
