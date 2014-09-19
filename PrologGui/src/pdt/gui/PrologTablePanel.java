package pdt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pdt.gui.data.PrologTableData;
import pdt.gui.utils.SimpleLogger;

public class PrologTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private PrologTableData prologTableModel;
	private JTable table;
	private int rowSelection = -1;
	private boolean skipListener = false;
	
	public PrologTablePanel(final PrologDataVisualizer parent, PrologTableData prologData) {
	        super(new BorderLayout());
	        
	        prologTableModel = prologData;
			table = new JTable(prologTableModel);

			table.setAutoCreateRowSorter(true);

	        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	        ListSelectionModel selectionModel = table.getSelectionModel();
	        selectionModel.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent evt) {
					if (!evt.getValueIsAdjusting()) {
						if (skipListener) {
							// only skip once
							skipListener = false;
						} else {
							String id = getSelectedId();
							if (parent.changePrologId(id)) {
								rowSelection = table.getSelectedRow();
							} else {
								// if prolog id wasn't changed (because of unsaved changes)
								// reset the id (and skip the next update, because its just resetting)
								skipListener = true;
								if (rowSelection == -1) {
									table.clearSelection();
								} else {
									table.setRowSelectionInterval(rowSelection, rowSelection);
								}
							}
						}
					}
				}
			});
	        
	        
	        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
	        table.setFillsViewportHeight(true);
	        
	        // Create the scroll pane and add the table to it.
	        JScrollPane scrollPane = new JScrollPane(table);

	        // Add the scroll pane to this panel.
	        add(scrollPane, BorderLayout.CENTER);
	        
	        JToolBar toolBar = new JToolBar();
	        toolBar.setFloatable(false);

	        JButton btClearSelection = new JButton("Auswahl zurücksetzen");
	        btClearSelection.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent evt) {
					// clear table selection, the ListSelectionListener will handle the rest
					table.clearSelection();
				}
			});
	        toolBar.add(btClearSelection);
	        add(toolBar, BorderLayout.NORTH);
	    }
	
	public void setTableModel(PrologTableData prologTableModel) {
		if (!this.prologTableModel.equals(prologTableModel)) {
			SimpleLogger.debug("data differs, so change table");
			String id = getSelectedId();
			this.prologTableModel = prologTableModel;
			table.setModel(prologTableModel);
			updateTableModel(id);
		}
	}
	
	public String getSelectedId() {
		int row = table.getSelectedRow();
		if (row > -1) {
			Object value = table.getModel().getValueAt(table.convertRowIndexToModel(row), 0);
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
			int viewRow = table.convertRowIndexToView(row);
			table.setRowSelectionInterval(viewRow, viewRow);
		}
	}

}
