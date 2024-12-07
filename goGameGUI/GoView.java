/*
 * @author: Ethan Kronebusch
 * Date: 2021-03-01
 * Project: winona-cs410-banana2
 * Program: GoView.java
 * Description: TODO
 */
package goGameGUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * @author not
 *
 */
public class GoView {

	private Shell shlGoPlayer;
	protected Label[][] goStones = new Label[19][19];
	protected Composite goban;
	protected Button passButton, quitButton, saveButton;
	protected CLabel turnTracker;
	private Label blackPoints, whitePoints;
	
	public GoView(Shell shlGoPlayer) {
		this.shlGoPlayer = shlGoPlayer;
		createContents();
	}
	
	public void createContents() {
		Rectangle content = shlGoPlayer.getClientArea();
		
		//shlGoPlayer.setBackgroundImage(SWTResourceManager.getSVGImage("sourceCode/goGameGUI/goban_19x19.svg",content.width,content.height));
		shlGoPlayer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		goban = new Composite(shlGoPlayer, SWT.NONE);
		goban.setBounds(0,0,content.width,content.height);
		goban.setBackgroundImage(SWTResourceManager.getSVGImage("sourceCode/goGameGUI/goban_19x19.svg",content.width,content.height));
		
		Composite statTracker = new Composite(shlGoPlayer, SWT.NONE);
		statTracker.setBounds(content.width, 0, (int)(content.width*0.4), content.height);
		int statWidth = statTracker.getClientArea().width;
		
		turnTracker = new CLabel(statTracker, SWT.NONE);
		turnTracker.setImage(SWTResourceManager.getSVGImage("sourceCode/goGameGUI/black_stone.svg",(int)(content.width*0.05),(int)(content.height*0.05)));
		turnTracker.setBounds((int)(statWidth*0.25), 40, (int)(statWidth*0.5), (int)(content.height*0.065));
		turnTracker.setText("Black's Turn");
		
		blackPoints = new Label(statTracker, SWT.NONE);
		blackPoints.setAlignment(SWT.CENTER);
		blackPoints.setText("Black has 0 points\r\n(0 prisoners, 0 territory)");
		blackPoints.setBounds((int)(statWidth*0.025), 129, (int)(statWidth*0.5), 30);
		
		whitePoints = new Label(statTracker, SWT.NONE);
		whitePoints.setAlignment(SWT.CENTER);
		whitePoints.setText("White has KOMI points\r\n(0 prisoners, 0 territory)");
		whitePoints.setBounds((int)(statWidth*0.525), 129, (int)(statWidth*0.475), 30);
		
		Label divider = new Label(statTracker, SWT.SEPARATOR | SWT.HORIZONTAL);
		divider.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		divider.setBounds((int)(statWidth*0.15)/2, (int)(content.height*0.275), (int)(statWidth*0.85), 1);
		
		saveButton = new Button(statTracker, SWT.NONE);
		saveButton.setText("Save");
		saveButton.setBounds((int)(statWidth*0.25), (int)(content.height*0.40), (int)(statWidth*0.5), (int)(content.height*0.065));
		
		passButton = new Button(statTracker, SWT.NONE);
		passButton.setText("Pass");
		passButton.setBounds((int)(statWidth*0.25), (int)(content.height*0.53), (int)(statWidth*0.5), (int)(content.height*0.065));
		
		quitButton = new Button(statTracker, SWT.NONE);
		quitButton.setText("Forfeit");
		quitButton.setBounds((int)(statWidth*0.25), (int)(content.height*0.66), (int)(statWidth*0.5), (int)(content.height*0.065));
	}

	public Label[][] getStones() {
		return this.goStones;
	}
	
	public void setPoints(double prisoners, double terrPoints, boolean isBlack) {
		//this is to handle plurals correctly
		String pointGrammar = prisoners+terrPoints == 1 ? "point" : "points";
		String prisonerGrammar = prisoners == 1 ? "prisoner" : "prisoners";
		
		if(isBlack) {
			blackPoints.setText(String.format("Black has %.0f %s %n(%.0f %s, %.0f territory)", prisoners+terrPoints, pointGrammar, prisoners, prisonerGrammar, terrPoints));
		}else {
			whitePoints.setText(String.format("White has %.1f %s %n(%.0f %s, %.0f territory)", prisoners+terrPoints, pointGrammar, prisoners, prisonerGrammar, terrPoints));
		}
	}
	
	public int getViewSize() {
		return shlGoPlayer.getClientArea().height;
	}
}
