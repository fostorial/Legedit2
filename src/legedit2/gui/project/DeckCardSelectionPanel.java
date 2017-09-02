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
import javax.swing.JLabel;
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
import legedit2.deck.Deck;
import legedit2.definitions.ItemType;
import legedit2.definitions.LegeditItem;
import legedit2.gui.LegeditFrame;
import legedit2.gui.dialogs.AddLegeditItemDialog;
import legedit2.gui.editor.CardEditorPanel;
import legedit2.helpers.LegeditHelper;

public class DeckCardSelectionPanel extends JPanel implements ActionListener, ItemListener, ListSelectionListener {

	private static final long serialVersionUID = 2441581697840723279L;
	
	private JComboBox<CardGroup> cardGroupFilter;
	private JButton newLegeditItem = new JButton(" + ");
	private JButton deleteLegeditItem = new JButton(" - ");
	
	private DefaultListModel<LegeditItem> cardListModel = new DefaultListModel<>();
	private JList<LegeditItem> cardList;
	
	private static DefaultListModel<LegeditItem> cardListModelStatic = null;

	private JScrollPane scroll = new JScrollPane();
	
	private Deck selectedDeck;
	
	private LegeditItemPreviewPanel previewPanel;
	
	private CardEditorPanel cardEditorPanel;
	
	private ProjectPanel projectPanel;
	
	public DeckCardSelectionPanel()
	{
		DeckCardSelectionPanel.cardListModelStatic = cardListModel;
		
		setLayout(new BorderLayout(1,1));
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
		
		//toolbar.add(new JLabel(" "), BorderLayout.CENTER);
		
		newLegeditItem.addActionListener(this);
		toolbar.add(newLegeditItem);
		
		deleteLegeditItem.addActionListener(this);
		toolbar.add(deleteLegeditItem);
		
		this.add(toolbar, BorderLayout.PAGE_START);
		
		cardList = new JList<>(cardListModel);
		if (selectedDeck != null)
		{
			for (LegeditItem c : selectedDeck.getCards())
			{
				cardListModel.addElement(c);
			}	
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newLegeditItem))
		{
			if (getSelectedDeck() != null)
			{
				try
				{
					ItemType type = AddLegeditItemDialog.addLegeditItemWithoutDecks(getSelectedDeck().getTemplate());
					if (type != null)
					{
						LegeditItem item = LegeditItem.generateLegeditItem(type);
						if (item != null)
						{
							selectedDeck.setChanged(true);
							
							cardListModel.addElement(item);
							getSelectedDeck().getCards().add((Card)item);
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
					JOptionPane.showMessageDialog(LegeditFrame.legedit, ex.getMessage() != null ? ex.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(LegeditFrame.legedit, "No Deck Selected", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			}		
		}
		if (e.getSource().equals(deleteLegeditItem))
		{
			
			if (cardList.getSelectedValue() != null)
			{
				try
				{
					if (getSelectedDeck() != null)
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

	@Override
	public void itemStateChanged(ItemEvent e) {
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (cardList.getSelectedValue() != null)
		{
			if (cardList.getSelectedValue() instanceof Deck)
			{
				Deck.setStaticDeck((Deck)cardList.getSelectedValue());
			}
			
			if (cardList.getSelectedValue() instanceof Card)
			{
				Card.setStaticCard((Card)cardList.getSelectedValue());
			}
			
			System.out.println(cardList.getSelectedValue().getLegeditName());
			getPreviewPanel().setSelectedItem(cardList.getSelectedValue());
		}
	}

	public static DefaultListModel<LegeditItem> getCardListModelStatic() {
		return cardListModelStatic;
	}

	public static void setCardListModelStatic(DefaultListModel<LegeditItem> cardListModelStatic) {
		DeckCardSelectionPanel.cardListModelStatic = cardListModelStatic;
	}

	public Deck getSelectedDeck() {
		return selectedDeck;
	}

	public void setSelectedDeck(Deck selectedDeck) {
		this.selectedDeck = selectedDeck;
		resetDeckList();
	}
	
	public void resetDeckList()
	{
		cardListModel.clear();
		
		if (selectedDeck != null)
		{
			for (LegeditItem c : selectedDeck.getCards())
			{
				cardListModel.addElement(c);
			}	
		}
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
	
	private void selectCardForDelete()
	{
		System.out.println("Deleting");
		if (selectedDeck != null && cardList.getSelectedValue() != null && cardList.getSelectedValue() instanceof Card)
		{
			selectedDeck.setChanged(true);
			
			Card.setStaticCard(null);
			selectedDeck.getCards().remove(cardList.getSelectedValue());
			LegeditFrame.refreshGUI();
		}
	}
}
