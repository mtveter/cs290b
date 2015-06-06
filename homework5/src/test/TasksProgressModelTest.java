package test;

import models.TasksProgressModel;
import static org.junit.Assert.*;

import org.junit.Test;

public class TasksProgressModelTest {
	TasksProgressModel model;
	@Test
	public void setTotalCitiesTest() {
		model = new TasksProgressModel();
		model.setTotalCities(10);
		
		int expectedResult = 10;
		int result = model.getTotalCities();
		assertEquals("Correct number of cities should be returned", expectedResult, result);
	}
	@Test
	public void increaseTotalPrunedTasksTest() {
		model = new TasksProgressModel();
		model.increaseTotalPrunedTasks(10);
		int expectedResult = 10;
		int result = model.getTotalPrunedTasks();
		
		assertEquals("Correct number of tasks should be returned", expectedResult, result);
	}
	@Test
	public void increaseTotalGeneratedTasksTest() {
		model = new TasksProgressModel();
		model.increaseTotalGeneratedTasks(10);
		
		int expectedResult = 10;
		int result = model.getTotalGeneratedTasks();
		assertEquals("Correct number of tasks should be returned", expectedResult, result);
	}
	@Test
	public void increaseTotalCompletedTasksTest() {
		model = new TasksProgressModel();
		model.increaseTotalCompletedTasks(10);
		int expectedResult = 10;
		int result = model.getTotalCompletedTasks();
		
		assertEquals("Correct number of tasks should be returned", expectedResult, result);
	}
	@Test
	public void addCompletedTaskWeightTest() {
		model = new TasksProgressModel();
		model.setTotalCities(10);
		
		model.addCompletedTaskWeight("012");
		int expectedResult = (int) (100/(10*9));
		int result = (int )model.getTasksCompletedPercentage();
		assertEquals("Corrent percentage should be returned", expectedResult, result);
	}

}
