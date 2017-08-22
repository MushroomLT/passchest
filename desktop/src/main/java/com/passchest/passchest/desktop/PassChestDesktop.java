package com.passchest.passchest.desktop;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.passchest.passchest.crypto.AES.InvalidAESStreamException;
import com.passchest.passchest.crypto.AES.InvalidPasswordException;
import com.passchest.passchest.crypto.AES.StrongEncryptionNotAvailableException;
import com.passchest.passchest.store.PassStore;

public class PassChestDesktop {
	public static void main(String[] args) {
		String password = JOptionPane.showInputDialog("Enter Master Password:");
		try {
			if(!PassStore.loadPassStore(password)) {
				PassStore.createEmptyPassStore();
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
	}
}
