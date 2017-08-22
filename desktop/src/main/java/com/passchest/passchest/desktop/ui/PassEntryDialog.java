package com.passchest.passchest.desktop.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.passchest.passchest.store.PassGroup;
import com.passchest.passchest.store.PassStore;

import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class PassEntryDialog extends JDialog {
	private JTextField userField;
	private JTextField emailField;
	private JPasswordField passwordField;
	private JComboBox<String> groupComboBox;
	
	public PassEntryDialog(JFrame owner) {
		super(owner, "New Password Entry", true);
		setResizable(false);
		setSize(300, 250);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(300, 175));
		getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {100, 185};
		gbl_panel.rowHeights = new int[] {0, 30, 30, 30, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblGroup = new JLabel("Group");
		lblGroup.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblGroup = new GridBagConstraints();
		gbc_lblGroup.anchor = GridBagConstraints.WEST;
		gbc_lblGroup.insets = new Insets(0, 0, 5, 5);
		gbc_lblGroup.gridx = 0;
		gbc_lblGroup.gridy = 0;
		panel.add(lblGroup, gbc_lblGroup);
		
		groupComboBox = new JComboBox<String>();
		populateGroups();
		GridBagConstraints gbc_groupComboBox = new GridBagConstraints();
		gbc_groupComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_groupComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_groupComboBox.gridx = 1;
		gbc_groupComboBox.gridy = 0;
		panel.add(groupComboBox, gbc_groupComboBox);
		
		JLabel lblUsername = new JLabel("Username");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.WEST;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 1;
		panel.add(lblUsername, gbc_lblUsername);
		
		userField = new JTextField();
		GridBagConstraints gbc_userField = new GridBagConstraints();
		gbc_userField.anchor = GridBagConstraints.NORTH;
		gbc_userField.fill = GridBagConstraints.HORIZONTAL;
		gbc_userField.insets = new Insets(0, 0, 5, 0);
		gbc_userField.gridx = 1;
		gbc_userField.gridy = 1;
		panel.add(userField, gbc_userField);
		userField.setColumns(10);
		
		JLabel lblEmail = new JLabel("Email");
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.WEST;
		gbc_lblEmail.insets = new Insets(0, 0, 5, 5);
		gbc_lblEmail.gridx = 0;
		gbc_lblEmail.gridy = 2;
		panel.add(lblEmail, gbc_lblEmail);
		
		emailField = new JTextField();
		GridBagConstraints gbc_emailField = new GridBagConstraints();
		gbc_emailField.anchor = GridBagConstraints.NORTH;
		gbc_emailField.fill = GridBagConstraints.HORIZONTAL;
		gbc_emailField.insets = new Insets(0, 0, 5, 0);
		gbc_emailField.gridx = 1;
		gbc_emailField.gridy = 2;
		panel.add(emailField, gbc_emailField);
		emailField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.WEST;
		gbc_lblPassword.insets = new Insets(0, 0, 0, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 3;
		panel.add(lblPassword, gbc_lblPassword);
		
		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.anchor = GridBagConstraints.NORTH;
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 3;
		panel.add(passwordField, gbc_passwordField);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((PassChestFrame) PassEntryDialog.this.getParent()).addPassword(groupComboBox.getSelectedItem().toString(), userField.getText(), emailField.getText(), new String(passwordField.getPassword()));
				PassEntryDialog.this.dispose();
			}
		});
		getContentPane().add(btnCreate);
		
	}

	private void populateGroups() {
		String[] groups = new String[PassStore.instance.passwords.size()];
		for(int i = 0; i < groups.length; i++) {
			groups[i] = PassStore.instance.passwords.get(i).groupName;
		}
		groupComboBox.setModel(new DefaultComboBoxModel<String>(groups));
	}
}
