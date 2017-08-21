package legedit2.definitions;

import legedit2.cardtype.CardType;
import legedit2.decktype.DeckType;

public class LegeditItem {

	public String getLegeditName()
	{
		return "Unknown Item";
	}
	
	public static LegeditItem generateLegeditItem(ItemType type)
	{
		if (type instanceof DeckType)
		{
			return DeckType.generateLegeditItem((DeckType)type);
		}
		if (type instanceof CardType)
		{
			return CardType.generateLegeditItem((CardType)type);
		}
		throw new RuntimeException("Unknown Item Type");
	}
	
	public int getDistinctCardCount()
	{
		return 0;
	}
	
	public int getTotalCardCount()
	{
		return 0;
	}
	
	public String getDifferenceXML()
	{
		return "";
	}
}
