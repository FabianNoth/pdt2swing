package pdt.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cs3.prolog.connector.common.QueryUtils;
import org.cs3.prolog.connector.cterm.CCompound;
import org.cs3.prolog.connector.process.PrologProcess;
import org.cs3.prolog.connector.process.PrologProcessException;

public class Queries {

	public static final String ARGS = "Args";
	public static final String NAME = "Name";
	
	private PrologProcess process;
	private String modelName;

	public Queries(PrologProcess process) {
		this.process = process;
		try {
			Map<String, Object> result = process.queryOnce("db_controller::current_model(Model)");
			modelName = result.get("Model").toString();
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
	}
	
	public String getModelName() {
		return modelName;
	}

	public List<Map<String,Object>> getFactTypes() {
		try {
			String query = QueryUtils.bT(modelName + "::fact_type", NAME, ARGS);
			return process.queryAll(query);
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] fixedAtomValues(String name) {
		String query = QueryUtils.bT(modelName + "::fixed_atom", name, "AtomList");
		try {
			Map<String, Object> result = process.queryOnce(query);
			List<?> atomList = (List<?>) result.get("AtomList");
			String[] atomArray = new String[atomList.size()];
			for (int i=0; i<atomList.size(); i++) {
				atomArray[i] = atomList.get(i).toString();
			}
			return atomArray;
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getBundles() {
		String query = QueryUtils.bT(modelName + "::bundle", "FactName", "_");
		try {
			List<Map<String, Object>> results = process.queryAll(query);
			List<String> bundles = new ArrayList<String>();
			for (int i = 0; i < results.size(); i++) {
				bundles.add(results.get(i).get("FactName").toString());
			}
			return bundles;
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<String> getRelations(String factName) {
		String query = QueryUtils.bT(modelName + "::bundle", factName, "RelationList");
		try {
			List<?> result = (List<?>) process.queryOnce(query).get("RelationList");
			List<String> relations = new ArrayList<String>();
			for (int i = 0; i < result.size(); i++) {
				relations.add(result.get(i).toString());
			}
			return relations;
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	public List<CCompound> getArgs(String factName) {
		try {
			String query = QueryUtils.bT(modelName + "::element", factName, ARGS);
			List<?> list = (List<?>) process.queryOnce(PrologProcess.CTERMS, query).get(ARGS);
			List<CCompound> compoundList = new ArrayList<CCompound>();
			for (int i=0; i<list.size(); i++) {
				compoundList.add((CCompound) list.get(i));
			}
			return compoundList;
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean hasTextFile(String factName) {
		String query = QueryUtils.bT(modelName + "::text_file", factName);
		try {
			Map<String, Object> result = process.queryOnce(query);
			return result != null;
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		return false;
	}

	public File getDataDirectory() {
		String query = QueryUtils.bT("logtalk_library_path", "data_directory", "FilePath");
		try {
			Map<String, Object> result = process.queryOnce(query);
			return new File((String) result.get("FilePath"));
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
