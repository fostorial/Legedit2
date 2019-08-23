package legedit2.cardtype;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import legedit2.card.Card;
import legedit2.cardtype.ElementCardName.HIGHLIGHT;
import legedit2.definitions.Icon;
import legedit2.definitions.Icon.ICON_TYPE;
import legedit2.imaging.CustomCardMaker;
import legedit2.definitions.ItemType;

public class CardType extends ItemType implements Cloneable {

	private String templateName;
	
	public HashMap<String, CustomElement> elementsHash = new HashMap<String, CustomElement>();
	public List<CustomElement> elements = new ArrayList<CustomElement>();
	private List<Style> styles = new ArrayList<>();
	
	public static List<CardType> cardTypes = null;
	
	public static List<CardType> failedTemplates = new ArrayList<>();
	private Exception exception;
	
	private boolean defaultsInDeck = false;
	private int defaultCopiesInDeck = 1;
	private String deck;
	
	private Style style;
	
	public String getName()
	{
		return templateName;
	}
	
	public void addElement(CustomElement element, Style s, ElementGroup group)
	{
		if (group != null)
		{
			group.getElements().add(element);
			group.getElementsHash().put(element.name, element);
		}
		else if (s == null)
		{
			elementsHash.put(element.name, element);
			elements.add(element);
		}
		else
		{
			s.getElements().add(element);
			s.getElementsHash().put(element.name, element);
		}
	}
	
	public static List<CardType> getCardTypes()
	{
		if (cardTypes != null)
		{
			return cardTypes;
		}
		
		cardTypes = new ArrayList<>();
		failedTemplates = new ArrayList<>();
		
		String templateFolder = "legedit" + File.separator + "cardtypes";
		File dir = new File(templateFolder);
		if (dir.exists())
		{
			for (File f : dir.listFiles())
			{
				if (f.isDirectory())
				{
					for (File f2 : f.listFiles())
					{
						if (f2.getName().toLowerCase().endsWith(".xml"))
						{
							CardType group = parseCardType(f2);
							if (group != null)
							{
								cardTypes.add(group);
							}
						}						
					}
				}
			}
		}
		
		return cardTypes;
	}
	
	public static List<CardType> reloadCardTypes()
	{
		cardTypes = null;
		return getCardTypes();
	}
	
