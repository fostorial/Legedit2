package legedit2.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.swing.JOptionPane;

import legedit2.gui.LegeditFrame;

public class LegeditHelper {
	
	static public enum PROPERTIES {lastExpansion};
	
	final static public String FRAME_NAME = "Legedit 2";
	
	final static private String[] errorMessages = new String[] {"The phoenix must burn to emerge.", "Giving up is the only sure way to fail.", 
			"It's failure that gives you the proper perspective on success.", "There is no failure except in no longer trying.",
			"I have not failed. I've just found 10,000 ways that won't work.", "Pain is temporary. Quitting lasts forever.",
			"We are all failures - at least the best of us are.", "I'd rather be partly great than entirely useless."};
	
	final static private Random rand = new Random(new Date().getTime());
	
	private static Properties applicationProps = new Properties();
	
	public static LegeditFrame getLegeditFrame()
	{
		return LegeditFrame.legedit;
	}
	
	public static void resetGUI()
	{
		LegeditFrame.refreshGUI();
	}
	
	public static String getFrameName()
	{
		if ((String)applicationProps.get("lastExpansion") != null)
		{
			File file = new File((String)applicationProps.get("lastExpansion"));
			if (file.exists())
			{
				return FRAME_NAME + " - " + file.getName();
			}			
		}
		
		return FRAME_NAME + " - [Untitled]";
	}
	
	public static boolean doChangesExist()
	{
		return false;
	}
	
	public static void handleWindowCloseSave()
	{
		if (doChangesExist())
    	{
    		int outcome = JOptionPane.showOptionDialog(getLegeditFrame(), "Save Changes?", "Save Changes", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			
			if (outcome == JOptionPane.YES_OPTION)
			{
				/* Save file */
			}
    	}
	}
	
	public static String getErrorMessage()
	{
		return errorMessages[rand.nextInt(errorMessages.length)];
	}
	
	public static void loadProperties()
	{
		try {
			FileInputStream in = new FileInputStream("appProperties");
			getApplicationProps().load(in);
			in.close();
		}
		catch (FileNotFoundException e)
		{
			saveProperties();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveProperties()
	{
		try {
			FileOutputStream out = new FileOutputStream("appProperties");
			getApplicationProps().store(out, "---No Comment---");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Properties getApplicationProps() {
		return applicationProps;
	}

	public static void setApplicationProps(Properties applicationProps) {
		LegeditHelper.applicationProps = applicationProps;
	}
	
	public static File getLastOpenDirectory()
	{
		if (applicationProps.get("lastOpenDirectory") != null)
		{
			File file = new File((String)applicationProps.get("lastOpenDirectory"));
			if (file.exists())
			{
				return file;
			}
		}
		
		return null;
	}
	
	public static void setLastOpenDirectory(String value)
	{
		putProperty("lastOpenDirectory", value);
	}
	
	public static void putProperty(String property, Object value)
	{
		if (value == null)
		{
			applicationProps.remove(property);
		}
		else
		{
			applicationProps.put(property, value);
		}
		saveProperties();
	}
	
	public static Object getProperty(String property)
	{
		return applicationProps.get(property);
	}

	public static String getProperty(PROPERTIES property)
	{
		if (applicationProps.get(property.name()) != null)
		{
			return (String)applicationProps.get(property.name());
		}
		
		return null;
	}
	
	public static void setProperty(PROPERTIES property, String value)
	{
		putProperty(property.name(), value);
	}
}
