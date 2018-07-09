package legedit2.card;

import java.io.File;
import java.util.Comparator;

import javax.swing.ImageIcon;

import legedit2.cardtype.CardType;
import legedit2.cardtype.CustomElement;
import legedit2.cardtype.CustomProperties;
import legedit2.cardtype.ElementCardName;
import legedit2.cardtype.ElementProperty;
import legedit2.cardtype.Style;
import legedit2.definitions.LegeditItem;

public class Card extends LegeditItem implements Comparator<Card>, Comparable<Card> {
	private String templateName;
	private CardType template;
	private String style;
	
	private ImageIcon imageSummary;
	
	private boolean changed;
	
	private static Card staticCard;
	
	public String getLegeditName()
	{
		return getCardName();
	}
	
	public String getCardName(String exportDir)
	{
			int i = 1;
			String filename = getExportCardName().replace(" ", "") + "_" + getName() + "_" + i;
			
			do
			{
				filename = getExportCardName().replace(" ", "") + "_" + getName() + "_" + i;
				i++;
			}
			while (new File(exportDir + File.separator + filename + ".jpg").exists() 
					|| new File(exportDir + File.separator + filename + ".png").exists());
			
			return filename;
	}
	
	public String getName()
	{
		return "";
	}
	
	public String generateOutputString()
	{
		return generateOutputString(false);
	}
	
	public String generateOutputString(boolean fullExport)
	{
		String str = "";
		
		str += "CUSTOMCARD;\n";
		str += "TEMPLATE;" + getTemplate().getTemplateName() + "\n";
		
		for (CustomElement e : getTemplate().elements)
		{
			str += e.generateOutputString();
		}
		
		return str;
	}
	
	public String getTextExportString()
	{
		String str = "";
		
		return str;
	}
	
	@Override
	public int compareTo(Card o) {
		return (this.getCardName()).compareTo(o.getCardName());
	}

	@Override
	public int compare(Card o1, Card o2) {
		return (o1.getCardName()).compareTo(o2.getCardName());
	}
	
	public int getNumberInDeck()
	{
		for (CustomElement ce : getTemplate().elements)
		{
			if (ce instanceof ElementProperty)
			{
				if (((ElementProperty)ce).name != null && ((ElementProperty)ce).property.name().equals("NUMBERINDECK"))
				{
					try
					{
						return Integer.parseInt(((ElementProperty)ce).getValue() != null ? ((ElementProperty)ce).getValue() : ((ElementProperty)ce).defaultValue);
					}
					catch (Exception e)
					{
						return 0;
					}
				}
			}
		}
		return 0;
	}
	
	public String getCardName()
	{
		for (CustomElement ce : getTemplate().elements)
		{
			if (ce instanceof ElementCardName)
			{
				if (((ElementCardName)ce).getSubnameValue() != null && !((ElementCardName)ce).getSubnameValue().isEmpty())
				{
					return ((ElementCardName)ce).getValue() + " (" + ((ElementCardName)ce).getSubnameValue() + ") - " + getTemplate().getTemplateDisplayName();
				}
				else
				{
					return ((ElementCardName)ce).getValue() + " - " + getTemplate().getTemplateDisplayName();
				}
			}
		}
		
		if (getTemplate().getStyle() != null)
		{
			for (CustomElement ce : getTemplate().getStyle().getElements())
			{
				if (ce instanceof ElementCardName)
				{
					if (((ElementCardName)ce).getSubnameValue() != null && !((ElementCardName)ce).getSubnameValue().isEmpty())
					{
						return ((ElementCardName)ce).getValue() + " (" + ((ElementCardName)ce).getSubnameValue() + ") - " + getTemplate().getTemplateDisplayName();
					}
					else
					{
						return ((ElementCardName)ce).getValue() + " - " + getTemplate().getTemplateDisplayName();
					}
				}
			}
			
		}
		
		return getTemplate().getTemplateDisplayName();
	}
	
	public String getExportCardName()
	{
		for (CustomElement ce : getTemplate().elements)
		{
			if (ce instanceof ElementCardName)
			{
				if (((ElementCardName)ce).getSubnameValue() != null)
				{
					return ((ElementCardName)ce).getSubnameValue() + "_" + getTemplate().getName();
				}
				else
				{
					return ((ElementCardName)ce).getValue() + "_" + getTemplate().getName();
				}
			}
		}
		
		if (getTemplate().getStyle() != null)
		{
			for (CustomElement ce : getTemplate().getStyle().getElements())
			{
				if (ce instanceof ElementCardName)
				{
					if (((ElementCardName)ce).getSubnameValue() != null)
					{
						return ((ElementCardName)ce).getSubnameValue() + "_" + getTemplate().getName();
					}
					else
					{
						return ((ElementCardName)ce).getValue() + "_" + getTemplate().getName();
					}
				}
			}
			
		}
		
		return getTemplate().getTemplateDisplayName();
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public ImageIcon getImageSummary() {
		return imageSummary;
	}

	public void setImageSummary(ImageIcon imageSummary) {
		this.imageSummary = imageSummary;
	}

	public CardType getTemplate() {
		return template;
	}

	public void setTemplate(CardType template) {
		this.template = template;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	public int getDistinctCardCount()
	{	
		return 1;
	}
	
	public int getTotalCardCount()
	{
		for (CustomElement ce : getTemplate().elements)
		{
			if (ce instanceof ElementProperty)
			{
				if (((ElementProperty)ce).property.equals(CustomProperties.NUMBERINDECK))
				{
					try
					{
						return Integer.parseInt(((ElementProperty)ce).value);
					}
					catch (Exception e)
					{
						return 0;
					}
				}
			}
		}
		return 0;
	}
	
	public void updateCardValues()
	{
		for (CustomElement ce : getTemplate().elements)
		{
			ce.updateCardValues();
		}
		
		for (Style s : getTemplate().getStyles())
		{
			for (CustomElement el : s.getElements())
			{
				el.updateCardValues();
			}
		}
	}

	public static Card getStaticCard() {
		return staticCard;
	}

	public static void setStaticCard(Card staticCard) {
		Card.staticCard = staticCard;
	}
	
	public String getDifferenceXML()
	{
		String str = "";
		
		str += "<card template=\"" + getTemplate().getName() + "\"";
		if (style != null)
		{
			str += " style=\""+ getStyle() + "\"";
		}
		str +=">\n";
		
		str += "<template>\n";
		
		for (CustomElement el : getTemplate().elements)
		{
			str += el.getDifferenceXML();
		}
		
		if (getTemplate().getStyles().size() > 0)
		{
			str += "<styles>\n";
			
			for (Style s : getTemplate().getStyles())
			{
				str += "<style name=\"" + s.getName() + "\">\n";
				for (CustomElement el : s.getElements())
				{
					str += el.getDifferenceXML();
				}
				str += "</style>\n";
			}
			
			str += "</styles>\n";
		}
		
		str += "</template>\n";
		
		str += "</card>\n\n";
		
		return str;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
}
