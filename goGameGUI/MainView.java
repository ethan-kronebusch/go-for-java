/*
 * @author: Ethan Kronebusch
 * Date: 2021-04-07
 * Project: winona-cs410-banana2
 * Program: MainView.java
 * Description: TODO
 */
package goGameGUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

/**
 * @author krone
 *
 */
public class MainView {

	/**
	 * 
	 */
	private Shell shlGoPlayer;
	protected Button btnPlay, btnConfig;

	public MainView(Shell shlGoPlayer) {
		this.shlGoPlayer = shlGoPlayer;
		createContents();
	}

	public void createContents() {
		btnPlay = new Button(shlGoPlayer, SWT.NONE);
		btnPlay.setBounds(shlGoPlayer.getClientArea().width/2-37, shlGoPlayer.getClientArea().height/2-13, 75, 25);
		btnPlay.setText("Play");
	}

	public void test() {
		shlGoPlayer.setBackgroundImage(null);
	}

}
