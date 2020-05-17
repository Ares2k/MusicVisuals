package ie.tudublin;

import C18395266.*;
import example.*;

public class Main
{	

	public void startUI()
	{
		String[] a = {"MAIN"};
        processing.core.PApplet.runSketch( a, new Visuals());		
	}

	public static void main(String[] args)
	{
		Main main = new Main();
		main.startUI();			
	}
}