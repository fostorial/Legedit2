package legedit2.gui.project;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javafx.util.Pair;
import legedit2.card.Card;
import legedit2.cardtype.CardType;
import legedit2.cardtype.CustomElement;
import legedit2.cardtype.ElementIcon;
import legedit2.cardtype.Style;
import legedit2.deck.Deck;
import legedit2.decktype.DeckType;
import legedit2.decktype.DeckTypeAttribute;
import legedit2.definitions.Icon;
import legedit2.gui.dialogs.StyleListCellRenderer;
import legedit2.gui.editor.IconListRenderer;

public class DeckEditorPanel extends JPanel implements ItemListener, ActionListener {
	
	private Deck selectedDeck;
	
	private JLabel nameLabel;
	private JTextField nameField;
	
	private JLabel styleLabel;
	private JComboBox<Style> styleList;
	
	private JButton updateButton = new JButton("Update");
	
	private ProjectPanel projectPanel;
	
	private List<Pair<DeckTypeAttribute, JComponent>> editableAttributes = new ArrayList<>();

	
	public DeckEditorPanel() {
		setGui();
	}
	
	private void setGui()
	{
		setLayout(new GridBagLayout());
		
		nameLabel = new JLabel("Name");
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		this.add(nameLabel, c);
		
		nameField = new JTextField();
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		this.add(nameField, c);
		
		
		styleLabel = new JLabel("Style");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		this.add(styleLabel, c);
		
		styleList = new JComboBox<Style>();
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		this.add(styleList, c);
		
		styleList.addItem(new Style());
		styleList.setRenderer(new StyleListCellRenderer());
		styleList.addItemListener(this);
		
		int row = 2;
		if (selectedDeck != null && selectedDeck.getTemplate() != null && selectedDeck.getTemplate().getAttributes() != null)
		{
			// Properties from the Deck Template
			for (DeckTypeAttribute attr : selectedDeck.getTemplate().getAttributes())
			{
				// TODO only supports Icons for now, should change
				if (attr.getType() != null && attr.getType().equals("icon"))
				{
					String attrType = attr.getType().toLowerCase();					
					if (attrType.equals("icon"))
					{
						row = addIconItems(attr, row);
					}
				}
			}
			
			// Editable attributes stored on the deck
			for (DeckTypeAttribute attr : selectedDeck.getAttributes())
			{
				if (attr.getType() != null)
				{
					// TODO only supports value for now, should be expanded
					String attrType = attr.getType().toLowerCase();
					if (attrType.equals("value"))
					{
						row = addStringItem(attr, row);
					}
				}
			}			
		}
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = row;
		c.weightx = 0.5;
		this.add(updateButton, c);
		updateButton.addActionListener(this);
		
		
		resetStyleMenu();
	}

	public Deck getSelectedDeck() {
		return selectedDeck;
	}

	public void setSelectedDeck(Deck selectedDeck) {
		editableAttributes.clear();
		this.selectedDeck = selectedDeck;
		
		this.removeAll();
		setGui();
		
		boolean visible = false;
		
		if (selectedDeck != null)
		{
			if (selectedDeck.getTemplate().getNameEditable())
			{
				nameField.setText(selectedDeck.getDeckName());
				nameField.setVisible(true);
				nameLabel.setVisible(true);
				visible = true;
			}
			else
			{
				nameField.setText(null);
				nameField.setVisible(false);
				nameLabel.setVisible(false);
			}
			
			if (doStylesExist())
			{
				styleLabel.setVisible(true);
				styleList.setVisible(true);
				visible = true;
			}
			else
			{
				styleLabel.setVisible(false);
				styleList.setVisible(false);
			}
			
			resetStyleMenu();
		}
		
		this.setVisible(visible);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub		
	}
	
	private void resetStyleMenu()
	{
		styleList.removeAllItems();
		
		if (selectedDeck != null)
		{
			DeckType dt = selectedDeck.getTemplate();
			List<String> usedStyles = new ArrayList<>(); 
			for (CardType ct : dt.getCardTypes())
			{
				for (Style s : ct.getStyles())
				{
					if (!usedStyles.contains(s.getName()))
					{
						usedStyles.add(s.getName());
						styleList.addItem(s);
					}
				}
			}
		}
		else
		{
			styleList.addItem(new Style());
		}
	}
	
