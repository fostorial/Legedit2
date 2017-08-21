package legedit2.gui.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import legedit2.card.Card;
import legedit2.gui.project.LegeditItemPreviewPanel;

public class CardEditorPanel extends JPanel {

	private static final long serialVersionUID = -8598696504340547847L;

	private JSplitPane splitPane = new JSplitPane();
	
	private Card selectedCard;
	
	private LegeditItemPreviewPanel previewPanel;
	
	private boolean templateMode;
	
	public CardEditorPanel()
	{
		setLayout(new BorderLayout(1,1));
		splitPane.setContinuousLayout(true);
		splitPane.setRightComponent(new JPanel());
		splitPane.setBorder(null);
		splitPane.setDividerLocation(0.5d);
		splitPane.setResizeWeight(0.5d);
		add(splitPane, BorderLayout.CENTER);
		
		previewPanel = new LegeditItemPreviewPanel();
		previewPanel.getCardMaker().setScale(0.5d);
		previewPanel.setSelectedItem(selectedCard);
		splitPane.setLeftComponent(previewPanel);
		
		splitPane.setRightComponent(new CardPropertyPanel(null, previewPanel));
	}

	public Card getSelectedCard() {
		return selectedCard;
	}

	public void setSelectedCard(Card selectedCard) {
		this.selectedCard = selectedCard;
		previewPanel.setSelectedItem(selectedCard);
		splitPane.setRightComponent(new CardPropertyPanel(selectedCard, previewPanel));
	}

	public boolean isTemplateMode() {
		return templateMode;
	}

	public void setTemplateMode(boolean templateMode) {
		this.templateMode = templateMode;
	}
}
