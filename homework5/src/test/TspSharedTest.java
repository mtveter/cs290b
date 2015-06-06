package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import system.TspShared;

public class TspSharedTest {
	TspShared shared = new TspShared(32);
	
	@Test
	public void isOlderThanTest() {
		
		TspShared other;
		boolean result;
		
		other = new TspShared(31);
		result = shared.isOlderThan(other);
		assertTrue("Should be a lower value", result);
		
		other = new TspShared(33);
		result = shared.isOlderThan(other);
		assertFalse("Should be a higher value", result);
	}
	@Test
	public void setCurrentMstCostTest() {
		shared.setCurrentMstCost(100.0);
		double expectedResult = 100.0;
		double result = shared.getCurrentMstCost();
		assertEquals(expectedResult, result, 1);
	}
	@Test
	public void decrementMstCostTest() {
		double expectedResult;
		double result;
		
		shared.decrementCurrentMstCost(10.0);
		expectedResult = 0.0;
		result = shared.getCurrentMstCost();
		assertEquals(expectedResult, result, 0.01);
		
		shared.incrementCurrentMstCost(11.0);
		shared.decrementCurrentMstCost(10.0);
		expectedResult = 1.0;
		result = shared.getCurrentMstCost();
		assertEquals(expectedResult, result, 0.01);
		
		shared.decrementCurrentMstCost(2.0);
		expectedResult = 1.0;
		result = shared.getCurrentMstCost();
		assertEquals(expectedResult, result, 0.01);
	}
	@Test
	public void incrementMstCostTest() {
		double expectedResult;
		double result;
		
		shared.incrementCurrentMstCost(10.0);
		expectedResult = 10.0;
		result = shared.getCurrentMstCost();
		assertEquals(expectedResult, result, 0.01);
		
		shared.incrementCurrentMstCost(5.0);
		expectedResult = 15.0;
		result = shared.getCurrentMstCost();
		assertEquals(expectedResult, result, 0.01);
		
		shared.incrementCurrentMstCost(-10.0);
		expectedResult = 15.0;
		result = shared.getCurrentMstCost();
		assertEquals(expectedResult, result, 0.01);
	}
	
	@Test
	public void addNeighborToCityTest() {
		ArrayList<Integer> expectedResult;
		ArrayList<Integer> result;
		
		
		shared.addNeighborToCity(1, 2);
		shared.addNeighborToCity(1, 3);
		
		expectedResult = new ArrayList<Integer>();
		expectedResult.add(2);
		expectedResult.add(3);
		result = shared.getLbAdjacencyMapValue(1);
		assertEquals("Should return the correct list of neighbors", expectedResult, result);
	}
	@Test
	public void removeNeighborFromCityTest(){
		ArrayList<Integer> expectedResult;
		ArrayList<Integer> result;
		
		
		shared.addNeighborToCity(1, 2);
		shared.addNeighborToCity(1, 3);
		shared.removeNeighborFromCity(1, 2);
		
		expectedResult = new ArrayList<Integer>();
		expectedResult.add(3);
		result = shared.getLbAdjacencyMapValue(1);
		assertEquals("", expectedResult, result);
		
		shared.removeNeighborFromCity(1, 3);
		
		expectedResult = new ArrayList<Integer>();
		result = shared.getLbAdjacencyMapValue(1);
		assertEquals("", expectedResult, result);
	}
}
