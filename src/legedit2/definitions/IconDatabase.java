package legedit2.definitions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public final class IconDatabase {	

	class IconCategory
	{
		public String categoryName = null;
		public List<Icon> icons = null;
		
		public IconCategory()
		{
		}
		
		public IconCategory(String categoryName)
		{
			this.categoryName = categoryName.toUpperCase();
			icons = new ArrayList<Icon>();		
		}
	};

	
	private List<IconCategory> m_categories = new ArrayList<IconCategory>();
	
	
	public IconDatabase()
	{
	}
	
	public void addCategory(String categoryName)
	{
		String catNameUpper = categoryName.toUpperCase();
		
		IconCategory category = getCategory(catNameUpper);
		if (category == null)
		{
			m_categories.add(new IconCategory(catNameUpper));
		}
	}
	
	private IconCategory getCategory(String categoryName)
	{
		for (IconCategory i : m_categories)
		{
			if (categoryName.equals(i.categoryName))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public void addIcon(String categoryName, Icon icon)
	{
		IconCategory category = getCategory(categoryName.toUpperCase());
		if (category != null)
		{
			icon.setCategory(category.categoryName);
			category.icons.add(icon);
		}
	}
	
	public Icon getIcon(String tag)
	{
		String tagUpper = tag.toUpperCase();
		
		for (IconCategory c : m_categories)
		{
			for (Icon i : c.icons)
			{
				if (tagUpper.equals(i.getTagName()))
				{
					return i;
				}
			}
		}

		return null;
	}
	
	public List<String> getAllCategories()
	{
		List<String> allCategories = new ArrayList<String>();
		
		for (IconCategory c : m_categories)
		{
			allCategories.add(c.categoryName);
		}		
		
		return allCategories;
	}
	
	public List<Icon> getIconsOfCategory(String categoryName)
	{
		List<Icon> icons = new ArrayList<Icon>();
		
		IconCategory category = getCategory(categoryName);
		if (category != null)
		{
			icons.addAll(category.icons);
		}		
		
		return icons;
	}

	public List<Icon> getAllIcons()
	{
		List<Icon> allIcons = new ArrayList<Icon>();
		
		for (IconCategory c : m_categories)
		{
			allIcons.addAll(c.icons);
		}		
		
		return allIcons;
	}
}
