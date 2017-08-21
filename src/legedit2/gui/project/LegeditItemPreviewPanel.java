package legedit2.gui.project;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import legedit2.card.Card;
import legedit2.cardtype.CardType;
import legedit2.deck.Deck;
import legedit2.decktype.DeckType;
import legedit2.definitions.ItemType;
import legedit2.definitions.LegeditItem;
import legedit2.helpers.LegeditHelper;
import legedit2.imaging.CustomCardMaker;

public class LegeditItemPreviewPanel extends JPanel {

	private static final long serialVersionUID = 2441581697840723279L;

	private JScrollPane scroll = new JScrollPane();
	
	private LegeditItem selectedItem;
	private ItemType selectedItemType;
	
	private JPanel innerPanel;
	
	private CustomCardMaker cardMaker;
	
	private HashMap<Card, BufferedImage> imageMap = new HashMap<>();
	private int lastItem = -1;
	
	private List<Card> tempCardList = new ArrayList<Card>();
	
	public LegeditItemPreviewPanel()
	{
		setLayout(new BorderLayout(1,1));
		
		cardMaker = new CustomCardMaker();
		cardMaker.setDebug(true);
		
		resetPreviewPanel();
		
		scroll.setViewportView(innerPanel);
		scroll.setBorder(null);
		
		this.add(scroll, BorderLayout.CENTER);
	}
	
	public LegeditItemPreviewPanel(LegeditItem item)
	{
		selectedItem = item;
		setLayout(new BorderLayout(1,1));
		
		resetPreviewPanel();
		
		scroll.setViewportView(innerPanel);
		scroll.setBorder(null);
		
		this.add(scroll, BorderLayout.CENTER);
	}
	
	public LegeditItemPreviewPanel(ItemType item)
	{
		selectedItemType = item;
		setLayout(new BorderLayout(1,1));
		
		resetPreviewPanel();
		
		scroll.setViewportView(innerPanel);
		scroll.setBorder(null);
		
		this.add(scroll, BorderLayout.CENTER);
	}

	public LegeditItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(LegeditItem selectedItem) {
		if (lastItem == -1 || selectedItem == null 
				|| lastItem != selectedItem.hashCode())
		{
			this.selectedItem = selectedItem;
			this.selectedItemType = null;
			rebuildImageList();
			resetPreviewPanel();
		}
	}
	
	public void resetPreviewPanel()
	{
		innerPanel = new JPanel();
		
		if (selectedItem != null)
		{
			if (selectedItem instanceof Deck)
			{
				innerPanel.setLayout(new GridLayout(((Deck)selectedItem).getCards().size(), 1));
				for (Card c : ((Deck)selectedItem).getCards())
				{
					if (imageMap.get(c) == null)
					{
						innerPanel.add(new JLabel("Loading..."));
					}
					else
					{
						innerPanel.add(new JLabel(new ImageIcon(imageMap.get(c))));
					}
				}
			}
			else if (selectedItem instanceof Card)
			{
				innerPanel.setLayout(new GridLayout(1, 1));
				if (imageMap.get((Card)selectedItem) == null)
				{
					innerPanel.add(new JLabel("Loading..."));
				}
				else
				{
					innerPanel.add(new JLabel(new ImageIcon(imageMap.get((Card)selectedItem))));
				}
			}
			else
			{
				innerPanel.setLayout(new GridLayout(1, 1));
				innerPanel.add(new JLabel());
			}
		}
		
		if (selectedItemType != null)
		{
			if (selectedItemType instanceof DeckType || selectedItemType instanceof CardType)
			{
				innerPanel.setLayout(new GridLayout(tempCardList.size(), 1));
				for (Card c : tempCardList)
				{
					if (imageMap.get(c) == null)
					{
						innerPanel.add(new JLabel("Loading..."));
					}
					else
					{
						innerPanel.add(new JLabel(new ImageIcon(imageMap.get(c))));
					}
				}
			}
			else
			{
				innerPanel.setLayout(new GridLayout(1, 1));
				innerPanel.add(new JLabel());
			}
		}
		
		scroll.setViewportView(innerPanel);
	}

	public ItemType getSelectedItemType() {
		return selectedItemType;
	}
	
	public void rebuildImageList()
	{
		imageMap = new HashMap<>();
		tempCardList = new ArrayList<>();
		
		if (selectedItem != null)
		{
			if (selectedItem instanceof Deck)
			{
				for (Card c : ((Deck)selectedItem).getCards())
				{
					imageMap.put(c, null);
					RenderTask task = new RenderTask(c);
					task.execute();
				}
			}
			else if (selectedItem instanceof Card)
			{
				imageMap.put((Card)selectedItem, null);
				RenderTask task = new RenderTask((Card)selectedItem);
				task.execute();
			}
		}
		
		if (selectedItemType != null)
		{
			if (selectedItemType instanceof DeckType)
			{
				for (CardType c : ((DeckType)selectedItemType).getCardTypes())
				{
					Card ca = new Card();
					ca.setTemplate(c);
					tempCardList.add(ca);
					imageMap.put(ca, null);
					RenderTask task = new RenderTask(ca);
					task.execute();
				}
			}
			else if (selectedItemType instanceof CardType)
			{
				Card c = new Card();
				c.setTemplate((CardType)selectedItemType);
				tempCardList.add(c);
				imageMap.put(c, null);
				RenderTask task = new RenderTask(c);
				task.execute();
			}
		}
	}

	public void setSelectedItemType(ItemType selectedItemType) {
		if (lastItem == -1 || selectedItemType == null 
				|| lastItem != selectedItemType.hashCode())
		{
			this.selectedItemType = selectedItemType;
			this.selectedItem = null;
			rebuildImageList();
			resetPreviewPanel();
		}
	}

	public CustomCardMaker getCardMaker() {
		return cardMaker;
	}

	public void setCardMaker(CustomCardMaker cardMaker) {
		this.cardMaker = cardMaker;
	}
	
	class RenderTask extends SwingWorker<Void, Void> {
		
		private Card taskCard;
		private CustomCardMaker taskCardMaker = new CustomCardMaker();
		
		public RenderTask(Card c) {
			taskCard = c;
		}

		@Override
		protected Void doInBackground() throws Exception {
			
			try
			{
				taskCardMaker.setScale(cardMaker.getScale());
				taskCardMaker.setCard(taskCard);
				BufferedImage bi = taskCardMaker.generateCard();
				imageMap.put(taskCard, bi);
				
				resetPreviewPanel();
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(LegeditItemPreviewPanel.this, ex.getMessage() != null ? ex.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			}
			
			return null;
		}
	}
}
