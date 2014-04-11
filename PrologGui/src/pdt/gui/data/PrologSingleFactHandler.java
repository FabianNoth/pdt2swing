package pdt.gui.data;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import org.cs3.prolog.common.QueryUtils;
import org.cs3.prolog.pif.PrologInterfaceException;

import pdt.gui.EditPanel;
import pdt.gui.utils.PrologUtils;
import pdt.prolog.elements.PrologArgument;
import pdt.prolog.elements.PrologGoal;

public class PrologSingleFactHandler extends PrologFactHandler {

	private EditPanel editPanel;
	private Map<String, Object> result;
	private String mainElementName;
	private final Map<String, ActionListener> additionalActions = new TreeMap<String, ActionListener>();
	
	public PrologSingleFactHandler(PrologConnection con, String name, File outputFile, boolean isMainPredicate, PrologGoal goal) {
		super(con, name, outputFile, isMainPredicate, goal);
	}
	
	public void setMainElementName(String mainElementName) {
		this.mainElementName = mainElementName;
	}
	
	public void setEditPanel(EditPanel editPanel) {
		this.editPanel = editPanel;
	}

	@Override
	public void showData() {
		String query = QueryUtils.bT(getFunctor(), (Object[]) argsWithId);
		System.out.println(query);
		try {
			result = pif.queryOnce(query);
			if (editPanel != null) {
				editPanel.setData(result);
			}
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
	}
	
	public String getMainElement() {
		return getElementByName(mainElementName);
	}
	
	public String getElementByName(String elementName) {
		if (result != null && elementName != null) {
			Object element = result.get(elementName);
			if (element != null) {
				return element.toString();
			}
		}
		return null;
	}

	public void updateFromPanel(HashMap<String, JComponent> textFields) {
		// build assert query
		String retractQuery = getRetractQuery();
		String assertQuery = getAssertQuery(textFields, argsWithId[0]);
		
		try {
			pif.queryOnce("retractall(" + retractQuery + ")");
			pif.queryOnce("assert(" + assertQuery + ")");
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
		
		updateVisualizer();
	}
	
	public void saveAsNew(HashMap<String, JComponent> textFields) {
		String assertQuery = getAssertQuery(textFields, "_");
		String id = null;
		try {
			Map<String, Object> result = pif.queryOnce("assert_as_new(" + assertQuery + ", ID)");
			if (result.get("ID") != null) {
				id = result.get("ID").toString();
			}
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
		
		updateVisualizer(id);
	}
	
	public void delete() {
		String retractQuery = getRetractQuery();

		try {
			pif.queryOnce("retract_with_derived(" + retractQuery + ")");
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
		
		updateVisualizer();
	}

	
	private String getAssertQuery(HashMap<String, JComponent> textFields, String id) {
		String[] assertArgs = new String[argsWithId.length];
		assertArgs[0] = id;
		
		for (int i=1; i<argsWithId.length; i++) {
			JComponent tf = textFields.get(argsWithId[i]);
			if (tf == null) {
				if (getArgs()[i-1].getType() == PrologArgument.NUMBER) {
					assertArgs[i] = "0";
				} else {
					assertArgs[i] = "''";
				}
			} else {
				String text = null;
				if (tf instanceof JTextField) {
					text = ((JTextField) tf).getText();
				} else if (tf instanceof JSpinner) {
					text = ((JSpinner) tf).getValue().toString();
				}
				if (text.isEmpty()) {
					if (getArgs()[i-1].getType() == PrologArgument.NUMBER) {
						assertArgs[i] = "0";
					} else {
						assertArgs[i] = "''";
					}
				} else {
					assertArgs[i] = PrologUtils.quoteIfNecessary(text);
				}
			}
		}
		
		return QueryUtils.bT(getFunctor(), (Object[]) assertArgs);
	}
	
	private String getRetractQuery() {
		String[] retractArgs = new String[argsWithId.length];
		retractArgs[0] = argsWithId[0];
		
		for (int i=1; i<argsWithId.length; i++) {
			retractArgs[i] = "_";
		}
		
		return QueryUtils.bT(getFunctor(), (Object[]) retractArgs);
	}

	public void addAction(String actionName, ActionListener actionListener) {
		additionalActions.put(actionName, actionListener);
	}
	
	public Map<String, ActionListener> getAdditionalActions() {
		return additionalActions;
	}

	public PrologArgument getArgumentWithName(String name) {
		for (int i=0; i<getArgs().length; i++) {
			if (getArgs()[i].getName().equals(name)) {
				return getArgs()[i];
			}
		}
		return null;
	}
	
}
