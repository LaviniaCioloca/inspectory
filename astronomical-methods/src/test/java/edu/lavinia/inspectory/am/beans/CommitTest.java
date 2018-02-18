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

import org.junit.Assert;
import org.junit.Test;

import edu.lavinia.inspectory.am.beans.Commit;
import net.codebox.javabeantester.JavaBeanTester;

public class CommitTest {
	@Test
	public void testBeanProperties() throws IntrospectionException {
		JavaBeanTester.test(Commit.class);
	}

	@Test
	public void testToString() {
		Commit commit = new Commit();
		String expected = "Commit [revision=null, date=null, author=null]";
		Assert.assertEquals(expected, commit.toString());
	}
}
