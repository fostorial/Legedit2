package legedit2.gui.project;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import legedit2.definitions.LegeditItem;

public class CardTypeCellRenderer implements ListCellRenderer<LegeditItem> {

	@Override
	public Component getListCellRendererComponent(JList<? extends LegeditItem> list, LegeditItem value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel label = new JLabel(value.getLegeditName());
		if (isSelected)
		{
			label.setBackground(Color.lightGray);
			label.setOpaque(true);
		}
		return label;
	}

}
