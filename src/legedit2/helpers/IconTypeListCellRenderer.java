package legedit2.helpers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class IconTypeListCellRenderer implements ListCellRenderer<String> {

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (value.equals(""))
		{
			return new JLabel("All Icons");
		}
		return new JLabel(value);
	}

}
