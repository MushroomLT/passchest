package com.passchest.passchest.desktop;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.passchest.passchest.crypto.AES.InvalidAESStreamException;
import com.passchest.passchest.crypto.AES.InvalidPasswordException;
import com.passchest.passchest.crypto.AES.StrongEncryptionNotAvailableException;
import com.passchest.passchest.desktop.ui.ConfigurationGuideFrame;
import com.passchest.passchest.desktop.ui.PassChestFrame;
import com.passchest.passchest.store.PassStore;

public class PassChestDesktop {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
		}
		
		if(!PassStore.passStoreFile.exists()) {
			new ConfigurationGuideFrame().setVisible(true);
			return;
		}
		
		try {
			if(!PassStore.loadPassStore()) {
				PassStore.createEmptyPassStore();
			} else {
				PassStore.decryptPassStore(displayPasswordInputDialog(null));
			}
		} catch (InvalidPasswordException e) {
			JOptionPane.showMessageDialog(null, "Incorrect password!");
			System.exit(0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to read pass store!");
			System.exit(0);
		} catch (InvalidAESStreamException e) {
			JOptionPane.showMessageDialog(null, "Invalid pass store file!");
			System.exit(0);
		} catch (StrongEncryptionNotAvailableException e) {
			JOptionPane.showMessageDialog(null, "Decryption not supported on this device!");
			System.exit(0);
		}
		
		new PassChestFrame().setVisible(true);
	}
	
	public static char[] displayPasswordInputDialog(JFrame parent) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Enter master password:");
		JPasswordField pass = new JPasswordField(10);
		panel.add(label);
		panel.add(pass);
		String[] options = new String[]{"OK", "Cancel"};
		int option = JOptionPane.showOptionDialog(parent, panel, "PassChest",
		                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
		                         null, options, options[0]);
		if(option == 0) {
		    return pass.getPassword();
		}
		return new char[0];
	}
}
