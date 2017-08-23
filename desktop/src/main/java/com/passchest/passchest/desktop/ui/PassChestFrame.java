package com.passchest.passchest.desktop.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.passchest.passchest.crypto.AES.InvalidKeyLengthException;
import com.passchest.passchest.crypto.AES.StrongEncryptionNotAvailableException;
import com.passchest.passchest.desktop.PassChestDesktop;
import com.passchest.passchest.store.PassEntry;
import com.passchest.passchest.store.PassGroup;
import com.passchest.passchest.store.PassStore;

@SuppressWarnings("serial")
public class PassChestFrame extends JFrame {
	
	private DefaultTreeModel treeModel;
	private JTree tree;
	private Map<String, DefaultMutableTreeNode> groupNodes;
	
	public PassChestFrame() {
		super("PassChest");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(660, 440);
		setLocationRelativeTo(null);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{650, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0};
		getContentPane().setLayout(gridBagLayout);
		
		tree = new JTree();
		treeModel = new DefaultTreeModel(populateTree(new DefaultMutableTreeNode("Passwords")));
		tree.setModel(treeModel);
		tree.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        int selRow = tree.getRowForLocation(e.getX(), e.getY());
		        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		        if(selRow != -1) {
		            if(e.getClickCount() == 2) {
		            	Object selectedNode = ((DefaultMutableTreeNode)selPath.getLastPathComponent()).getUserObject();
		            	if(selectedNode instanceof PassEntry) {
		            		String[] options = new String[]{"Save To Clipboard", "Display", "Edit"};
		            		int option = JOptionPane.showOptionDialog(PassChestFrame.this, "What would you like to do?", "PassChest",
		            		                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
		            		                         null, options, options[0]);
		            		switch(option) {
		            		case 0:
		            			StringSelection selection = new StringSelection(((PassEntry)selectedNode).password);
		            			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		            			clipboard.setContents(selection, selection);
		            			break;
		            		case 1:
		            			JOptionPane.showMessageDialog(PassChestFrame.this, "Password: " + ((PassEntry)selectedNode).password);
		            			break;
		            		case 2:
		        				JDialog dialog = new PassEntryDialog(PassChestFrame.this, ((PassEntry)selectedNode).username, ((PassEntry)selectedNode).email, ((PassEntry)selectedNode).password);
		        				dialog.setLocationRelativeTo(PassChestFrame.this);
		        				dialog.setVisible(true);
		            			break;
		            		}
		            	}
		            }
		        }
		    }
		});
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
				dialog.setLocationRelativeTo(PassChestFrame.this);
				dialog.setVisible(true);
			}
		});
		panel.add(btnAddPassword);
		
		JButton btnAddGroup = new JButton("Add Group");
		btnAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String groupName = JOptionPane.showInputDialog(PassChestFrame.this, "Enter new group name:");
				if(!groupName.equals("")) {
					PassStore.instance.passwords.add(new PassGroup(groupName, new ArrayList<String>(), new ArrayList<PassEntry>()));
					DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupName);
					groupNodes.put(groupName, groupNode);
					treeModel.insertNodeInto(groupNode, (MutableTreeNode) treeModel.getRoot(), ((DefaultMutableTreeNode) treeModel.getRoot()).getChildCount());
				}
			}
		});
		panel.add(btnAddGroup);
		
		JButton btnSavePasswords = new JButton("Save Passwords");
		btnSavePasswords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					PassStore.savePassStore();
					JOptionPane.showMessageDialog(PassChestFrame.this, "Pass store saved successfully!");
				} catch (InvalidKeyLengthException e) {
					JOptionPane.showMessageDialog(PassChestFrame.this, "Unable to write to pass store!");
				} catch (StrongEncryptionNotAvailableException e) {
					JOptionPane.showMessageDialog(PassChestFrame.this, "Encryption not supported on this device!");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(PassChestFrame.this, "Unable to write to pass store!");
				}
			}
		});
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if(node != null) {
					if(node.getLevel() == 1) {
						PassGroup removeGroup = null;
						for(PassGroup group : PassStore.instance.passwords) {
							if(group.groupName.equals(((DefaultMutableTreeNode)node).getUserObject())) {
								if(JOptionPane.showConfirmDialog(PassChestFrame.this, "Are you sure you want to remove group " + group.groupName + " with all it's entries?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
									removeGroup = group;
									break;
								}
							}
						}
						if(removeGroup != null) {
							PassStore.instance.passwords.remove(removeGroup);
							treeModel.removeNodeFromParent(node);
						}
					} else if(node.getLevel() == 2) {
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
		panel.add(btnRemove);
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
