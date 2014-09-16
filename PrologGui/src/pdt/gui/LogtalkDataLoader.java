package pdt.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cs3.prolog.connector.common.Util;
import org.cs3.prolog.connector.cterm.CCompound;

import pdt.gui.data.BundleProvider;
import pdt.gui.data.PrologAdapter;
import pdt.gui.data.PrologGuiBundle;
import pdt.gui.data.PrologTableData;
import pdt.gui.datapanels.handler.PrologDataHandler;
import pdt.gui.datapanels.handler.PrologFactHandler;
import pdt.gui.datapanels.handler.PrologRatingHandler;
import pdt.gui.datapanels.handler.PrologRelationHandler;
import pdt.prolog.elements.PrologArgument;
import pdt.prolog.elements.PrologDisplayGoal;
import pdt.prolog.elements.PrologGoal;
import pdt.prolog.elements.PrologReferenceType;

public class LogtalkDataLoader {
	
	private PrologAdapter prolog;
	
	public LogtalkDataLoader() {
		
		File pluginDir = new File("logtalk/plugin");
		File loadFile = new File(pluginDir, "loader.lgt");
	
		// Open Prolog connection with load file
		prolog = new PrologAdapter(loadFile);

//		this.connection = connection;
//		try {
//			connection.getProcess().consult(loadFile);
//		} catch (PrologProcessException e) {
//			e.printStackTrace();
//		}

		new PrologGui(prolog, new DefaultBundleProvider(prolog, createBundles()));
	}

	private List<PrologGuiBundle> createBundles() {
		
		List<PrologGuiBundle> bundleList = new ArrayList<>();
		
		// collect all bundles from metamodel
		List<String> bundleNames = prolog.getBundles();
		for (String factName : bundleNames) {
			List<PrologDataHandler<?>> handlers = new ArrayList<>();

			// get args for bundle main fact
			List<CCompound> mainArgs = prolog.getArgs(factName);
			List<CCompound> displayArgs = prolog.getDisplayArgs(factName);
			
			// create main goal and prolog handler
			PrologGoal mainGoal = new PrologGoal(factName, mainArgs, prolog);
			PrologGoal displayGoal = new PrologDisplayGoal(factName, displayArgs, prolog);
			PrologFactHandler factHandler = new PrologFactHandler(prolog, "Data", true, mainGoal, prolog.getDataDirectory());
			handlers.add(factHandler);
			if (prolog.hasTextFile(factName)) {
				handlers.add(factHandler.createTextFileHandler("Text", null, false));
			}
			
			// get all relations
			List<String> relations = prolog.getRelations(factName);
			
			for (String relation : relations) {
				List<CCompound> relationArgList = prolog.getArgs(relation);
				// create goal for relation
				PrologGoal relationGoal = new PrologGoal(relation, relationArgList, prolog);
				
				if (prolog.isRatingRelation(relation)) {
					handlers.add(new PrologRatingHandler(prolog, "Rating", relationGoal));
				} else if (prolog.isManyRelation(relation)) {
					handlers.add(new PrologRelationHandler(prolog, relation, relationGoal));
				}  else if (prolog.isSingleRelation(relation)) {
					handlers.add(new PrologFactHandler(prolog, relation, false, relationGoal, null));
				} 
			}
			
			PrologDataHandler<?>[] handlersArray = handlers.toArray(new PrologDataHandler<?>[handlers.size()]);

			PrologTableData tableData = new PrologTableData(prolog, displayGoal);
			bundleList.add(new PrologGuiBundle(factName, tableData, handlersArray));
		}
		
		return bundleList;

	}
	
	private class DefaultBundleProvider extends BundleProvider {

		private PrologAdapter prolog;
		private List<PrologGuiBundle> bundles;
		
		
		public DefaultBundleProvider(PrologAdapter prolog, List<PrologGuiBundle> bundles) {
			this.prolog = prolog;
			this.bundles = bundles;
			for(PrologGuiBundle b : bundles) {
				addListener(b);
			}
		}

		@Override
		public QueryNode createRoot() {
			
			QueryNode root = new QueryNode("Root", bundles.get(0));
			
			for(PrologGuiBundle b : bundles) {
				QueryNode node = new QueryNode(b.getName(), b);
				root.add(node);
				createFilters(node, b);
			}
			
			return root;
		}

		private void createFilters(QueryNode node, PrologGuiBundle b) {
			Map<String, List<CCompound>> filters = prolog.getFilter(node.getGoal().getFunctor());
			for(String typeName : filters.keySet()) {
				QueryNode typeNode = new QueryNode(typeName, b, node.getGoal());
				node.add(typeNode);
				
				for (CCompound filter : filters.get(typeName)) {
					PrologDisplayGoal goal = new PrologDisplayGoal(node.getGoal(), filter.toString());
					
					PrologArgument argument = goal.getArgument(typeName);
					String filterName = filter.getArgument(1).getFunctorImage();
					
					if (argument instanceof PrologReferenceType) {
						filterName = prolog.getElementName(((PrologReferenceType) argument).getRefType(), Util.unquoteAtom(filterName));
					}
					filterName = Util.unquoteAtom(filterName);
					if (filterName.isEmpty()) {
						filterName = "(empty)";
					}
					if (goal.isTrue(prolog)) {
						typeNode.add(new QueryNode(filterName, b, goal));
					}
				}
				
			}
		}

		@Override
		public PrologGuiBundle getDefault() {
			return bundles.get(0);
		}
		
	}
	
	public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
            	new LogtalkDataLoader();
            }
        });
    }
	

}
