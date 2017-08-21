package legedit2.gui.dialogs;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import legedit2.definitions.ItemType;

public class ItemTypeCellRenderer implements ListCellRenderer<ItemType> {

	@Override
	public Component getListCellRendererComponent(JList<? extends ItemType> list, ItemType value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel label = new JLabel(value.getDisplayName());
		if (isSelected)
		{
			label.setBackground(Color.lightGray);
			label.setOpaque(true);
		}
		return label;
	}

}
