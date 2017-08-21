package legedit2.gui.project;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import legedit2.card.Card;
import legedit2.cardtype.CardType;
import legedit2.cardtype.Style;
import legedit2.deck.Deck;
import legedit2.decktype.DeckType;
import legedit2.gui.dialogs.StyleListCellRenderer;

public class DeckEditorPanel extends JPanel implements ItemListener, ActionListener {
	
	private Deck selectedDeck;
	
	private JLabel nameLabel;
	private JTextField nameField;
	
	private JLabel styleLabel;
	private JComboBox<Style> styleList;
	
	private JButton updateButton = new JButton("Update");
	
	private ProjectPanel projectPanel;
	
	public DeckEditorPanel()
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
		
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.5;
		this.add(updateButton, c);
		updateButton.addActionListener(this);
		
		resetStyleMenu();
	}

	public Deck getSelectedDeck() {
		return selectedDeck;
	}

	public void setSelectedDeck(Deck selectedDeck) {
		this.selectedDeck = selectedDeck;
		
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
				styleList.setVisible(true);
				styleList.setVisible(true);
				visible = true;
			}
			else
			{
				styleList.setVisible(false);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (selectedDeck != null)
		{
			if (selectedDeck.getTemplate().getNameEditable())
			{
				selectedDeck.setName(nameField.getText());
			}
			
			for (Card c : selectedDeck.getCards())
			{
				for (Style s : c.getTemplate().getStyles())
				{
					if (s.getName().equals(((Style)styleList.getSelectedItem()).getName()))
					{
						c.getTemplate().setStyle(s);
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
}
