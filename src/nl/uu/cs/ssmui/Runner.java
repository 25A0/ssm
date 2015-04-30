/* 
	Runner.java

	Title:			Simple Stack Machine Runner
	Author:			atze
	Description:	
*/

package nl.uu.cs.ssmui;

import java.io.File;

import javax.swing.UIManager;

public class Runner extends Thread
{
    static protected int delay = 50 ;
    static protected boolean autoStart = false;
    static protected boolean autoQuit = false;
    static protected boolean useSTDIO = false;
    SSMRunner  ssmRunner  ;
    
	public Runner( File initialFile ) 
	{
		try {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} 
			catch (Exception e) { 
			}
		    ssmRunner = new SSMRunner( this, useSTDIO );
			ssmRunner.initComponents();
			ssmRunner.setVisible(true);
			//System.out.println( "Foc Trav=" + ssmRunner.isFocusTraversable() ) ;
			ssmRunner.requestFocus() ;
			if ( initialFile != null )
				ssmRunner.loadFile( initialFile ) ;
			
			if (autoStart)
				ssmRunner.tbStartForwardButtonActionPerformed(null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while( true )
		{
			int steppingState = ssmRunner.steppingState() ;
			if ( steppingState != SSMRunner.STEP_BY_STEP )
			{
				if ( ssmRunner.hasBreakpointAtPC() )
					ssmRunner.stopContinuouslyDoingSteps() ;
				else if ( steppingState == SSMRunner.STEP_CONT_FORWARD )
					ssmRunner.doAStepForward() ;
				else if ( steppingState == SSMRunner.STEP_CONT_BACKWARD )
					ssmRunner.doAStepBack() ;
			}
			
			if (ssmRunner.isHalted() && autoQuit)
				ssmRunner.thisWindowClosing(null);
			
			try { sleep( delay ) ; } catch ( InterruptedException e ) {}
		}
	}

	// Main entry point
	static public void main(String[] args) 
	{
		File initialFile = null ;
		
		for (int i=0; i<args.length; i++)
		{
			if (args[i].equals("--autostart"))
				autoStart = true;
			else if (args[i].equals("--autoquit"))
				autoQuit = true;
			else if (args[i].equals("--fast"))
				delay = 5;
			else if (args[i].equals("--usestdio"))
				useSTDIO = true;
			else
			{
				File f = new File( args[i] );
				if ( f.exists() )
					initialFile = f;
			}
		}
		
		new Runner( initialFile ) ;
	}
	
}
