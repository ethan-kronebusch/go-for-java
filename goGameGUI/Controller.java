package goGameGUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
/*
 * Main class for running the Go board
 */

public class Controller {
	private MainView mainView;
	private GoView gameView;
	private GoModel model;
	private Shell shlGoMenu, shlGoGame;
	private MouseAdapter playPiece, startGame, passTurn, forfeit;
	private MouseTrackAdapter showPreview;
	private Image blackStone, whiteStone, blackStone_trans, whiteStone_trans;

	public Controller() {
		shlGoMenu = new Shell();
		shlGoMenu.setBackgroundImage(null);
		shlGoMenu.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		shlGoMenu.setImage(SWTResourceManager.getSVGImage("sourceCode/goGameGUI/black_stone.svg",36,36));
		shlGoMenu.setSize(770, 800);// 770, 800
		shlGoMenu.setText("Go Player");
		
		mainView = new MainView(shlGoMenu);
		model = new GoModel();
		this.addMainListeners();
		open();
	}
	
	public void open() {
		Display display = Display.getDefault();
		shlGoMenu.open();
		shlGoMenu.layout();
		while (!shlGoMenu.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void addMainListeners() {
		startGame = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				//make the menu screen invisible do it doesn't get in the way
				shlGoMenu.setVisible(false);
				
				//create game shell, make it non-resizable and add values
				shlGoGame = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN);
				shlGoGame.setText("Go Player");
				shlGoGame.setImage(SWTResourceManager.getSVGImage("sourceCode/goGameGUI/black_stone.svg",36,36));
				
				//bind the souls of the gameboard window and the menu window, so they perish as one
				shlGoGame.addDisposeListener(new DisposeListener() {

					@Override
					public void widgetDisposed(DisposeEvent arg0) {
						shlGoMenu.close();
					}
					
				});
				
				//correctly size go board
				Rectangle content = shlGoMenu.getClientArea();
				if(content.width != content.height) {
					Rectangle window = shlGoMenu.getBounds();
					int horDiff = window.width - content.width;
					int verDiff = window.height - content.height;
					int edgeLength = (content.width + content.height) / 2;
					shlGoGame.setSize(edgeLength+horDiff, edgeLength + verDiff);
				}
				
				//set up go board
				gameView = new GoView(shlGoGame);
				model.setUpStones(gameView, shlGoGame);
				gameView.setPoints(model.getWhitePoints(), 0, false);
				shlGoGame.setSize((int)(shlGoGame.getSize().x*1.4),shlGoGame.getSize().y);
				shlGoGame.open();
				
				//load stone images
				blackStone = SWTResourceManager.getSVGImage("sourceCode/goGameGUI/black_stone.svg",model.getStoneSize(),model.getStoneSize());
				whiteStone = SWTResourceManager.getSVGImage("sourceCode/goGameGUI/white_stone.svg",model.getStoneSize(),model.getStoneSize());
				blackStone_trans = SWTResourceManager.getSVGImage("sourceCode/goGameGUI/black_stone_trans.svg",model.getStoneSize(),model.getStoneSize());
				whiteStone_trans = SWTResourceManager.getSVGImage("sourceCode/goGameGUI/white_stone_trans.svg",model.getStoneSize(),model.getStoneSize());
				
				addGoListeners();
			}
		};
		
