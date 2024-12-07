/*
 * @author: Ethan Kronebusch
 * Date: 2021-02-08
 * Project: goGameGUI
 * Program: GoModel.java
 * Description: TODO
 */
package goGameGUI;

import java.io.Serializable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * @author not
 *
 */
public class GoModel implements Serializable {

	/**
	 * 
	 */
	// data members
	private Stone[][] goStones;
	private boolean lastPass, blackTurn;
	private PPoint lastCapture;
	private int boardSize, stoneSize, moves;
	private double whitePoints, blackPoints, komi, spaceSize, offsetConstant;
	private static final long serialVersionUID = 5580589718510413105L;
	
	// constructors
	public GoModel() {
		lastPass = false;
		blackTurn = true;
		lastCapture = new PPoint(-1, -1);
		whitePoints = 0;
		blackPoints = 0;
		boardSize = 19;
		stoneSize = 36;
		moves = 0;
		komi = 5.5;
	}

	// utility function
	public Stone makeStoneFromLabel(Label stone) {
		boolean isEmpty = (boolean) stone.getData("isEmpty");
		boolean isBlack;
		if(!isEmpty) {
		isBlack = (boolean) stone.getData("isBlack");
		}else {
			isBlack = false;
		}
		PPoint p = new PPoint(stone.getLocation().x, stone.getLocation().y);
		return new Stone(isBlack, isEmpty, p);
	}

	// setters and getters
	public Stone[][] getStones() {
		return goStones;
	}

	// unsure if method is ever used, but updates all stones according to input of
	// stone label matrix
	public void setStones(Label[][] stoneBoard) {
		Stone[][] stones = new Stone[stoneBoard.length][stoneBoard.length];
		//Stone s = null;
		for (int i = 0; i < stoneBoard.length; i++) {
			for (int j = 0; j < stoneBoard.length; j++) {
				stones[i][j] = this.makeStoneFromLabel(stoneBoard[i][j]);
			}
		}
		goStones = stones;
	}

	public void setStone(Label stone, int x, int y) {
		goStones[x][y] = this.makeStoneFromLabel(stone);
	}

	public boolean isStoneBlack(int x, int y) {
		return (boolean) goStones[x][y].isBlack();
	}

	public boolean isStoneEmpty(int x, int y) {
		return (boolean) goStones[x][y].isEmpty();
	}

	public boolean isBlackTurn() {
		return blackTurn;
	}

	public void changeTurn(GoView goView) {
		lastPass = false;
		blackTurn = !blackTurn;
		changeTurnTracker(goView);
	}

	public void pass(GoView goView) {
		if(lastPass) {
			//TODO Add game-end code here
			System.out.println("game should end now");
		}
		
		if(blackTurn) {
			blackPoints--;
			whitePoints++;
		} else {
			blackPoints++;
			whitePoints--;
		}
		
		lastPass = true;
		blackTurn = !blackTurn;
		changeTurnTracker(goView);
	}
	
	public void changeTurnTracker(GoView goView) {
		int goIconSize = (int)(goView.getViewSize()*0.05);
		
		if(blackTurn) {
			goView.turnTracker.setText("Black's Turn");
			goView.turnTracker.setImage(SWTResourceManager.getSVGImage("sourceCode/goGameGUI/black_stone.svg",goIconSize,goIconSize));
		}else {
			goView.turnTracker.setText("White's Turn");
			goView.turnTracker.setImage(SWTResourceManager.getSVGImage("sourceCode/goGameGUI/white_stone.svg",goIconSize,goIconSize));
		}
		
		moves++;
	}
	
	public boolean didPassLast() {
		return lastPass;
	}

	public void setLastCap(PPoint captureLocation) {
		lastCapture = captureLocation;
	}

	public void setLastCap(Point captureLocation) {
		this.lastCapture = new PPoint(captureLocation.x, captureLocation.y);
	}

