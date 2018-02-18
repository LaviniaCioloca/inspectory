/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.lavinia.inspectory.am.beans;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import edu.lavinia.inspectory.am.beans.MethodChangesInformation;
import edu.lavinia.inspectory.am.beans.PulsarCriteria;
import edu.lavinia.inspectory.am.beans.SupernovaCriteria;
import net.codebox.javabeantester.JavaBeanTester;

public class MethodInformationTest {
	@Test
	public void testBeanProperties() throws IntrospectionException {
		JavaBeanTester.test(MethodChangesInformation.class);
	}

	@Test
	public void testGetMethodInformationLine() {
		MethodChangesInformation methodChangesInformation = new MethodChangesInformation();
		ArrayList<String> expected = new ArrayList<>();
		methodChangesInformation.setInitialSize(10);
		methodChangesInformation.setActualSize(100);
		methodChangesInformation.setNumberOfChanges(10);
		ArrayList<Integer> changesList = new ArrayList<>(Arrays.asList(1, 2, 3));
		methodChangesInformation.setChangesList(changesList);
		methodChangesInformation.setSupernova(false);
		methodChangesInformation.setPulsar(false);
		methodChangesInformation.setSupernovaSeverity(5);
		methodChangesInformation.setPulsarSeverity(5);
		SupernovaCriteria supernovaCriteria = new SupernovaCriteria();
		PulsarCriteria pulsarCriteria = new PulsarCriteria();
		methodChangesInformation.setSupernovaCriteria(supernovaCriteria);
		methodChangesInformation.setPulsarCriteria(pulsarCriteria);

		for (int i = 0; i < 3; ++i) {
			expected.add(null);
		}
		expected.add("10");
		expected.add("100");
		expected.add("10");
		expected.add(changesList.toString());
		expected.add(methodChangesInformation.isSupernova().toString());
		expected.add("5");
		expected.add("0");
		expected.add("0");
		expected.add("0");
		expected.add("0");
		expected.add("0");
		expected.add(methodChangesInformation.isPulsar().toString());
		expected.add("5");
		expected.add("0");
		expected.add("0");
		expected.add("0");
		expected.add("0");
		Assert.assertEquals(expected, methodChangesInformation.getMethodInformationLine());
	}
}