		mainView.btnPlay.addMouseListener(startGame);
	}
	
	public void addGoListeners() {
		showPreview = new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				if (model.isBlackTurn()) {
					((Label) e.widget).setImage(blackStone_trans);
				} else {
					((Label) e.widget).setImage(whiteStone_trans);
				}
			}

			public void mouseExit(MouseEvent e) {
				((Label) e.widget).setImage(null);
			}
		};

		playPiece = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				((Label) e.widget).setData("isBlack", model.isBlackTurn());
				((Label) e.widget).setData("isEmpty", false);

				// make sure move is legal
				if (verifyMove(((Label) e.widget))) {
					((Label) e.widget).removeMouseTrackListener(showPreview);
					((Label) e.widget).removeMouseListener(playPiece);
					if (model.isBlackTurn()) {
						((Label) e.widget).setImage(blackStone);
					} else {
						((Label) e.widget).setImage(whiteStone);
					}
					
					int[] terr = getTerritory(gameView.goStones);
					gameView.setPoints(model.getBlackPoints(), terr[0], true);
					gameView.setPoints(model.getWhitePoints(), terr[1], false);
					model.changeTurn(gameView);
				} else {
					((Label) e.widget).setData("isBlack", null);
					((Label) e.widget).setData("isEmpty", true);
				}
			}
		};
		
		passTurn = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(model.didPassLast()) {
					endGame(false);
					return;
				}
				model.pass(gameView);
				int[] terr = getTerritory(gameView.goStones);
				gameView.setPoints(model.getBlackPoints(), terr[0], true);
				gameView.setPoints(model.getWhitePoints(), terr[1], false);
			}
		};
		
		forfeit = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String otherPlayer = model.isBlackTurn() ? "White" : "Black";
				MessageBox dialog = new MessageBox(shlGoMenu, SWT.ICON_WARNING | SWT.OK| SWT.CANCEL);
				dialog.setText("Forfeit?");
				dialog.setMessage("Do you really want to forfeit the game? " + otherPlayer + " will win by default!");
				if(dialog.open() == 32) {
					endGame(true);
				}
			}
		};
		
		Label[][] stones = gameView.getStones();
		for (int i = 0; i < model.getBoardSize(); i++) {
			for (int j = 0; j < model.getBoardSize(); j++) {
				stones[j][i].addMouseTrackListener(showPreview);
				stones[j][i].addMouseListener(playPiece);
			}
		}
		
		gameView.passButton.addMouseListener(passTurn);
		gameView.quitButton.addMouseListener(forfeit);
	}
	
	/*
	 * Verifies that a stone placement is legal. Returns true if legal, false if
	 * illegal.
	 */
	protected boolean verifyMove(Label stone) {
		Point loc = stone.getLocation();
		Point stoneXY = model.getDiscreteStoneLocation(loc);
		int x = stoneXY.x;
		int y = stoneXY.y;
		boolean isBlack = (boolean) stone.getData("isBlack");
		ArrayList<Label> temp, captures = new ArrayList<Label>();

		for (int i = -1; i <= 1; i++) {
			// set j to 0 if i is -1 or 1, but if i equals 0 set j to -1.
			int j = i == 0 ? -1 : 0;
			do {
				try {
					// if space is not empty, and the stone in the space is coloured opposite to the
					// stone that was just placed,
					// check to see if it has any liberties.
					if (!(boolean) gameView.getStones()[x + i][y + j].getData("isEmpty")
							&& ((boolean) gameView.getStones()[x + i][y + j].getData("isBlack") != isBlack)) {
						temp = getCaptures(x + i, y + j);
						if (temp != null) {
							// trim out duplicates so that we only capture each stone once.
							for (Label capStone : temp) {
								if (!captures.contains(capStone)) {
									captures.add(capStone);
								}
							}
						}
					}
				} catch (ArrayIndexOutOfBoundsException ae) {
				}
				j += 2;
			} while (i == 0 && j < 2);
		}

		if (captures.isEmpty()) {
			// check to see if this move is suicide
			captures = getCaptures(x, y);

			if (captures != null) {
				return false;
			} else {
				model.setLastCap(new PPoint(-1, -1));
			}
		} else {
			// if the move captures 1 stone, it may be a superko violation.
			if (captures.size() == 1) {
				// If the stone is placed in the location of the last capture, it is a
				// superko violation and should not be allowed.
				if (loc.x == model.getLastCapture().x && loc.y == model.getLastCapture().y) {
					return false;
				} else {
					// If the move is legal, store it in lastCapture.
					model.setLastCap(captures.get(0).getLocation());
				}
			} else {
				model.setLastCap(new Point(-1, -1));
			}
			/*
			 * If this move captured any stones, here's where they actually get removed from
			 * the board.
			 */
			for (Label capStone : captures) {
				capStone.setImage(null);
				capStone.setData("isEmpty", true);
				capStone.setData("isBlack", null);
				capStone.addMouseTrackListener(showPreview);
				capStone.addMouseListener(playPiece);
			}

			// Add prisoners to the player's score
			if (model.isBlackTurn()) {
				model.addBlackPoints(captures.size());
			} else {
				model.addWhitePoints(captures.size());
			}
		}
		return true;
	}

	/*
	 * checks a string for liberties. Returns an arrayList of all stones in the
	 * string if there are no liberties, otherwise returns null if there are
	 * liberties.
	 */
	protected ArrayList<Label> getCaptures(int x, int y) {
		Label stone = gameView.getStones()[x][y];
		boolean isBlack = (boolean) stone.getData("isBlack");
		ArrayList<Label> stones = new ArrayList<Label>();
		ArrayList<Label> temp;
		stones.add(stone);

		// compare left stone with this stone. If space is empty, stone is free. If
		// space is occupied by a stone of the same colour,
		// stone is free. If space is off the board or occupied by opposite colour,
		// stone is not free.

		for (int i = -1; i <= 1; i++) {
			// set j to 0 if i is -1 or 1, but if i equals 0 set j to -1, just like in the
			// method above.
			int j = i == 0 ? -1 : 0;
			do {
				try {
					// if any of the spaces around this stone are empty, return null
					if ((boolean) gameView.getStones()[x + i][y + j].getData("isEmpty")) {
						return null;
					}

					// if any of the stones around this stone are the same colour, they are part of
					// the string and must
					// be checked as well..
					if ((boolean) gameView.getStones()[x + i][y + j].getData("isBlack") == isBlack) {
						stone.setData("isBlack", !isBlack);
						temp = getCaptures(x + i, y + j);
						stone.setData("isBlack", isBlack);

						// if the substring has liberties, then this string has liberties and will
						// return null.
						if (temp == null) {
							return null;
						}

						// if the substring has no liberties, then the stones within it can possibly be
						// captured.
						stones.addAll(temp);
					}
				} catch (ArrayIndexOutOfBoundsException ae) {
				}
				j += 2;
			} while (i == 0 && j < 2);
		}

		return stones;
	}
	
	protected int[] getTerritory(Label[][] goStones) {
		Label[][] safety = new Label[goStones.length][];
		int[] territory = new int[3];
		
		for(int i = 0; i < goStones.length; i++) {
			safety[i] = Arrays.copyOf(goStones[i], goStones.length);
		}
		
		for(int i = 0; i < safety.length; i++) {
			for(int j = 0; j < safety[0].length; j++) {
				if(safety[j][i] != null) {
					int[] temp = calcRegion(safety, j, i);
					if(temp[0] == -1) {
						temp[0] = 2;
					}
					territory[temp[0]] += temp[1];
				}
			}
		}
		
		return territory;
	}
	
	private int[] calcRegion(Label[][] goStones, int x, int y) {
		try {
			if(goStones[x][y] == null) {
				return null;
			}
		} catch (ArrayIndexOutOfBoundsException ae) {
			return null;
		}
		
		int[] region = {-1,0};
		int[] regU, regD, regL, regR;
		
		if(!(boolean)goStones[x][y].getData("isEmpty")) {
			if((boolean)goStones[x][y].getData("isBlack")) {
				region[0] = 0;
				return region;
			}else {
				region[0] = 1;
				return region;
			}
		}
		
		goStones[x][y] = null;
		
		regU = calcRegion(goStones, x, y-1);
		regD = calcRegion(goStones, x, y+1);
		regL = calcRegion(goStones, x-1, y);
		regR = calcRegion(goStones, x+1, y);
		
		if(regU != null) {
			region[0] = regU[0];
			region[1] += regU[1];
		}
		
		if(regD != null) {
			if(region[0] == -1) {
				region[0] = regD[0];
			} else if(regD[0] != -1 && region[0] != regD[0]) {
				region[0] = 2;
			}
			
			region[1] += regD[1];
		}
		
		if(regL != null) {
			if(region[0] == -1) {
				region[0] = regL[0];
			} else if(regL[0] != -1 && region[0] != regL[0]){
				region[0] = 2;
			}
			
			region[1] += regL[1];
		}
		
		if(regR != null) {
			if(region[0] == -1) {
				region[0] = regR[0];
			} else if(regR[0] != -1 && region[0] != regR[0]){
				region[0] = 2;
			}
			
			region[1] += regR[1];
		}
		
		region[1]++;
		return region;
	}
	
	public void endGame(boolean didForfeit) {
		String winMessage = "";
		int[] terr = new int[3];
		
		if(didForfeit) {
			String winner = model.isBlackTurn() ? "White" : "Black";
			String loser = !model.isBlackTurn() ? "White" : "Black";
			winMessage = (loser + " forfeited to " + winner + ", making " + winner + " the winner of this game!\nCongratulations, " + winner + "!");
		} else {
			terr = getTerritory(gameView.goStones);
			double pointsDiff = (terr[0]+model.getBlackPoints())-(terr[1]+model.getWhitePoints());
			String winner = pointsDiff > 0 ? "Black" : "White";
			String loser = pointsDiff < 0 ? "Black" : "White";
			
			if(model.getMoves() < 250) {
				winMessage = "The game of Go has ended. ";
			} else {
				winMessage = "The game has finished. It was a long and hard battle, but in the end ";
			}
			
			winMessage += winner + " beat " + loser + " by " + Math.abs(pointsDiff) + " points after " + model.getMoves() + " moves.\nCongratulations, " + winner + "!";
		}
		
		MessageBox dialog = new MessageBox(shlGoMenu, SWT.ICON_INFORMATION | SWT.OK);
		dialog.setText("Game Over");
		dialog.setMessage(winMessage);
		dialog.open();
		
		shlGoGame.removeListener(SWT.Dispose, shlGoGame.getListeners(SWT.Dispose)[0]);
		shlGoGame.dispose();
		shlGoMenu.setSize(770, 800);
		shlGoMenu.setVisible(true);
	}

	public void saveGame(File fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(this.model);
		fos.close();
		out.close();
	}

	public GoModel openGame(File fileName) throws IOException {
		FileInputStream fis = new FileInputStream(fileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		GoModel model = null;
		try {
			model = (GoModel) ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fis.close();
		ois.close();

		return model;
	}

	public static void main(String args[]) {
		new Controller();
	}
	
	
}
