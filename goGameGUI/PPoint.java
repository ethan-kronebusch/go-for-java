package goGameGUI;

import java.io.Serializable;

public class PPoint implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1459428560384061906L;
	int x;
	int y;
	
	public PPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (this.getClass() != o.getClass()) return false;
		if(this.x == ((PPoint)o).x && this.y == ((PPoint)o).y) return true;
		return false;
	}
	
}