	public double addBlackPoints(double points) {
		blackPoints += points;
		return blackPoints;
	}

	public double addWhitePoints(double points) {
		whitePoints += points;
		return whitePoints;
	}

	public PPoint getLastCapture() {
		return lastCapture;
	}

	public double getWhitePoints() {
		return whitePoints;
	}

	public double getBlackPoints() {
		return blackPoints;
	}
	
	public void setKomi(double komi) {
		this.komi = komi;
	}
	
	public double getKomi() {
		return komi;
	}
	
	public int getStoneSize() {
		return stoneSize;
	}
	
	public int getBoardSize() {
		return boardSize;
	}
	
	public int getMoves() {
		return moves;
	}
	
	public void setUpStones(GoView view, Shell goShell) {
		double borderCoeff, spaceSize, stoneSize, offsetConstant;
		
		borderCoeff = (0.025*Math.pow(boardSize, 2)-1.3*boardSize+21.175)/1000;
		spaceSize = goShell.getClientArea().width*(1-borderCoeff*2)/boardSize;
		this.spaceSize = spaceSize;
		stoneSize = (spaceSize*8)/9;
		this.stoneSize = (int) Math.round(stoneSize);
		offsetConstant = goShell.getClientArea().width*borderCoeff + (spaceSize - stoneSize)/2;
		this.offsetConstant = offsetConstant;
		addWhitePoints(komi);
		
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				view.goStones[j][i] = new Label(view.goban, SWT.NONE);
				view.goStones[j][i].setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
				view.goStones[j][i].setImage(null);
				view.goStones[j][i].setBounds((int) Math.round(spaceSize*j + offsetConstant), (int) Math.round(spaceSize*i + offsetConstant), this.stoneSize, this.stoneSize);
				view.goStones[j][i].setData("isEmpty", true);
			}
		}
	}
	
	public Point getDiscreteStoneLocation(Point pixelValues) {
		Point discreteLoc = new Point(0, 0);
		discreteLoc.x = (int)Math.round((Double.parseDouble(Integer.toString(pixelValues.x)) - offsetConstant)/spaceSize);
		discreteLoc.y = (int)Math.round((Double.parseDouble(Integer.toString(pixelValues.y)) - offsetConstant)/spaceSize);
		return discreteLoc;
	}
	
	/*
	 * //ended up not using Externalizable interface but keeping here just in case
	 * 
	 * @Override public void writeExternal(ObjectOutput out) throws IOException {
	 * Stone[][] stones = new Stone[19][19];
	 * 
	 * int x = this.lastCapture.x; int y = this.lastCapture.y;
	 * out.writeObject(stones); out.writeInt(x); out.writeInt(y);
	 * out.writeBoolean(lastPass); out.writeBoolean(blackTurn);
	 * out.writeInt(blackPoints); out.writeInt(whitePoints);
	 * 
	 * }
	 * 
	 * @Override public void readExternal(ObjectInput in) throws IOException,
	 * ClassNotFoundException {
	 * 
	 * Stone[][] stones = (Stone[][])in.readObject(); Label[][] goStones = new
	 * Label[stones.length][stones[0].length]; for (int i = 0; i < stones.length;
	 * i++) { for (int j = 0; j< stones[0].length; j++) { Label stone = new
	 * Label(null, j); //TODO fix this boolean isBlack = stones[i][j].isBlack;
	 * boolean isEmpty = stones[i][j].isEmpty; stone.setData("isBlack", isBlack);
	 * stone.setData("isEmpty", isEmpty); //rest of each label's attributes have to
	 * be assigned } } this.goStones = stones; int x = in.readInt(); int y =
	 * in.readInt(); this.lastCapture = new Point(x,y); this.lastPass =
	 * in.readBoolean(); this.blackTurn = in.readBoolean(); this.blackPoints =
	 * in.readInt(); this.whitePoints = in.readInt();
	 * 
	 * 
	 * }
	 */

}
