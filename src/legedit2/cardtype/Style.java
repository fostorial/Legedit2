package legedit2.cardtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Style implements Cloneable {

	private String name;
	private List<CustomElement> elements = new ArrayList<>();
	private HashMap<String, CustomElement> elementsHash = new HashMap<String, CustomElement>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CustomElement> getElements() {
		return elements;
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

	public void setElements(List<CustomElement> elements) {
		this.elements = elements;
	}

	public HashMap<String, CustomElement> getElementsHash() {
		return elementsHash;
	}

	public void setElementsHash(HashMap<String, CustomElement> elementsHash) {
		this.elementsHash = elementsHash;
	}
	
	public Style getCopy(CardType template)
	{
		try {
			Style s = (Style) clone();
			
			List<CustomElement> elements = new ArrayList<CustomElement>();
			for (CustomElement e : this.elements)
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
			s.setElements(elements);
			
			return s;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
