package legedit2.decktype;

import java.io.Serializable;

public class DeckTypeAttribute implements Serializable {

	private String name;
	
	private String displayName;
	
	private String type;
	
	private String value;
	
	private Boolean isEditable = false;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		if (displayName == null || displayName.isEmpty())
			return getName();
		
		return displayName;
	}

	public void setDisplayName(String name) {
		this.displayName = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Boolean isUserEditable() {
		return isEditable;
	}

	public void setUserEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	public void copy(DeckTypeAttribute attribute) {
		this.setName(attribute.getName());
		this.setDisplayName(attribute.getDisplayName());
		this.setType(attribute.getType());
		this.setValue(attribute.getValue());
		this.setUserEditable(attribute.isUserEditable());
	}
}
