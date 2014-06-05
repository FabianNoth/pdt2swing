package pdt.gui.data;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.cs3.prolog.common.QueryUtils;
import org.cs3.prolog.common.Util;
import org.cs3.prolog.pif.PrologInterface;
import org.cs3.prolog.pif.PrologInterfaceException;

public class PrologConnection {

	private File directory; 
	private PrologInterface pif;
	
	public PrologConnection() {
		this(null);
	}
	
	public PrologConnection(File loadFile) {
		URL res = ClassLoader.getSystemClassLoader().getResource("prolog");
		try {
			directory = new File(res.toURI()); 
			pif = Util.newStandalonePrologInterface();
			consultData(new File(directory, "gui_hooks.pl"));
			if (loadFile != null) {
				consultData(loadFile);
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void consultData(File file) {
		String prologFileName = Util.prologFileName(file);
		consultData(prologFileName);
	}
	
	private void consultData(String prologFileName) {
		try {
			String consultQuery = QueryUtils.buildTerm("reconsult", Util.quoteAtomIfNeeded(prologFileName));
			pif.queryOnce(consultQuery);
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
	}

	public PrologInterface getPif() {
		return pif;
	}
	
}
