package pdt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import pdt.gui.data.IdListener;
import pdt.gui.data.InvisibleFactHandler;
import pdt.gui.data.PrologConnection;
import pdt.gui.data.PrologData;
import pdt.gui.data.PrologFactHandler;
import pdt.gui.data.PrologMultipleFactHandler;
import pdt.gui.data.PrologSingleFactHandler;
import pdt.gui.data.QuerySelectionProvider;

public class PrologGui implements PrologDataVisualizer {
	
	private final Set<IdListener> listeners = new HashSet<IdListener>();
	private PrologTablePanel tablePanel;
	private PrologConnection con;
	
	public PrologGui(PrologConnection con, PrologData prolog, QuerySelectionProvider selectionCreator, File imgDir, ActionListener imgListener, PrologFactHandler... factHandler) {
		this.con = con;
		
		for (PrologFactHandler handler : factHandler) {
			handler.setVisualizer(this);
		}
		
        createAndShowGUI(prolog, selectionCreator, imgDir, imgListener, factHandler);
	}
	
	@Override
	public void changePrologId(String id) {
		for(IdListener l : listeners) {
			l.setId(id);
		}
	}
	
	 /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI(PrologData prolog, QuerySelectionProvider selectionCreator, File imgDir, ActionListener imgListener, PrologFactHandler... editFacts) {
        //Create and set up the window.
        JFrame frame = new JFrame("Prolog Data Demo");
        frame.addWindowListener(new WindowAdapter() {
        	@Override
			public void windowClosing(WindowEvent evt) {
				for(IdListener l : listeners) {
					l.persistFacts();
				}
			}
		});
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10) );
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setOpaque(true);
        
        tablePanel = new PrologTablePanel(this, prolog);
        QuerySelectionPanel querySelectionPanel = new QuerySelectionPanel(this, selectionCreator);

        contentPane.add(tablePanel, BorderLayout.CENTER);
        contentPane.add(querySelectionPanel, BorderLayout.WEST);
        
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.setPreferredSize(new Dimension(300, 100));
        eastPanel.setMinimumSize(new Dimension(300, 100));
		contentPane.add(eastPanel, BorderLayout.EAST);
        
        if (editFacts.length == 1) {
        	eastPanel.add(getPanel(editFacts[0]), BorderLayout.CENTER);
        	listeners.add(editFacts[0]);
        } else if (editFacts.length > 1) {
        	// tabbed pane
        	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        	eastPanel.add(tabbedPane, BorderLayout.CENTER);
    		
        	for(int i=0; i<editFacts.length; i++) {
        		if (!(editFacts[i] instanceof InvisibleFactHandler)) {
        			tabbedPane.addTab(editFacts[i].getName(), null, getPanel(editFacts[i]), null);
        		}
        		listeners.add(editFacts[i]);
        	}
        }
        
        if (imgDir != null) {
			ImagePanel imagePanel = new ImagePanel(imgDir, imgListener);
			listeners.add(imagePanel);
			eastPanel.add(imagePanel, BorderLayout.NORTH);
        }
        
        frame.setContentPane(contentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel getPanel(PrologFactHandler prologFactHandler) {
    	if (prologFactHandler instanceof PrologMultipleFactHandler) {
    		return new OneToManyPanel((PrologMultipleFactHandler) prologFactHandler);
    	} else if (prologFactHandler instanceof PrologSingleFactHandler) {
    		return new EditPanel((PrologSingleFactHandler) prologFactHandler);
    	}
    	return new JPanel();
	}

	@Override
	public void changedDatabase(String id) {
		tablePanel.updateTableModel(id);
	}

	@Override
	public void changePrologData(PrologData data) {
		tablePanel.setTableModel(data);
	}

	@Override
	public PrologConnection getPrologConnection() {
		return con;
	}

}
