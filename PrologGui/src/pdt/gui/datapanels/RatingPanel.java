package pdt.gui.datapanels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import pdt.gui.datapanels.handler.PrologRatingHandler;
import pdt.gui.utils.SpinnerEditor;

public class RatingPanel extends JPanel implements DataPanel {

	private static final long serialVersionUID = 1L;
	private RatingTableModel ratingTableModel;
	private JTable table;
	private JButton btUpdate;
	private List<Integer> dummyValues;
	private List<Boolean> dummyUnsure;

	/**
	 * Create the panel.
	 */
	public RatingPanel(final PrologRatingHandler prolog) {

		prolog.setEditPanel(this);

		setLayout(new BorderLayout());
		
		ratingTableModel = new RatingTableModel(prolog.getArgs());
		table = new JTable(ratingTableModel);
		
		TableColumn ratingColumn = table.getColumnModel().getColumn(1);
		ratingColumn.setCellEditor(new SpinnerEditor());


		table.setEnabled(false);
		 // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        updateDummyValues();
        // Add the scroll pane to this panel.
		add(scrollPane, BorderLayout.CENTER);
        
		btUpdate = new JButton("Update");
		btUpdate.setEnabled(false);
		btUpdate.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent evt) {
				updateDummyValues();
				prolog.updateFromPanel(ratingTableModel);
			}
		});

		add(btUpdate, BorderLayout.SOUTH);
		
	}

	public void setData(Map<String, Object> result) {
		ratingTableModel.setData(result);
		updateButtons(result != null);
        updateDummyValues();
	}
	
	public String getSingleEntry(String key) {
		return ratingTableModel.getSingleEntry(key);
	}

	@Override
	public void clearPanel() {
		ratingTableModel.setData(null);
        updateDummyValues();
		updateButtons(false);
	}

	private void updateDummyValues() {
		dummyValues = new ArrayList<Integer>(ratingTableModel.getValues());
        dummyUnsure = new ArrayList<Boolean>(ratingTableModel.getUnsure());
	}

	private void updateButtons(boolean enabled) {
		table.setEnabled(enabled);
		btUpdate.setEnabled(enabled);
	}

	@Override
	public boolean changed() {
		boolean valuesChanged = !ratingTableModel.getValues().equals(dummyValues);
		boolean unsureChanged = !ratingTableModel.getUnsure().equals(dummyUnsure);
		// values or unsure changed
		return valuesChanged || unsureChanged;
	}

}
