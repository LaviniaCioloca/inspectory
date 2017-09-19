/*******************************************************************************
 * Copyright (c) 2017 Lavinia Cioloca
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
package org.lavinia.metrics;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

public class MethodMetricsTest {

	@Test
	public void testGetDifferenceInDays() throws ParseException {
		MethodMetrics methodMetric = new PulsarMetric();
		Date start = new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01");
		Date end = new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/05");
		assertTrue(methodMetric.getDifferenceInDays(start, end) == 4.0);
	}

	@Test
	public void testGetCommitsTypes() {
		MethodMetrics methodMetric = new PulsarMetric();
		ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-10);
		changesList.add(1);
		changesList.add(10);
		ArrayList<String> expectedResultList = new ArrayList<>();
		expectedResultList.add("refactor");
		expectedResultList.add("refine");
		expectedResultList.add("develop");
		assertTrue(methodMetric.getCommitsTypes(changesList).equals(expectedResultList));
	}
}
