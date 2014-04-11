package pdt.gui.data;

import java.io.File;
import java.io.IOException;

import org.cs3.prolog.common.QueryUtils;
import org.cs3.prolog.common.Util;
import org.cs3.prolog.pif.PrologInterface;
import org.cs3.prolog.pif.PrologInterfaceException;

public class PrologConnection {

	// FIXME: automatically find directory
	private final static File directory = new File("X:\\dev\\quetee\\pdt2swing\\PrologGui\\src\\prolog"); 
	private PrologInterface pif;
	
	public PrologConnection() {
		this(null);
	}
	
	public PrologConnection(File loadFile) {
		try {
			pif = Util.newStandalonePrologInterface();
			consultData(new File(directory, "output.pl"));
			consultData(new File(directory, "data_handling.pl"));
			if (loadFile != null) {
				consultData(loadFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void consultData(File file) {
//		System.out.println(file.getAbsolutePath());
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
