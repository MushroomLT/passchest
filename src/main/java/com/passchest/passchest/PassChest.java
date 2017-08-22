package com.passchest.passchest;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.passchest.passchest.crypto.AES.InvalidAESStreamException;
import com.passchest.passchest.crypto.AES.InvalidPasswordException;
import com.passchest.passchest.crypto.AES.StrongEncryptionNotAvailableException;
import com.passchest.passchest.store.PassStore;

public class PassChest {
	


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
		
		//PassStore.savePassStore(password);
		
//		try {
//			AES.decrypt(password.toCharArray(), new FileInputStream(PassStore.passStoreFile), new FileOutputStream(new File(PassStore.passStoreFile.getParent(), "decrypt.txt")));
//		} catch (InvalidPasswordException | InvalidAESStreamException | IOException
//				| StrongEncryptionNotAvailableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
