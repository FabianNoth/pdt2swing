package pdt.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import pdt.gui.data.BundleProvider;

/**
 *
 * This panel displays a tree with all possible queries.
 *
 */
public class QuerySelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JTextField textField = new JTextField();
	private TreePath treeSelectionPath = null;
	private boolean skipListener = false;

	/**
	 * Create the panel and fill the tree with the queries provided by the QuerySelectionProvider.
	 * If a query is selected, the PrologDataVisualizer will be called to display the data of this query.
	 * 
	 * @param visualizer The PrologGui which will display the data of the query
	 * @param selectionCreator The QuerySelectionProvider which provides all the possible queries
	 */
	public QuerySelectionPanel(final PrologGui visualizer, BundleProvider selectionCreator) {
		setLayout(new BorderLayout(5, 5));
		
		// create the tree element
		final JTree tree = new JTree(selectionCreator.createRoot());
	
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		treeSelectionPath = tree.getPathForRow(0);
		tree.setSelectionPath(treeSelectionPath);
		
		// add action for changing the selection
		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override public void valueChanged(TreeSelectionEvent evt) {
				if (skipListener) {
					// only skip once
					skipListener = false;
				} else {
					if (visualizer.abort()) {
						// if prolog id wasn't changed (because of unsafed changes)
						// reset the id (and skip the next update, because its just resetting)
						skipListener = true;
						tree.setSelectionPath(treeSelectionPath);
					} else {
						treeSelectionPath = tree.getSelectionPath();
						if (treeSelectionPath != null) {
							Object lastPathComponent = treeSelectionPath.getLastPathComponent();
							if (lastPathComponent instanceof QueryNode) {
								// if a query node was selected
								QueryNode queryNode = (QueryNode) lastPathComponent;
								// update the filter
								queryNode.getBundle().getTableData().setGoal(queryNode.getGoal());
								// display it
								visualizer.setBundle(queryNode.getBundle());
							}
						}
					}
				}
				
			}
		});
		add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(2, 2));
		
		// textfield for manual queries
		// TODO: still has to be implemented (see #24)
		panel.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Show");
		panel.add(btnNewButton, BorderLayout.EAST);

	}

}
