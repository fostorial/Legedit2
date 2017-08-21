package legedit2.gui.project;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import legedit2.card.Card;
import legedit2.gui.editor.CardEditorPanel;

public class ProjectPanelColumns extends ProjectPanel {

	private static final long serialVersionUID = -6132850075460022938L;
	
	private JSplitPane leftSplit = new JSplitPane();
	private JSplitPane rightSplit = new JSplitPane();
	
	private JPanel previewHolderPanel;
	
	public ProjectPanelColumns()
	{
		LegeditItemPreviewPanel previewPanel = new LegeditItemPreviewPanel();
		previewPanel.getCardMaker().setScale(0.3d);
		setPreviewPanel(previewPanel);
		
		CardEditorPanel cardEditorPanel = new CardEditorPanel();
		setCardEditorPanel(cardEditorPanel);

		DeckCardSelectionPanel deckPanel = new DeckCardSelectionPanel();
		setDeckPanel(deckPanel);
		deckPanel.setPreviewPanel(previewPanel);
		deckPanel.setCardEditorPanel(cardEditorPanel);
		deckPanel.setProjectPanel(this);
		
		DeckEditorPanel deckEditorPanel = new DeckEditorPanel();
		deckEditorPanel.setProjectPanel(this);
		setDeckEditorPanel(deckEditorPanel);
		
		previewHolderPanel = new JPanel();
		previewHolderPanel.setLayout(new BorderLayout(1,1));
		previewHolderPanel.add(previewPanel, BorderLayout.CENTER);
		previewHolderPanel.add(deckEditorPanel, BorderLayout.PAGE_START);
		
		deckEditorPanel.setVisible(false);
		
		leftSplit.setBorder(null);
		rightSplit.setBorder(null);
		
		setLayout(new BorderLayout(0, 0));
		leftSplit.setRightComponent(rightSplit);
		
		CardTypeSelectionPanel cardTypePanel = new CardTypeSelectionPanel();
		setItemPanel(cardTypePanel);
		cardTypePanel.setPreviewPanel(previewPanel);
		cardTypePanel.setDeckPanel(getDeckPanel());
		cardTypePanel.setCardEditorPanel(cardEditorPanel);
		cardTypePanel.setProjectPanel(this);
		leftSplit.setLeftComponent(cardTypePanel);
		rightSplit.setLeftComponent(getDeckPanel());
		rightSplit.setRightComponent(previewHolderPanel);
		
		
		add(leftSplit,BorderLayout.CENTER);

		leftSplit.setDividerLocation(0.5d);
		leftSplit.setResizeWeight(0.33d);
		rightSplit.setDividerLocation(0.5d);
		rightSplit.setResizeWeight(0.5d);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
