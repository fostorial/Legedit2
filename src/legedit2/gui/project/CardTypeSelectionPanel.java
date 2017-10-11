package legedit2.gui.project;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import legedit2.card.Card;
import legedit2.cardgroup.CardGroup;
import legedit2.cardgroup.CardGroupListCellRenderer;
import legedit2.deck.Deck;
import legedit2.definitions.ItemType;
import legedit2.definitions.LegeditItem;
import legedit2.gui.LegeditFrame;
import legedit2.gui.dialogs.AddLegeditItemDialog;
import legedit2.gui.editor.CardEditorPanel;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;

public class CardTypeSelectionPanel extends JPanel implements ActionListener, ItemListener, ListSelectionListener {

	private static final long serialVersionUID = 2441581697840723279L;
	
	private JComboBox<CardGroup> cardGroupFilter;
	private JButton newLegeditItem = new JButton(" + ");
	private JButton deleteLegeditItem = new JButton(" - ");
	
	private DefaultListModel<LegeditItem> cardListModel = new DefaultListModel<>();
	private JList<LegeditItem> cardList;
	
	private static DefaultListModel<LegeditItem> cardListModelStatic = null;

	private JScrollPane scroll = new JScrollPane();
	
	private DeckCardSelectionPanel deckPanel;
	
	private CardEditorPanel cardEditorPanel;
	
	private LegeditItemPreviewPanel previewPanel;
	
	private ProjectPanel projectPanel;
	
	public CardTypeSelectionPanel()
	{
		CardTypeSelectionPanel.cardListModelStatic = cardListModel;
		
		setLayout(new BorderLayout(1,1));
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		//toolbar.setLayout(new BorderLayout(0, 0));
		toolbar.setLayout(new FlowLayout(FlowLayout.RIGHT,0, 0));
		
		cardGroupFilter = new JComboBox<CardGroup>();
		cardGroupFilter.addItem(new CardGroup());
		for (CardGroup g : CardGroup.getCardTypes())
		{
			cardGroupFilter.addItem(g);
		}
		cardGroupFilter.setRenderer(new CardGroupListCellRenderer());
		cardGroupFilter.addItemListener(this);
		//this.add(cardGroupFilter, BorderLayout.PAGE_START);
		toolbar.add(cardGroupFilter);
		
		newLegeditItem.addActionListener(this);
		toolbar.add(newLegeditItem);
		
		deleteLegeditItem.addActionListener(this);
		toolbar.add(deleteLegeditItem);
		
		this.add(toolbar, BorderLayout.PAGE_START);
		
		cardList = new JList<>(cardListModel);
		for (LegeditItem c : ProjectHelper.getLegeditItems())
		{
			cardListModel.addElement(c);
		}
		cardList.setCellRenderer(new CardTypeCellRenderer());
		cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cardList.setEnabled(true);
		//this.add(cardList, BorderLayout.CENTER);
		
		cardList.addMouseListener(new MouseAdapter(){
		    @Override
		    public void mouseClicked(MouseEvent e){
		        if(e.getClickCount()==2){
		            selectCardForEdit();
		        }
		    }
		});
		
		scroll.setViewportView(cardList);
		scroll.setBorder(null);
		
		this.add(scroll, BorderLayout.CENTER);
		
		cardList.addListSelectionListener(this);
	}
	
