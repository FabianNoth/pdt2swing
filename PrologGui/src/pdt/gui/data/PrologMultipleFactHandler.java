package pdt.gui.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.cs3.prolog.common.QueryUtils;
import org.cs3.prolog.common.Util;
import org.cs3.prolog.pif.PrologInterfaceException;

import pdt.gui.OneToManyPanel;
import pdt.prolog.elements.PrologGoal;

public class PrologMultipleFactHandler extends PrologFactHandler {

	private OneToManyPanel editPanel;
	private boolean autoCompletion;
	
	public PrologMultipleFactHandler(PrologConnection con, String name, File outputFile, PrologGoal goal) {
		this(con, name, outputFile, goal, false);
	}
	
	public PrologMultipleFactHandler(PrologConnection con, String name, File outputFile, PrologGoal goal, boolean autoCompletion) {
		super(con, name, outputFile, false, goal);
		this.autoCompletion = autoCompletion;
	}
	
	public boolean isAutoCompletion() {
		return autoCompletion;
	}

	@Override
	public void showData() {
		String query = QueryUtils.bT(getFunctor(), (Object[]) argsWithId);
		try {
			List<Map<String, Object>> results = pif.queryAll(query);
			if (editPanel != null) {
				List<String> entries = new ArrayList<String>();
				for (Map<String, Object> m : results) {
					String entry = m.get(argsWithId[1]).toString();
					entries.add(entry);
				}
				
				editPanel.setData(entries);
			}
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
	}

	public void addValue(String newValue) {
		String assertValue = Util.quoteAtomIfNeeded(newValue);
		
		try {
			Map<String, Object> check = pif.queryOnce("check_for_existing_value(" + getFunctor() + ","  + assertValue + ")");
			if (check == null) {
				// TODO Value doesn't exist, ask if user wants to add value (might be a typo)
				System.out.println("doesn't exist, won't add");
				
				int answer = JOptionPane.showConfirmDialog(editPanel, "Eintrag mit dem Wert \"" + newValue + "\" existiert nicht. Soll er hinzugefügt werden?", "Neuen Eintrag hinzufügen", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.NO_OPTION) {
					return;
				}
			}
			
			String assertQuery = QueryUtils.bT(getFunctor(), argsWithId[0], assertValue);
			System.out.println(assertQuery);
			try {
				pif.queryOnce("assert_if_not_exists(" + assertQuery + ")");
			} catch (PrologInterfaceException e) {
				e.printStackTrace();
			}
			
			// update table
			updateVisualizer();
			
		} catch (PrologInterfaceException e1) {
			e1.printStackTrace();
		}
	}
	
	public void setEditPanel(OneToManyPanel editPanel) {
		this.editPanel = editPanel;
	}

	public void removeValue(String value) {
		String retractQuery = QueryUtils.bT(getFunctor(), argsWithId[0], value);
		System.out.println(retractQuery);
		try {
			pif.queryOnce("retractall(" + retractQuery + ")");
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
		
		// update table
		updateVisualizer();
	}

	
}
