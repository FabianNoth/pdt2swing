package pdt.prolog.elements;

import org.cs3.prolog.connector.common.Util;
import org.cs3.prolog.connector.cterm.CCompound;
import org.cs3.prolog.connector.cterm.CTerm;

public class PrologTransactionResult {
	
	private static final String SUCCESS = "success";
	private static final String WARNING = "warning";
	private static final String ERROR = "error";
	private CTerm result;
	private CCompound compound;
	
	public PrologTransactionResult(Object o) {
		if (o instanceof CTerm) {
			result = (CTerm) o;
		}
		if (o instanceof CCompound) {
			compound = (CCompound) o;
		}
	}
	
	public boolean isSuccess() {
		return result.getFunctorValue().equals(SUCCESS);
	}

	public boolean isError() {
		return result.getFunctorValue().equals(ERROR);
	}
	
	public boolean isWarning() {
		return result.getFunctorValue().equals(WARNING);
	}
	
	public String getId() {
		if (isSuccess()) {
			if (compound != null && compound.getArity() > 0) {
				return compound.getArgument(0).getFunctorImage();
			}
		}
		return null;
	}

	public String getDialogMessage() {
		if (isError() || isWarning()) {
			StringBuilder message = new StringBuilder();
			if (compound == null) {
				message.append("Undefinierter Fehler");
			} else {
				for (int i=0; i<compound.getArity(); i++) {
					message.append(compound.getArgument(i));
				}
			}
			return Util.unquoteAtom(message.toString());
		} else {
			return null;
		}
	}
	
	public CTerm getResult() {
		return result;
	}
	
}
