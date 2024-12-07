package goGameGUI;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GoModelTestJunit {
	
	GoModel model = new GoModel();
	
	@Test
	void testSetLastCap() {
		assertEquals(true, new PPoint(-1,-1).equals(model.getLastCapture()));
	}

	@Test
	void testIsBlackTurn() {
		assertEquals(true, model.isBlackTurn());
	}

	@Test
	void testAddBlackPoints() {
		model.addBlackPoints(100);
		assertEquals(100, model.getBlackPoints());
	}

}
