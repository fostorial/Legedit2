package legedit2.deck;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;

import legedit2.card.Card;
import legedit2.decktype.DeckType;
import legedit2.definitions.LegeditItem;

public class Deck extends LegeditItem implements Comparator<Deck>, Comparable<Deck> {
	
	private String name;

	private String templateName;
	private DeckType template;
	
	private ImageIcon imageSummary;
	
	private boolean changed;
	
	private List<Card> cards = new ArrayList<>();
	
	private static Deck staticDeck;
	
	public String getLegeditName()
	{
		
		return getDeckName() + " - " + template.getDisplayName() + " (" + getDeckTotal() + " Cards)";
	}
	
	public int getDeckTotal()
	{
		int total = 0;
		for (Card c : cards)
		{
			total += c.getNumberInDeck();
		}
		return total;
	}
	
	public String getDeckName(String exportDir)
	{
			int i = 1;
			String filename = getName().replace(" ", "") + "_" + getName() + "_" + i;
			
			do
			{
				filename = getName().replace(" ", "") + "_" + getName() + "_" + i;
				i++;
			}
			while (new File(exportDir + File.separator + filename + ".jpg").exists() 
					|| new File(exportDir + File.separator + filename + ".png").exists());
			
			return filename;
	}
	
	public String getDeckName()
	{
		return name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String generateOutputString()
	{
		return generateOutputString(false);
	}
	
	public String generateOutputString(boolean fullExport)
	{
		String str = "";
		
		str += "CUSTOMCARD;\n";
		/*
		str += "TEMPLATE;" + getTemplate().getTemplateName() + "\n";
		
		for (CustomElement e : getTemplate().elements)
		{
			str += e.generateOutputString();
		}
		
		*/
		
		return str;
	}
	
	public String getTextExportString()
	{
		String str = "";
		
		return str;
	}
	
	@Override
	public int compareTo(Deck o) {
		return (this.getName()).compareTo(o.getName());
	}

	@Override
	public int compare(Deck o1, Deck o2) {
		return (o1.getName()).compareTo(o2.getName());
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

	public DeckType getTemplate() {
		return template;
	}

	public void setTemplate(DeckType template) {
		this.template = template;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	public int getDistinctCardCount()
	{
		return cards.size();
	}
	
	public int getTotalCardCount()
	{
		int i = 0;
		for (Card c : cards)
		{
			i += c.getTotalCardCount();
		}
		return i;
	}

	public static Deck getStaticDeck() {
		return staticDeck;
	}

	public static void setStaticDeck(Deck staticDeck) {
		Deck.staticDeck = staticDeck;
	}
	
	public String getDifferenceXML()
	{
		String str = "";
		
		str += "<deck template=\"" + getTemplate().getName() + "\" name=\"" + name + "\">\n\n";
		
		str += "<template>\n";
		
		str += "</template>\n\n";
		
		str += "<cards>\n";
		
		for (Card c : cards)
		{
			str += c.getDifferenceXML();
		}
		
		str += "</cards>\n\n";
		
		str += "</deck>\n\n";
		
		return str;
	}
}
