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

	/**
	 * Create the panel and fill the tree with the queries provided by the QuerySelectionProvider.
	 * If a query is selected, the PrologDataVisualizer will be called to display the data of this query.
	 * 
	 * @param visualizer The PrologDataVisualizer which will display the data of the query
	 * @param selectionCreator The QuerySelectionProvider which provides all the possible queries
	 */
	public QuerySelectionPanel(final PrologDataVisualizer visualizer, BundleProvider selectionCreator) {
		setLayout(new BorderLayout(5, 5));
		
		// create the tree element
		final JTree tree = new JTree(selectionCreator.createRoot());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		// add action for changing the selection
		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override public void valueChanged(TreeSelectionEvent evt) {
				TreePath selectionPath = tree.getSelectionPath();
				if (selectionPath != null) {
					Object lastPathComponent = selectionPath.getLastPathComponent();
					if (lastPathComponent instanceof QueryNode) {
						// if a query node was selected
						QueryNode queryNode = (QueryNode) lastPathComponent;
						// update the filter
						queryNode.getBundle().setFilter(queryNode.getFilter());
						// display it
						visualizer.setBundle(queryNode.getBundle());
					}
				}
			}
		});
		add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(2, 2));
		
		// textfield for manual queries
		// TODO: still has to be implemented
		panel.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Show");
		panel.add(btnNewButton, BorderLayout.EAST);

	}

}
