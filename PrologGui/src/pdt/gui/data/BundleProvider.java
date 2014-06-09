package pdt.gui.data;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.apache.commons.io.FileUtils;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

import pdt.gui.QueryNode;
import pdt.gui.datapanels.handler.PrologDataHandler;

public abstract class BundleProvider {

	private final Set<IdListener> listeners = new HashSet<IdListener>();
	private BasicTextEncryptor textEncryptor;
	
	/**
	 * Create the complete node hierarchy and return the root node
	 * @return the root element for all possible queries
	 */
	public abstract QueryNode createRoot();
	public abstract PrologGuiBundle getDefault();
	
	public void addListener(PrologGuiBundle bundle) {
		for (PrologDataHandler<?> f : bundle.getFactHandlers()) {
			addListener(f);
		}
	}
	
	public void addListener(IdListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IdListener listener) {
		listeners.remove(listener);
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	public void persistFacts() {
		for(IdListener l : listeners) {
			l.persistFacts();
		}
	}
	
	public void initTextEncryptor() throws IOException {
		textEncryptor = new BasicTextEncryptor();
//		textEncryptor.setPassword("test123");
		
		// load pw from file
		String encryptedPassword = FileUtils.readFileToString(new File("pwfile"));
		StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

		// show input dialog
		String input = showPasswordDialog();
		if (input == null) {
//			System.out.println("nothing");
			throw new IOException("Password is null");
		} else {
			// compare passwords
			if (passwordEncryptor.checkPassword(input, encryptedPassword)) {
				textEncryptor.setPassword(input);
//				JOptionPane.showMessageDialog(null, "Korrektes Passwort", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
//				initGui();
			} else {
				JOptionPane.showMessageDialog(null, "Falsches Passwort", "Error", JOptionPane.ERROR_MESSAGE);
				throw new IOException("Wrong Password");
			}
		}
	}

	public BasicTextEncryptor getTextEncryptor() {
		return textEncryptor;
	}

	private String showPasswordDialog() {
		JPasswordField passwordField = new JPasswordField(10);
		passwordField.setEchoChar('*');
		JOptionPane.showMessageDialog(
				null,
				passwordField,
				"Passwort eingeben:",
				JOptionPane.OK_OPTION);
		return new String(passwordField.getPassword());
	}
	
	public static void createPassword(String pw) {
		StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
		String encryptedPassword = passwordEncryptor.encryptPassword(pw);
		try {
			FileUtils.write(new File("pwfile"), encryptedPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
