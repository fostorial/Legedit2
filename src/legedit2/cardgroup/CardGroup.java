package legedit2.cardgroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CardGroup {
	
	private String name;
	private String displayName;
	
	private static List<CardGroup> cardGroups = null;

	public static List<CardGroup> getCardTypes()
	{
		if (cardGroups != null)
		{
			return cardGroups;
		}
		
		cardGroups = new ArrayList<>();
		
		String templateFolder = "legedit" + File.separator + "cardgroups";
		File dir = new File(templateFolder);
		if (dir.exists())
		{
			for (File f : dir.listFiles())
			{
				if (f.getName().endsWith(".xml"))
				{
					CardGroup group = parseCardGroup(f);
					cardGroups.add(group);
				}
			}
		}
		
		return cardGroups;
	}
	
	public static List<CardGroup> reloadCardTypes()
	{
		cardGroups = null;
		return getCardTypes();
	}
	
	public static CardGroup parseCardGroup(File structureFile)
	{
		CardGroup t = new CardGroup();
		
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
					}
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
