package pdt.gui.data;

public interface IdListener {

	/**
	 * Set new id and update data
	 * @param id
	 */
	public void setId(String id);
	
	/**
	 * Write facts to output file
	 */
	public void persistFacts();
	
	/**
	 * Check if something changed before overwrite the data
	 * @return true if something changed, false otherwise
	 */
	public boolean changed();
	
}
