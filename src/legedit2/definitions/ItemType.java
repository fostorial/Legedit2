package legedit2.definitions;

import java.util.ArrayList;
import java.util.List;

import legedit2.cardtype.CardType;
import legedit2.decktype.DeckType;

public class ItemType {
	
	private String cardGroup;
	private String name;
	private String displayName;

	public static List<ItemType> getItemTypes()
	{
		List<ItemType> list = new ArrayList<>();
		list.addAll(DeckType.getDeckTypes());
		list.addAll(CardType.getCardTypes());
		
		return list;
	}
	
	public boolean includeInFilters()
	{
		if (cardGroup != null)
			return true;
		else
			return false;
	}

	public String getCardGroup() {
		return cardGroup;
	}

	public void setCardGroup(String cardGroup) {
		this.cardGroup = cardGroup;
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
