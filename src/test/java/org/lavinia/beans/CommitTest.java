package org.lavinia.beans;

import java.beans.IntrospectionException;

import org.junit.Assert;
import org.junit.Test;

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
