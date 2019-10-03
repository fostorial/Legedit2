package legedit2.definitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import legedit2.gui.LegeditFrame;
import legedit2.helpers.LegeditHelper;


public final class Icon implements Comparator<Icon>, Comparable<Icon> {
	
	private static IconDatabase m_icons = null;
	
	private String tagName = null;
	private String imagePath = null;
	private boolean underlayMinimized = false;
	private String category = null;
	private boolean userAddedIcon = false;
	
	public static Icon noneIcon = new Icon();
	public static String iconFolder = "legedit" + File.separator + "icons";
	
	public Icon()
	{
	}
	
	private Icon(String imagePath)
	{
		this.imagePath = imagePath;
	}
	
	public Icon(String imagePath, boolean underlayMinimized)
	{
		this.imagePath = imagePath;
		this.underlayMinimized = underlayMinimized;
	}
	
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isUnderlayMinimized() {
		return underlayMinimized;
	}

	public void setUnderlayMinimized(boolean underlayMinimized) {
		this.underlayMinimized = underlayMinimized;
	}	
	
	public String getCategory() {
		return category;
	}
	
	public boolean isEditable() {
		return userAddedIcon;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String toString()
	{
		return getEnumName();
	}
	
	public String getEnumName()
	{
		if (imagePath == null)
		{
			return "NONE";
		}
		
		if (tagName != null)
		{
			return tagName;
		}
		
		String imageSplit = File.separator;
		if (imageSplit.equals("\\")) { imageSplit = File.separator+File.separator; }
		String name = imagePath;
		String[] split = name.split(imageSplit);
		name = split[split.length - 1];
		name = name.substring(0, name.lastIndexOf("."));
		name = name.toUpperCase().replace(" ", "_").replace("-", "_");
		return name;
	}
	
	private static void loadIconsFromDirectory(String folder, File dir)
	{
		if (dir == null)
		{
			dir = new File(folder);
			if (!dir.exists())
				return;
		}
		
		for (File f : dir.listFiles())
		{
			String filename = f.getName().toUpperCase();
			if (f.isFile())
			{
				String extension = "";
				int index = filename.lastIndexOf(".");
				if (index > 0)
					extension = filename.substring(index + 1).toUpperCase();
				
				if (extension.equals("XML"))
				{
					try
					{
						NodeList docNodesList = LegeditHelper.getXMLNodes(f);
						for (int count = 0; count < docNodesList.getLength(); count++) 
						{
							Node node = docNodesList.item(count);
							String nodeName = node.getNodeName();
							
							boolean gotUserIcon = nodeName.equals("user_icon");
							if (nodeName.equals("icon") || gotUserIcon)
							{
								String category = "";
								String name = "";
								String iconFilePath = "";
								
								Node categoryAttribute = node.getAttributes().getNamedItem("category"); 
								if (categoryAttribute != null)
									category = categoryAttribute.getNodeValue().toUpperCase();

								Node nameAttribute = node.getAttributes().getNamedItem("name"); 
								if (nameAttribute != null)
									name = nameAttribute.getNodeValue().toUpperCase();

								Node iconFilePathAttribute = node.getAttributes().getNamedItem("file"); 
								if (iconFilePathAttribute != null)
									iconFilePath = iconFilePathAttribute.getNodeValue().toUpperCase();

								if (!name.isEmpty() && !iconFilePath.isEmpty())
								{
									m_icons.addCategory(category);
									//System.out.println("Adding Icon type: " + category + " from " + f.getAbsolutePath());
									
									Icon i = new Icon(folder + File.separator + iconFilePath);
									i.setTagName(name);
									i.userAddedIcon = gotUserIcon;
									m_icons.addIcon(category, i);
									//System.out.println("Loaded: " + i.getTagName());
								}
							}
						}
					}
					catch (Exception e)
					{
						String errorMessage = "Something wrong happened when trying to read " + f.getAbsolutePath() + ". Content from that file may have been ignored.";
						if (e.getMessage() != null)
							errorMessage += " Error Message: " + e.getMessage();
						
						JOptionPane.showMessageDialog(LegeditFrame.legedit, errorMessage, LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
			else if (f.isDirectory())
			{
				loadIconsFromDirectory(folder + File.separator + filename, null);
			}
		}
	}
	
	public static void loadIcons()
	{
		//System.out.println("Loading Icons...");
		
		m_icons = new IconDatabase(); 
		m_icons.addCategory("");
		m_icons.addIcon("", Icon.noneIcon);
		
		File dir = new File(iconFolder);
		if (dir.exists())
		{
			loadIconsFromDirectory(iconFolder, dir);
		}
		else
		{
			String errorMessage = "Something wrong happened when trying to read " + iconFolder + " . You should not delete or rename that folder. Please re-install LegEdit.";
			JOptionPane.showMessageDialog(LegeditFrame.legedit, errorMessage, LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			return;
		}
	
		//System.out.println("Icons Loaded...");
	}
	
	public static Icon valueOf(String tag)
	{
		if (m_icons == null)
		{
			loadIcons();
		}
		
		if (tag != null && tag.equals("NONE"))
		{
			return noneIcon;
		}
		
		return m_icons.getIcon(tag);
	}
	
	public static List<Icon> values()
	{
		if (m_icons == null)
		{
			loadIcons();
		}
		
		return m_icons.getAllIcons();
	}
	
	public static List<Icon> sorted_values()
	{
		List<Icon> icons = Icon.values();
		Collections.sort(icons);
		return icons;
	}	
	
	public static List<String> categories()
	{
		if (m_icons == null)
		{
			loadIcons();
		}
		
		return m_icons.getAllCategories();
	}
	
	public static void addIcon(String categoryName, Icon icon, boolean userAddedIcon)
	{
		icon.userAddedIcon = userAddedIcon;
		m_icons.addIcon(categoryName, icon);
	}
	
	public static void saveIconDefinitions()
	{
		FileWriter fw = null;
		try
		{
			String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
			str += "<xml>\n\n";
			
			for (Icon i : values())
			{
				if (i.userAddedIcon)
				{
					File iconFile = new File(i.getImagePath());	// need to store ONLY the filename because when loaded the full path will be stored into the icon object
					str += "<user_icon category=\"" + i.getCategory() + "\" name=\"" + i.getTagName() + "\" file=\"" + iconFile.getName();
					str += "\" minimizedUnderlay=\"" + i.isUnderlayMinimized() + "\" />\n";
				}
			}
			
			str += "\n</xml>";
			
			fw = new FileWriter(iconFolder + File.separator + "icons.xml");
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(str);
			
			bw.close();
			fw.close();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(LegeditFrame.legedit, e.getMessage() != null ? e.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		finally 
		{
			if (fw != null)
			{
				try
				{
					fw.close();
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(LegeditFrame.legedit, e.getMessage() != null ? e.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int compareTo(Icon i) {
		return (this.getEnumName()).compareTo(i.getEnumName());
	}

	@Override
	public int compare(Icon i1, Icon i2) {
		return (i1.getEnumName()).compareTo(i2.getEnumName());
	}
	
}