	private boolean doStylesExist()
	{
		if (selectedDeck != null)
		{
			for (Card c : selectedDeck.getCards())
			{
				if (c.getTemplate().getStyles().size() > 0)
				{
					return true;
				}
			}			
		}
		
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (selectedDeck != null)
		{
			if (selectedDeck.getTemplate().getNameEditable())
			{
				selectedDeck.setName(nameField.getText());
			}
			
			///////////////////////////////////////////
			// Update Global deck attributes
			///////////////////////////////////////////
			
			for (Pair<DeckTypeAttribute, JComponent> attrib : editableAttributes)
			{
				DeckTypeAttribute deckAttrib = attrib.getKey();
				JComponent uiAttrib = attrib.getValue();
				if (uiAttrib instanceof JComboBox)
				{
					// TODO Implement appropriate support here
				}
				else if (uiAttrib instanceof JTextField)
				{
					JTextField uiText = (JTextField)uiAttrib;
					deckAttrib.setValue(uiText.getText());
				}
			}			
			
			
			///////////////////////////////////////////
			// Here the code will forcibly update values on cards, based of the "deck" UI values
			// I feel like this could all be simplified by keeping the values on the deck itself and then have the cards
			// retrieve the info when it needs it. Just like its doing for Global deck attributes
			// TODO SHOULD BE REFACTORED
			///////////////////////////////////////////

			for (Card c : selectedDeck.getCards())
			{
				///////////////////////////////////////////
				// Update the drop down values coming from the deck type template (which is pretty much only for Teams atm
				// TODO NEEDS TO BE REFACTORED
				///////////////////////////////////////////

				for (DeckTypeAttribute attr : selectedDeck.getTemplate().getAttributes())
				{
					for (CustomElement el : c.getTemplate().elements)
					{
						if (el.name != null && el.name.equalsIgnoreCase(attr.getName()))
						{
							if (el instanceof ElementIcon && attr.getType().equalsIgnoreCase("icon"))
							{
								JComboBox box = null;
								for (Component comp : this.getComponents())
								{
									if (comp instanceof JComboBox && comp.getName() != null && comp.getName().equals("icondropdown_"+attr.getName()))
									{
										box = (JComboBox)comp;
										break;
									}
								}
								
								if (box != null)
								{
									((ElementIcon)el).value = (Icon)box.getSelectedItem();
								}
							}
						}
					}					
				}
				
				///////////////////////////////////////////
				// Update the drop down values representing the style of the card
				// TODO SHOULD BE REFACTORED
				///////////////////////////////////////////

				for (Style s : c.getTemplate().getStyles())
				{
					for (DeckTypeAttribute attr : selectedDeck.getTemplate().getAttributes())
					{
						for (CustomElement el : s.getElements())
						{
							if (el.name != null && el.name.equalsIgnoreCase(attr.getName()))
							{
								if (el instanceof ElementIcon && attr.getType().equalsIgnoreCase("icon"))
								{
									JComboBox box = null;
									for (Component comp : this.getComponents())
									{
										if (comp instanceof JComboBox && comp.getName() != null && comp.getName().equals("icondropdown_"+attr.getName()))
										{
											box = (JComboBox)comp;
											break;
										}
									}
									
									if (box != null)
									{
										((ElementIcon)el).value = (Icon)box.getSelectedItem();
									}
								}
							}
						}					
					}
					
					if (s.getName().equals(((Style)styleList.getSelectedItem()).getName()))
					{
						c.getTemplate().setStyle(s);
						c.setStyle(s.getName());
					}
				}
			}
			
			getProjectPanel().getItemPanel().repaint();
			getProjectPanel().getDeckPanel().repaint();
			getProjectPanel().getPreviewPanel().resetPreviewPanel();
		}
	}

	public ProjectPanel getProjectPanel() {
		return projectPanel;
	}

	public void setProjectPanel(ProjectPanel projectPanel) {
		this.projectPanel = projectPanel;
	}
	
	private int addIconItems(DeckTypeAttribute attr, int row)
	{		
		JLabel nameLabel = new JLabel(attr.getDisplayName());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = row;
		this.add(nameLabel, c);
		
		JComboBox<Icon> icons = new JComboBox<Icon>();
		icons.setName("icondropdown_"+attr.getName());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 1;
		c.gridy = row;
		c.weightx = 0.5;
		icons.setRenderer(new IconListRenderer());
		for (Icon icon : Icon.sorted_values())
		{
			if (attr.getValue() != null && icon.getCategory() != null && (icon.getCategory().equalsIgnoreCase(attr.getValue()) || icon.getEnumName().equalsIgnoreCase("NONE")))
			{
				icons.addItem(icon);
			}
			else if (attr.getValue() == null)
			{
				icons.addItem(icon);
			}
			
			if (icon.getEnumName().equalsIgnoreCase("NONE"))
			{
				icons.setSelectedItem(icon);
			}
		}
		this.add(icons, c);
		
		return row + 1;
	}
	
	private int addStringItem(DeckTypeAttribute attr, int row)
	{
		JLabel nameLabel = new JLabel(attr.getDisplayName());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = row;
		this.add(nameLabel, c);

		JTextField nameField = new JTextField(attr.getValue());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = row;
		c.weightx = 0.5;
		nameField.setEnabled(attr.isUserEditable());
		this.add(nameField, c);
		
		if (attr.isUserEditable())
			editableAttributes.add(new Pair<>(attr, nameField));
		
		return row + 1;
	}
}
