package legedit2.definitions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Icon implements Comparator<Icon>, Comparable<Icon> {
	
	private static List<Icon> icons = null;
	
	public enum ICON_TYPE {NONE, ATTRIBUTE, TEAM, POWER, MISC};
	
	private String tagName;
	private String imagePath;
	private boolean underlayMinimized;
	private ICON_TYPE type;
	
	private static Icon noneIcon = new Icon(null, ICON_TYPE.NONE);
	
	public Icon()
	{
		
	}
	
	private Icon(String image, ICON_TYPE type)
	{
		this.imagePath = image;
		this.type = type;
	}
	
	public Icon(String tagName, String image, boolean underlayMinimized, ICON_TYPE type)
	{
		this.imagePath = image;
		this.underlayMinimized = underlayMinimized;
		this.type = type;
		this.tagName = tagName;
	}
	
	public String getImagePath()
	{
		return imagePath;
	}
	
	public boolean isUnderlayMinimized()
	{
		return underlayMinimized;
	}
	
	public ICON_TYPE getIconType()
	{
		return type;
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
	
	public static void loadIcons()
	{
		System.out.println("Loading Icons...");
		
		icons = new ArrayList<Icon>();
		
		System.out.println("Loading Teams...");
		
		File file = new File("legedit" + File.separator + "icons" + File.separator + "teams" + File.separator + "teams.txt");
		if (file.exists())
		{	
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
				   if (line != null && !line.startsWith("#") && !line.isEmpty())
				   {
					   try
					   {
						   String[] split = line.split(",");
						   boolean underlay = false;
						   if (split[2].equals("true"))
						   {
							   underlay = true;
						   }
						   Icon i = new Icon(split[0], "legedit" + File.separator + "icons" + File.separator +"teams"+File.separator+split[1], underlay, ICON_TYPE.TEAM);
						   icons.add(i);
						   System.out.println("Loaded: " + i.getEnumName()); 
					   }
					   catch (Exception e)
					   {
						   System.err.println("Failed to load: " + line);
					   }					   
				   }
				}
				br.close();
			}
			catch (Exception e)
			{
				System.err.println("Error loading Team Icons");
				e.printStackTrace();
			}
		}
		
		System.out.println("Loading Powers...");
		
		file = new File("legedit" + File.separator + "icons" + File.separator + "powers" + File.separator);
		if (file.exists())
		{
			File[] files = file.listFiles();
			for (File f : files)
			{
				if (f.getName().toLowerCase().endsWith(".png"))
				{
					
					Icon i = new Icon(f.getAbsolutePath(), ICON_TYPE.POWER);
					icons.add(i);
					System.out.println("Loaded: " + i.getEnumName());
				}
			}
		}
		
		System.out.println("Loading Attributes...");
		
		file = new File("legedit" + File.separator + "icons" + File.separator + "attributes" + File.separator);
		if (file.exists())
		{
			File[] files = file.listFiles();
			for (File f : files)
			{
				if (f.getName().toLowerCase().endsWith(".png"))
				{
					
					Icon i = new Icon(f.getAbsolutePath(), ICON_TYPE.ATTRIBUTE);
					icons.add(i);
					System.out.println("Loaded: " + i.getEnumName());
				}
			}
		}
		
		System.out.println("Loading Misc Icons...");
		
		file = new File("legedit" + File.separator + "icons" + File.separator + "misc" + File.separator);
		if (file.exists())
		{
			File[] files = file.listFiles();
			for (File f : files)
			{
				if (f.getName().toLowerCase().endsWith(".png"))
				{
					
					Icon i = new Icon(f.getAbsolutePath(), ICON_TYPE.MISC);
					icons.add(i);
					System.out.println("Loaded: " + i.getEnumName());
				}
			}
		}
		
		icons.add(Icon.noneIcon);
		
		System.out.println("Icons Loaded...");
	}
	
	public static Icon valueOf(String key)
	{
		if (icons == null)
		{
			loadIcons();
		}
		
		if (key != null && key.equals("NONE"))
		{
			return noneIcon;
		}
		
		for (Icon i : icons)
		{
			if (key.toUpperCase().equals(i.getEnumName()))
			{
				return i;
			}
		}
		return null;
	}
	
	public static List<Icon> values()
	{
		if (icons == null)
		{
			loadIcons();
		}
		
		return icons;
	}
	
	public static void saveIconDefinitions()
	{
		try
		{
			String str = "";
			
			for (Icon i : Icon.values())
			{
				if (i.type.equals(Icon.ICON_TYPE.TEAM))
				{
					str += i.getEnumName() + "," + new File(i.getImagePath()).getName() + "," + i.isUnderlayMinimized() + "\n";
				}
			}
			
			File file = new File("legedit" + File.separator + "icons" + File.separator + "teams" + File.separator + "teams.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(str);
			
			bw.close();
			fw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public ICON_TYPE getType() {
		return type;
	}

	public void setType(ICON_TYPE type) {
		this.type = type;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setUnderlayMinimized(boolean underlayMinimized) {
		this.underlayMinimized = underlayMinimized;
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
