package pdt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import pdt.gui.data.BundleProvider;
import pdt.gui.data.IdListener;
import pdt.gui.data.InvisibleFactHandler;
import pdt.gui.data.PrologConnection;
import pdt.gui.data.PrologDataHandler;
import pdt.gui.data.PrologFactHandler;
import pdt.gui.data.PrologGuiBundle;
import pdt.gui.data.PrologRatingHandler;
import pdt.gui.data.PrologRelationHandler;
import pdt.gui.data.PrologTableData;
import pdt.gui.data.PrologTextFileHandler;
import pdt.gui.utils.AppZip;
import pdt.gui.utils.SimpleLogger;

public class PrologGui implements PrologDataVisualizer {

	private static final int BACKUP_FILTER = 5;
	private final Set<IdListener> activeListeners = new HashSet<IdListener>();
	private PrologTablePanel tablePanel;
	private PrologConnection con;
	private JFrame frame;
	private QuerySelectionPanel querySelectionPanel;
	
	private BundleProvider bundleProvider;
	
	public PrologGui(PrologConnection con, BundleProvider bundleProvider) {
		this.con = con;
		this.bundleProvider = bundleProvider;
		
        createAndShowGUI();
	}
	
	public PrologGui(PrologConnection con, BundleProvider bundleProvider, File baseDir, File backupDir) {
		this(con, bundleProvider);
		updateBackupFolder(baseDir, backupDir);
	}

	private void updateBackupFolder(File baseDir, File backupDir) {
		File[] fileList = backupDir.listFiles(new FileFilter() {
			@Override public boolean accept(File f) {
				return f.getName().endsWith(".zip");
			}
		});
		
		int n = fileList.length;
		int i = 0;
		
		while(n >= BACKUP_FILTER) {
			fileList[i].delete();
			i++;
			n--;
		}
		
		AppZip zipper = new AppZip(baseDir.getAbsolutePath());
		zipper.generateFileList(baseDir);
		String zipName = "backup_" + System.currentTimeMillis() + ".zip";
		File zipFile = new File(backupDir, zipName);
		zipper.zipIt(zipFile.getAbsolutePath());
		
	}

	@Override
	public void changePrologId(String id) {
		for(IdListener l : activeListeners) {
			l.setId(id);
		}
	}
	
	 /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        frame = new JFrame("Prolog Data Demo");
        frame.addWindowListener(new WindowAdapter() {
        	@Override
			public void windowClosing(WindowEvent evt) {
        		bundleProvider.persistFacts();
//				for(IdListener l : allListeners) {
//					l.persistFacts();
//				}
			}
		});
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        querySelectionPanel = new QuerySelectionPanel(this, bundleProvider);
        
        setBundle(bundleProvider.getDefault());
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    
    private JPanel createEmptyContentPane() {
    	JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10) );
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setOpaque(true);
        contentPane.add(querySelectionPanel, BorderLayout.WEST);
        frame.setContentPane(contentPane);
        return contentPane;
    }
    
    private PrologGuiBundle currentBundle;
    
	@Override
	public void setBundle(PrologGuiBundle bundle) {
		if (bundle != currentBundle) {
			currentBundle = bundle;
			SimpleLogger.println("set new bundle");
			activeListeners.clear();
			for (PrologDataHandler handler : bundle.getFactHandlers()) {
				handler.setVisualizer(this);
			}
			
			tablePanel = new PrologTablePanel(this, bundle.getTableData());
			
			JPanel contentPane = createEmptyContentPane();
			contentPane.add(tablePanel, BorderLayout.CENTER);
			
			JPanel eastPanel = new JPanel();
			eastPanel.setLayout(new BorderLayout());
			eastPanel.setPreferredSize(new Dimension(300, 100));
			eastPanel.setMinimumSize(new Dimension(300, 100));
			contentPane.add(eastPanel, BorderLayout.EAST);
			
			List<PrologDataHandler> factHandlers = bundle.getFactHandlers();
			if (factHandlers.size() == 1) {
				eastPanel.add(getPanel(factHandlers.get(0)), BorderLayout.CENTER);
				addToListeners(factHandlers.get(0));
			} else if (factHandlers.size() > 1) {
				// tabbed pane
				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
				eastPanel.add(tabbedPane, BorderLayout.CENTER);
				
				for(int i=0; i<factHandlers.size(); i++) {
					if (!(factHandlers.get(i) instanceof InvisibleFactHandler)) {
						tabbedPane.addTab(factHandlers.get(i).getName(), null, getPanel(factHandlers.get(i)), null);
					}
					addToListeners(factHandlers.get(i));
				}
			}
			
			if (bundle.getImgDir() != null) {
				ImagePanel imagePanel = new ImagePanel(bundle.getImgDir(), bundle.getImgListener());
				addToListeners(imagePanel);
				eastPanel.add(imagePanel, BorderLayout.NORTH);
			}
		}
		tablePanel.updateTableModel(null);
		frame.revalidate();
	}

	private void addToListeners(IdListener listener) {
		activeListeners.add(listener);
	}

	private JPanel getPanel(PrologDataHandler prologFactHandler) {
    	if (prologFactHandler instanceof PrologRelationHandler) {
    		return new RelationPanel((PrologRelationHandler) prologFactHandler);
    	} else if (prologFactHandler instanceof PrologFactHandler) {
    		return new FactPanel((PrologFactHandler) prologFactHandler);
    	} else if (prologFactHandler instanceof PrologRatingHandler) {
    		return new RatingPanel((PrologRatingHandler) prologFactHandler);
    	} else if (prologFactHandler instanceof PrologTextFileHandler) {
    		return new TextFilePanel((PrologTextFileHandler) prologFactHandler);
    	}
    	return new JPanel();
	}

	@Override
	public void changedDatabase(String id) {
		tablePanel.updateTableModel(id);
	}

	@Override
	public void changePrologData(PrologTableData data) {
		tablePanel.setTableModel(data);
	}

	@Override
	public PrologConnection getPrologConnection() {
		return con;
	}

}
