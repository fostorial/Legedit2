package legedit2.helpers;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import legedit2.card.Card;
import legedit2.cardtype.CardType;
import legedit2.cardtype.CustomElement;
import legedit2.cardtype.CustomProperties;
import legedit2.cardtype.ElementBackgroundImage;
import legedit2.cardtype.ElementCardName;
import legedit2.cardtype.ElementIcon;
import legedit2.cardtype.ElementImage;
import legedit2.cardtype.ElementProperty;
import legedit2.cardtype.ElementText;
import legedit2.cardtype.ElementTextArea;
import legedit2.cardtype.Style;
import legedit2.cardtype.ElementCardName.HIGHLIGHT;
import legedit2.deck.Deck;
import legedit2.decktype.DeckType;
import legedit2.definitions.Icon;
import legedit2.definitions.LegeditItem;
import legedit2.definitions.Icon.ICON_TYPE;
import legedit2.gui.LegeditFrame;
import legedit2.gui.project.CardTypeSelectionPanel;
import legedit2.imaging.CustomCardMaker;

public class ProjectHelper {
	
	private static File currentFile = null;
	
	private static List<Card> cards = new ArrayList<>();
	private static List<Deck> decks = new ArrayList<>();
	
	public static void resetProject()
	{
		currentFile = null;
		cards = new ArrayList<>();
		decks = new ArrayList<>();
		
		if (CardTypeSelectionPanel.getCardListModelStatic() != null)
		{
			CardTypeSelectionPanel.getCardListModelStatic().clear();
		}
	}
	
	public static List<Card> getCards()
	{
		return cards;
	}
	
	public static void setCards(List<Card> cards)
	{
		ProjectHelper.cards = cards;
	}

	public static List<LegeditItem> getLegeditItems()
	{
		List<LegeditItem> items = new ArrayList<>();
		items.addAll(getDecks());
		items.addAll(getCards());
		
		return items;
	}
	
	public static void addLegeditItem(LegeditItem item)
	{
		if (item instanceof Deck)
		{
			getDecks().add((Deck)item);
		}
		if (item instanceof Card)
		{
			getCards().add((Card)item);
		}
	}
	
	public static void deleteLegeditItem(LegeditItem item)
	{
		if (item instanceof Deck)
		{
			getDecks().remove((Deck)item);
		}
		if (item instanceof Card)
		{
			getCards().remove((Card)item);
		}
	}
	
	public static void loadProject(File inputFile)
	{
		resetProject();
		
		BufferedReader br = null;
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			
			doc.getDocumentElement().normalize();
			
			if (doc.hasChildNodes() && doc.getChildNodes().item(0).hasChildNodes()) 
			{
				NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
				for (int count = 0; count < nodeList.getLength(); count++) {
					Node node = nodeList.item(count);
					
					if (node.getNodeName().equals("legedititems"))
					{
						for (int count1 = 0; count1 < node.getChildNodes().getLength(); count1++) {
							Node node2 = node.getChildNodes().item(count1);
							if (node2.getNodeName().equals("deck"))
							{	
								parseDeck(node2);
							}
							
							if (node2.getNodeName().equals("card"))
							{	
								parseCard(node2, null);
							}
						}
					}
					
					
				}
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(LegeditFrame.legedit, e.getMessage() != null ? e.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			setCurrentFile(null);
		}
		finally {
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (Exception e)
				{
					//TODO Error Handling
					e.printStackTrace();
				}
			}
		}
		
		setCurrentFile(inputFile);
		
		LegeditFrame.refreshGUI();
	}
	
	public static void newProject()
	{
		resetProject();
		
		setCurrentFile(null);
		
		LegeditHelper.getProperty(legedit2.helpers.LegeditHelper.PROPERTIES.lastExpansion);
		LegeditHelper.setProperty(legedit2.helpers.LegeditHelper.PROPERTIES.lastExpansion, null);
		
		LegeditFrame.refreshGUI();
	}
	
	private static void parseDeck(Node node)
	{
		Deck deck = new Deck();
		
		if (node.getAttributes().getNamedItem("name") != null)
		{
			deck.setName(node.getAttributes().getNamedItem("name").getNodeValue());
		}
		
		if (node.getAttributes().getNamedItem("template") != null)
		{
			for (DeckType type : DeckType.getDeckTypes())
			{
				if (type.getName().equals(node.getAttributes().getNamedItem("template").getNodeValue()))
				{
					deck.setTemplateName(type.getName());
					deck.setTemplate(type.getCopy());
					break;
				}
			}
		}
		
		for (int count = 0; count < node.getChildNodes().getLength(); count++) {
			Node node1 = node.getChildNodes().item(count);
			
			if (node1.getNodeName().equals("cards"))
			{
				for (int count1 = 0; count1 < node1.getChildNodes().getLength(); count1++) {
					Node node2 = node1.getChildNodes().item(count1);
					if (node2.getNodeName().equals("card"))
					{
						parseCard(node2, deck);
					}
				}
			}
		}
		
		addLegeditItem(deck);
	}
	
