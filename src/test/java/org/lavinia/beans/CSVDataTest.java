package org.lavinia.beans;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import net.codebox.javabeantester.JavaBeanTester;

public class CSVDataTest {
	@Test
	public void testBeanProperties() throws IntrospectionException {
		JavaBeanTester.test(CSVData.class);
	}

	@Test
	public void testGetCSVLine() {
		CSVData csvData = new CSVData();
		ArrayList<String> expected = new ArrayList<>();
		csvData.setInitialSize(10);
		csvData.setActualSize(100);
		csvData.setNumberOfChanges(10);
		ArrayList<Integer> changesList = new ArrayList<>(Arrays.asList(1, 2, 3));
		csvData.setChangesList(changesList);
		csvData.setSupernova(false);
		csvData.setPulsar(false);
		csvData.setSupernovaSeverity(5);
		csvData.setPulsarSeverity(5);
		
		for (int i = 0; i < 3; ++i) {
			expected.add(null);
		}
		expected.add("10");
		expected.add("100");
		expected.add("10");
		expected.add(changesList.toString());
		expected.add("false");
		expected.add("false");
		expected.add("5");
		expected.add("5");
		Assert.assertEquals(expected, csvData.getCSVLine());
	}
}
