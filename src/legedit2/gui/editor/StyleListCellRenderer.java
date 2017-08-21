package legedit2.gui.editor;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import legedit2.cardtype.Style;

public class StyleListCellRenderer implements ListCellRenderer<Style> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Style> list, Style value, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (value == null || value.getName() == null)
		{
			return new JLabel("No Style");
		}
		return new JLabel(value.getName());
	}

}
