package legedit2.cardtype;

public class LineInformation {

	public String text = "";
	public int drawXPosition = 0;
	public int drawYPosition = 0;
	public int lineThickness = 0;
	
	public LineInformation(String line, int drawXPosition, int drawYPosition, int lineThickness)
	{
		this.text = line;
		this.drawXPosition = drawXPosition;
		this.drawYPosition = drawYPosition;
		this.lineThickness = lineThickness;
	}
}
