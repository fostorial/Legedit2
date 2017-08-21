package legedit2.gui.templatemanager;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import legedit2.definitions.Icon.ICON_TYPE;

public class IconTypeListCellRenderer implements ListCellRenderer<ICON_TYPE> {

	@Override
	public Component getListCellRendererComponent(JList<? extends ICON_TYPE> list, ICON_TYPE value, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (value.equals(ICON_TYPE.NONE))
		{
			return new JLabel("All Icons");
		}
		return new JLabel(value.name());
	}

}
