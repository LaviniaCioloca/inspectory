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
package edu.lavinia.inspectory.op.beans;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.IntrospectionException;

import org.junit.Test;

import net.codebox.javabeantester.JavaBeanTester;

public class EntityOwnershipInformationTest {

	/**
	 * JavaBeanTester tests bean class MethodChangesInformation and might throw
	 * {@code IntrospectionException} if any error appears.
	 * 
	 * @throws IntrospectionException
	 */
	@Test
	public void testBeanProperties() throws IntrospectionException {
		JavaBeanTester.test(EntityOwnershipInformation.class);
	}

	@Test
	public void testEqualsTrue() {
		EntityOwnershipInformation entityOwnershipInformation = new EntityOwnershipInformation();

		assertTrue(
				entityOwnershipInformation.equals(entityOwnershipInformation));
	}

	@Test
	public void testEqualsFalseNotSameObject() {
		EntityOwnershipInformation entityOwnershipInformation1 = new EntityOwnershipInformation();
		EntityOwnershipInformation entityOwnershipInformation2 = new EntityOwnershipInformation();
		entityOwnershipInformation2.setNumberOfChanges(10);

		assertFalse(entityOwnershipInformation1
				.equals(entityOwnershipInformation2));
	}

	@Test
	public void testEqualsFalseNotInstanceOfEntityBean() {
		EntityOwnershipInformation entityOwnershipInformation1 = new EntityOwnershipInformation();
		Object entityOwnershipInformation2 = new Object();

		assertFalse(entityOwnershipInformation1
				.equals(entityOwnershipInformation2));
	}

	@Test
	public void testGetHashCode() {
		EntityOwnershipInformation entityOwnershipInformation1 = new EntityOwnershipInformation();

		entityOwnershipInformation1.hashCode();
	}
}