	public static CardType parseCardType(File structureFile)
	{
		CardType t = new CardType();
		
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
					
					parseNode(node, t, null, null);
				}
				
			}
			
			if (t.getStyles().size() > 0)
			{
				t.setStyle(t.getStyles().get(0));
			}
			
			return t;
		}
		catch (Exception e)
		{
			CardType t2 = new CardType();
			t2.setName(structureFile.getName().replace(".xml", ""));
			t2.setException(e);
			failedTemplates.add(t2);
		}
		
		return null;
	}
	
	public static void parseStyles(Node node, CardType t) throws Exception
	{
		NodeList nodeList = node.getChildNodes();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node node2 = nodeList.item(count);
			
			if (node2.getNodeName().equals("style"))
			{
				parseStyle(node2, t);
			}
		}
	}
	
	public static void parseStyle(Node node, CardType t) throws Exception
	{
		Style style = new Style();
		if (node.getNodeName().equals("style"))
		{
			if (node.getAttributes().getNamedItem("name") != null)
			{
				style.setName(node.getAttributes().getNamedItem("name").getNodeValue());
			}
		}
		
		NodeList nodeList = node.getChildNodes();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node node2 = nodeList.item(count);
			
			parseNode(node2, t, style, null);
		}
		
		t.getStyles().add(style);
	}
	
	public static void parseNode(Node node, CardType t, Style s, ElementGroup group) throws Exception
	{
		if (node.getNodeName().equals("styles"))
		{
			parseStyles(node, t);
		}
		
		if (node.getNodeName().equals("template"))
		{	
			if (node.getAttributes().getNamedItem("name") != null)
			{
				t.setTemplateName(node.getAttributes().getNamedItem("name").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("displayname") != null)
			{
				t.setTemplateDisplayName(node.getAttributes().getNamedItem("displayname").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("tab") != null)
			{
				t.setCardGroup(node.getAttributes().getNamedItem("tab").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("group") != null)
			{
				t.setCardGroup(node.getAttributes().getNamedItem("group").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("deck") != null)
			{
				t.setDeck(node.getAttributes().getNamedItem("deck").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("defaultsindeck") != null)
			{
				t.setDefaultsInDeck(Boolean.parseBoolean(node.getAttributes().getNamedItem("defaultsindeck").getNodeValue()));
			}
			if (node.getAttributes().getNamedItem("defaultcopies") != null)
			{
				t.setDefaultCopiesInDeck(Integer.parseInt(node.getAttributes().getNamedItem("defaultcopies").getNodeValue()));
			}
		}
		
		if (node.getNodeName().equals("cardsize"))
		{
			if (node.getAttributes().getNamedItem("cardwidth") != null)
			{
				CustomCardMaker.cardWidth = Integer.parseInt(node.getAttributes().getNamedItem("cardwidth").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("cardheight") != null)
			{
				CustomCardMaker.cardHeight = Integer.parseInt(node.getAttributes().getNamedItem("cardheight").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("dpi") != null)
			{
				CustomCardMaker.dpi = Integer.parseInt(node.getAttributes().getNamedItem("dpi").getNodeValue());
			}
		}
		
		if (node.getNodeName().equals("bgimage"))
		{
			ElementBackgroundImage element = new ElementBackgroundImage();
			element.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				element.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("path") != null)
			{
				element.path = node.getAttributes().getNamedItem("path").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("allowchange") != null)
			{
				element.allowChange = Boolean.parseBoolean(node.getAttributes().getNamedItem("allowchange").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("x") != null)
			{
				element.x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("y") != null)
			{
				element.y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("maxwidth") != null)
			{
				element.maxWidth = Integer.parseInt(node.getAttributes().getNamedItem("maxwidth").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("maxheight") != null)
			{
				element.maxHeight = Integer.parseInt(node.getAttributes().getNamedItem("maxheight").getNodeValue());
			}
			
			
			if (node.getAttributes().getNamedItem("zoomable") != null)
			{
				element.zoomable = Boolean.parseBoolean(node.getAttributes().getNamedItem("zoomable").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("fullsize") != null)
			{
				element.fullSize = Boolean.parseBoolean(node.getAttributes().getNamedItem("fullsize").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("templatefile") != null)
			{
				element.templateFile = Boolean.parseBoolean(node.getAttributes().getNamedItem("templatefile").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				element.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("rotate") != null)
			{
				element.rotate = Integer.parseInt(node.getAttributes().getNamedItem("rotate").getNodeValue());
			}
			
			t.addElement(element, s, group);
		}
		
		if (node.getNodeName().equals("icon"))
		{
			ElementIcon elementIcon = new ElementIcon();
			elementIcon.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				elementIcon.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("defaultvalue") != null)
			{
				elementIcon.defaultValue = Icon.valueOf(node.getAttributes().getNamedItem("defaultvalue").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("icontype") != null)
			{
				elementIcon.iconType = ICON_TYPE.valueOf(node.getAttributes().getNamedItem("icontype").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("allowchange") != null)
			{
				elementIcon.allowChange = Boolean.parseBoolean(node.getAttributes().getNamedItem("allowchange").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("optional") != null)
			{
				elementIcon.optional = Boolean.parseBoolean(node.getAttributes().getNamedItem("optional").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("x") != null)
			{
				elementIcon.x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("y") != null)
			{
				elementIcon.y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("maxwidth") != null)
			{
				elementIcon.maxWidth = Integer.parseInt(node.getAttributes().getNamedItem("maxwidth").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("maxheight") != null)
			{
				elementIcon.maxHeight = Integer.parseInt(node.getAttributes().getNamedItem("maxheight").getNodeValue());
			}
			
			
			if (node.getAttributes().getNamedItem("drawunderlay") != null)
			{
				elementIcon.drawUnderlay = Boolean.parseBoolean(node.getAttributes().getNamedItem("drawunderlay").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurradius") != null)
			{
				elementIcon.blurRadius = Integer.parseInt(node.getAttributes().getNamedItem("blurradius").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("bluedouble") != null)
			{
				elementIcon.blurDouble = Boolean.parseBoolean(node.getAttributes().getNamedItem("bluedouble").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurexpand") != null)
			{
				elementIcon.blurExpand = Integer.parseInt(node.getAttributes().getNamedItem("blurexpand").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurcolour") != null)
			{
				elementIcon.blurColour = Color.decode(node.getAttributes().getNamedItem("blurcolour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				elementIcon.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("rotate") != null)
			{
				elementIcon.rotate = Integer.parseInt(node.getAttributes().getNamedItem("rotate").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("valuefrom") != null)
			{
				elementIcon.valueFrom = node.getAttributes().getNamedItem("valuefrom").getNodeValue();
			}
			
			t.addElement(elementIcon, s, group);
		}
		
		if (node.getNodeName().equals("iconbg"))
		{
			ElementIconImage elementIcon = new ElementIconImage();
			elementIcon.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				elementIcon.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("defaultvalue") != null)
			{
				elementIcon.defaultValue = Icon.valueOf(node.getAttributes().getNamedItem("defaultvalue").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("icontype") != null)
			{
				elementIcon.iconType = ICON_TYPE.valueOf(node.getAttributes().getNamedItem("icontype").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("allowchange") != null)
			{
				elementIcon.allowChange = Boolean.parseBoolean(node.getAttributes().getNamedItem("allowchange").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("optional") != null)
			{
				elementIcon.optional = Boolean.parseBoolean(node.getAttributes().getNamedItem("optional").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("x") != null)
			{
				elementIcon.x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("y") != null)
			{
				elementIcon.y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("maxwidth") != null)
			{
				elementIcon.maxWidth = Integer.parseInt(node.getAttributes().getNamedItem("maxwidth").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("maxheight") != null)
			{
				elementIcon.maxHeight = Integer.parseInt(node.getAttributes().getNamedItem("maxheight").getNodeValue());
			}
			
			
			if (node.getAttributes().getNamedItem("drawunderlay") != null)
			{
				elementIcon.drawUnderlay = Boolean.parseBoolean(node.getAttributes().getNamedItem("drawunderlay").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurradius") != null)
			{
				elementIcon.blurRadius = Integer.parseInt(node.getAttributes().getNamedItem("blurradius").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("bluedouble") != null)
			{
				elementIcon.blurDouble = Boolean.parseBoolean(node.getAttributes().getNamedItem("bluedouble").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurexpand") != null)
			{
				elementIcon.blurExpand = Integer.parseInt(node.getAttributes().getNamedItem("blurexpand").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurcolour") != null)
			{
				elementIcon.blurColour = Color.decode(node.getAttributes().getNamedItem("blurcolour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				elementIcon.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("rotate") != null)
			{
				elementIcon.rotate = Integer.parseInt(node.getAttributes().getNamedItem("rotate").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("imagex") != null)
			{
				elementIcon.imageX = Integer.parseInt(node.getAttributes().getNamedItem("imagex").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("imagey") != null)
			{
				elementIcon.imageY = Integer.parseInt(node.getAttributes().getNamedItem("imagey").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("imagemaxwidth") != null)
			{
				elementIcon.imageMaxWidth = Integer.parseInt(node.getAttributes().getNamedItem("imagemaxwidth").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("imagemaxheight") != null)
			{
				elementIcon.imageMaxHeight = Integer.parseInt(node.getAttributes().getNamedItem("imagemaxheight").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("imageprefix") != null)
			{
				elementIcon.imagePrefix = node.getAttributes().getNamedItem("imageprefix").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("imageextension") != null)
			{
				elementIcon.imageExtension = node.getAttributes().getNamedItem("imageextension").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("imagefilter") != null)
			{
				elementIcon.imageFilter = node.getAttributes().getNamedItem("imagefilter").getNodeValue();
			}
			
			t.addElement(elementIcon, s, group);
		}
		
		if (node.getNodeName().equals("image"))
		{
			ElementImage element = new ElementImage();
			element.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				element.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("path") != null)
			{
				element.path = node.getAttributes().getNamedItem("path").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("allowchange") != null)
			{
				element.allowChange = Boolean.parseBoolean(node.getAttributes().getNamedItem("allowchange").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("x") != null)
			{
				element.x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("y") != null)
			{
				element.y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("maxwidth") != null)
			{
				element.maxWidth = Integer.parseInt(node.getAttributes().getNamedItem("maxwidth").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("maxheight") != null)
			{
				element.maxHeight = Integer.parseInt(node.getAttributes().getNamedItem("maxheight").getNodeValue());
			}
			
			
			if (node.getAttributes().getNamedItem("zoomable") != null)
			{
				element.zoomable = Boolean.parseBoolean(node.getAttributes().getNamedItem("zoomable").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("fullsize") != null)
			{
				element.fullSize = Boolean.parseBoolean(node.getAttributes().getNamedItem("fullsize").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("templatefile") != null)
			{
				element.templateFile = Boolean.parseBoolean(node.getAttributes().getNamedItem("templatefile").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				element.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("rotate") != null)
			{
				element.rotate = Integer.parseInt(node.getAttributes().getNamedItem("rotate").getNodeValue());
			}
			
			t.addElement(element, s, group);
		}
	
		if (node.getNodeName().equals("property"))
		{
			ElementProperty element = new ElementProperty();
			element.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				element.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("defaultvalue") != null)
			{
				element.defaultValue = node.getAttributes().getNamedItem("defaultvalue").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("property") != null)
			{
				element.property = CustomProperties.valueOf(node.getAttributes().getNamedItem("property").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				element.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			t.addElement(element, s, group);
		}
		
		if (node.getNodeName().equals("text"))
		{
			ElementText element = new ElementText();
			element.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				element.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("defaultvalue") != null)
			{
				element.defaultValue = node.getAttributes().getNamedItem("defaultvalue").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("linkedelement") != null)
			{
				element.linkedElement = t.elementsHash.get(node.getAttributes().getNamedItem("linkedelement").getNodeValue());
				if (element.linkedElement != null)
				{
					element.linkedElement.childElements.add(element);
				}
			}
			
			if (node.getAttributes().getNamedItem("allowchange") != null)
			{
				element.allowChange = Boolean.parseBoolean(node.getAttributes().getNamedItem("allowchange").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("x") != null)
			{
				element.x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("y") != null)
			{
				element.y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("colour") != null)
			{
				element.colour = Color.decode(node.getAttributes().getNamedItem("colour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("drawunderlay") != null)
			{
				element.drawUnderlay = Boolean.parseBoolean(node.getAttributes().getNamedItem("drawunderlay").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurradius") != null)
			{
				element.blurRadius = Integer.parseInt(node.getAttributes().getNamedItem("blurradius").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("bluedouble") != null)
			{
				element.blurDouble = Boolean.parseBoolean(node.getAttributes().getNamedItem("bluedouble").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurexpand") != null)
			{
				element.blurExpand = Integer.parseInt(node.getAttributes().getNamedItem("blurexpand").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurcolour") != null)
			{
				element.blurColour = Color.decode(node.getAttributes().getNamedItem("blurcolour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("textsize") != null)
			{
				element.textSize = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("uppercase") != null)
			{
				element.uppercase = Boolean.parseBoolean(node.getAttributes().getNamedItem("uppercase").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("fontname") != null)
			{
				element.fontName = node.getAttributes().getNamedItem("fontname").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("fontstyle") != null)
			{
				element.fontStyle = Integer.parseInt(node.getAttributes().getNamedItem("fontstyle").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("textsize") != null)
			{
				element.textSize = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("alignment") != null)
			{
				element.alignment = ALIGNMENT.valueOf(node.getAttributes().getNamedItem("alignment").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				element.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("rotate") != null)
			{
				element.rotate = Integer.parseInt(node.getAttributes().getNamedItem("rotate").getNodeValue());
			}
			
			t.addElement(element, s, group);
		}
		
		if (node.getNodeName().equals("cardname"))
		{
			ElementCardName element = new ElementCardName();
			element.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				element.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("defaultvalue") != null)
			{
				element.defaultValue = node.getAttributes().getNamedItem("defaultvalue").getNodeValue();
			}

			if (node.getAttributes().getNamedItem("nameprefix") != null)
			{
				element.setNamePrefix(node.getAttributes().getNamedItem("nameprefix").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("namesuffix") != null)
			{
				element.setNameSuffix(node.getAttributes().getNamedItem("namesuffix").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("includesubname") != null)
			{
				element.includeSubname = Boolean.parseBoolean(node.getAttributes().getNamedItem("includesubname").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("subnametext") != null)
			{
				element.subnameText = node.getAttributes().getNamedItem("subnametext").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("subnameprefix") != null)
			{
				element.setSubnamePrefix(node.getAttributes().getNamedItem("subnameprefix").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("subnamesuffix") != null)
			{
				element.setSubnamePrefix(node.getAttributes().getNamedItem("subnamesuffix").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("subnameeditable") != null)
			{
				element.subnameEditable = Boolean.parseBoolean(node.getAttributes().getNamedItem("subnameeditable").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("allowchange") != null)
			{
				element.allowChange = Boolean.parseBoolean(node.getAttributes().getNamedItem("allowchange").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("x") != null)
			{
				element.x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("y") != null)
			{
				element.y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("colour") != null)
			{
				element.colour = Color.decode(node.getAttributes().getNamedItem("colour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("subnamecolour") != null)
			{
				element.subNameColour = Color.decode(node.getAttributes().getNamedItem("subnamecolour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("drawunderlay") != null)
			{
				element.drawUnderlay = Boolean.parseBoolean(node.getAttributes().getNamedItem("drawunderlay").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurradius") != null)
			{
				element.blurRadius = Integer.parseInt(node.getAttributes().getNamedItem("blurradius").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("bluedouble") != null)
			{
				element.blurDouble = Boolean.parseBoolean(node.getAttributes().getNamedItem("bluedouble").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("blurexpand") != null)
			{
				element.blurExpand = Integer.parseInt(node.getAttributes().getNamedItem("blurexpand").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("highlightcolour") != null)
			{
				element.highlightColour = Color.decode(node.getAttributes().getNamedItem("highlightcolour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("uppercase") != null)
			{
				element.uppercase = Boolean.parseBoolean(node.getAttributes().getNamedItem("uppercase").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("fontname") != null)
			{
				element.fontName = node.getAttributes().getNamedItem("fontname").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("textsize") != null)
			{
				element.textSize = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("subnamesize") != null)
			{
				element.subnameSize = Integer.parseInt(node.getAttributes().getNamedItem("subnamesize").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("alignment") != null)
			{
				element.alignment = ALIGNMENT.valueOf(node.getAttributes().getNamedItem("alignment").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("highlight") != null)
			{
				element.highlight = HIGHLIGHT.valueOf(node.getAttributes().getNamedItem("highlight").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				element.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("subnamegap") != null)
			{
				element.subnameGap = Integer.parseInt(node.getAttributes().getNamedItem("subnamegap").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("rotate") != null)
			{
				element.rotate = Integer.parseInt(node.getAttributes().getNamedItem("rotate").getNodeValue());
			}
			
			t.addElement(element, s, group);
		}
		
		if (node.getNodeName().equals("textarea"))
		{
			ElementTextArea element = new ElementTextArea();
			element.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				element.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("defaultvalue") != null)
			{
				element.defaultValue = node.getAttributes().getNamedItem("defaultvalue").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("allowchange") != null)
			{
				element.allowChange = Boolean.parseBoolean(node.getAttributes().getNamedItem("allowchange").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("colour") != null)
			{
				element.colour = Color.decode(node.getAttributes().getNamedItem("colour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("fontname") != null)
			{
				element.fontName = node.getAttributes().getNamedItem("fontname").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("fontnamebold") != null)
			{
				element.fontNameBold = node.getAttributes().getNamedItem("fontnamebold").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("textsize") != null)
			{
				element.textSize = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
				element.textSizeBold = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("textsizebold") != null)
			{
				element.textSizeBold = Integer.parseInt(node.getAttributes().getNamedItem("textsizebold").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("alignmenthorizontal") != null)
			{
				element.alignmentHorizontal = ALIGNMENT.valueOf(node.getAttributes().getNamedItem("alignmenthorizontal").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("alignmentvertical") != null)
			{
				element.alignmentVertical = ALIGNMENT.valueOf(node.getAttributes().getNamedItem("alignmentvertical").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("rectxarray") != null)
			{
				element.rectXArray = node.getAttributes().getNamedItem("rectxarray").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("rectyarray") != null)
			{
				element.rectYArray = node.getAttributes().getNamedItem("rectyarray").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("debug") != null)
			{
				element.debug = Boolean.parseBoolean(node.getAttributes().getNamedItem("debug").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				element.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("rotate") != null)
			{
				element.rotate = Integer.parseInt(node.getAttributes().getNamedItem("rotate").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("spacebetweenlines") != null)
			{
				element.gapSizeBetweenLines = Double.parseDouble(node.getAttributes().getNamedItem("spacebetweenlines").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("spacebetweenparagraphs") != null)
			{
				element.gapSizeBetweenParagraphs = Double.parseDouble(node.getAttributes().getNamedItem("spacebetweenparagraphs").getNodeValue());
			}

			t.addElement(element, s, group);
		}
		
		if (node.getNodeName().equals("scrollingtextarea"))
		{
			ElementScrollingTextArea element = new ElementScrollingTextArea();
			element.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				element.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}

			if (node.getAttributes().getNamedItem("headertext") != null)
			{
				element.headerText = node.getAttributes().getNamedItem("headertext").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("defaultvalue") != null)
			{
				element.defaultValue = node.getAttributes().getNamedItem("defaultvalue").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("allowchange") != null)
			{
				element.allowChange = Boolean.parseBoolean(node.getAttributes().getNamedItem("allowchange").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("colour") != null)
			{
				element.colour = Color.decode(node.getAttributes().getNamedItem("colour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("fontname") != null)
			{
				element.fontName = node.getAttributes().getNamedItem("fontname").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("fontnamebold") != null)
			{
				element.fontNameBold = node.getAttributes().getNamedItem("fontnamebold").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("fontstyle") != null)
			{
				element.fontStyle = Integer.parseInt(node.getAttributes().getNamedItem("fontstyle").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("textsize") != null)
			{
				element.textSize = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
				element.textSizeBold = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("textsizebold") != null)
			{
				element.textSizeBold = Integer.parseInt(node.getAttributes().getNamedItem("textsizebold").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("alignmenthorizontal") != null)
			{
				element.alignmentHorizontal = ALIGNMENT.valueOf(node.getAttributes().getNamedItem("alignmenthorizontal").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("alignmentvertical") != null)
			{
				element.alignmentVertical = ALIGNMENT.valueOf(node.getAttributes().getNamedItem("alignmentvertical").getNodeValue().toUpperCase());
			}
			
			if (node.getAttributes().getNamedItem("startx") != null)
			{
				element.startX = Integer.parseInt(node.getAttributes().getNamedItem("startx").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("endx") != null)
			{
				element.endX = Integer.parseInt(node.getAttributes().getNamedItem("endx").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("starty") != null)
			{
				element.startY = Integer.parseInt(node.getAttributes().getNamedItem("starty").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("endy") != null)
			{
				element.endY = Integer.parseInt(node.getAttributes().getNamedItem("endy").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("direction") != null)
			{
				element.direction = node.getAttributes().getNamedItem("direction").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("debug") != null)
			{
				element.debug = Boolean.parseBoolean(node.getAttributes().getNamedItem("debug").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				element.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("rotate") != null)
			{
				element.rotate = Integer.parseInt(node.getAttributes().getNamedItem("rotate").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("headercolour") != null)
			{
				element.headerColour = Color.decode(node.getAttributes().getNamedItem("headercolour").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("spacebetweenlines") != null)
			{
				element.gapSizeBetweenLines = Double.parseDouble(node.getAttributes().getNamedItem("spacebetweenlines").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("spacebetweenparagraphs") != null)
			{
				element.gapSizeBetweenParagraphs = Double.parseDouble(node.getAttributes().getNamedItem("spacebetweenparagraphs").getNodeValue());
			}
			

			/* Attach a BG image */
			ElementBackgroundImage bg = new ElementBackgroundImage();
			bg.template = t;

			if (node.getAttributes().getNamedItem("imagename") != null)
			{
				bg.name = node.getAttributes().getNamedItem("imagename").getNodeValue();
			}

			if (node.getAttributes().getNamedItem("imagepath") != null)
			{
				bg.path = node.getAttributes().getNamedItem("imagepath").getNodeValue();
			}

			if (node.getAttributes().getNamedItem("imageallowchange") != null)
			{
				bg.allowChange = Boolean.parseBoolean(node.getAttributes().getNamedItem("imageallowchange").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("imagex") != null)
			{
				bg.x = Integer.parseInt(node.getAttributes().getNamedItem("imagex").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("imagey") != null)
			{
				bg.y = Integer.parseInt(node.getAttributes().getNamedItem("imagey").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("imagemaxwidth") != null)
			{
				bg.maxWidth = Integer.parseInt(node.getAttributes().getNamedItem("imagemaxwidth").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("imagemaxheight") != null)
			{
				bg.maxHeight = Integer.parseInt(node.getAttributes().getNamedItem("imagemaxheight").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("imagezoomable") != null)
			{
				bg.zoomable = Boolean.parseBoolean(node.getAttributes().getNamedItem("imagezoomable").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("imagefullsize") != null)
			{
				bg.fullSize = Boolean.parseBoolean(node.getAttributes().getNamedItem("imagefullsize").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("imagetemplatefile") != null)
			{
				bg.templateFile = Boolean.parseBoolean(node.getAttributes().getNamedItem("imagetemplatefile").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("imagevisible") != null)
			{
				bg.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("imagevisible").getNodeValue());
			}

			if (node.getAttributes().getNamedItem("imagerotate") != null)
			{
				bg.rotate = Integer.parseInt(node.getAttributes().getNamedItem("imagerotate").getNodeValue());
			}

			if (bg.path != null)
			{
				element.setBg(bg);
			}
			
			t.addElement(element, s, group);
		}
		
		if (node.getNodeName().equals("elementgroup"))
		{
			ElementGroup element = new ElementGroup();
			element.template = t;
			
			if (node.getAttributes().getNamedItem("name") != null)
			{
				element.name = node.getAttributes().getNamedItem("name").getNodeValue();
			}
			
			if (node.getAttributes().getNamedItem("visible") != null)
			{
				element.visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
			}
			
			if (node.getAttributes().getNamedItem("rotate") != null)
			{
				element.rotate = Integer.parseInt(node.getAttributes().getNamedItem("rotate").getNodeValue());
			}
			
			for (int i = 0; i < node.getChildNodes().getLength(); i++)
			{
				parseNode(node.getChildNodes().item(i), t, s, element);
			}
			
			t.addElement(element, s, group);
		}
	}
	
	public CardType getCopy()
	{
		try {
			CardType template = (CardType) clone();
			List<CustomElement> elements = new ArrayList<CustomElement>();
			for (CustomElement e : template.elements)
			{
				elements.add(e.getCopy(template));
			}
			
			for (CustomElement e : elements)
			{
				if (e.childElements != null && e.childElements.size() > 0)
				{
					List<CustomElement> newChildElements = new ArrayList<CustomElement>();
					for (CustomElement ce : e.childElements)
					{
						for (CustomElement ne : elements)
						{
							if (ne.name.equals(ce.name))
							{
								newChildElements.add(ne);
							}
						}
					}
					e.childElements = newChildElements;
				}
			}
			template.elements = elements;
			
			List<Style> styleList = new ArrayList<>();
			for (Style s : styles)
			{
				Style copy = s.getCopy(template);
				styleList.add(copy);
				if (getStyle() != null && s.getName() != null && s.getName().equals(getStyle().getName()))
				{
					template.setStyle(copy);
				}
				
			}
			template.styles = styleList;
			
			
			
			return template;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	public CustomElement getElement(String name)
	{
		for (CustomElement e : elements)
		{
			if (e.name.equals(name))
			{
				return e;
			}
		}
		return null;
	}
	
	public ElementProperty getProperty(CustomProperties property)
	{
		for (CustomElement ce : elements)
		{
			if (ce instanceof ElementProperty && ((ElementProperty)ce).property != null && ((ElementProperty)ce).property.equals(property))
			{
				return ((ElementProperty)ce);
			}
		}
		return null;
	}
	
	public String getTemplateDisplayName() {
		return getDisplayName();
	}

	public void setTemplateDisplayName(String templateDisplayName) {
		this.setDisplayName(templateDisplayName);
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	public static Card generateLegeditItem(CardType type)
	{
		Card cc = new Card();
		for (CardType t : CardType.getCardTypes())
		   {
			   if (t.getTemplateName().equals(type.getTemplateName()))
			   {
				   cc.setTemplateName(type.getTemplateName());
				   cc.setTemplate(t.getCopy());
				   break;
			   }
		   }
		   if (cc.getTemplate() == null)
		   {
			   throw new RuntimeException("Template not found: " + type.templateName);
		   }
		return cc;
	}

	public boolean isDefaultsInDeck() {
		return defaultsInDeck;
	}

	public void setDefaultsInDeck(boolean defaultsInDeck) {
		this.defaultsInDeck = defaultsInDeck;
	}

	public String getDeck() {
		return deck;
	}

	public void setDeck(String deck) {
		this.deck = deck;
	}

	public int getDefaultCopiesInDeck() {
		return defaultCopiesInDeck;
	}

	public void setDefaultCopiesInDeck(int defaultCopiesInDeck) {
		this.defaultCopiesInDeck = defaultCopiesInDeck;
	}

	public List<Style> getStyles() {
		return styles;
	}

	public void setStyles(List<Style> styles) {
		this.styles = styles;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	public CustomElement findElementByName(String name)
	{
		for (CustomElement ce : elements)
		{
			if (ce.name.equalsIgnoreCase(name))
			{
				return ce;
			}
		}
		return null;
	}
	
	public CustomElement findElementByType(Class clazz)
	{
		for (CustomElement ce : elements)
		{
			if (ce.getClass().getSimpleName().equalsIgnoreCase(clazz.getSimpleName()))
			{
				return ce;
			}
		}
		return null;
	}
}
