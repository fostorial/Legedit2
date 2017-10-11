package legedit2.decktype;

import java.io.Serializable;

public class DeckTypeAttribute implements Serializable {

	private String name;
	
	private String type;
	
	private String iconType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIconType() {
		return iconType;
	}

	public void setIconType(String iconType) {
		this.iconType = iconType;
	}
}
