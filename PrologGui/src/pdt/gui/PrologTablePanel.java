package pdt.gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pdt.gui.data.PrologTableData;

public class PrologTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private PrologTableData prologTableModel;
	private JTable table;
	
	public PrologTablePanel(final PrologDataVisualizer parent, PrologTableData prologData) {
	        super(new GridLayout(1,0));
	        
	        prologTableModel = prologData;
			table = new JTable(prologTableModel);

	        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	        ListSelectionModel selectionModel = table.getSelectionModel();
	        selectionModel.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent evt) {
					if (!evt.getValueIsAdjusting()) {
						String id = getSelectedId();
						if (id != null) {
							parent.changePrologId(id);
						}
					}
				}
			});
	        
	        
	        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
	        table.setFillsViewportHeight(true);
	        
	        // Create the scroll pane and add the table to it.
	        JScrollPane scrollPane = new JScrollPane(table);

	        // Add the scroll pane to this panel.
	        add(scrollPane);
	    }
	
	public void setTableModel(PrologTableData prologTableModel) {
		if (!this.prologTableModel.equals(prologTableModel)) {
			System.out.println("data differs, so change table");
			String id = getSelectedId();
			this.prologTableModel = prologTableModel;
			table.setModel(prologTableModel);
			updateTableModel(id);
		}
	}
	
	public String getSelectedId() {
		int row = table.getSelectedRow();
		if (row > -1) {
			Object value = table.getModel().getValueAt(row, 0);
			return value.toString();
		}
		return null;
	}
	
	public void updateTableModel(String id) {
		if (id == null) {
			id = getSelectedId();
		}
		prologTableModel.updateResultData();
		prologTableModel.fireTableDataChanged();
		
		int row = prologTableModel.getPositionOfId(id);
		if (row > -1) {
			table.setRowSelectionInterval(row, row);
		}
	}

}
