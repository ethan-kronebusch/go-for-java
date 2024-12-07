package goGameGUI;

import java.io.Serializable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Stone implements Serializable{ //class for model so data is seralizable
	/**
	 * 
	 */
	private static final long serialVersionUID = -3720922144082554170L;
	private boolean isBlack;
	private boolean isEmpty;
	private PPoint p;
	
	public Stone(boolean isBlack,boolean isEmpty, PPoint p) {
		this.isBlack = isBlack;
		this.isEmpty = isEmpty;
		this.p.x = p.x;
		this.p.y = p.y;
		
	}
	
	public boolean isBlack() {
		return this.isBlack;
	}
	
	public Label makeLabelStone(Shell shell) {
		  Label goStone = new Label(shell, SWT.NONE);
		  goStone.setData("isBlack", this.isBlack);
		  goStone.setData("isEmpty",this.isEmpty);
		  //add if statement to set image based on Booleans
//		  goStone.setBounds(this.p.x, this.p.y, /*width calc*/, /*height calc*/);
		  
		  return goStone;
		}

	public boolean isEmpty() {
		return this.isEmpty;
	}
	
	
}