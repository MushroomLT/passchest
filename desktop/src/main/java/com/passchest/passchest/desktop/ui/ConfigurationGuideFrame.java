package com.passchest.passchest.desktop.ui;

import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;

import com.passchest.passchest.DriveHelper;
import com.passchest.passchest.crypto.AES.InvalidAESStreamException;
import com.passchest.passchest.crypto.AES.InvalidPasswordException;
import com.passchest.passchest.crypto.AES.StrongEncryptionNotAvailableException;
import com.passchest.passchest.store.PassStore;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class ConfigurationGuideFrame extends JFrame {
	
	private Font titleFont = new Font("Yu Gothic Light", Font.PLAIN, 37);
	private Font subtitleFont = new Font("Yu Gothic Light", Font.PLAIN, 18);
	private Font smallFont = new Font("Yu Gothic UI", Font.PLAIN, 14);
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JPasswordField passwordField_2;
	
	public ConfigurationGuideFrame() {
		super("PassChest");
		setSize(600, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final CardLayout layout = new CardLayout(0, 0);
		getContentPane().setLayout(layout);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, "name_961316260834029");
		panel.setLayout(null);
		
		JLabel lblWelcomeToPasschest = new JLabel("Welcome to PassChest");
		lblWelcomeToPasschest.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcomeToPasschest.setFont(titleFont);
		lblWelcomeToPasschest.setBounds(92, 280, 392, 79);
		panel.add(lblWelcomeToPasschest);
		
		JButton btnStart = new JButton("Start");
		btnStart.setFont(smallFont);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				layout.next(getContentPane());
			}
		});
		btnStart.setBounds(228, 370, 108, 36);
		panel.add(btnStart);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, "name_961321262601007");
		panel_1.setLayout(null);
		
		JLabel lblLoginToGoogle = new JLabel("Login to Google");
		lblLoginToGoogle.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginToGoogle.setFont(titleFont);
		lblLoginToGoogle.setBounds(92, 280, 392, 79);
		panel_1.add(lblLoginToGoogle);
		
		JLabel lblYourPasswordsWill = new JLabel("Your passwords will be stored encrypted in Google Drive");
		lblYourPasswordsWill.setFont(subtitleFont);
		lblYourPasswordsWill.setHorizontalAlignment(SwingConstants.CENTER);
		lblYourPasswordsWill.setBounds(48, 348, 474, 43);
		panel_1.add(lblYourPasswordsWill);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setFont(smallFont);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DriveHelper.authorize();
					try {
						if(!PassStore.loadPassStore()) {
							PassStore.createEmptyPassStore();
							layout.next(getContentPane());
							ConfigurationGuideFrame.this.toFront();
						} else {
							layout.last(getContentPane());
							ConfigurationGuideFrame.this.toFront();
						}
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Unable to read pass store!");
						System.exit(0);
					}
				} catch (IOException e1) {
					ConfigurationGuideFrame.this.toFront();
					if(JOptionPane.showConfirmDialog(ConfigurationGuideFrame.this, "Permission for Google Drive access wasn't granted, continue in offline mode?"
							, "Authentication Failure", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						layout.next(getContentPane());
					}
				}
			}
		});
		btnLogin.setBounds(235, 402, 94, 36);
		panel_1.add(btnLogin);
		
		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, "name_963321941175804");
		panel_2.setLayout(null);
		
		JLabel lblChooseAMaster = new JLabel("Choose a Master Password");
		lblChooseAMaster.setBounds(36, 192, 506, 76);
		panel_2.add(lblChooseAMaster);
		lblChooseAMaster.setHorizontalAlignment(SwingConstants.CENTER);
		lblChooseAMaster.setFont(titleFont);
		
		JLabel lblMakeSureIts = new JLabel("<html><center>Make sure it's secure and remember it,<br> it'll be used to encrypt all your passwords.</center></html>");
		lblMakeSureIts.setHorizontalAlignment(SwingConstants.CENTER);
		lblMakeSureIts.setBounds(58, 235, 466, 95);
		panel_2.add(lblMakeSureIts);
		lblMakeSureIts.setFont(subtitleFont);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(68, 341, 164, 31);
		panel_2.add(passwordField);
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(new String(passwordField.getPassword()).equals(new String(passwordField_1.getPassword()))) {
					try {
						PassStore.decryptPassStore(passwordField.getPassword());
						dispose();
						new PassChestFrame().setVisible(true);
					} catch (InvalidPasswordException e1) {
						JOptionPane.showMessageDialog(null, "Incorrect password!");
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Unable to read pass store!");
					} catch (InvalidAESStreamException e1) {
						JOptionPane.showMessageDialog(null, "Invalid pass store file!");
					} catch (StrongEncryptionNotAvailableException e1) {
						JOptionPane.showMessageDialog(null, "Decryption not supported on this device!");
					}
				} else {
					JOptionPane.showMessageDialog(ConfigurationGuideFrame.this, "Passwords do not match!");
				}
			}
		});
		btnFinish.setFont(smallFont);
		btnFinish.setBounds(221, 399, 128, 39);
		panel_2.add(btnFinish);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(334, 341, 164, 31);
		panel_2.add(passwordField_1);
		
		JLabel lblRepeat = new JLabel("Repeat");
		lblRepeat.setHorizontalAlignment(SwingConstants.CENTER);
		lblRepeat.setFont(smallFont);
		lblRepeat.setBounds(242, 341, 82, 31);
		panel_2.add(lblRepeat);
		
		JPanel panel_3 = new JPanel();
		getContentPane().add(panel_3, "name_965157969845216");
		panel_3.setLayout(null);
		
		JLabel lblEnterYourMaster = new JLabel("Enter Your Master Password");
		lblEnterYourMaster.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnterYourMaster.setFont(new Font("Yu Gothic Light", Font.PLAIN, 37));
		lblEnterYourMaster.setBounds(34, 209, 506, 76);
		panel_3.add(lblEnterYourMaster);
		
		JLabel lblItAppearsThat = new JLabel("It appears that you already use PassChest");
		lblItAppearsThat.setHorizontalAlignment(SwingConstants.CENTER);
		lblItAppearsThat.setFont(new Font("Yu Gothic Light", Font.PLAIN, 18));
		lblItAppearsThat.setBounds(34, 269, 506, 43);
		panel_3.add(lblItAppearsThat);
		
		passwordField_2 = new JPasswordField();
		passwordField_2.setBounds(203, 323, 164, 31);
		panel_3.add(passwordField_2);
		
		JButton button = new JButton("Finish");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PassStore.decryptPassStore(passwordField_2.getPassword());
					dispose();
					new PassChestFrame().setVisible(true);
				} catch (InvalidPasswordException e1) {
					JOptionPane.showMessageDialog(null, "Incorrect password!");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Unable to read pass store!");
				} catch (InvalidAESStreamException e1) {
					JOptionPane.showMessageDialog(null, "Invalid pass store file!");
				} catch (StrongEncryptionNotAvailableException e1) {
					JOptionPane.showMessageDialog(null, "Decryption not supported on this device!");
				}
			}
		});
		button.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		button.setBounds(220, 379, 128, 39);
		panel_3.add(button);
	}
}
