package legedit2.gui.project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import legedit2.card.Card;
import legedit2.deck.Deck;
import legedit2.gui.LegeditFrame;
import legedit2.gui.editor.CardEditorPanel;

public class ProjectPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -6132850075460022938L;
	
	private CardTypeSelectionPanel itemPanel;
	
	private DeckCardSelectionPanel deckPanel;
	
	private CardEditorPanel cardEditorPanel;
	
	private DeckEditorPanel deckEditorPanel;
	
	private LegeditItemPreviewPanel previewPanel;

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	public DeckCardSelectionPanel getDeckPanel() {
		return deckPanel;
	}

	public void setDeckPanel(DeckCardSelectionPanel deckPanel) {
		this.deckPanel = deckPanel;
	}

	public CardEditorPanel getCardEditorPanel() {
		return cardEditorPanel;
	}

	public void setCardEditorPanel(CardEditorPanel cardEditorPanel) {
		this.cardEditorPanel = cardEditorPanel;
	}
	
	public void selectCardForEdit(Card c)
	{
		LegeditFrame.legedit.selectCardForEdit(c);
	}
	
	public void selectDeck(Deck d)
	{
		getDeckEditorPanel().setSelectedDeck(d);
	}

	public DeckEditorPanel getDeckEditorPanel() {
		return deckEditorPanel;
	}

	public void setDeckEditorPanel(DeckEditorPanel deckEditorPanel) {
		this.deckEditorPanel = deckEditorPanel;
	}

	public CardTypeSelectionPanel getItemPanel() {
		return itemPanel;
	}

	public void setItemPanel(CardTypeSelectionPanel itemPanel) {
		this.itemPanel = itemPanel;
	}

	public LegeditItemPreviewPanel getPreviewPanel() {
		return previewPanel;
	}

	public void setPreviewPanel(LegeditItemPreviewPanel previewPanel) {
		this.previewPanel = previewPanel;
	}

}