	private void selectCardForEdit()
	{
		if (cardList.getSelectedValue() != null && cardList.getSelectedValue() instanceof Deck)
		{
			Deck.setStaticDeck((Deck)cardList.getSelectedValue());
		}
		
		if (cardList.getSelectedValue() != null && cardList.getSelectedValue() instanceof Card)
		{
			Card.setStaticCard((Card)cardList.getSelectedValue());
			getProjectPanel().selectCardForEdit((Card)cardList.getSelectedValue());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newLegeditItem))
		{
			try
			{
				ItemType type = AddLegeditItemDialog.addLegeditItem();
				if (type != null)
				{
					LegeditItem item = LegeditItem.generateLegeditItem(type);
					if (item != null)
					{
						cardListModel.addElement(item);
						ProjectHelper.addLegeditItem(item);
						cardList.setSelectedValue(item, true);
						
						if (item instanceof Deck)
						{
							Deck.setStaticDeck((Deck)item);
						}
						
						if (item instanceof Card)
						{
							Card.setStaticCard((Card)item);
						}
					}					
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(LegeditFrame.legedit, ex.getMessage() != null ? ex.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
		if (e.getSource().equals(deleteLegeditItem))
		{
			if (cardList.getSelectedValue() != null)
			{
				try
				{
					if (cardList.getSelectedValue() != null && cardList.getSelectedValue() instanceof Deck)
					{
						selectCardForDelete();
					}
					else if (cardList.getSelectedValue() != null && cardList.getSelectedValue() instanceof Card)
					{
						selectCardForDelete();
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(LegeditFrame.legedit, ex.getMessage() != null ? ex.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(LegeditFrame.legedit, "No Item Selected", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			}		
		}
	}
	
	private void selectCardForDelete()
	{
		if (cardList.getSelectedValue() != null && cardList.getSelectedValue() instanceof Deck)
		{
			if (cardList.getSelectedValue() != null && cardList.getSelectedValue() instanceof Deck)
			{
				Deck.setStaticDeck(null);
				ProjectHelper.getDecks().remove(cardList.getSelectedValue());
				LegeditFrame.refreshGUI();
			}
		}
		else if (cardList.getSelectedValue() != null && cardList.getSelectedValue() instanceof Card)
		{
			if (cardList.getSelectedValue() != null && cardList.getSelectedValue() instanceof Card)
			{
				Card.setStaticCard(null);
				ProjectHelper.getCards().remove(cardList.getSelectedValue());
				LegeditFrame.refreshGUI();
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		resetCardsByType((CardGroup)e.getItem());
	}
	
	private void resetCardsByType(CardGroup group)
	{
		cardListModel.clear();
		
		for (Card c : ProjectHelper.getCards())
		{
			if (group != null && group.getName() != null)
			{
				if (c.getTemplate() != null && group.getName().equals(c.getTemplate().getCardGroup()))
				{
					cardListModel.addElement(c);
				}
			}
			else
			{
				cardListModel.addElement(c);
			}			
		}
		
		for (Deck c : ProjectHelper.getDecks())
		{
			if (group != null && group.getName() != null)
			{
				if (c.getTemplate() != null && group.getName().equals(c.getTemplate().getCardGroup()))
				{
					cardListModel.addElement(c);
				}
			}
			else
			{
				cardListModel.addElement(c);
			}			
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (cardList.getSelectedValue() != null)
		{
			//System.out.println(cardList.getSelectedValue().getLegeditName());
			if (cardList.getSelectedValue() instanceof Deck)
			{
				if (cardList.getSelectedValue() instanceof Deck)
				{
					Deck.setStaticDeck((Deck)cardList.getSelectedValue());
				}
				
				if (cardList.getSelectedValue() instanceof Card)
				{
					Card.setStaticCard((Card)cardList.getSelectedValue());
				}
				
				/* Its a deck */
				if (getDeckPanel() != null)
				{
					getDeckPanel().setSelectedDeck((Deck)cardList.getSelectedValue());
				}

				if (cardList.getSelectedValue() instanceof Deck)
				{
					getProjectPanel().selectDeck((Deck)cardList.getSelectedValue());
				}
				
				previewPanel.setSelectedItem(cardList.getSelectedValue());
				previewPanel.resetPreviewPanel();
			}
			else
			{
				/* Its a card */
				previewPanel.setSelectedItem(cardList.getSelectedValue());
				previewPanel.resetPreviewPanel();
				
				getProjectPanel().selectDeck(null);
			}
		}
	}

	public static DefaultListModel<LegeditItem> getCardListModelStatic() {
		return cardListModelStatic;
	}

	public static void setCardListModelStatic(DefaultListModel<LegeditItem> cardListModelStatic) {
		CardTypeSelectionPanel.cardListModelStatic = cardListModelStatic;
	}

	public DeckCardSelectionPanel getDeckPanel() {
		return deckPanel;
	}

	public void setDeckPanel(DeckCardSelectionPanel deckPanel) {
		this.deckPanel = deckPanel;
	}

	public LegeditItemPreviewPanel getPreviewPanel() {
		return previewPanel;
	}

	public void setPreviewPanel(LegeditItemPreviewPanel previewPanel) {
		this.previewPanel = previewPanel;
	}

	public CardEditorPanel getCardEditorPanel() {
		return cardEditorPanel;
	}

	public void setCardEditorPanel(CardEditorPanel cardEditorPanel) {
		this.cardEditorPanel = cardEditorPanel;
	}

	public ProjectPanel getProjectPanel() {
		return projectPanel;
	}

	public void setProjectPanel(ProjectPanel projectPanel) {
		this.projectPanel = projectPanel;
	}
}
