package legedit2.decktype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import legedit2.card.Card;
import legedit2.cardtype.CardType;
import legedit2.cardtype.Style;
import legedit2.deck.Deck;
import legedit2.definitions.ItemType;

public class DeckType extends ItemType implements Cloneable {
	
	private String name;
	private String defaultStyle;
	private Boolean nameEditable = Boolean.FALSE;
	
	private static List<DeckType> deckTypes = null;
	private List<CardType> cardTypes = new ArrayList<>();	
	private List<DeckTypeAttribute> attributes = new ArrayList<>();
	
	
	public DeckType()
	{		
	}
	
	public static List<DeckType> getDeckTypes()
	{
		if (deckTypes != null)
		{
			return deckTypes;
		}
		
		deckTypes = new ArrayList<>();
		
		String templateFolder = "legedit" + File.separator + "decktypes";
		File dir = new File(templateFolder);
		if (dir.exists())
		{
			for (File f : dir.listFiles())
			{
				if (f.getName().endsWith(".xml"))
				{
					DeckType group = parseDeckType(f);
					deckTypes.add(group);
				}
			}
		}
		
		return deckTypes;
	}
	
	public static List<DeckType> reloadDeckTypes()
	{
		deckTypes = null;
		return getDeckTypes();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static DeckType parseDeckType(File structureFile)
	{
		DeckType t = new DeckType();
		
		try
		{
			File fXmlFile = structureFile;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
			
			if (doc.hasChildNodes() && doc.getChildNodes().item(0).hasChildNodes()) 
			{
				NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
				for (int count = 0; count < nodeList.getLength(); count++) {
					Node node = nodeList.item(count);
					
					if (node.getNodeName().equals("structure"))
					{	
						if (node.getAttributes().getNamedItem("name") != null)
						{
							t.setName(node.getAttributes().getNamedItem("name").getNodeValue());
						}
						
						if (node.getAttributes().getNamedItem("displayname") != null)
						{
							t.setDisplayName(node.getAttributes().getNamedItem("displayname").getNodeValue());
						}
						
						if (node.getAttributes().getNamedItem("defaultstyle") != null)
						{
							t.setDefaultStyle(node.getAttributes().getNamedItem("defaultstyle").getNodeValue());
						}
						
						if (node.getAttributes().getNamedItem("group") != null)
						{
							t.setCardGroup(node.getAttributes().getNamedItem("group").getNodeValue());
						}
						
						if (node.getAttributes().getNamedItem("nameeditable") != null)
						{
							t.setNameEditable(Boolean.parseBoolean(node.getAttributes().getNamedItem("nameeditable").getNodeValue()));
						}
					}
					
					if (node.getNodeName().equals("attributes"))
					{	
						if (node.getChildNodes() != null)
						{
							NodeList nodes = node.getChildNodes();
							for (int count1 = 0; count1 < nodes.getLength(); count1++) {
								Node node2 = nodes.item(count1);
								DeckTypeAttribute attribute = new DeckTypeAttribute();
								
								if (node2.getAttributes() != null && node2.getAttributes().getNamedItem("name") != null)
								{
									attribute.setName(node2.getAttributes().getNamedItem("name").getNodeValue());
								}
								
								if (node2.getAttributes() != null && node2.getAttributes().getNamedItem("displayname") != null)
								{
									attribute.setDisplayName(node2.getAttributes().getNamedItem("displayname").getNodeValue());
								}

								if (node2.getAttributes() != null && node2.getAttributes().getNamedItem("type") != null)
								{
									attribute.setType(node2.getAttributes().getNamedItem("type").getNodeValue());
								}
								
								// kept for backwards compatibility
								if (node2.getAttributes() != null && node2.getAttributes().getNamedItem("icontype") != null)
								{
									attribute.setValue(node2.getAttributes().getNamedItem("icontype").getNodeValue());
								}
								
								if (node2.getAttributes() != null && node2.getAttributes().getNamedItem("value") != null)
								{
									attribute.setValue(node2.getAttributes().getNamedItem("value").getNodeValue());
								}

								if (node2.getAttributes() != null && node2.getAttributes().getNamedItem("iseditable") != null)
								{
									attribute.setUserEditable(Boolean.parseBoolean(node2.getAttributes().getNamedItem("iseditable").getNodeValue()));
								}

								if (attribute.getName() != null)
								{
									t.getAttributes().add(attribute);
								}
							}
							
						}
					}
				}
				
			}
			
			for (CardType ct : CardType.getCardTypes())
			{
				if (ct.getDeck() != null && ct.getDeck().equals(t.getName()))
				{
					t.cardTypes.add(ct);
				}
			}
			
			return t;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	public List<CardType> getCardTypes() {
		return cardTypes;
	}

	public void setCardTypes(List<CardType> cardTypes) {
		this.cardTypes = cardTypes;
	}

	public String getDefaultStyle() {
		return defaultStyle;
	}

	public void setDefaultStyle(String defaultStyle) {
		this.defaultStyle = defaultStyle;
	}
	
	/////////////////////////////////////////////////////////
	// This is called whenever a new Deck needs to be created
	// The caller will specify which type of deck is meant to be created
	// This code solely checks the deckType template data
	// It does not in any way possibly check any data stored into the project file
	/////////////////////////////////////////////////////////
	public static Deck generateLegeditItem(DeckType type)
	{
		Deck cc = new Deck();
		for (DeckType t : DeckType.getDeckTypes())
		   {
			   if (t.getName().equals(type.getName()))
			   {
				   cc.setTemplateName(type.getName());
				   cc.setTemplate(t.getCopy());
				   cc.setName(type.getDisplayName());
				   break;
			   }
		   }
		   if (cc.getTemplate() == null)
		   {
			   throw new RuntimeException("Template not found: " + type.getName());
		   }
		   
		   for (CardType ct : cc.getTemplate().getCardTypes())
		   {
			   if (ct.isDefaultsInDeck())
			   {
				   for (int i = 0; i < ct.getDefaultCopiesInDeck(); i++)
				   {
					   Card c = CardType.generateLegeditItem(ct);
					   c.setOwner(cc);
					   cc.getCards().add(c);
					   
					   if (cc.getTemplate().getDefaultStyle() != null 
							   && c.getTemplate() != null
							   && c.getTemplate().getStyles() != null)
					   {
						   for (Style s : c.getTemplate().getStyles())
						   {
							   if (s.getName().equalsIgnoreCase(cc.getTemplate().getDefaultStyle()))
							   {
								   c.getTemplate().setStyle(s);
							   }
						   }
					   }
				   }
			   }
		   }
		return cc;
	}
	
	public DeckType getCopy()
	{
		try {
			DeckType template = (DeckType) clone();
			template.setCardTypes(cardTypes);
			
			return template;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public Boolean getNameEditable() {
		return nameEditable;
	}

	public void setNameEditable(Boolean nameEditable) {
		this.nameEditable = nameEditable;
	}

	public List<DeckTypeAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<DeckTypeAttribute> attributes) {
		this.attributes = attributes;
	}
}
