package legedit2.cardtype;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Node;

import legedit2.card.Card;
import legedit2.definitions.Icon;

public class ElementGroup extends CustomElement {

	private List<CustomElement> elements = new ArrayList<>();
	private HashMap<String, CustomElement> elementsHash = new HashMap<String, CustomElement>();

	public List<CustomElement> getElements() {
		return elements;
	}

	public void setElements(List<CustomElement> elements) {
		this.elements = elements;
	}
	
	public void setScale(double scale) {
		super.setScale(scale);
		
		for (CustomElement e : elements)
		{
			e.setScale(scale);
		}
	}
	
	public void drawElement(Graphics2D g)
	{
		for (CustomElement e : elements)
		{
			e.drawElement(g);
		}
	}
	
	public void updateCardValues()
	{
		for (CustomElement e : elements)
		{
			e.updateCardValues();
		}
	}
	
	public void loadValues(Node node, Card card)
	{
		if (!node.getNodeName().equals("elementgroup"))
		{
			return;
		}
		
		if (node.getAttributes().getNamedItem("visible") != null)
		{
			visible = Boolean.parseBoolean(node.getAttributes().getNamedItem("visible").getNodeValue());
		}
		
		for (int count1 = 0; count1 < node.getChildNodes().getLength(); count1++) {
			Node node2 = node.getChildNodes().item(count1);
			
			if (Arrays.asList(CustomElement.elementTypes).contains(node2.getNodeName()))
			{
				for (CustomElement e : elements)
				{
					if (node2.getAttributes().getNamedItem("name") != null
							&& e.name != null
							&& e.name.equals(node2.getAttributes().getNamedItem("name").getNodeValue()))
					{
						e.loadValues(node2, card);
						e.visible = visible;
					}
				}	
			}
		}
	}
	
	public String getDifferenceXML()
	{
		String str = "";
		
		str += "<elementgroup name=\"" + replaceNonXMLCharacters(name) + "\" visible=\""+visible+"\">\n";
		
		for (CustomElement e : elements)
		{
			str += e.getDifferenceXML();
		}
		
		str += "</elementgroup>\n";
		
		return str;
	}

	public HashMap<String, CustomElement> getElementsHash() {
		return elementsHash;
	}

	public void setElementsHash(HashMap<String, CustomElement> elementsHash) {
		this.elementsHash = elementsHash;
	}
	
	public ElementGroup getCopy(CardType template)
	{
		try {
			ElementGroup e = (ElementGroup) clone();
			e.template = template;
			
			List<CustomElement> newElements = new ArrayList<>();
			for (CustomElement ce : elements)
			{
				CustomElement nce = ce.getCopy(template);
				nce.template = template;
				newElements.add(nce);
			}
			
			e.elements = newElements;
			
			return e;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
