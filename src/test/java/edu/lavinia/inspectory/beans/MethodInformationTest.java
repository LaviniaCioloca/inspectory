package edu.lavinia.inspectory.beans;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import edu.lavinia.inspectory.beans.MethodInformation;
import net.codebox.javabeantester.JavaBeanTester;

public class MethodInformationTest {
	@Test
	public void testBeanProperties() throws IntrospectionException {
		JavaBeanTester.test(MethodInformation.class);
	}

	@Test
	public void testGetMethodInformationLine() {
		MethodInformation methodInformation = new MethodInformation();
		ArrayList<String> expected = new ArrayList<>();
		methodInformation.setInitialSize(10);
		methodInformation.setActualSize(100);
		methodInformation.setNumberOfChanges(10);
		ArrayList<Integer> changesList = new ArrayList<>(Arrays.asList(1, 2, 3));
		methodInformation.setChangesList(changesList);
		methodInformation.setSupernova(false);
		methodInformation.setPulsar(false);
		methodInformation.setSupernovaSeverity(5);
		methodInformation.setPulsarSeverity(5);
		SupernovaCriteria supernovaCriteria = new SupernovaCriteria();
		PulsarCriteria pulsarCriteria = new PulsarCriteria();
		methodInformation.setSupernovaCriteria(supernovaCriteria);
		methodInformation.setPulsarCriteria(pulsarCriteria);

		for (int i = 0; i < 3; ++i) {
			expected.add(null);
		}
		expected.add("10");
		expected.add("100");
		expected.add("10");
		expected.add(changesList.toString());
		expected.add(methodInformation.isSupernova().toString());
		expected.add(methodInformation.isPulsar().toString());
		expected.add("5");
		expected.add("5");
		expected.add(
				"SupernovaCriteria [leapsSizePoints=0, recentLeapsSizePoints=0, subsequentRefactoringPoints=0, methodSizePoints=0, activityStatePoints=0]");
		expected.add(
				"PulsarCriteria [recentCyclesPoints=0, averageSizeIncreasePoints=0, methodSizePoints=0, activityStatePoints=0]");
		Assert.assertEquals(expected, methodInformation.getMethodInformationLine());
	}
}
