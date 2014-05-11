package pdt.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import pdt.gui.data.PrologRatingHandler;
import pdt.gui.utils.SpinnerEditor;

public class RatingPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private RatingTableModel ratingTableModel;

	/**
	 * Create the panel.
	 */
	public RatingPanel(final PrologRatingHandler prolog) {

		prolog.setEditPanel(this);

		setLayout(new BorderLayout());
		
		ratingTableModel = new RatingTableModel(prolog.getArgs());
		JTable table = new JTable(ratingTableModel);
		
		TableColumn ratingColumn = table.getColumnModel().getColumn(1);
		ratingColumn.setCellEditor(new SpinnerEditor());
//		ratingColumn.setCellEditor(new SpinnerEditor2(0, 10, 1));

//		table.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "selectNextRowCell");

		 // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the scroll pane to this panel.
		add(scrollPane, BorderLayout.CENTER);
        
		JButton btUpdate = new JButton("Update");
		btUpdate.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent evt) {
				prolog.updateFromPanel(ratingTableModel);
			}
		});

		add(btUpdate, BorderLayout.SOUTH);
		
	}

	public void setData(Map<String, Object> result) {
		ratingTableModel.setData(result);
	}
	
	public String getSingleEntry(String key) {
		return ratingTableModel.getSingleEntry(key);
	}

	public void clearPanel() {
		ratingTableModel.setData(null);
	}

}
