package pdt.gui.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cs3.prolog.connector.Connector;
import org.cs3.prolog.connector.common.QueryUtils;
import org.cs3.prolog.connector.cterm.CCompound;
import org.cs3.prolog.connector.process.PrologProcess;
import org.cs3.prolog.connector.process.PrologProcessException;

import pdt.gui.utils.SimpleLogger;

public class PrologAdapter {

	public static final String ARGS = "Args";
	public static final String NAME = "Name";
	
	private File directory; 
	private PrologProcess process;
	private AutoCompletionProvider autoCompletionProvider;
	private String modelName;
	
	public PrologAdapter() {
		this(null);
	}
	
	public PrologAdapter(File loadFile) {
//		URL res = ClassLoader.getSystemClassLoader().getResource("logtalk");
		try {
//			directory = new File(res.toURI()); 
			directory = new File("logtalk");
			process = Connector.newPrologProcess();
			process.setAdditionalStartupFile("\"%LOGTALKHOME%\\integration\\logtalk_swi.pl\"");
			process.consult(new File(directory, "lib\\loader.lgt"));
			if (loadFile != null) {
				process.consult(loadFile);
			}
			Map<String, Object> result = process.queryOnce("db_controller::current_model(Model)");
			modelName = result.get("Model").toString();
		} catch (IOException | PrologProcessException e) {
			e.printStackTrace();
		}
		autoCompletionProvider = new AutoCompletionProvider(this);
	}

	public Map<String, Object> queryOnce(String... predicates) {
		try {
			return process.queryOnce(predicates);
		} catch (PrologProcessException e) {
			SimpleLogger.error(e);
		}
		return null;
	}
	
	public List<Map<String, Object>> queryAll(String... predicates) {
		try {
			return process.queryAll(predicates);
		} catch (PrologProcessException e) {
			SimpleLogger.error(e);
		}
		return null;
	}
	
	public Map<String, Object> queryOnceCTerms(String... predicates) {
		try {
			return process.queryOnce(PrologProcess.CTERMS, predicates);
		} catch (PrologProcessException e) {
			SimpleLogger.error(e);
		}
		return null;
	}
	
	public List<Map<String, Object>> queryAllCTerms(String... predicates) {
		try {
			return process.queryAll(PrologProcess.CTERMS, predicates);
		} catch (PrologProcessException e) {
			SimpleLogger.error(e);
		}
		return null;
	}
	
	public PrologProcess getProcess() {
		return process;
	}

	public List<String> getAllAsString(String query) {
		List<String> result = new ArrayList<String>();
		try {
			List<Map<String, Object>> queryAll = process.queryAll(query);
			for (Map<String, Object> m : queryAll) {
				result.add(m.get("Value").toString());
			}
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		return result;
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
	
	public boolean isRatingRelation(String name) {
		return isSpecificRelationType(name, "relation_rating_type");
	}
	
	public boolean isManyRelation(String name) {
		return isSpecificRelationType(name, "relation_many_type");
	}
	
	public boolean isSingleRelation(String name) {
		return isSpecificRelationType(name, "relation_single_type");
	}
	
	public boolean isSpecificRelationType(String name, String type) {
		String query = QueryUtils.bT(modelName + "::" + type, name, "_");
		try {
			return process.queryOnce(query) != null;
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		return false;
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

	public List<CCompound> getArgs(String factName) {
		return getArgs(factName, "element_simple_arg");
	}

	public List<CCompound> getDisplayArgs(String factName) {
		return getArgs(factName, "element_display_arg");
	}
	
	public List<CCompound> getArgs(String factName, String predicateName) {
		try {
			// we need this workaround since CTerms can't handle lists
			String query = QueryUtils.bT(modelName + "::" + predicateName, factName, "Arg");
			
			List<Map<String, Object>> results = process.queryAll(PrologProcess.CTERMS, query);
			List<CCompound> compoundList = new ArrayList<CCompound>(results.size());
			
			for (Map<String, Object> result : results) {
				compoundList.add((CCompound) result.get("Arg"));
			}
			return compoundList;
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getAutoCompletions(String factName) {
		String query = QueryUtils.bT(modelName + "::auto_completion", factName, "Values");
		try {
			List<String> resultList = new ArrayList<>();
			SimpleLogger.debug("get auto completions: " + query);
			Map<String, Object> result = process.queryOnce(query);
			if (result != null) {
				Object o = result.get("Values");
				if (o instanceof List<?>) {
					List<?> l = (List<?>) o;
					for (Object elem : l) {
						resultList.add(elem.toString());
					}
					return resultList;
				}
			}
		} catch (PrologProcessException e) {
			SimpleLogger.error(e);
		}
		return null;
	}

	public AutoCompletionProvider getAutoCompletionProvider() {
		return autoCompletionProvider;
	}

	public Map<String, List<CCompound>> getFilter(String functor) {
		String query = QueryUtils.bT(modelName + "::filter", functor, "Filter");
		Map<String, List<CCompound>> filters = new HashMap<>();
		try {
			List<Map<String, Object>> results = process.queryAll(PrologProcess.CTERMS, query);
			for (Map<String, Object> result : results) {
				Object o = result.get("Filter");
				if (o instanceof CCompound) {
					CCompound comp = (CCompound) o;
					String type = comp.getArgument(0).getFunctorValue();
					List<CCompound> list = filters.get(type);
					if (list == null) {
						list = new ArrayList<CCompound>();
						filters.put(type, list);
					}
					list.add(comp);
				}
			}
		} catch (PrologProcessException e) {
			SimpleLogger.error(e);
		}
		return filters;
	}

	public String getElementName(String type, String id) {
		String query = QueryUtils.bT(modelName + "::argument_value", type, id, "name", "Value");
		try {
			Map<String, Object> result = process.queryOnce(query);
			return result.get("Value").toString();
		} catch (PrologProcessException e) {
			SimpleLogger.error(e);
		}
		return null;
	}
	
}
