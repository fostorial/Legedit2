package legedit2.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import legedit2.definitions.Icon;
import legedit2.definitions.Icon.ICON_TYPE;
import legedit2.gui.LegeditFrame;
import legedit2.gui.config.IconManager;
import legedit2.helpers.LegeditHelper;

public class ManageIconPanel extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = -3477281344351970710L;
	
	private JScrollPane scroll = new JScrollPane();
	private JPanel panel = new JPanel();
	
	private boolean addMode = false;
	
	private Icon selectedItem;
	
	private JLabel iconTypeDropDownLabel;
	private JComboBox<ICON_TYPE> iconTypeDropDown;
	
	private JLabel fileNameLabel;
	private JButton fileBrowse;
	private File iconFile;
	
	private JLabel tagNameLabel;
	private JTextField tagNameField;
	
	private JLabel drawUnderlayLabel;
	private JCheckBox drawUnderlayField;
	
	private JButton addButton;
	private JButton saveButton;
	
	private IconManager iconManager;

	public ManageIconPanel() {
		
		setLayout(new BorderLayout(0, 0));
		
		scroll.setBorder(null);
		this.add(scroll);
		
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		scroll.setViewportView(panel);
		
		//panel.add(new JLabel("Label 1"));
		//panel.add(new JLabel("Label 2"));
		
		JLabel padLabel = new JLabel("");
		panel.add(padLabel);
		layout.putConstraint(SpringLayout.EAST, padLabel, -15, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, padLabel, 5, SpringLayout.NORTH, panel);
		
		iconTypeDropDownLabel = new JLabel("Icon Type");
		iconTypeDropDownLabel.setBounds(0, 0, 50, 15);
		panel.add(iconTypeDropDownLabel);
		layout.putConstraint(SpringLayout.WEST, iconTypeDropDownLabel, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, iconTypeDropDownLabel, 5, SpringLayout.NORTH, panel);
		
		List<ICON_TYPE> iconTypes = new ArrayList<>();
		for (ICON_TYPE it : ICON_TYPE.values())
		{
			if (!it.equals(ICON_TYPE.NONE))
			{
				iconTypes.add(it);
			}
		}
		iconTypeDropDown = new JComboBox<>(iconTypes.toArray(new ICON_TYPE[0]));
		iconTypeDropDown.addItemListener(this);
		panel.add(iconTypeDropDown);
		layout.putConstraint(SpringLayout.EAST, iconTypeDropDown, 5, SpringLayout.EAST, padLabel);
		layout.putConstraint(SpringLayout.WEST, iconTypeDropDown, 5, SpringLayout.EAST, iconTypeDropDownLabel);
		layout.putConstraint(SpringLayout.NORTH, iconTypeDropDown, 5, SpringLayout.NORTH, panel);
		
		Component lastItem = iconTypeDropDown;
		
		fileNameLabel = new JLabel("File: ");
		panel.add(fileNameLabel);
		layout.putConstraint(SpringLayout.WEST, fileNameLabel, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, fileNameLabel, 5, SpringLayout.SOUTH, lastItem);
		
		fileBrowse = new JButton("Browse");
		fileBrowse.addActionListener(this);
		panel.add(fileBrowse);
		layout.putConstraint(SpringLayout.EAST, fileBrowse, 5, SpringLayout.EAST, padLabel);
		layout.putConstraint(SpringLayout.WEST, fileBrowse, 5, SpringLayout.EAST, fileNameLabel);
		layout.putConstraint(SpringLayout.NORTH, fileBrowse, 5, SpringLayout.SOUTH, lastItem);
		
		lastItem = fileBrowse;
		
		tagNameLabel = new JLabel("Tag");
		panel.add(tagNameLabel);
		layout.putConstraint(SpringLayout.WEST, tagNameLabel, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, tagNameLabel, 5, SpringLayout.SOUTH, lastItem);
		
		tagNameField = new JTextField();
		panel.add(tagNameField);
		layout.putConstraint(SpringLayout.EAST, tagNameField, 5, SpringLayout.EAST, padLabel);
		layout.putConstraint(SpringLayout.WEST, tagNameField, 5, SpringLayout.EAST, tagNameLabel);
		layout.putConstraint(SpringLayout.NORTH, tagNameField, 5, SpringLayout.SOUTH, lastItem);
		
		lastItem = tagNameField;
		
		drawUnderlayLabel = new JLabel("Draw Underlay?");
		panel.add(drawUnderlayLabel);
		layout.putConstraint(SpringLayout.WEST, drawUnderlayLabel, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, drawUnderlayLabel, 5, SpringLayout.SOUTH, lastItem);
		
		drawUnderlayField = new JCheckBox();
		panel.add(drawUnderlayField);
		layout.putConstraint(SpringLayout.EAST, drawUnderlayField, 5, SpringLayout.EAST, padLabel);
		layout.putConstraint(SpringLayout.WEST, drawUnderlayField, 5, SpringLayout.EAST, drawUnderlayLabel);
		layout.putConstraint(SpringLayout.NORTH, drawUnderlayField, 5, SpringLayout.SOUTH, lastItem);
		
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		tb.setAlignmentX(JToolBar.RIGHT_ALIGNMENT);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		tb.add(saveButton);
		
		addButton = new JButton("Add");
		addButton.addActionListener(this);
		tb.add(addButton);
		
		add(tb, BorderLayout.PAGE_END);
		
		resetFields();
	}
	
	public void resetFields()
	{
		if (selectedItem == null && addMode == false)
		{
			iconTypeDropDownLabel.setVisible(false);
			iconTypeDropDown.setVisible(false);
			tagNameField.setVisible(false);
			tagNameLabel.setVisible(false);
			drawUnderlayField.setVisible(false);
			drawUnderlayLabel.setVisible(false);
			fileBrowse.setVisible(false);
			fileNameLabel.setVisible(false);
		}
		else
		{
			iconTypeDropDownLabel.setVisible(true);
			iconTypeDropDown.setVisible(true);
			tagNameField.setVisible(true);
			tagNameLabel.setVisible(true);
			drawUnderlayField.setVisible(true);
			drawUnderlayLabel.setVisible(true);
			fileBrowse.setVisible(true);
			fileNameLabel.setVisible(true);
			
			if (selectedItem != null)
			{
				tagNameField.setText(selectedItem.getEnumName());
				iconTypeDropDown.setSelectedItem(selectedItem.getIconType());
				drawUnderlayField.setSelected(selectedItem.isUnderlayMinimized());
				fileBrowse.setVisible(false);
				fileNameLabel.setText("File: " + new File(selectedItem.getImagePath()).getName());
				iconTypeDropDown.setEnabled(false);
			}
			else
			{
				tagNameField.setText("");
				iconTypeDropDown.setSelectedItem(null);
				drawUnderlayField.setSelected(false);
				fileBrowse.setVisible(true);
				fileNameLabel.setText("File: ");
				iconTypeDropDown.setEnabled(true);
			}
		}
		
		if (!addMode)
		{
			iconTypeDropDown.setEnabled(false);
			if (selectedItem != null && selectedItem.getIconType().equals(ICON_TYPE.TEAM))
			{
				saveButton.setVisible(true);
			}
			else
			{
				saveButton.setVisible(false);
			}
			addButton.setVisible(false);
		}
		else
		{
			saveButton.setVisible(false);
			addButton.setVisible(true);
		}
		
		if (selectedItem != null && selectedItem.getIconType().equals(ICON_TYPE.TEAM))
		{
			tagNameField.setEnabled(true);
			tagNameField.setVisible(true);
			tagNameLabel.setVisible(true);
			drawUnderlayField.setEnabled(true);
			drawUnderlayField.setVisible(true);
			drawUnderlayLabel.setVisible(true);
		}
		else
		{
			tagNameField.setEnabled(false);
			tagNameField.setVisible(false);
			tagNameLabel.setVisible(false);
			drawUnderlayField.setEnabled(false);
			drawUnderlayField.setVisible(false);
			drawUnderlayLabel.setVisible(false);
		}
	}

	public boolean isAddMode() {
		return addMode;
	}

	public void setAddMode(boolean addMode) {
		this.addMode = addMode;
	}

	public Icon getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Icon selectedItem) {
		this.selectedItem = selectedItem;
	}

	public JLabel getTagNameLabel() {
		return tagNameLabel;
	}

	public void setTagNameLabel(JLabel tagNameLabel) {
		this.tagNameLabel = tagNameLabel;
	}

	public JTextField getTagNameField() {
		return tagNameField;
	}

	public void setTagNameField(JTextField tagNameField) {
		this.tagNameField = tagNameField;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(fileBrowse))
		{
			iconFile = null;
			
			JFileChooser chooser = new JFileChooser();
			int outcome = chooser.showOpenDialog(LegeditFrame.legedit);
			if (outcome == JFileChooser.APPROVE_OPTION)
			{
				iconFile = chooser.getSelectedFile();
				fileNameLabel.setText("File: " + iconFile.getName());
			}
		}
		
		if (e.getSource().equals(saveButton))
		{
			if (selectedItem != null)
			{
				boolean validated = true;
				if (selectedItem.getIconType().equals(ICON_TYPE.TEAM) && tagNameField.getText() == null || tagNameField.getText().isEmpty())
				{
					JOptionPane.showMessageDialog(LegeditFrame.legedit, "Tag must be populated!", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
					validated = false;
				}
				
				if (validated)
				{
					if (selectedItem.getIconType().equals(ICON_TYPE.TEAM))
					{
						String tagName = tagNameField.getText().toUpperCase().replace(" ", "_");
						selectedItem.setTagName(tagName);
						selectedItem.setUnderlayMinimized(drawUnderlayField.isSelected());
					}
					
					Icon.saveIconDefinitions();
					
					File newFile = new File("legendary"+File.separator+selectedItem.getIconType().name()+File.separator+iconFile.getName());
					try {
						if (!newFile.exists())
						{
							copyFile(iconFile, newFile);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			else
			{
				JOptionPane.showMessageDialog(LegeditFrame.legedit, "No Icon Selected", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource().equals(addButton))
		{
			boolean validated = true;
			if (iconTypeDropDown.getSelectedItem() == null)
			{
				JOptionPane.showMessageDialog(LegeditFrame.legedit, "Icon Type must be selected!", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				validated = false;
			}

			if (iconTypeDropDown.getSelectedItem().equals(ICON_TYPE.TEAM) && (tagNameField.getText() == null || tagNameField.getText().isEmpty()))
			{
				JOptionPane.showMessageDialog(LegeditFrame.legedit, "Tag must be populated!", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				validated = false;
			}
			
			if (iconFile == null)
			{
				JOptionPane.showMessageDialog(LegeditFrame.legedit, "An image file must be provided!", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				validated = false;
			}
			
			if (validated)
			{
				selectedItem = new Icon();
				
				selectedItem.setType(((ICON_TYPE)iconTypeDropDown.getSelectedItem()));
				
				String type = "";
				switch (((ICON_TYPE)iconTypeDropDown.getSelectedItem()))
				{
				case ATTRIBUTE: type = "attributes";
					break;
				case MISC: type = "misc";
					break;
				case NONE: type = "none";
					break;
				case POWER: type = "powers";
					break;
				case TEAM: type = "teams";
					break;
				default:
					break;
				
				}
				File newFile = new File("legedit"+File.separator+"icons"+File.separator+type+File.separator+iconFile.getName());
				try {
					copyFile(iconFile, newFile);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(LegeditFrame.legedit, "Couldnt add icon!", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
					return;
				}
				
				selectedItem.setImagePath(newFile.getAbsolutePath());
				
				if (selectedItem.getIconType().equals(ICON_TYPE.TEAM))
				{
					String tagName = tagNameField.getText().toUpperCase().replace(" ", "_");
					selectedItem.setTagName(tagName);
					selectedItem.setUnderlayMinimized(drawUnderlayField.isSelected());
				}
				
				Icon.values().add(selectedItem);
				
				Icon.saveIconDefinitions();
				
				getIconManager().resetIcons();
				
				selectedItem = null;
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (iconTypeDropDown.getSelectedItem() != null && iconTypeDropDown.getSelectedItem().equals(ICON_TYPE.TEAM))
		{
			tagNameField.setEnabled(true);
			tagNameField.setVisible(true);
			tagNameLabel.setVisible(true);
			drawUnderlayField.setEnabled(true);
			drawUnderlayField.setVisible(true);
			drawUnderlayLabel.setVisible(true);
		}
		else
		{
			tagNameField.setEnabled(false);
			tagNameField.setVisible(false);
			tagNameLabel.setVisible(false);
			drawUnderlayField.setEnabled(false);
			drawUnderlayField.setVisible(false);
			drawUnderlayLabel.setVisible(false);
		}
	}

	public IconManager getIconManager() {
		return iconManager;
	}

	public void setIconManager(IconManager iconManager) {
		this.iconManager = iconManager;
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }
	    else
	    {
	    	destFile.delete();
	    	destFile = new File(destFile.getAbsolutePath());
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}
