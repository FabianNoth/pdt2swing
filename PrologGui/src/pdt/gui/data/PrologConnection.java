package pdt.gui.data;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cs3.prolog.connector.Connector;
import org.cs3.prolog.connector.process.PrologProcess;
import org.cs3.prolog.connector.process.PrologProcessException;

public class PrologConnection {

	private File directory; 
	private PrologProcess process;
	
	public PrologConnection() {
		this(null);
	}
	
	public PrologConnection(File loadFile) {
		URL res = ClassLoader.getSystemClassLoader().getResource("prolog");
		try {
			directory = new File(res.toURI()); 
			process = Connector.newPrologProcess();
			process.setAdditionalStartupFile("\"%LOGTALKHOME%\\integration\\logtalk_swi.pl\"");
//			process.start();
//			process.setAdditionalStartupFile(System.getenv("LOGTALKHOME") + "/integration/logtalk_swi.pl");
			process.consult(new File(directory, "gui_hooks.pl"));
//			consultData(new File(directory, "gui_hooks.pl"));
			if (loadFile != null) {
				process.consult(loadFile);
//				consultData(loadFile);
			}
		} catch (IOException | URISyntaxException | PrologProcessException e) {
			e.printStackTrace();
		}
	}
	
//	public void consultData(File file) {
//		try {
//			process.consult(file);
//		} catch (PrologProcessException e) {
//			e.printStackTrace();
//		}
//	}
	
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
	
}
