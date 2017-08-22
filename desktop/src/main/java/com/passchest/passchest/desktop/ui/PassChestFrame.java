package com.passchest.passchest.desktop.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.passchest.passchest.crypto.AES.InvalidKeyLengthException;
import com.passchest.passchest.crypto.AES.StrongEncryptionNotAvailableException;
import com.passchest.passchest.desktop.PassChestDesktop;
import com.passchest.passchest.store.PassEntry;
import com.passchest.passchest.store.PassGroup;
import com.passchest.passchest.store.PassStore;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class PassChestFrame extends JFrame {
	
	private DefaultTreeModel treeModel;
	private JTree tree;
	private Map<String, DefaultMutableTreeNode> groupNodes;
	
	public PassChestFrame() {
		super("PassChest");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(660, 440);
		
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
		}
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{650, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0};
		getContentPane().setLayout(gridBagLayout);
		
		tree = new JTree();
		treeModel = new DefaultTreeModel(populateTree(new DefaultMutableTreeNode("Passwords")));
		tree.setModel(treeModel);
		GridBagConstraints gbc_tree = new GridBagConstraints();
		gbc_tree.insets = new Insets(0, 0, 5, 0);
		gbc_tree.fill = GridBagConstraints.BOTH;
		gbc_tree.gridx = 0;
		gbc_tree.gridy = 0;
		getContentPane().add(tree, gbc_tree);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		getContentPane().add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JButton btnAddPassword = new JButton("Add Password");
		btnAddPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new PassEntryDialog(PassChestFrame.this);
				dialog.setVisible(true);
			}
		});
		panel.add(btnAddPassword);
		
		JButton btnRemovePassword = new JButton("Remove Password");
		btnRemovePassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if(node != null) {
					if(node.getLevel() == 2) {
						for(PassGroup group : PassStore.instance.passwords) {
							if(group.groupName.equals(((DefaultMutableTreeNode)node.getParent()).getUserObject())) {
								if(JOptionPane.showConfirmDialog(PassChestFrame.this, "Are you sure you want to remove entry " + node.getUserObject() + " from group " + group.groupName + "?") == JOptionPane.OK_OPTION) {
									group.groupEntries.remove(node.getUserObject());
									treeModel.removeNodeFromParent(node);
								} else {
									return;
								}
							}
						}
					}
				}
			}
		});
		panel.add(btnRemovePassword);
		
		JButton btnAddGroup = new JButton("Add Group");
		panel.add(btnAddGroup);
		
		JButton btnRemoveGroup = new JButton("Remove Group");
		panel.add(btnRemoveGroup);
		
		JButton btnSavePasswords = new JButton("Save Passwords");
		btnSavePasswords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					PassStore.savePassStore(PassChestDesktop.displayPasswordInputDialog(PassChestFrame.this));
					JOptionPane.showMessageDialog(PassChestFrame.this, "Pass store saved successfully!");
				} catch (InvalidKeyLengthException e) {
					JOptionPane.showMessageDialog(PassChestFrame.this, "Unable to write to pass store!");
				} catch (StrongEncryptionNotAvailableException e) {
					JOptionPane.showMessageDialog(PassChestFrame.this, "Decryption not supported on this device!");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(PassChestFrame.this, "Unable to write to pass store!");
				}
			}
		});
		panel.add(btnSavePasswords);
	}

	private TreeNode populateTree(DefaultMutableTreeNode root) {
		groupNodes = new HashMap<>();
		for(PassGroup group : PassStore.instance.passwords) {
			DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group.groupName);
			groupNodes.put(group.groupName, groupNode);
			for(PassEntry entry : group.groupEntries) {
				groupNode.add(new DefaultMutableTreeNode(entry));
			}
			root.add(groupNode);
		}
		return root;
	}

	public void addPassword(String group, String username, String email, String password) {
		treeModel.insertNodeInto(new DefaultMutableTreeNode(PassStore.instance.putPassword(group, username, email, password)), groupNodes.get(group), groupNodes.get(group).getChildCount());
	}
	
}
