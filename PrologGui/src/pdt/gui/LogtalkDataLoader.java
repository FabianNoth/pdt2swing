package pdt.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.cs3.prolog.connector.cterm.CCompound;
import org.cs3.prolog.connector.process.PrologProcess;

import pdt.gui.data.BundleProvider;
import pdt.gui.data.PrologConnection;
import pdt.gui.data.PrologGuiBundle;
import pdt.gui.data.PrologTableData;
import pdt.gui.datapanels.handler.PrologDataHandler;
import pdt.gui.datapanels.handler.PrologFactHandler;
import pdt.gui.datapanels.handler.PrologRatingHandler;
import pdt.gui.datapanels.handler.PrologRelationHandler;
import pdt.prolog.elements.PrologGoal;

public class LogtalkDataLoader {
	
	private PrologConnection connection;
	private Queries queries;
	
	public LogtalkDataLoader() {
		
		File dataDir = new File("logtalk/plugin");
		File loadFile = new File(dataDir, "loader.lgt");
	
		// Open Prolog connection with load file
		connection = new PrologConnection(loadFile);

//		this.connection = connection;
//		try {
//			connection.getProcess().consult(loadFile);
//		} catch (PrologProcessException e) {
//			e.printStackTrace();
//		}

		queries = new Queries(connection.getProcess());

		new PrologGui(connection, new DefaultBundleProvider(connection, createBundles()));
	}

	private List<PrologGuiBundle> createBundles() {
		
		List<PrologGuiBundle> bundleList = new ArrayList<>();
		
		// collect all bundles from metamodel
		List<String> bundleNames = queries.getBundles();
		for (String factName : bundleNames) {
			List<PrologDataHandler<?>> handlers = new ArrayList<>();

			// get args for bundle main fact
			List<CCompound> mainArgs = queries.getArgs(factName);
			
			// create main goal and prolog handler
			PrologGoal mainGoal = new PrologGoal(factName, mainArgs, queries);
			PrologFactHandler factHandler = new PrologFactHandler(connection, "Data", true, mainGoal, queries.getDataDirectory());
			handlers.add(factHandler);
			if (queries.hasTextFile(factName)) {
				handlers.add(factHandler.createTextFileHandler("Text", null, false));
			}
			
			// get all relations
			List<String> relations = queries.getRelations(factName);
			
			for (String relation : relations) {
				List<CCompound> relationArgList = queries.getArgs(relation);
				// create goal for relation
				PrologGoal relationGoal = new PrologGoal(relation, relationArgList, queries);
				if (relation.endsWith("_rating")) {
					// if relation ends with _rating, use the PrologRatingHandler
					handlers.add(new PrologRatingHandler(connection, "Rating", relationGoal));
				} else {
					// otherwise use the PrologRelationHandler
					handlers.add(new PrologRelationHandler(connection, relation, relationGoal, true));
				}
			}
			
			PrologDataHandler<?>[] handlersArray = handlers.toArray(new PrologDataHandler<?>[handlers.size()]);

			PrologTableData tableData = new PrologTableData(connection, mainGoal);
			bundleList.add(new PrologGuiBundle(factName, tableData, handlersArray));
		}
		
		return bundleList;

	}
	
	private class DefaultBundleProvider extends BundleProvider {

		@SuppressWarnings("unused")
		private PrologProcess process;
		private List<PrologGuiBundle> bundles;
		
		
		public DefaultBundleProvider(PrologConnection connection, List<PrologGuiBundle> bundles) {
			this.process = connection.getProcess();
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
				
			}
			
			return root;
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
