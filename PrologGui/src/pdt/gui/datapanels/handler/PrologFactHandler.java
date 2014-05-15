package pdt.gui.datapanels.handler;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import org.cs3.prolog.common.QueryUtils;
import org.cs3.prolog.pif.PrologInterfaceException;

import pdt.gui.data.PrologConnection;
import pdt.gui.datapanels.FactPanel;
import pdt.gui.datapanels.SpinnerWithCheckbox;
import pdt.gui.utils.PrologUtils;
import pdt.gui.utils.SimpleLogger;
import pdt.prolog.elements.PrologArgument;
import pdt.prolog.elements.PrologGoal;

public class PrologFactHandler extends PrologDataHandler {

	private FactPanel editPanel;
	private Map<String, Object> result;
	private String mainElementName;
	private final Map<String, ActionListener> additionalActions = new TreeMap<String, ActionListener>();
	
	public PrologFactHandler(PrologConnection con, String name, File outputFile, boolean isMainPredicate, PrologGoal goal) {
		super(con, name, outputFile, isMainPredicate, goal);
	}

	public PrologTextFileHandler createTextFileHandler(String title) {
		File textOutputDir = new File(outputFile.getParentFile(), getFunctor());
		PrologTextFileHandler textData = new PrologTextFileHandler(title, textOutputDir);
		return textData;
	}

	public void setMainElementName(String mainElementName) {
		this.mainElementName = mainElementName;
	}
	
	public void setEditPanel(FactPanel editPanel) {
		this.editPanel = editPanel;
	}

	@Override
	public void showData() {
		SimpleLogger.debug(getQuery());
		try {
			result = pif.queryOnce(getQuery());
			result.put("ID", currentId);
			if (editPanel != null) {
				editPanel.setData(result);
			}
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clearData() {
		editPanel.clearPanel();
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
		if (currentId == null) {
			return;
		}
		
		// get goal for assertion, use current id
		String goal = getGoalWithData(textFields, currentId);
		
		try {
			pif.queryOnce(QueryUtils.bT(UPDATE_FACT, goal));
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
		
		updateVisualizer();
	}
	
	public void saveAsNew(HashMap<String, JComponent> textFields) {
		// get goal for assertion, use empty ID
		String goal = getGoalWithData(textFields, "_");
		String id = null;
		try {
			Map<String, Object> result = pif.queryOnce(QueryUtils.bT(ADD_FACT, goal, "ID"));
			if (result.get("ID") != null) {
				id = result.get("ID").toString();
			}
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
		
		updateVisualizer(id);
	}
	
	public void delete() {
		if (currentId == null) {
			return;
		}
		
		String goal = getSimpleGoal();

		try {
			pif.queryOnce(QueryUtils.bT(REMOVE_FACT, goal));
			
			// if there is a text file, remove it also
			File textOutputDir = new File(outputFile.getParentFile(), getFunctor());
			File textFile = new File(textOutputDir, currentId);
			if (textFile.isFile()) {
				textFile.delete();
			}
			
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
		
		updateVisualizer();
	}

	
	private String getGoalWithData(HashMap<String, JComponent> textFields, String id) {
		String[] argNames = getArgNames();
		String[] assertArgs = new String[argNames.length];
		assertArgs[0] = id;
		
		for (int i=1; i<argNames.length; i++) {
			JComponent tf = textFields.get(argNames[i]);
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
				} else if (tf instanceof JComboBox<?>) {
					text = ((JComboBox<?>) tf).getSelectedItem().toString();
				} else if (tf instanceof JSpinner) {
					text = ((JSpinner) tf).getValue().toString();
				} else if (tf instanceof SpinnerWithCheckbox) {
					text = ((SpinnerWithCheckbox) tf).getValue();
				} else if (tf instanceof JCheckBox) {
					if (((JCheckBox) tf).isSelected()) {
						text = "true";
					} else {
						text = "false";
					}
				}
				if (text.isEmpty()) {
					if (getArgs()[i-1].getType() == PrologArgument.NUMBER) {
						assertArgs[i] = "0";
					} else {
						assertArgs[i] = "''";
					}
				} else {
					if (tf instanceof SpinnerWithCheckbox) {
						assertArgs[i] = text;
					} else {
						assertArgs[i] = PrologUtils.quoteIfNecessary(text);
					}
				}
			}
		}
		
		return QueryUtils.bT(getFunctor(), (Object[]) assertArgs);
	}
	
	private String getSimpleGoal() {
		
		String[] retractArgs = new String[getArity()];
		retractArgs[0] = currentId;
		
		for (int i=1; i<getArity(); i++) {
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

	public boolean isElementSelected() {
		return currentId != null;
	}

	@Override
	public boolean changed() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
