/*******************************************************************************
 * Copyright (c) 2017, 2018 Lavinia Cioloca
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package edu.lavinia.inspectory.am.beans;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import net.codebox.javabeantester.JavaBeanTester;

public class MethodInformationTest {

	/**
	 * JavaBeanTester tests bean class MethodChangesInformation and might throw
	 * {@code IntrospectionException} if any error appears.
	 * 
	 * @throws IntrospectionException
	 */
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
		ArrayList<Integer> changesList = new ArrayList<>(
				Arrays.asList(1, 2, 3));
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
		Assert.assertEquals(expected,
				methodChangesInformation.getMethodInformationLine());
	}
}