	private static void parseCard(Node node, Deck deck)
	{
		Card card = new Card();
		
		if (node.getAttributes().getNamedItem("template") != null)
		{
			for (CardType type : CardType.getCardTypes())
			{
				if (type.getName().equals(node.getAttributes().getNamedItem("template").getNodeValue()))
				{
					card.setTemplateName(type.getName());
					card.setTemplate(type.getCopy());
					break;
				}
			}
		}
		
		for (int count = 0; count < node.getChildNodes().getLength(); count++) {
			Node node1 = node.getChildNodes().item(count);
			
			if (node1.getNodeName().equals("template"))
			{
				/* Parse Template Elements */
				for (int count1 = 0; count1 < node1.getChildNodes().getLength(); count1++) {
					Node node2 = node1.getChildNodes().item(count1);
					
					if (Arrays.asList(CustomElement.elementTypes).contains(node2.getNodeName()))
					{
						for (CustomElement e : card.getTemplate().elements)
						{
							if (node2.getAttributes().getNamedItem("name") != null
									&& e.name != null
									&& e.name.equals(node2.getAttributes().getNamedItem("name").getNodeValue()))
							{
								e.loadValues(node2, card);
							}
						}	
					}
					
					if (node2.getNodeName().equals("styles"))
					{
						/* Deal with Style */
						for (int count2 = 0; count2 < node2.getChildNodes().getLength(); count2++) {
							Node node3 = node2.getChildNodes().item(count2);
							if (node3.getNodeName().equals("style"))
							{
								if (card.getTemplate() != null && node3.getAttributes().getNamedItem("name") != null)
								{
									for (Style s : card.getTemplate().getStyles())
									{
										if (s.getName().equals(node3.getAttributes().getNamedItem("name").getNodeValue()))
										{
											/* Style Found */
											for (int count3 = 0; count3 < node3.getChildNodes().getLength(); count3++) {
												Node node4 = node3.getChildNodes().item(count3);
												if (Arrays.asList(CustomElement.elementTypes).contains(node4.getNodeName()))
												{
													for (CustomElement e : s.getElements())
													{
														if (node4.getAttributes().getNamedItem("name") != null
																&& e.name != null
																&& e.name.equals(node4.getAttributes().getNamedItem("name").getNodeValue()))
														{
															e.loadValues(node4, card);
														}
													}											
												}
											}
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (deck == null)
		{
			addLegeditItem(card);
		}
		else
		{
			deck.getCards().add(card);
		}
	}
	
	

	public static File getCurrentFile() {
		return currentFile;
	}

	public static void setCurrentFile(File currentFile) {
		ProjectHelper.currentFile = currentFile;
	}

	public static List<Deck> getDecks() {
		return decks;
	}

	public static void setDecks(List<Deck> decks) {
		ProjectHelper.decks = decks;
	}
	
	public static int getDistinctCardCount()
	{
		int value = 0;
		
		for (LegeditItem i : getLegeditItems())
		{
			value += i.getDistinctCardCount();
		}
		
		return value;
	}
	
	public static int getTotalCardCount()
	{
		int value = 0;
		
		for (LegeditItem i : getLegeditItems())
		{
			value += i.getTotalCardCount();
		}
		
		return value;
	}

	public static void saveProject(File saveFile)
	{
		FileWriter fw = null;
		try
		{
			String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
			str += "<xml>\n\n";
			
			str += "<legedititems>\n";
			for (LegeditItem item : ProjectHelper.getLegeditItems())
			{
				str += item.getDifferenceXML() + "\n\n";
			}
			str += "</legedititems>\n\n";
			
			str += "</xml>";
			
			fw = new FileWriter(saveFile);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(str);
			
			bw.close();
			fw.close();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(LegeditFrame.legedit, e.getMessage() != null ? e.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			setCurrentFile(null);
		}
		finally {
			if (fw != null)
			{
				try
				{
					fw.close();
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(LegeditFrame.legedit, e.getMessage() != null ? e.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}
	}
}
