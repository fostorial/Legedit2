package legedit2.cardgroup;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CardGroupListCellRenderer implements ListCellRenderer<CardGroup> {

	@Override
	public Component getListCellRendererComponent(JList<? extends CardGroup> list, CardGroup value, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (value == null || value.getName() == null)
		{
			return new JLabel("All Card Groups");
		}
		return new JLabel(value.getDisplayName());
	}

}
